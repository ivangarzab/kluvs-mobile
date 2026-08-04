package com.ivangarzab.kluvs.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivangarzab.bark.Bark
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

    fun onHandleChanged(handle: String) {
        _state.update { state ->
            state.copy(
                editedHandle = handle,
                hasChanges = computeHasChanges(state.copy(editedHandle = handle))
            )
        }
    }

    fun onSaveProfile() {
        val state = _state.value
        if (state.isSaving) return
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
                    _state.update { it.copy(isSaving = false, saveError = error.message) }
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

    fun clearAvatarError() {
        _state.update { it.copy(avatarError = null) }
    }

    private fun computeHasChanges(state: SettingsState): Boolean {
        val profile = state.profile ?: return false
        return state.editedName != profile.name || state.editedHandle != profile.handle
    }
}
