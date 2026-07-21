// Engineered by uncoalesced
package com.uncoalesced.impart.domain.usecase

import com.uncoalesced.impart.domain.repository.ContactRepository
import javax.inject.Inject

class RevokeContactUseCase @Inject constructor(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(uuid: String) {
        contactRepository.revokeContact(uuid)
    }
}
