package com.ivangarzab.kluvs.ui.me

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.loading.rememberShimmerColor
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Full-screen placeholder shown while the profile loads — mirrors [MeScreenContent]'s shape
 * (profile row, stats row, up-next card, shelf rows) so nothing jumps around once the real
 * content swaps in.
 */
@Composable
fun MeScreenSkeleton(modifier: Modifier = Modifier) {
    val shimmerColor = rememberShimmerColor()

    fun Modifier.bone(shape: Shape = RoundedCornerShape(4.dp)) =
        this.clip(shape).background(shimmerColor)

    Column(
        modifier = modifier
            .background(color = KluvsTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.size(60.dp).bone(CircleShape)) {}
            Spacer(Modifier.padding(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.width(120.dp).height(20.dp).bone()) {}
                Column(modifier = Modifier.width(80.dp).height(14.dp).bone()) {}
            }
        }

        HorizontalDivider(color = KluvsTheme.colors.divider)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column(modifier = Modifier.width(32.dp).height(20.dp).bone()) {}
                    Column(modifier = Modifier.width(48.dp).height(12.dp).bone()) {}
                }
            }
        }

        HorizontalDivider(color = KluvsTheme.colors.divider)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.width(80.dp).height(12.dp).bone()) {}
            Column(modifier = Modifier.fillMaxWidth(0.7f).height(24.dp).bone()) {}
        }

        HorizontalDivider(color = KluvsTheme.colors.divider)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.width(100.dp).height(12.dp).bone()) {}
            repeat(2) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(
                        modifier = Modifier
                            .width(64.dp)
                            .height(96.dp)
                            .bone(RoundedCornerShape(4.dp))
                    ) {}
                    Column(
                        modifier = Modifier.weight(1f).padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp).bone()) {}
                        Column(modifier = Modifier.fillMaxWidth(0.4f).height(12.dp).bone()) {}
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_MeScreenSkeleton() = KluvsTheme {
    MeScreenSkeleton()
}
