package com.ivangarzab.bookclub.data.local.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

/**
 * Android implementation of [SecureStorage] using [EncryptedSharedPreferences].
 *
 * Provides hardware-backed encryption for storing sensitive auth tokens.
 */
class AndroidSecureStorage(context: Context) : SecureStorage {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

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