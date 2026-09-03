package com.ivangarzab.kluvs.app

import com.ivangarzab.kluvs.shared.BuildKonfig

/**
 * Single source of truth for club invite links — both building them for the share sheet and
 * parsing them back out of an incoming deep link.
 *
 * Keeping both directions here is deliberate: before this existed, Android and iOS each
 * hardcoded their own copy of the web origin, and both had drifted to `https://kluvs.com`
 * (the marketing domain, which has no `/join/:token` route) while the web app itself shared
 * `app.kluvs.com` links. A shared builder makes that class of drift impossible.
 */
object InviteLink {

    private const val SCHEME = "https://"
    private const val JOIN_PATH = "/join/"

    /**
     * Every origin whose invite links this app will accept, regardless of the flavor it was
     * built with. [build] emits only the current flavor's origin, but parsing stays permissive:
     * the Android `applicationId` and iOS bundle ID are identical across environments, so one
     * install can legitimately be handed a link from either origin — and quietly failing to
     * parse a valid link is worse than joining via the other environment's web app.
     */
    private val KNOWN_HOSTS = setOf("app.kluvs.com", "app.kluvs.xyz")

    /** Shareable invite URL for [token], pointing at the origin this build was flavored for. */
    fun build(token: String): String = "${BuildKonfig.WEB_APP_URL}$JOIN_PATH$token"

    /**
     * Extracts the invite token from [url], or null when it isn't a Kluvs invite link.
     *
     * Only `https` is accepted, matching the Android intent filter and the iOS associated
     * domain — an App Link that verified over anything else did not come from us.
     */
    fun parseToken(url: String): String? {
        val afterScheme = url.trim()
            .takeIf { it.startsWith(SCHEME, ignoreCase = true) }
            ?.drop(SCHEME.length)
            ?: return null

        val pathStart = afterScheme.indexOf('/').takeIf { it >= 0 } ?: return null

        // Drop any :port before comparing, and normalize case — hosts are case-insensitive.
        val host = afterScheme.substring(0, pathStart).substringBefore(':').lowercase()
        if (host !in KNOWN_HOSTS) return null

        val path = afterScheme.substring(pathStart).substringBefore('#').substringBefore('?')
        if (!path.startsWith(JOIN_PATH)) return null

        val token = path.drop(JOIN_PATH.length).trimEnd('/')
        return token.takeIf { it.isNotEmpty() && !it.contains('/') }
    }
}
