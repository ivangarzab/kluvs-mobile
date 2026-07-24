package com.ivangarzab.kluvs.ui.books

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.designsystem.components.dropdowns.Dropdown
import com.ivangarzab.kluvs.designsystem.components.icons.IconType
import com.ivangarzab.kluvs.designsystem.components.pills.TogglePill
import com.ivangarzab.kluvs.model.ShelfStatus

/**
 * Shelf status selector + like toggle for the book detail screen, styled to match web's
 * `LikePill`/`ShelfPill`: fully rounded outline pills, copper border when active, neutral
 * otherwise. Hidden entirely for unregistered books (fresh from search, or a "more by this
 * author" entry), which can't be shelved/liked until registered — mirrors [BookCard]'s guard.
 */
@Composable
fun BookDetailActions(
    modifier: Modifier = Modifier,
    isRegistered: Boolean,
    shelfStatus: ShelfStatus?,
    isLiked: Boolean,
    isMutationInProgress: Boolean,
    onShelfChange: (ShelfStatus?) -> Unit,
    onToggleLike: () -> Unit
) {
    if (!isRegistered) return

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TogglePill(
            checked = isLiked,
            onToggle = onToggleLike,
            iconChecked = IconType.Favorite,
            iconUnchecked = IconType.FavoriteOutline,
            contentDescription = stringResource(if (isLiked) R.string.unlike_book else R.string.like_book),
            enabled = !isMutationInProgress
        )

        val shelfLabels = mapOf(
            ShelfStatus.CURRENTLY_READING to stringResource(R.string.shelf_currently_reading),
            ShelfStatus.READ to stringResource(R.string.shelf_read),
            ShelfStatus.WANT_TO_READ to stringResource(R.string.shelf_want_to_read),
            ShelfStatus.NOT_FINISHED to stringResource(R.string.shelf_not_finished),
        )

        Dropdown(
            options = ShelfStatus.entries,
            selected = shelfStatus,
            onSelect = onShelfChange,
            label = { status -> shelfLabels.getValue(status) },
            placeholder = stringResource(R.string.shelf_add_to_shelf),
            clearLabel = stringResource(R.string.shelf_none),
            enabled = !isMutationInProgress
        )
    }
}

@Composable
private fun shelfActionLabel(status: ShelfStatus): String = when (status) {
    ShelfStatus.CURRENTLY_READING -> stringResource(R.string.shelf_currently_reading)
    ShelfStatus.READ -> stringResource(R.string.shelf_read)
    ShelfStatus.WANT_TO_READ -> stringResource(R.string.shelf_want_to_read)
    ShelfStatus.NOT_FINISHED -> stringResource(R.string.shelf_not_finished)
}
