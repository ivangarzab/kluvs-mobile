package com.ivangarzab.kluvs.ui.books

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.loading.rememberShimmerColor
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Placeholder shown while the shelf loads — mirrors [ShelfSection]'s shape (eyebrow label,
 * horizontal row of [BookCard]-shaped covers) so nothing jumps around once real sections
 * swap in.
 */
@Composable
fun BooksShelfSkeleton(modifier: Modifier = Modifier) {
    val shimmerColor = rememberShimmerColor()

    fun Modifier.bone(shape: Shape = RoundedCornerShape(4.dp)) =
        this.clip(shape).background(shimmerColor)

    Column(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(2) {
            Column {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .width(80.dp)
                        .height(12.dp)
                        .bone()
                ) {}
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) {
                        Column(
                            modifier = Modifier.width(120.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(2f / 3f)
                                    .bone(RoundedCornerShape(4.dp))
                            ) {}
                            Column(modifier = Modifier.fillMaxWidth(0.8f).height(14.dp).bone()) {}
                            Column(modifier = Modifier.fillMaxWidth(0.5f).height(12.dp).bone()) {}
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_BooksShelfSkeleton() = KluvsTheme {
    BooksShelfSkeleton(modifier = Modifier.background(color = KluvsTheme.colors.background))
}
