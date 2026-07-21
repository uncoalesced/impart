// Engineered by uncoalesced
package com.uncoalesced.impart.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uncoalesced.impart.domain.model.Contact

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val uuid: String,
    val fcmToken: String,
    val publicKeyBase64: String,
    val nickname: String?,
    val addedAt: Long,
    val revoked: Boolean
) {
    fun toDomain() = Contact(
        uuid = uuid,
        fcmToken = fcmToken,
        publicKeyBase64 = publicKeyBase64,
        nickname = nickname,
        addedAt = addedAt,
        revoked = revoked
    )

    companion object {
        fun fromDomain(contact: Contact) = ContactEntity(
            uuid = contact.uuid,
            fcmToken = contact.fcmToken,
            publicKeyBase64 = contact.publicKeyBase64,
            nickname = contact.nickname,
            addedAt = contact.addedAt,
            revoked = contact.revoked
        )
    }
}
