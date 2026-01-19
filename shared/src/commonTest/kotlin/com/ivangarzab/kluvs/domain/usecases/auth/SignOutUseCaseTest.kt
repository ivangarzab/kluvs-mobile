package com.ivangarzab.kluvs.domain.usecases.auth

import com.ivangarzab.kluvs.auth.domain.SignOutUseCase
import com.ivangarzab.kluvs.data.auth.AuthError
import com.ivangarzab.kluvs.data.auth.AuthRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignOutUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: SignOutUseCase

    @BeforeTest
    fun setup() {
        authRepository = mock<AuthRepository>()
        useCase = SignOutUseCase(authRepository)
    }

    @Test
    fun `returns success when repository succeeds`() = runTest {
        // Given
        everySuspend { authRepository.signOut() } returns Result.success(Unit)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        verifySuspend { authRepository.signOut() }
    }

    @Test
    fun `returns failure when repository fails`() = runTest {
        // Given
        val error = AuthError.UnexpectedError
        everySuspend { authRepository.signOut() } returns Result.failure(error)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        verifySuspend { authRepository.signOut() }
    }

    @Test
    fun `returns failure with auth error when repository fails with auth error`() = runTest {
        // Given
        val error = AuthError.NoConnection
        everySuspend { authRepository.signOut() } returns Result.failure(error)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(AuthError.NoConnection, result.exceptionOrNull())
        verifySuspend { authRepository.signOut() }
    }
}
