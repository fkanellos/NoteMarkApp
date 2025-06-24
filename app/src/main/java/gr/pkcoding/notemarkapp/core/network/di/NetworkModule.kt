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
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // API Configuration
    private const val BASE_URL = "https://notemark.pl-coding.com"
    private const val USER_EMAIL = "your-email@example.com" // TODO: Replace with actual email

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
            url("$BASE_URL/api/")
            header("X-User-Email", USER_EMAIL)
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

        // TODO: Add JWT Authentication με TokenStorage όταν ολοκληρωθεί το auth flow
    }
}