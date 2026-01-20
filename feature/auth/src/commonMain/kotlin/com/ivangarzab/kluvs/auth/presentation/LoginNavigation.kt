package com.ivangarzab.kluvs.auth.presentation

/**
 * The purpose of this class is to contain all of the different navigation options
 * from the Login screen.
 */
sealed class LoginNavigation {
    object SignIn: LoginNavigation()
    object SignUp: LoginNavigation()
    object ForgetPassword: LoginNavigation()
}