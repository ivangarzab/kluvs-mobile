package com.ivangarzab.kluvs.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.error.toAppError
import com.ivangarzab.kluvs.presentation.error.toUserMessage
import com.ivangarzab.kluvs.settings.domain.GetEditableProfileUseCase
import com.ivangarzab.kluvs.settings.domain.UpdateAvatarUseCase
import com.ivangarzab.kluvs.settings.domain.UpdateUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings screen.
 *
 * Loads the current user's profile for editing and handles save operations.
 */
class SettingsViewModel(
    private val getEditableProfile: GetEditableProfileUseCase,
    private val updateUserProfile: UpdateUserProfileUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getEditableProfile(userId)
                .onSuccess { profile ->
                    Bark.i("Settings profile loaded (Member ID: ${profile.memberId})")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            profile = profile,
                            editedName = profile.name,
                            editedHandle = profile.handle,
                            hasChanges = false
                        )
                    }
                }
                .onFailure { error ->
                    Bark.e("Failed to load profile for Settings screen.", error)
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun onNameChanged(name: String) {
        _state.update { state ->
            state.copy(
                editedName = name,
                hasChanges = computeHasChanges(state.copy(editedName = name))
            )
        }
    }

    /**
     * Case is a soft transform — uppercase input is silently lowercased as the user types, since
     * the backend's handle format is lowercase-only. Any other out-of-charset character (e.g.
     * underscore, accented letters) is kept as typed and surfaced as [SettingsState.handleError]
     * instead, so the user sees exactly what they typed and why it's rejected. Full structural
     * validation (segment/hyphen rules, length) happens in [UpdateUserProfileUseCase] on save —
     * only the server can authoritatively confirm the handle is free.
     */
    fun onHandleChanged(handle: String) {
        val lowered = handle.lowercase()
        val handleError = if (HANDLE_CHARSET_REGEX.matches(lowered)) {
            null
        } else {
            "Handles can only contain lowercase letters, numbers, and hyphens"
        }
        _state.update { state ->
            val updated = state.copy(editedHandle = lowered, handleError = handleError)
            updated.copy(hasChanges = computeHasChanges(updated))
        }
    }

    fun onSaveProfile() {
        val state = _state.value
        if (state.isSaving || state.handleError != null) return
        val profile = state.profile ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, saveError = null) }

            updateUserProfile(profile.memberId, state.editedName, state.editedHandle)
                .onSuccess {
                    Bark.i("Profile saved successfully (Member ID: ${profile.memberId})")
                    _state.update {
                        it.copy(
                            isSaving = false,
                            saveSuccess = true,
                            profile = it.profile?.copy(name = it.editedName, handle = it.editedHandle),
                            hasChanges = false
                        )
                    }
                }
                .onFailure { error ->
                    // Covers the 409 "handle already taken" case — AppError.Conflict carries the
                    // backend's own clean, human-readable detail via toUserMessage().
                    Bark.e("Failed to save profile (Member ID: ${profile.memberId}).", error)
                    _state.update {
                        it.copy(isSaving = false, saveError = error.toAppError().toUserMessage())
                    }
                }
        }
    }

    fun onDismissSaveSuccess() {
        _state.update { it.copy(saveSuccess = false) }
    }

    fun uploadAvatar(imageData: ByteArray) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingAvatar = true, avatarError = null) }

            val memberId = _state.value.profile?.memberId
            if (memberId == null) {
                Bark.e("No member ID available to update avatar. Please retry.", null)
                _state.update {
                    it.copy(
                        isUploadingAvatar = false,
                        avatarError = "No member ID available"
                    )
                }
                return@launch
            }

            updateAvatarUseCase(memberId, imageData)
                .onSuccess { newAvatarUrl ->
                    Bark.i("Avatar uploaded successfully (ID: $memberId)")
                    _state.update {
                        it.copy(profile = it.profile?.copy(avatarUrl = newAvatarUrl))
                    }
                }
                .onFailure { error ->
                    Bark.e("Failed to upload avatar (ID: $memberId). Please retry.", error)
                    _state.update { it.copy(avatarError = error.message) }
                }

            _state.update { it.copy(isUploadingAvatar = false) }
        }
    }

    /** Surfaces a failure that happened while reading/compressing a picked image, before upload was even attempted. */
    fun onAvatarPickFailed(reason: String?) {
        Bark.e("Failed to read picked avatar image. ${reason.orEmpty()}", null)
        _state.update { it.copy(avatarError = "Failed to read selected image") }
    }

    fun clearAvatarError() {
        _state.update { it.copy(avatarError = null) }
    }

    private fun computeHasChanges(state: SettingsState): Boolean {
        val profile = state.profile ?: return false
        return state.editedName != profile.name || state.editedHandle != profile.handle
    }

    companion object {
        // Charset-only check for live typing feedback — lowercase letters, digits, and hyphens.
        // Full structural validation (segment/hyphen placement, length) is enforced at save time
        // by UpdateUserProfileUseCase's HANDLE_REGEX, which matches the backend exactly.
        private val HANDLE_CHARSET_REGEX = Regex("^[a-z0-9-]*$")
    }
}
