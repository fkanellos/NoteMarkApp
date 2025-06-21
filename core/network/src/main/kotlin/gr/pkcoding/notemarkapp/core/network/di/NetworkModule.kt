package gr.pkcoding.notemarkapp.core.network.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gr.pkcoding.notemarkapp.core.network.BuildConfig
import gr.pkcoding.notemarkapp.core.network.service.AuthService
import gr.pkcoding.notemarkapp.core.network.service.AuthServiceImpl
import gr.pkcoding.notemarkapp.core.network.storage.SecureTokenStorage
import gr.pkcoding.notemarkapp.core.network.storage.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        json: Json,
        tokenStorage: TokenStorage,
        @ApplicationContext context: Context
    ): HttpClient = HttpClient(Android) {

        // Base URL and headers
        defaultRequest {
            url(BuildConfig.BASE_URL + "/api/")
            header("X-User-Email", BuildConfig.USER_EMAIL)
            contentType(ContentType.Application.Json)
        }

        // JSON serialization
        install(ContentNegotiation) {
            json(json)
        }

        // Logging (only in debug builds)
        install(Logging) {
            level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
        }

        // JWT Authentication with automatic token refresh
        install(Auth) {
            bearer {
                loadTokens {
                    // Load tokens from secure storage
                    val tokens = tokenStorage.getTokens()
                    tokens?.let {
                        BearerTokens(it.accessToken, it.refreshToken)
                    }
                }

                refreshTokens {
                    // Automatic token refresh when 401 occurs
                    val refreshToken = tokenStorage.getRefreshToken()
                    if (refreshToken != null) {
                        try {
                            // Note: We'll need to inject AuthService differently to avoid circular dependency
                            // For now, we'll implement this in the Repository layer
                            null
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    }
                }

                sendWithoutRequest { request ->
                    // Always send tokens except for auth endpoints
                    !request.url.encodedPath.contains("/auth/")
                }
            }
        }
    }
}