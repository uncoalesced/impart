// Engineered by uncoalesced
package com.uncoalesced.impart.core.security

import com.uncoalesced.impart.domain.model.HandshakePayload
import org.json.JSONObject

object QrPayloadCodec {
    fun encode(payload: HandshakePayload): String {
        val json = JSONObject()
        json.put("uuid", payload.uuid)
        json.put("fcmToken", payload.fcmToken)
        json.put("publicKey", payload.publicKeyBase64)
        return json.toString()
    }

    fun decode(jsonString: String): HandshakePayload? {
        return try {
            val json = JSONObject(jsonString)
            HandshakePayload(
                uuid = json.getString("uuid"),
                fcmToken = json.getString("fcmToken"),
                publicKeyBase64 = json.getString("publicKey")
            )
        } catch (e: Exception) {
            null
        }
    }
}
