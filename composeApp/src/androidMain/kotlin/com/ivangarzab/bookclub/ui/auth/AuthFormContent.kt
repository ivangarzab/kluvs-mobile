package com.ivangarzab.bookclub.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivangarzab.bookclub.R
import com.ivangarzab.bookclub.domain.models.AuthProvider
import com.ivangarzab.bookclub.presentation.viewmodels.auth.AuthMode
import com.ivangarzab.bookclub.presentation.viewmodels.auth.AuthUiState
import com.ivangarzab.bookclub.presentation.viewmodels.auth.LoginNavigation
import com.ivangarzab.bookclub.theme.KluvsTheme
import com.ivangarzab.bookclub.theme.signInDiscord
import com.ivangarzab.bookclub.theme.signInGoogle
import com.ivangarzab.bookclub.ui.components.InputField
import com.ivangarzab.bookclub.ui.components.SocialButton
import com.ivangarzab.bookclub.ui.components.TextDivider

@Composable
fun AuthFormContent(
    modifier: Modifier = Modifier,
    mode: AuthMode,
    state: AuthUiState,
    errorMessage: String? = null,
    onEmailFieldChange: (String) -> Unit,
    onPasswordFieldChange: (String) -> Unit,
    onConfirmPasswordFieldChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onOAuthSignIn: (AuthProvider) -> Unit,
    onNavigate: (LoginNavigation) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
            )
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    actionColor = MaterialTheme.colorScheme.error
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Welcome to your Kluvs",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = if (mode == AuthMode.LOGIN) {
                    "Sign in to your account"
                } else {
                    "Create a new account"
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(24.dp))

            SocialButton(
                text = "Continue with Discord",
                icon = painterResource(R.drawable.ic_discord),
                iconSize = 20.dp,
                backgroundColor = signInDiscord,
                textColor = Color(0xFFFFFFFF),
                onClick = { onOAuthSignIn(AuthProvider.DISCORD) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SocialButton(
                text = "Continue with Google",
                icon = painterResource(R.drawable.ic_google),
                iconSize = 40.dp,
                backgroundColor = signInGoogle,
                textColor = Color(0xFF1F1F1F),
                onClick = { onOAuthSignIn(AuthProvider.GOOGLE) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextDivider(text = "or continue with email")

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                modifier = Modifier.fillMaxWidth(),
                label = "Email",
                value = state.emailField,
                onValueChange = onEmailFieldChange,
                iconRes = R.drawable.ic_email,
                iconDescription = "Email text field icon",
                supportingText = state.emailError ?: "Enter valid email address",
                supportingTextColor = if (state.emailError != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color.Gray
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            InputField(
                modifier = Modifier.fillMaxWidth(),
                isPassword = true,
                label = "Password",
                value = state.passwordField,
                onValueChange = onPasswordFieldChange,
                iconRes = R.drawable.ic_password,
                iconDescription = "Password text field icon",
                supportingText = state.passwordError ?: "Minimum 8 characters",
                supportingTextColor = if (state.emailError != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color.Gray
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (mode == AuthMode.LOGIN) {
                        ImeAction.Go
                    } else {
                        ImeAction.Next
                    }
                ),
                keyboardActions = KeyboardActions(
                    onGo = { onSubmit()}
                ),
            )

            if (mode == AuthMode.SIGNUP) {
                Spacer(modifier = Modifier.height(8.dp))

                InputField(
                    modifier = Modifier.fillMaxWidth(),
                    isPassword = true,
                    label = "Confirm Password",
                    value = state.confirmPasswordField,
                    onValueChange = onConfirmPasswordFieldChange,
                    iconRes = R.drawable.ic_password,
                    iconDescription = "Confirm password text field icon",
                    supportingText = state.confirmPasswordError ?: "Must match password above",
                    supportingTextColor = if (state.emailError != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        Color.Gray
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = { onSubmit() }
                    ),
                )
            }

            if (mode == AuthMode.LOGIN) {
                TextButton(
                    modifier = Modifier
                        .align(Alignment.End),
                    onClick = { onNavigate(LoginNavigation.ForgetPassword) },
                    content = {
                        Text(
                            text = "Forgot password?",
                            textAlign = TextAlign.Right,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = { onSubmit() }
            ) {
                Text(
                    text = if (mode == AuthMode.LOGIN) {
                        "Sign In"
                    } else {
                        "Sign Up"
                    },
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.background
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (mode == AuthMode.LOGIN) {
                        "Don't have an account?"
                    } else {
                        "Already have an account?"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                onNavigate(
                                    if (mode == AuthMode.LOGIN) {
                                        LoginNavigation.SignUp
                                    } else {
                                        LoginNavigation.SignIn
                                    }
                                )
                            }
                        ),
                    text = if (mode == AuthMode.LOGIN) {
                        "Sign up"
                    } else {
                        "Sign in"
                    },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun Preview_LoginScreen() = KluvsTheme {
    AuthFormContent(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        mode = AuthMode.LOGIN,
        state = AuthUiState(),
        onEmailFieldChange = { _ -> },
        onPasswordFieldChange = { _ -> },
        onConfirmPasswordFieldChange = { _ -> },
        onOAuthSignIn = { _ -> },
        onSubmit = { },
        onNavigate = { _ -> },
    )
}

@PreviewLightDark
@Composable
fun Preview_SignupScreen() = KluvsTheme {
    AuthFormContent(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        mode = AuthMode.SIGNUP,
        state = AuthUiState(),
        onEmailFieldChange = { _ -> },
        onPasswordFieldChange = { _ -> },
        onConfirmPasswordFieldChange = { _ -> },
        onOAuthSignIn = { _ -> },
        onSubmit = { },
        onNavigate = { _ -> },
    )
}