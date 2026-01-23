package com.ivangarzab.kluvs.auth.persistence

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanFalse
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
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrService
import platform.Security.kSecAttrSynchronizable
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

/**
 * iOS implementation of [com.ivangarzab.kluvs.auth.persistence.SecureStorage] using Keychain Services.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosSecureStorage(
    private val isTesting: Boolean = false
) : SecureStorage {

    override fun save(key: String, value: String) {
        remove(key)

        val valueData = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding)
            ?: throw IllegalArgumentException("Failed to encode value as UTF-8")

        val keyRef = CFBridgingRetain(key as NSString)
        val serviceRef = CFBridgingRetain(SERVICE_NAME as NSString)
        val dataRef = CFBridgingRetain(valueData)

        val query = CFDictionaryCreateMutable(null, 6, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrAccount, keyRef)
        CFDictionarySetValue(query, kSecAttrService, serviceRef)
        CFDictionarySetValue(query, kSecValueData, dataRef)
        
        // In testing/simulator environment, we might need different accessibility
        CFDictionarySetValue(query, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlock)
        CFDictionarySetValue(query, kSecAttrSynchronizable, kCFBooleanFalse)

        val status = SecItemAdd(query, null)

        CFRelease(query)
        CFBridgingRelease(dataRef)
        CFBridgingRelease(serviceRef)
        CFBridgingRelease(keyRef)

        if (status != errSecSuccess) {
            // If we are in testing and hit -25291 (No Keychain), we can fallback to a simple in-memory map or log it
            // Status -25291 is errSecNotAvailable
            if (isTesting && status == -25291) {
                fallbackStorage[key] = value
                return
            }
            throw RuntimeException("Failed to save to keychain. Status: $status. Key: $key")
        }
    }

    override fun get(key: String): String? {
        if (isTesting && fallbackStorage.containsKey(key)) {
            return fallbackStorage[key]
        }

        val keyRef = CFBridgingRetain(key as NSString)
        val serviceRef = CFBridgingRetain(SERVICE_NAME as NSString)

        val query = CFDictionaryCreateMutable(null, 6, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrAccount, keyRef)
        CFDictionarySetValue(query, kSecAttrService, serviceRef)
        CFDictionarySetValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionarySetValue(query, kSecMatchLimit, kSecMatchLimitOne)
        CFDictionarySetValue(query, kSecAttrSynchronizable, kCFBooleanFalse)

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

        CFRelease(query)
        CFBridgingRelease(serviceRef)
        CFBridgingRelease(keyRef)

        return result
    }

    override fun remove(key: String) {
        if (isTesting) fallbackStorage.remove(key)

        val keyRef = CFBridgingRetain(key as NSString)
        val serviceRef = CFBridgingRetain(SERVICE_NAME as NSString)

        val query = CFDictionaryCreateMutable(null, 4, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrAccount, keyRef)
        CFDictionarySetValue(query, kSecAttrService, serviceRef)
        CFDictionarySetValue(query, kSecAttrSynchronizable, kCFBooleanFalse)

        SecItemDelete(query)

        CFRelease(query)
        CFBridgingRelease(serviceRef)
        CFBridgingRelease(keyRef)
    }

    override fun clear() {
        if (isTesting) fallbackStorage.clear()

        val serviceRef = CFBridgingRetain(SERVICE_NAME as NSString)

        val query = CFDictionaryCreateMutable(null, 3, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrService, serviceRef)
        CFDictionarySetValue(query, kSecAttrSynchronizable, kCFBooleanFalse)

        SecItemDelete(query)

        CFRelease(query)
        CFBridgingRelease(serviceRef)
    }

    companion object {
        private const val SERVICE_NAME = "com.ivangarzab.kluvs"
        // Internal fallback for environments where Keychain is unavailable (like some CI/CLI test runs)
        private val fallbackStorage = mutableMapOf<String, String>()
    }
}
