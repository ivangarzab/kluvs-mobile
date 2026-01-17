package com.ivangarzab.kluvs.util

import com.ivangarzab.bark.Trainer
import com.ivangarzab.bark.trainers.NSLogTrainer

/**
 * iOS implementation of createTestTrainer.
 */
actual fun createTestTrainer(): Trainer {
    return NSLogTrainer()
}
