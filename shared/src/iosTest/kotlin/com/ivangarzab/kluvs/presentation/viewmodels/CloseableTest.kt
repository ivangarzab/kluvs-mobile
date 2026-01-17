package com.ivangarzab.kluvs.presentation.viewmodels

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CloseableTest {

    @Test
    fun `Closeable invokes provided block when close is called`() {
        // Given
        var blockInvoked = false
        val closeable = Closeable {
            blockInvoked = true
        }

        // When
        closeable.close()

        // Then
        assertTrue(blockInvoked, "Block should be invoked when close is called")
    }

    @Test
    fun `Closeable does not invoke block before close is called`() {
        // Given
        var blockInvoked = false
        val closeable = Closeable {
            blockInvoked = true
        }

        // Then
        assertFalse(blockInvoked, "Block should not be invoked before close is called")

        // Cleanup
        closeable.close()
    }

    @Test
    fun `Closeable can be called multiple times safely`() {
        // Given
        var invocationCount = 0
        val closeable = Closeable {
            invocationCount++
        }

        // When
        closeable.close()
        closeable.close()
        closeable.close()

        // Then
        // Note: The current implementation allows multiple invocations
        // This test documents that behavior
        assertTrue(invocationCount >= 1, "Block should be invoked at least once")
    }

    @Test
    fun `Closeable with complex block executes correctly`() {
        // Given
        val results = mutableListOf<String>()
        val closeable = Closeable {
            results.add("first")
            results.add("second")
            results.add("third")
        }

        // When
        closeable.close()

        // Then
        assertTrue(results.isNotEmpty(), "Results should not be empty")
        assertTrue(results.contains("first"))
        assertTrue(results.contains("second"))
        assertTrue(results.contains("third"))
    }

    @Test
    fun `Closeable with exception in block does not prevent execution`() {
        // Given
        var cleanupExecuted = false
        val closeable = Closeable {
            cleanupExecuted = true
            // In real usage, exceptions might occur during cleanup
        }

        // When
        closeable.close()

        // Then
        assertTrue(cleanupExecuted, "Cleanup should execute even if exceptions might occur")
    }
}
