package gr.pkcoding.notemarkapp.core.network.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gr.pkcoding.notemarkapp.core.network.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.http.encodedPath
import io.ktor.client.plugins.auth.providers.bearer
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
                    // TODO: Load tokens from EncryptedSharedPreferences
                    // This will be implemented when we create the token storage
                    null
                }

                refreshTokens {
                    // TODO: Implement automatic token refresh
                    // When access token expires (401), this will:
                    // 1. Use refresh token to get new access token
                    // 2. Save new tokens to secure storage
                    // 3. Return new BearerTokens for retry
                    null
                }

                sendWithoutRequest { request ->
                    // Always send tokens except for auth endpoints
                    !request.url.encodedPath.contains("/auth/")
                }
            }
        }
    }
}