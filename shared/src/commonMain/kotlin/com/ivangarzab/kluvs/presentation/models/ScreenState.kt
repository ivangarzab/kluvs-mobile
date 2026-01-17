package com.ivangarzab.kluvs.presentation.models

/**
 * Shared interface defining general screen states.
 */
sealed interface ScreenState {
    data object Loading : ScreenState
    data class Error(val message: String) : ScreenState
    data object Empty : ScreenState
    data object Content : ScreenState
}