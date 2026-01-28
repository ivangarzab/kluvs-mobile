package com.ivangarzab.kluvs.util

import com.ivangarzab.kluvs.shared.BuildKonfig
import io.sentry.kotlin.multiplatform.Sentry


fun initializeSentry() {
    Sentry.init { options ->
        options.dsn = BuildKonfig.SENTRY_DNS
        options.debug = false
        options.environment = "debug"
        options.tracesSampleRate = 1.0
        options.attachStackTrace = true
        options.attachThreads = true
        options.sendDefaultPii = true // Optional: adds user IP/headers
    }
}