package com.ivangarzab.kluvs.util

import com.ivangarzab.bark.Trainer
import com.ivangarzab.bark.trainers.ColoredUnitTestTrainer

/**
 * Android implementation of createTestTrainer.
 * Returns a ColoredUnitTestTrainer for colorful console output during Android unit tests.
 */
actual fun createTestTrainer(): Trainer {
    // For example, you might want to adjust the log level or other settings
    return ColoredUnitTestTrainer()
}
