package com.ivangarzab.bookclub.domain.usecases.auth

import com.ivangarzab.bookclub.data.auth.AuthRepository

/**
 * Sign out the current user through the [AuthRepository].
 */
class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.signOut()
    }
}