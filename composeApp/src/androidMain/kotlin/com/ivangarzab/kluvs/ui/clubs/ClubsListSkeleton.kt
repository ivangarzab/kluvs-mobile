package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
 * Placeholder rows shown while the club list loads — mirrors [ClubListRow]'s shape (cover
 * thumb, name + subtitle lines, avatar dots) so nothing jumps around once real rows swap in.
 */
@Composable
fun ClubsListSkeleton(modifier: Modifier = Modifier) {
    val shimmerColor = rememberShimmerColor()

    fun Modifier.bone(shape: Shape = RoundedCornerShape(4.dp)) = this.clip(shape).background(shimmerColor)

    Column(modifier = modifier) {
        repeat(5) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .width(40.dp)
                        .aspectRatio(2f / 3f)
                        .bone(RoundedCornerShape(4.dp))
                ) {}
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.5f).height(16.dp).bone()) {}
                    Column(modifier = Modifier.fillMaxWidth(0.3f).height(12.dp).bone()) {}
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(3) {
                            Column(modifier = Modifier.size(20.dp).bone(CircleShape)) {}
                        }
                    }
                }
            }
            HorizontalDivider(color = KluvsTheme.colors.cardAlt)
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_ClubsListSkeleton() = KluvsTheme {
    ClubsListSkeleton(modifier = Modifier.background(color = KluvsTheme.colors.background))
}
