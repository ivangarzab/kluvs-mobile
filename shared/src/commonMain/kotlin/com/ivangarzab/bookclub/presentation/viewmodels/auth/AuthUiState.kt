package com.ivangarzab.bookclub.presentation.viewmodels.auth

data class AuthUiState(
    val emailField: String = "",
    val passwordField: String = "",
    val confirmPasswordField: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
)

enum class AuthMode { LOGIN, SIGNUP }
