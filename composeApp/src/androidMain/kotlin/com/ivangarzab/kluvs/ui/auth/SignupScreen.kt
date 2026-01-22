package com.ivangarzab.kluvs.ui.auth

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ivangarzab.kluvs.auth.presentation.AuthMode
import com.ivangarzab.kluvs.auth.presentation.AuthState
import com.ivangarzab.kluvs.auth.presentation.AuthViewModel
import com.ivangarzab.kluvs.auth.presentation.LoginNavigation
import com.ivangarzab.kluvs.model.AuthProvider
import com.ivangarzab.kluvs.ui.OAuthCallbackHandler
import com.ivangarzab.kluvs.ui.components.LoadingScreen
import com.ivangarzab.kluvs.ui.extensions.toLocalizedMessage
import org.koin.androidx.compose.koinViewModel
import androidx.core.net.toUri

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
    onNavigateToLogIn: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Collect OAuth callbacks from MainActivity
    LaunchedEffect(Unit) {
        OAuthCallbackHandler.callbacks.collect { callbackUrl ->
            viewModel.handleOAuthCallback(callbackUrl)
        }
    }

    // Launch browser when OAuthPending state is emitted
    LaunchedEffect(state) {
        if (state is AuthState.OAuthPending) {
            val url = (state as AuthState.OAuthPending).url
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
            viewModel.onOAuthUrlLaunched()
        }
    }

    when (state) {
        is AuthState.Loading -> LoadingScreen()
        is AuthState.Authenticated -> { /* No-op */ }
        is AuthState.OAuthPending -> LoadingScreen() // Show loading while browser opens
        is AuthState.Unauthenticated,
        is AuthState.Error -> {
            AuthFormContent(
                modifier = modifier,
                mode = AuthMode.SIGNUP,
                state = uiState,
                errorMessage = (state as? AuthState.Error)?.error?.toLocalizedMessage(),
                onEmailFieldChange = viewModel::onEmailFieldChanged,
                onPasswordFieldChange = viewModel::onPasswordFieldChanged,
                onConfirmPasswordFieldChange = viewModel::onConfirmPasswordFieldChanged,
                onSubmit = viewModel::validateAndSignUp,
                onOAuthSignIn = { provider ->
                    when (provider) {
                        AuthProvider.DISCORD -> viewModel.signInWithDiscord()
                        AuthProvider.GOOGLE -> viewModel.signInWithGoogle()
                        else -> { /* Apple handled natively on iOS only */ }
                    }
                },
                onNavigate = {
                    when (it) {
                        LoginNavigation.SignIn -> onNavigateToLogIn()
                        else -> onNavigateToForgotPassword()
                    }
                }
            )
        }
    }
}