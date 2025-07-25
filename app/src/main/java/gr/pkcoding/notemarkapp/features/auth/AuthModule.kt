package gr.pkcoding.notemarkapp.features.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import gr.pkcoding.notemarkapp.features.auth.data.repository.AuthRepositoryImpl
import gr.pkcoding.notemarkapp.features.auth.domain.repository.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    /**
     * Binds the AuthRepositoryImpl to the AuthRepository interface
     *
     * This allows us to inject AuthRepository anywhere and get the implementation
     * Following Dependency Inversion Principle
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}