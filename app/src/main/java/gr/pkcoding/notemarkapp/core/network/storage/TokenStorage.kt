package gr.pkcoding.notemarkapp.core.network.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)

interface TokenStorage {
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun getTokens(): AuthTokens?
    suspend fun clearTokens()
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
}

@Singleton
class SecureTokenStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : TokenStorage {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_tokens",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_TOKENS = "auth_tokens"
    }

    override suspend fun saveTokens(tokens: AuthTokens) {
        val tokensJson = json.encodeToString(AuthTokens.serializer(), tokens)
        sharedPreferences.edit()
            .putString(KEY_TOKENS, tokensJson)
            .apply()
    }

    override suspend fun getTokens(): AuthTokens? {
        val tokensJson = sharedPreferences.getString(KEY_TOKENS, null)
        return tokensJson?.let {
            try {
                json.decodeFromString(AuthTokens.serializer(), it)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun clearTokens() {
        sharedPreferences.edit()
            .remove(KEY_TOKENS)
            .apply()
    }

    override suspend fun getAccessToken(): String? {
        return getTokens()?.accessToken
    }

    override suspend fun getRefreshToken(): String? {
        return getTokens()?.refreshToken
    }
}