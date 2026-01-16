package com.ivangarzab.bookclub.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.ivangarzab.bookclub.presentation.viewmodels.auth.AuthMode
import com.ivangarzab.bookclub.presentation.viewmodels.auth.AuthState
import com.ivangarzab.bookclub.presentation.viewmodels.auth.AuthViewModel
import com.ivangarzab.bookclub.presentation.viewmodels.auth.LoginNavigation
import com.ivangarzab.bookclub.ui.components.LoadingScreen
import com.ivangarzab.bookclub.ui.extensions.toLocalizedMessage
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    val uiState by viewModel.uiState.collectAsState()

    when (state) {
        is AuthState.Loading -> LoadingScreen()
        is AuthState.Authenticated ->  { /* No-op */ }
        is AuthState.Unauthenticated,
        is AuthState.Error -> {
            AuthFormContent(
                modifier = modifier,
                mode = AuthMode.LOGIN,
                state = uiState,
                errorMessage = (state as? AuthState.Error)?.error?.toLocalizedMessage(),
                onEmailFieldChange = viewModel::onEmailFieldChanged,
                onPasswordFieldChange = viewModel::onPasswordFieldChanged,
                onConfirmPasswordFieldChange = viewModel::onConfirmPasswordFieldChanged,
                onSubmit = viewModel::validateAndSignIn,
                onOAuthSignIn = { provider ->
                    // TODO: Implement provider login
                },
                onNavigate = {
                    when (it) {
                        LoginNavigation.SignUp -> onNavigateToSignUp()
                        else -> onNavigateToForgotPassword()
                    }
                }
            )
        }
    }
}