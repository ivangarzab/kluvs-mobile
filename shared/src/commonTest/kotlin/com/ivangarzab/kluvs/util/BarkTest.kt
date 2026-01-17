package com.ivangarzab.kluvs.util

import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base test class that automatically sets up barK  with a [BarkTestRule] for all tests.
 */
abstract class BarkTest {

    private val barkRule = BarkTestRule()

    @BeforeTest
    fun setupBark() {
        barkRule.setup()
        setup()
    }

    @AfterTest
    fun teardownBark() {
        teardown()
        barkRule.teardown()
    }

    /**
     * Override this in subclasses if you need additional setup logic.
     */
    protected open fun setup() {
        // Subclasses can override
    }

    /**
     * Override this in subclasses if you need additional teardown logic.
     */
    protected open fun teardown() {
        // Subclasses can override
    }
}
