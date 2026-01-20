package com.ivangarzab.kluvs.auth.domain

/**
 * Sign out the current user through the [com.ivangarzab.kluvs.auth.AuthRepository].
 */
class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.signOut()
    }
}