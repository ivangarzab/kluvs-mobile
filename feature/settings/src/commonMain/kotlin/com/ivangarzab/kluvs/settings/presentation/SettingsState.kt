package com.ivangarzab.kluvs.settings.presentation

data class SettingsState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val profile: EditableProfile? = null,
    // Editable field values (user's in-progress edits)
    val editedName: String = "",
    val editedHandle: String = "",
    // Inline format validation for editedHandle (e.g. disallowed characters); null when valid
    val handleError: String? = null,
    // Save operation state
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val saveSuccess: Boolean = false,
    // Derived: are there unsaved changes?
    val hasChanges: Boolean = false,
    // Avatar upload state
    val isUploadingAvatar: Boolean = false,
    val avatarError: String? = null
)
