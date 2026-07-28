package com.ivangarzab.kluvs.ui.books

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
 * Full-body placeholder shown while enrichment loads — mirrors [BookDetailScreenContent]'s
 * shape (cover + title block, chip row, action row, then About/Details/Author/More-by
 * sections) so nothing jumps around once the real content swaps in. Deliberately shown
 * for the *entire* body rather than per-section, so the screen waits for enrichment to
 * fully resolve before revealing anything, rather than piecemeal-revealing sections as
 * their data arrives.
 */
@Composable
fun BookDetailSkeleton(modifier: Modifier = Modifier) {
    val shimmerColor = rememberShimmerColor()

    fun Modifier.bone(shape: Shape = RoundedCornerShape(4.dp)) = this.clip(shape).background(shimmerColor)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(2f / 3f)
                    .bone(RoundedCornerShape(4.dp))
            ) {}
            Column(
                modifier = Modifier.weight(1f).padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.8f).height(20.dp).bone()) {}
                Column(modifier = Modifier.fillMaxWidth(0.5f).height(14.dp).bone()) {}
                Column(modifier = Modifier.fillMaxWidth(0.6f).height(12.dp).bone()) {}
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.width(64.dp).height(28.dp).bone(CircleShape)) {}
            Column(modifier = Modifier.width(48.dp).height(28.dp).bone(CircleShape)) {}
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(modifier = Modifier.width(40.dp).height(40.dp).bone(CircleShape)) {}
            Column(modifier = Modifier.width(120.dp).height(40.dp).bone(RoundedCornerShape(20.dp))) {}
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.width(60.dp).height(12.dp).bone()) {}
            Column(modifier = Modifier.fillMaxWidth().height(14.dp).bone()) {}
            Column(modifier = Modifier.fillMaxWidth().height(14.dp).bone()) {}
            Column(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp).bone()) {}
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.width(60.dp).height(12.dp).bone()) {}
            repeat(3) {
                Column(modifier = Modifier.fillMaxWidth().height(14.dp).bone()) {}
            }
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.width(60.dp).height(12.dp).bone()) {}
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.size(48.dp).bone(CircleShape)) {}
                Column(modifier = Modifier.fillMaxWidth(0.4f).height(16.dp).bone()) {}
            }
            Column(modifier = Modifier.fillMaxWidth().height(12.dp).bone()) {}
            Column(modifier = Modifier.fillMaxWidth(0.8f).height(12.dp).bone()) {}
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_BookDetailSkeleton() = KluvsTheme {
    BookDetailSkeleton(
        modifier = Modifier
            .background(color = KluvsTheme.colors.background)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
}
