// Engineered by uncoalesced
package com.uncoalesced.impart.presentation.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uncoalesced.impart.domain.model.Contact
import com.uncoalesced.impart.domain.repository.ContactRepository
import com.uncoalesced.impart.domain.usecase.RevokeContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactMatrixViewModel @Inject constructor(
    contactRepository: ContactRepository,
    private val revokeContactUseCase: RevokeContactUseCase
) : ViewModel() {

    val contacts: StateFlow<List<Contact>> = contactRepository.getActiveContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun revokeContact(uuid: String) {
        viewModelScope.launch {
            revokeContactUseCase(uuid)
        }
    }
}
