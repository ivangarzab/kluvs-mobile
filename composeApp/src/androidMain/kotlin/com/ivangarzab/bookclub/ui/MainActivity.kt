package com.ivangarzab.bookclub.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ivangarzab.bookclub.app.AppCoordinator
import com.ivangarzab.bookclub.app.NavigationState
import com.ivangarzab.bookclub.theme.KluvsTheme
import com.ivangarzab.bookclub.ui.auth.LoginScreen
import com.ivangarzab.bookclub.ui.auth.SignupScreen
import com.ivangarzab.bookclub.ui.components.LoadingScreen
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            KluvsTheme {
                val navController = rememberNavController()
                MainNavHost(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background),
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val appCoordinator: AppCoordinator = koinViewModel()
    val navState by appCoordinator.navigationState.collectAsState()

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

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavDestinations.LOGIN
    ) {
        composable(NavDestinations.LOGIN) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(NavDestinations.SIGNUP)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(NavDestinations.FORGOT_PASSWORD)
                },
                onNavigateToMain = {
                    // Navigation handled by AppCoordinator - no-op
                },
            )
        }
        composable(NavDestinations.SIGNUP) {
            SignupScreen(
                onNavigateToLogIn = {
                    navController.navigate(NavDestinations.LOGIN)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(NavDestinations.FORGOT_PASSWORD)
                },
                onNavigateToMain = {
                    // Navigation handled by AppCoordinator - no-op
                },
            )
        }
        composable(NavDestinations.FORGOT_PASSWORD) {
            Text("Coming Soon...")
        }
        composable(NavDestinations.MAIN) {
            val userId = (navState as? NavigationState.Authenticated)?.userId
            if (userId != null) {
                MainScreen(userId = userId)
            }
        }
    }
}

object NavDestinations {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val MAIN = "main"
}