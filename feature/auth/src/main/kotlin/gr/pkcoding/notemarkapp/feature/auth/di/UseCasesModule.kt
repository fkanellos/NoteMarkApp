package gr.pkcoding.notemarkapp.feature.auth.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import gr.pkcoding.notemarkapp.feature.auth.domain.repository.AuthRepository
import gr.pkcoding.notemarkapp.feature.auth.domain.usecase.CheckAuthStateUseCase
import gr.pkcoding.notemarkapp.feature.auth.domain.usecase.GetCurrentUserUseCase
import gr.pkcoding.notemarkapp.feature.auth.domain.usecase.LoginUseCase
import gr.pkcoding.notemarkapp.feature.auth.domain.usecase.LogoutUseCase
import gr.pkcoding.notemarkapp.feature.auth.domain.usecase.RefreshTokensUseCase
import gr.pkcoding.notemarkapp.feature.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Hilt module for providing Use Case dependencies
 *
 * This module:
 * - Provides all auth-related use cases
 * - Injects them into ViewModels with proper scoping
 * - Configures coroutine dispatchers for background execution
 * - Ensures single instance per ViewModel lifecycle
 *
 * ViewModelComponent scoping ensures:
 * - Use cases are created when ViewModel is created
 * - Use cases are destroyed when ViewModel is destroyed
 * - Shared instances within the same ViewModel
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCasesModule {

    /**
     * Provides IO dispatcher for use cases
     *
     * IO dispatcher is optimal for:
     * - Network operations (API calls)
     * - File/database operations (token storage)
     * - Other blocking I/O operations
     */
    @Provides
    @ViewModelScoped
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides LoginUseCase for authentication
     *
     * @param repository AuthRepository for API communication
     * @param dispatcher CoroutineDispatcher for background execution
     */
    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(
        repository: AuthRepository,
        dispatcher: CoroutineDispatcher
    ): LoginUseCase = LoginUseCase(repository, dispatcher)

    /**
     * Provides RegisterUseCase for account creation
     *
     * @param repository AuthRepository for API communication
     * @param dispatcher CoroutineDispatcher for background execution
     */
    @Provides
    @ViewModelScoped
    fun provideRegisterUseCase(
        repository: AuthRepository,
        dispatcher: CoroutineDispatcher
    ): RegisterUseCase = RegisterUseCase(repository, dispatcher)

    /**
     * Provides LogoutUseCase for session termination
     *
     * @param repository AuthRepository for token cleanup
     * @param dispatcher CoroutineDispatcher for background execution
     */
    @Provides
    @ViewModelScoped
    fun provideLogoutUseCase(
        repository: AuthRepository,
        dispatcher: CoroutineDispatcher
    ): LogoutUseCase = LogoutUseCase(repository, dispatcher)

    /**
     * Provides GetCurrentUserUseCase for user data retrieval
     *
     * @param repository AuthRepository for user information
     * @param dispatcher CoroutineDispatcher for background execution
     */
    @Provides
    @ViewModelScoped
    fun provideGetCurrentUserUseCase(
        repository: AuthRepository,
        dispatcher: CoroutineDispatcher
    ): GetCurrentUserUseCase = GetCurrentUserUseCase(repository, dispatcher)

    /**
     * Provides CheckAuthStateUseCase for reactive auth monitoring
     *
     * @param repository AuthRepository for state monitoring
     * @param dispatcher CoroutineDispatcher for background execution
     */
    @Provides
    @ViewModelScoped
    fun provideCheckAuthStateUseCase(
        repository: AuthRepository,
        dispatcher: CoroutineDispatcher
    ): CheckAuthStateUseCase = CheckAuthStateUseCase(repository, dispatcher)

    /**
     * Provides RefreshTokensUseCase for automatic token management
     *
     * @param repository AuthRepository for token refresh
     * @param dispatcher CoroutineDispatcher for background execution
     */
    @Provides
    @ViewModelScoped
    fun provideRefreshTokensUseCase(
        repository: AuthRepository,
        dispatcher: CoroutineDispatcher
    ): RefreshTokensUseCase = RefreshTokensUseCase(repository, dispatcher)
}