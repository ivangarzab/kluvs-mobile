package com.ivangarzab.kluvs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivangarzab.kluvs.designsystem.components.loading.LoadingSpinner
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = KluvsTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        LoadingSpinner()
    }
}