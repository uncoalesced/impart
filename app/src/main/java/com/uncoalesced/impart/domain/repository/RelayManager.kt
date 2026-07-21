// Engineered by uncoalesced
package com.uncoalesced.impart.domain.repository

interface RelayManager {
    suspend fun dispatchPanic(targetFcmToken: String, counterpartPublicKeyBase64: String): Result<Unit>
}
