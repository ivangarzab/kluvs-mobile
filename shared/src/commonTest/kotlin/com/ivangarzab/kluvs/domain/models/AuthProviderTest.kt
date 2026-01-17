package com.ivangarzab.kluvs.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals

class AuthProviderTest {

    @Test
    fun `fromString returns EMAIL for email string`() {
        assertEquals(AuthProvider.EMAIL, AuthProvider.fromString("email"))
    }

    @Test
    fun `fromString returns DISCORD for discord string`() {
        assertEquals(AuthProvider.DISCORD, AuthProvider.fromString("discord"))
    }

    @Test
    fun `fromString returns GOOGLE for google string`() {
        assertEquals(AuthProvider.GOOGLE, AuthProvider.fromString("google"))
    }

    @Test
    fun `fromString returns APPLE for apple string`() {
        assertEquals(AuthProvider.APPLE, AuthProvider.fromString("apple"))
    }

    @Test
    fun `fromString is case insensitive`() {
        assertEquals(AuthProvider.EMAIL, AuthProvider.fromString("EMAIL"))
        assertEquals(AuthProvider.DISCORD, AuthProvider.fromString("DISCORD"))
        assertEquals(AuthProvider.GOOGLE, AuthProvider.fromString("Google"))
        assertEquals(AuthProvider.APPLE, AuthProvider.fromString("ApPlE"))
    }

    @Test
    fun `fromString handles mixed case`() {
        assertEquals(AuthProvider.EMAIL, AuthProvider.fromString("EmAiL"))
        assertEquals(AuthProvider.DISCORD, AuthProvider.fromString("DiScOrD"))
    }

    @Test
    fun `fromString defaults to EMAIL for unknown provider`() {
        assertEquals(AuthProvider.EMAIL, AuthProvider.fromString("unknown"))
        assertEquals(AuthProvider.EMAIL, AuthProvider.fromString("facebook"))
        assertEquals(AuthProvider.EMAIL, AuthProvider.fromString("twitter"))
        assertEquals(AuthProvider.EMAIL, AuthProvider.fromString(""))
    }

    @Test
    fun `fromString defaults to EMAIL for invalid input`() {
        assertEquals(AuthProvider.EMAIL, AuthProvider.fromString("123"))
        assertEquals(AuthProvider.EMAIL, AuthProvider.fromString("!@#"))
        assertEquals(AuthProvider.EMAIL, AuthProvider.fromString("   "))
    }
}
