// Engineered by uncoalesced
package com.uncoalesced.impart.data.remote

import com.google.firebase.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.uncoalesced.impart.core.security.CryptoManager
import com.uncoalesced.impart.domain.repository.RelayManager
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.util.UUID
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

    private suspend fun getMyFcmToken(): String {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            ""
        }
    }

    override suspend fun dispatchPanic(
        targetFcmToken: String, 
        counterpartPublicKeyBase64: String,
        lat: Double?,
        lng: Double?
    ): Result<Unit> {
        return try {
            val functions = getFunctions()
                ?: return Result.failure(IllegalStateException("Firebase is not initialized."))
            
            val myToken = getMyFcmToken()
            val myPublicKey = cryptoManager.getPublicKey()
            
            val payloadObj = JSONObject().apply {
                put("type", "PANIC")
                put("urgency", "CRITICAL")
                put("senderFcmToken", myToken)
                put("senderPublicKey", myPublicKey)
                if (lat != null && lng != null) {
                    put("lat", lat)
                    put("lng", lng)
                }
            }
            val plaintextPayload = payloadObj.toString()
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

    override suspend fun dispatchAck(
        targetFcmToken: String,
        counterpartPublicKeyBase64: String
    ): Result<Unit> {
        return try {
            val functions = getFunctions()
                ?: return Result.failure(IllegalStateException("Firebase is not initialized."))
            
            val payloadObj = JSONObject().apply {
                put("type", "ACK")
            }
            val plaintextPayload = payloadObj.toString()
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
