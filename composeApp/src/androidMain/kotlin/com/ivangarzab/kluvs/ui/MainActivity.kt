package com.ivangarzab.kluvs.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.ivangarzab.kluvs.designsystem.components.KluvsSnackbar
import com.ivangarzab.kluvs.designsystem.components.KluvsSnackbarVisuals
import com.ivangarzab.kluvs.designsystem.components.SnackbarVariant
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.app.AppCoordinator
import com.ivangarzab.kluvs.app.AutoJoinResult
import com.ivangarzab.kluvs.app.NavigationState
import com.ivangarzab.kluvs.app.PendingJoinCoordinator
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.ui.auth.LoginScreen
import com.ivangarzab.kluvs.ui.auth.SignupScreen
import com.ivangarzab.kluvs.ui.join.JoinScreen
import com.ivangarzab.kluvs.ui.settings.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Handle OAuth callback if app was launched via deep link
        handleOAuthIntent(intent)

        setContent {
            KluvsTheme {
                val navController = rememberNavController()
                MainNavHost(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = KluvsTheme.colors.background),
                    navController = navController
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Expand once we have more intents/deeplinks to handle
        handleOAuthIntent(intent)
    }

    private fun handleOAuthIntent(intent: Intent?) {
        val uri = intent?.data ?: return

        if (uri.scheme == "kluvs" && uri.host == "auth" && uri.path == "/callback") {
            Bark.d("Processing OAuth callback from deep link")
            OAuthCallbackHandler.handleCallback(uri.toString())
        }
    }
}

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val appCoordinator: AppCoordinator = koinViewModel()
    val pendingJoinCoordinator: PendingJoinCoordinator = koinViewModel()
    val navState by appCoordinator.navigationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Only set on a successful auto-join after sign-in; consumed by the MAIN destination
    // to open straight into that club (see PendingJoinCoordinator).
    var autoJoinedClubId by remember { mutableStateOf<String?>(null) }

    // Navigate based on app-level state
    LaunchedEffect(navState) {
        when (navState) {
            is NavigationState.Unauthenticated -> {
                // Only navigate if not already on login
                if (navController.currentDestination?.route != NavDestinations.LOGIN) {
                    navController.navigate(NavDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is NavigationState.Authenticated -> {
                // Only navigate if not already on main
                if (navController.currentDestination?.route != NavDestinations.MAIN) {
                    navController.navigate(NavDestinations.MAIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            NavigationState.Initializing -> {
                // Do nothing - show splash/loading
            }
        }
    }

    LaunchedEffect(Unit) {
        pendingJoinCoordinator.autoJoinResult.collect { result ->
            when (result) {
                is AutoJoinResult.Success -> autoJoinedClubId = result.clubId
                is AutoJoinResult.Failure -> {
                    snackbarHostState.showSnackbar(
                        KluvsSnackbarVisuals(
                            message = result.message ?: "Failed to join club",
                            variant = SnackbarVariant.DANGER,
                        )
                    )
                }
            }
        }
    }

    Box(modifier = modifier) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = NavDestinations.LOGIN
        ) {
            composable(NavDestinations.LOGIN) {
                LoginScreen(
                    onNavigateToSignUp = {
                        navController.navigate(NavDestinations.SIGNUP)
                    },
                )
            }
            composable(NavDestinations.SIGNUP) {
                SignupScreen(
                    onNavigateToLogIn = {
                        navController.navigate(NavDestinations.LOGIN)
                    },
                )
            }
            composable(NavDestinations.MAIN) {
                val userId = (navState as? NavigationState.Authenticated)?.userId
                if (userId != null) {
                    MainScreen(
                        userId = userId,
                        initialClubId = autoJoinedClubId,
                        onNavigateToSettings = {
                            navController.navigate("${NavDestinations.SETTINGS}/$userId")
                        },
                    )
                }
            }
            composable("${NavDestinations.SETTINGS}/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                SettingsScreen(
                    userId = userId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            // "Join with a code" from inside the Clubs tab now opens JoinBottomSheet in-place
            // instead of navigating here — this route has no current entry point. It's kept
            // registered for the not-yet-built Android App Links deep-link case (tapping a raw
            // invite URL while signed out, per JoinScreen's own doc comment): that flow needs a
            // full screen it can land on before auth even resolves, plus the needsSignIn→Login
            // handoff below, neither of which fits a bottom sheet nested inside the (already
            // authenticated) Clubs tab.
            composable(NavDestinations.JOIN) {
                JoinScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToClub = { clubId ->
                        autoJoinedClubId = clubId
                        navController.navigate(NavDestinations.MAIN) {
                            popUpTo(NavDestinations.JOIN) { inclusive = true }
                        }
                    },
                    onNeedsSignIn = { token ->
                        pendingJoinCoordinator.setPendingToken(token)
                        navController.navigate(NavDestinations.LOGIN)
                    }
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
            snackbar = { KluvsSnackbar(it) }
        )
    }
}

object NavDestinations {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val MAIN = "main"
    const val SETTINGS = "settings"
    const val JOIN = "join"
}