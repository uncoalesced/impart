// Engineered by uncoalesced
package com.uncoalesced.impart.domain.repository

interface RelayManager {
    suspend fun dispatchPanic(
        targetFcmToken: String, 
        counterpartPublicKeyBase64: String,
        lat: Double? = null,
        lng: Double? = null
    ): Result<Unit>

    suspend fun dispatchAck(
        targetFcmToken: String,
        counterpartPublicKeyBase64: String
    ): Result<Unit>
}
