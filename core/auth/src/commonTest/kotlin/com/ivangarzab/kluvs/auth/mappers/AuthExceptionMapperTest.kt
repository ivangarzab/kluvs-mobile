package com.ivangarzab.kluvs.auth.mappers

import com.ivangarzab.kluvs.auth.domain.AuthError
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthExceptionMapperTest {

    @Test
    fun `maps invalid credentials exception`() {
        val exception = Exception("Invalid login credentials")
        assertEquals(AuthError.InvalidCredentials, exception.toAuthError())
    }

    @Test
    fun `maps invalid credentials exception case insensitive`() {
        val exception = Exception("INVALID LOGIN CREDENTIALS")
        assertEquals(AuthError.InvalidCredentials, exception.toAuthError())
    }

    @Test
    fun `maps email not confirmed exception`() {
        val exception = Exception("Email not confirmed")
        assertEquals(AuthError.EmailNotConfirmed, exception.toAuthError())
    }

    @Test
    fun `maps unable to resolve host exception to no connection`() {
        val exception = Exception("Unable to resolve host")
        assertEquals(AuthError.NoConnection, exception.toAuthError())
    }

    @Test
    fun `maps failed to connect exception to no connection`() {
        val exception = Exception("Failed to connect to server")
        assertEquals(AuthError.NoConnection, exception.toAuthError())
    }

    @Test
    fun `maps rate limit exception`() {
        val exception = Exception("Email rate limit exceeded")
        assertEquals(AuthError.RateLimitExceeded, exception.toAuthError())
    }

    @Test
    fun `maps user not found exception`() {
        val exception = Exception("User not found")
        assertEquals(AuthError.UserNotFound, exception.toAuthError())
    }

    @Test
    fun `maps weak password exception`() {
        val exception = Exception("Password should be at least 6 characters")
        assertEquals(AuthError.WeakPassword, exception.toAuthError())
    }

    @Test
    fun `maps user already exists exception`() {
        val exception = Exception("User already registered")
        assertEquals(AuthError.UserAlreadyExists, exception.toAuthError())
    }

    @Test
    fun `maps unknown exception to authentication failed`() {
        val exception = Exception("Something unexpected happened")
        assertEquals(AuthError.AuthenticationFailed, exception.toAuthError())
    }

    @Test
    fun `maps null message exception to unexpected error`() {
        val exception = Exception()
        assertEquals(AuthError.UnexpectedError, exception.toAuthError())
    }

    @Test
    fun `maps exception with complex message containing target phrase`() {
        val exception = Exception("The following error occurred: Invalid login credentials. Please try again.")
        assertEquals(AuthError.InvalidCredentials, exception.toAuthError())
    }
}