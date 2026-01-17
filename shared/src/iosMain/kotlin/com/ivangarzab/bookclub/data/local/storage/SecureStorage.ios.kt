package com.ivangarzab.bookclub.data.local.storage

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

/**
 * iOS implementation of [SecureStorage] using Keychain Services.
 *
 * Provides hardware-backed encryption for storing sensitive auth tokens.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosSecureStorage : SecureStorage {

    override fun save(key: String, value: String) {
        // First, delete any existing value to avoid duplicate item error
        remove(key)

        // Convert value to NSData
        val valueData = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding)
            ?: throw IllegalArgumentException("Failed to encode value as UTF-8")

        // Bridge objects to CF
        val keyRef = CFBridgingRetain(key)
        val serviceRef = CFBridgingRetain(SERVICE_NAME)
        val dataRef = CFBridgingRetain(valueData)

        // Create CFMutableDictionary
        val query = CFDictionaryCreateMutable(null, 4, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrAccount, keyRef)
        CFDictionarySetValue(query, kSecAttrService, serviceRef)
        CFDictionarySetValue(query, kSecValueData, dataRef)

        // Add to keychain
        val status = SecItemAdd(query, null)

        // Release all CF objects (must release in reverse order)
        CFBridgingRelease(query)
        CFBridgingRelease(dataRef)
        CFBridgingRelease(serviceRef)
        CFBridgingRelease(keyRef)

        if (status != errSecSuccess) {
            throw RuntimeException("Failed to save to keychain: $status")
        }
    }

    override fun get(key: String): String? {
        // Bridge objects to CF
        val keyRef = CFBridgingRetain(key)
        val serviceRef = CFBridgingRetain(SERVICE_NAME)

        // Create CFMutableDictionary
        val query = CFDictionaryCreateMutable(null, 5, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrAccount, keyRef)
        CFDictionarySetValue(query, kSecAttrService, serviceRef)
        CFDictionarySetValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionarySetValue(query, kSecMatchLimit, kSecMatchLimitOne)

        val result = memScoped {
            val resultPtr = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query, resultPtr.ptr)

            if (status == errSecSuccess) {
                val data = CFBridgingRelease(resultPtr.value) as? NSData
                data?.let {
                    NSString.create(data = it, encoding = NSUTF8StringEncoding) as? String
                }
            } else {
                null
            }
        }

        // Release all CF objects
        CFBridgingRelease(query)
        CFBridgingRelease(serviceRef)
        CFBridgingRelease(keyRef)

        return result
    }

    override fun remove(key: String) {
        // Bridge objects to CF
        val keyRef = CFBridgingRetain(key)
        val serviceRef = CFBridgingRetain(SERVICE_NAME)

        // Create CFMutableDictionary
        val query = CFDictionaryCreateMutable(null, 3, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrAccount, keyRef)
        CFDictionarySetValue(query, kSecAttrService, serviceRef)

        SecItemDelete(query)

        // Release all CF objects
        CFBridgingRelease(query)
        CFBridgingRelease(serviceRef)
        CFBridgingRelease(keyRef)
        // Note: We ignore the status because deleting a non-existent item is not an error
    }

    override fun clear() {
        // Bridge objects to CF
        val serviceRef = CFBridgingRetain(SERVICE_NAME)

        // Delete all items for this service
        val query = CFDictionaryCreateMutable(null, 2, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrService, serviceRef)

        SecItemDelete(query)

        // Release all CF objects
        CFBridgingRelease(query)
        CFBridgingRelease(serviceRef)
        // Note: We ignore the status because clearing an empty keychain is not an error
    }

    companion object {
        private const val SERVICE_NAME = "com.ivangarzab.kluvs"
    }
}
