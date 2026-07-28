package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
 * Placeholder shown while a club's details load — mirrors [ClubsScreenContent]'s shape
 * (meta row, tab row, a generic content block) so the tab row doesn't sit there statically
 * before the rest of the screen is ready. Doesn't attempt to mirror each tab's exact,
 * state-dependent layout (session vs. no session, role-gated actions) — just enough shape
 * that nothing jumps around once the real tab row and content swap in.
 */
@Composable
fun ClubDetailsSkeleton(modifier: Modifier = Modifier) {
    val shimmerColor = rememberShimmerColor()

    fun Modifier.bone(shape: Shape = RoundedCornerShape(4.dp)) = this.clip(shape).background(shimmerColor)

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.width(50.dp).height(12.dp).bone()) {}
            Column(modifier = Modifier.width(70.dp).height(12.dp).bone()) {}
            Column(modifier = Modifier.width(60.dp).height(12.dp).bone()) {}
        }

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            repeat(3) {
                Column(modifier = Modifier.width(70.dp).height(14.dp).bone()) {}
            }
        }

        HorizontalDivider(color = KluvsTheme.colors.cardAlt)

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.fillMaxWidth(0.5f).height(20.dp).bone()) {}
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .bone(RoundedCornerShape(8.dp))
            ) {}
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) {
                    Column(modifier = Modifier.size(28.dp).bone(CircleShape)) {}
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_ClubDetailsSkeleton() = KluvsTheme {
    ClubDetailsSkeleton(
        modifier = Modifier
            .background(color = KluvsTheme.colors.background)
            .padding(vertical = 16.dp)
    )
}
