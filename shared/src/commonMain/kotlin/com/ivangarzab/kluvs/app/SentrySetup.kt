package com.ivangarzab.kluvs.app

import io.sentry.kotlin.multiplatform.Sentry

fun initializeSentry() {
    Sentry.init { options ->
        options.dsn = "https://a79555c36dc1de4abd85b8b4d31af625@o4510775544709120.ingest.us.sentry.io/4510775568105472"
        options.debug = true // Useful for testing your setup
        options.environment = "debug"
        options.tracesSampleRate = 1.0
        options.attachStackTrace = true
        options.sendDefaultPii = true // Optional: adds user IP/headers
    }
}