// Engineered by uncoalesced
package com.uncoalesced.impart.data.remote

import com.google.firebase.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.uncoalesced.impart.core.security.CryptoManager
import com.uncoalesced.impart.domain.repository.RelayManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelayManagerImpl @Inject constructor(
    private val cryptoManager: CryptoManager
) : RelayManager {

    private fun getFunctions(): FirebaseFunctions? {
        return try {
            if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isNotEmpty()) {
                Firebase.functions
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun dispatchPanic(targetFcmToken: String, counterpartPublicKeyBase64: String): Result<Unit> {
        return try {
            val functions = getFunctions()
                ?: return Result.failure(IllegalStateException("Firebase is not initialized. Please ensure google-services.json is configured."))
            val plaintextPayload = """{"urgency": "CRITICAL"}"""
            val ciphertext = cryptoManager.encrypt(plaintextPayload, counterpartPublicKeyBase64)

            val data = hashMapOf(
                "targetFcmToken" to targetFcmToken,
                "envelope" to hashMapOf(
                    "ciphertext" to ciphertext
                )
            )

            functions.getHttpsCallable("initiatePanic").call(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
