package com.ivangarzab.kluvs.util

import com.ivangarzab.kluvs.shared.BuildKonfig
import io.sentry.kotlin.multiplatform.Sentry


fun initializeSentry() {
    val isDebug = BuildKonfig.DEBUG

    Sentry.init { options ->
        options.dsn = BuildKonfig.SENTRY_DNS

        options.environment = if (isDebug) "debug" else "production"
        options.debug = isDebug // Only see Sentry's internal logs during dev

        // Performance Monitoring
        options.tracesSampleRate = if (isDebug) 1.0 else 0.1

        options.attachStackTrace = true
        options.attachThreads = true

        // Privacy & PII
        options.sendDefaultPii = isDebug

        // Breadcrumb Management (To keep the history clean)
        options.maxBreadcrumbs = if (isDebug) 100 else 50
        options.beforeBreadcrumb = { breadcrumb ->
            // Filter out the "BATTERY_CHANGED" noise you saw earlier
            if (breadcrumb.category == "device.event") null else breadcrumb
        }
    }
}