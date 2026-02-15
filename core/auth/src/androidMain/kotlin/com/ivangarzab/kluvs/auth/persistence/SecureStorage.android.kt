package com.ivangarzab.kluvs.auth.persistence

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ivangarzab.bark.Bark

/**
 * Android implementation of [com.ivangarzab.kluvs.auth.persistence.SecureStorage] using [EncryptedSharedPreferences].
 *
 * Provides hardware-backed encryption for storing sensitive auth tokens.
 */
class AndroidSecureStorage(context: Context) : SecureStorage {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = try {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        Bark.e("Handled exception attempting to get encrypted shared preferences", e)
        // Handle corrupted encrypted preferences (e.g., from keystore reset or key rotation)
        // This can happen on device factory reset or Android security updates
        context.deleteSharedPreferences(PREFS_NAME)
        try {
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (retryException: Exception) {
            Bark.e("Caught exception attempting to get encrypted shared preferences again", e)
            throw retryException
        }
    }

    override fun save(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }

    override fun get(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun remove(key: String) {
        sharedPreferences.edit { remove(key) }
    }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }

    companion object {
        private const val PREFS_NAME = "kluvs_secure_prefs"
    }
}