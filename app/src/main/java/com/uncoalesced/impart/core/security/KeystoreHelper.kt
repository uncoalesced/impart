// Engineered by uncoalesced
package com.uncoalesced.impart.core.security

import android.content.Context
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.hybrid.HybridConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeystoreHelper @Inject constructor(
    private val context: Context
) {
    init {
        // Register all Hybrid encryption key types with the Tink runtime.
        HybridConfig.register()
    }

    private val KEYSET_NAME = "impart_keyset"
    private val PREF_FILE_NAME = "impart_key_prefs"
    private val MASTER_KEY_URI = "android-keystore://impart_master_key"

    fun getOrGenerateKeysetHandle(): KeysetHandle {
        // DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_AES_256_GCM is Tink's HPKE template for X25519
        // ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM is standard ECIES
        val template = try {
            KeyTemplates.get("DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_AES_256_GCM")
        } catch (e: Exception) {
            KeyTemplates.get("ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM")
        }

        return AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(template)
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
    }
}
