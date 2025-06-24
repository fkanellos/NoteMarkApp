package gr.pkcoding.notemarkapp.core.network.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gr.pkcoding.notemarkapp.BuildConfig
import gr.pkcoding.notemarkapp.core.network.service.AuthService
import gr.pkcoding.notemarkapp.core.network.service.AuthServiceImpl
import gr.pkcoding.notemarkapp.core.network.storage.SecureTokenStorage
import gr.pkcoding.notemarkapp.core.network.storage.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

// Bindings Module για interfaces
@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindsModule {

    @Binds
    abstract fun bindTokenStorage(
        secureTokenStorage: SecureTokenStorage
    ): TokenStorage

    @Binds
    abstract fun bindAuthService(
        authServiceImpl: AuthServiceImpl
    ): AuthService
}
