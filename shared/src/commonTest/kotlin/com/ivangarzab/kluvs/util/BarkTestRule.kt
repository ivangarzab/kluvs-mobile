package com.ivangarzab.kluvs.util

import com.ivangarzab.bark.Bark
import com.ivangarzab.bark.Trainer

/**
 * Test rule for configuring barK logging in unit tests.
 *
 * Usage:
 * ```
 * class MyTest {
 *     private val barkRule = BarkTestRule()
 *
 *     @BeforeTest
 *     fun setup() {
 *         barkRule.setup()
 *     }
 *
 *     @AfterTest
 *     fun teardown() {
 *         barkRule.teardown()
 *     }
 * }
 * ```
 */
class BarkTestRule {

    private lateinit var trainer: Trainer

    /**
     * Sets up barK with the appropriate trainer for the test environment.
     * Call this in your @BeforeTest method.
     */
    fun setup() {
        trainer = createTestTrainer()
        Bark.train(trainer)
    }

    /**
     * Cleans up barK after tests.
     * Call this in your @AfterTest method if needed.
     */
    fun teardown() {
        if (::trainer.isInitialized) {
            Bark.untrain(trainer)
        }
    }
}

/**
 * Platform-specific function to create the appropriate trainer for tests.
 * Implement this in androidUnitTest and iosTest source sets.
 */
expect fun createTestTrainer(): Trainer
