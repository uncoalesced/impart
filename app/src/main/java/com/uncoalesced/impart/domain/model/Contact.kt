// Engineered by uncoalesced
package com.uncoalesced.impart.domain.model

data class Contact(
    val uuid: String,
    val fcmToken: String,
    val publicKeyBase64: String,
    val nickname: String? = null,
    val addedAt: Long = System.currentTimeMillis(),
    val revoked: Boolean = false
)
