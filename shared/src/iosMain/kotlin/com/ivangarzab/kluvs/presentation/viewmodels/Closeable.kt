package com.ivangarzab.kluvs.presentation.viewmodels

/**
 * Closeable interface for iOS to properly manage subscriptions.
 */
interface Closeable {
    fun close()
}

fun Closeable(block: () -> Unit): Closeable = object : Closeable {
    override fun close() = block()
}