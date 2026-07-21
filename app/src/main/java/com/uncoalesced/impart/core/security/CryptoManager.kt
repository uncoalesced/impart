// Engineered by uncoalesced
package com.uncoalesced.impart.core.security

import android.util.Base64
import com.google.crypto.tink.BinaryKeysetReader
import com.google.crypto.tink.BinaryKeysetWriter
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.HybridDecrypt
import com.google.crypto.tink.HybridEncrypt
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor(
    private val keystoreHelper: KeystoreHelper
) {
    fun getPublicKey(): String {
        val keysetHandle = keystoreHelper.getOrGenerateKeysetHandle()
        val publicKeysetHandle = keysetHandle.publicKeysetHandle
        
        val baos = ByteArrayOutputStream()
        CleartextKeysetHandle.write(
            publicKeysetHandle,
            BinaryKeysetWriter.withOutputStream(baos)
        )
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
    }

    fun encrypt(plaintext: String, counterpartPublicKeyBase64: String): String {
        val decodedKeyset = Base64.decode(counterpartPublicKeyBase64, Base64.NO_WRAP)
        val counterpartPublicKeysetHandle = CleartextKeysetHandle.read(
            BinaryKeysetReader.withBytes(decodedKeyset)
        )
        
        val hybridEncrypt = counterpartPublicKeysetHandle.getPrimitive(HybridEncrypt::class.java)
        // Tink's HybridEncrypt expects contextInfo as the second parameter (can be empty)
        val ciphertext = hybridEncrypt.encrypt(plaintext.toByteArray(Charsets.UTF_8), ByteArray(0))
        return Base64.encodeToString(ciphertext, Base64.NO_WRAP)
    }

    fun decrypt(ciphertextBase64: String): String {
        val keysetHandle = keystoreHelper.getOrGenerateKeysetHandle()
        val hybridDecrypt = keysetHandle.getPrimitive(HybridDecrypt::class.java)
        
        val ciphertext = Base64.decode(ciphertextBase64, Base64.NO_WRAP)
        val plaintext = hybridDecrypt.decrypt(ciphertext, ByteArray(0))
        return String(plaintext, Charsets.UTF_8)
    }
}
