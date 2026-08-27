package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.fields.PickerField
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.components.pickers.KluvsDatePicker
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.model.Book
import kotlinx.datetime.LocalDateTime

/**
 * Bottom sheet for creating a new reading session.
 *
 * Book selection is a real search-and-select field ([BookSearchField]) backed by
 * [com.ivangarzab.kluvs.clubs.presentation.ClubDetailsViewModel]'s book-search state —
 * selecting a result registers it server-side to obtain a real `bookId`, matching web's
 * `NewSessionModal`/`BookSearchInput`.
 *
 * Due date is day-only — no time-of-day picker. A picked date is modeled as a deadline of
 * end-of-that-day (23:59) when built into a [LocalDateTime] for the shared session model.
 *
 * Date picker is shelled via [KluvsDatePicker], to match every other sheet's header/footer chrome.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSessionBottomSheet(
    bookSearchQuery: String,
    bookSearchResults: List<Book>,
    selectedBook: Book?,
    isSearchingBooks: Boolean,
    isRegisteringBook: Boolean,
    bookSearchError: String?,
    onBookSearchQueryChange: (String) -> Unit,
    onSearchBooks: (String) -> Unit,
    onSelectBook: (Book) -> Unit,
    onClearBook: () -> Unit,
    onSave: (book: Book, dueDate: LocalDateTime?) -> Unit,
    onDismiss: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    val dateDisplayText = selectedDateMillis?.let { formatDateMillis(it) } ?: ""

    BottomSheet(
        header = "Create Session",
        onDismiss = onDismiss,
        footer = {
            BottomSheetFooter(
                actionLabel = "Create",
                onAction = {
                    val dueDate = selectedDateMillis?.let { millis -> buildLocalDateTime(millis, 23, 59) }
                    selectedBook?.let { onSave(it, dueDate) }
                },
                onCancel = onDismiss,
                actionEnabled = selectedBook != null,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BookSearchField(
                query = bookSearchQuery,
                onQueryChange = onBookSearchQueryChange,
                onSearch = onSearchBooks,
                results = bookSearchResults,
                selectedBook = selectedBook,
                isSearching = isSearchingBooks,
                isRegistering = isRegisteringBook,
                error = bookSearchError,
                onSelect = onSelectBook,
                onClear = onClearBook,
            )
            PickerField(label = "Due Date (optional)", value = dateDisplayText, onClick = { showDatePicker = true })
        }
    }

    if (showDatePicker) {
        KluvsDatePicker(
            initialSelectedDateMillis = selectedDateMillis,
            onConfirm = { millis ->
                selectedDateMillis = millis
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
}

@PreviewLightDark
@Composable
fun Preview_CreateSessionBottomSheet() = KluvsTheme {
    CreateSessionBottomSheet(
        bookSearchQuery = "",
        bookSearchResults = emptyList(),
        selectedBook = null,
        isSearchingBooks = false,
        isRegisteringBook = false,
        bookSearchError = null,
        onBookSearchQueryChange = {},
        onSearchBooks = {},
        onSelectBook = {},
        onClearBook = {},
        onSave = { _, _ -> },
        onDismiss = {}
    )
}
