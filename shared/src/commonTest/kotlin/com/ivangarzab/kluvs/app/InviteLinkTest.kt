package com.ivangarzab.kluvs.app

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InviteLinkTest {

    @Test
    fun `build produces a join URL for the flavored origin`() {
        val url = InviteLink.build("abc-123")

        assertTrue(url.endsWith("/join/abc-123"), "unexpected URL: $url")
        assertTrue(url.startsWith("https://app.kluvs."), "unexpected origin: $url")
    }

    @Test
    fun `build output round-trips back through parseToken`() {
        assertEquals("abc-123", InviteLink.parseToken(InviteLink.build("abc-123")))
    }

    @Test
    fun `parses a production invite link`() {
        assertEquals(
            "9f8c1e2a-0000-4444-8888-aabbccddeeff",
            InviteLink.parseToken("https://app.kluvs.com/join/9f8c1e2a-0000-4444-8888-aabbccddeeff")
        )
    }

    @Test
    fun `parses an integration invite link regardless of build flavor`() {
        assertEquals("tok", InviteLink.parseToken("https://app.kluvs.xyz/join/tok"))
    }

    @Test
    fun `parses despite host casing`() {
        assertEquals("tok", InviteLink.parseToken("https://APP.KLUVS.COM/join/tok"))
    }

    @Test
    fun `ignores query string and fragment`() {
        assertEquals("tok", InviteLink.parseToken("https://app.kluvs.com/join/tok?utm=discord"))
        assertEquals("tok", InviteLink.parseToken("https://app.kluvs.com/join/tok#section"))
    }

    @Test
    fun `tolerates a trailing slash`() {
        assertEquals("tok", InviteLink.parseToken("https://app.kluvs.com/join/tok/"))
    }

    @Test
    fun `rejects the marketing domain that has no join route`() {
        assertNull(InviteLink.parseToken("https://kluvs.com/join/tok"))
    }

    @Test
    fun `rejects an unrelated host`() {
        assertNull(InviteLink.parseToken("https://evil.example.com/join/tok"))
    }

    @Test
    fun `rejects a host that merely ends with a known host`() {
        assertNull(InviteLink.parseToken("https://notapp.kluvs.com/join/tok"))
    }

    @Test
    fun `rejects non-https schemes`() {
        assertNull(InviteLink.parseToken("http://app.kluvs.com/join/tok"))
        assertNull(InviteLink.parseToken("kluvs://auth/callback"))
    }

    @Test
    fun `rejects a different path on a known host`() {
        assertNull(InviteLink.parseToken("https://app.kluvs.com/clubs/tok"))
        assertNull(InviteLink.parseToken("https://app.kluvs.com/joinery/tok"))
    }

    @Test
    fun `rejects a missing token`() {
        assertNull(InviteLink.parseToken("https://app.kluvs.com/join/"))
        assertNull(InviteLink.parseToken("https://app.kluvs.com/join"))
    }

    @Test
    fun `rejects extra path segments after the token`() {
        assertNull(InviteLink.parseToken("https://app.kluvs.com/join/tok/extra"))
    }

    @Test
    fun `rejects garbage`() {
        assertNull(InviteLink.parseToken(""))
        assertNull(InviteLink.parseToken("not a url"))
        assertNull(InviteLink.parseToken("https://app.kluvs.com"))
    }
}
