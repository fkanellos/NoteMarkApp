package gr.pkcoding.notemarkapp.core.network.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gr.pkcoding.notemarkapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import gr.pkcoding.notemarkapp.core.network.storage.TokenStorage
import gr.pkcoding.notemarkapp.core.network.model.RefreshTokenRequest
import gr.pkcoding.notemarkapp.core.network.model.AuthResponse
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // API Configuration
    private const val BASE_URL = "https://notemark.pl-coding.com"

    private const val USER_EMAIL = "filippos.kanellos@gmail.com"

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
            url("$BASE_URL/api/")
            header("X-User-Email", USER_EMAIL)
            contentType(ContentType.Application.Json)

            // Add debug header for testing (makes tokens expire in 30s)
            if (BuildConfig.DEBUG) {
                header("Debug", "true")
            }
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
                    val tokens = tokenStorage.getTokens()
                    if (tokens != null) {
                        BearerTokens(
                            accessToken = tokens.accessToken,
                            refreshToken = tokens.refreshToken
                        )
                    } else {
                        null
                    }
                }

                refreshTokens {
                    try {
                        // Get current refresh token
                        val currentTokens = tokenStorage.getTokens()
                        val refreshToken = currentTokens?.refreshToken

                        if (refreshToken == null) {
                            // No refresh token available - user needs to login again
                            tokenStorage.clearTokens()
                            return@refreshTokens null
                        }

                        // Make refresh request
                        val response = client.post("auth/refresh") {
                            contentType(ContentType.Application.Json)
                            setBody(RefreshTokenRequest(refreshToken))

                            // Don't include auth header for refresh request
                            markAsRefreshTokenRequest()

                            // Include debug header if in debug mode
                            if (BuildConfig.DEBUG) {
                                header("Debug", "true")
                            }
                        }

                        if (response.status.value == 200) {
                            // Successful refresh
                            val authResponse = response.body<AuthResponse>()

                            // Save new tokens
                            val newTokens = gr.pkcoding.notemarkapp.core.network.storage.AuthTokens(
                                accessToken = authResponse.accessToken,
                                refreshToken = authResponse.refreshToken
                            )
                            tokenStorage.saveTokens(newTokens)

                            // Return new bearer tokens
                            BearerTokens(
                                accessToken = authResponse.accessToken,
                                refreshToken = authResponse.refreshToken
                            )
                        } else {
                            // Refresh failed - clear tokens and force re-login
                            tokenStorage.clearTokens()
                            null
                        }
                    } catch (e: Exception) {
                        // Refresh failed - clear tokens and force re-login
                        tokenStorage.clearTokens()
                        null
                    }
                }
            }
        }
    }

    /**
     * Provides configuration for the User Email
     * This can be overridden in tests or different build variants
     */
    @Provides
    @Singleton
    fun provideUserEmail(): String = USER_EMAIL

    /**
     * Provides base URL for the API
     * This can be overridden for different environments
     */
    @Provides
    @Singleton
    fun provideBaseUrl(): String = BASE_URL
}