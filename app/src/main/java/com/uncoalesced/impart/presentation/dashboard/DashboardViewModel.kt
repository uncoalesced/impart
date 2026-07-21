// Engineered by uncoalesced
package com.uncoalesced.impart.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uncoalesced.impart.domain.model.Contact
import com.uncoalesced.impart.domain.repository.ContactRepository
import com.uncoalesced.impart.domain.repository.RelayManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    object Idle : DashboardUiState
    object Loading : DashboardUiState
    object Success : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    contactRepository: ContactRepository,
    private val relayManager: RelayManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Idle)
    val uiState: StateFlow<DashboardUiState> = _uiState

    val activeContacts: StateFlow<List<Contact>> = contactRepository.getActiveContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun initiatePanic(contact: Contact) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            
            val result = relayManager.dispatchPanic(contact.fcmToken, contact.publicKeyBase64)
            if (result.isSuccess) {
                _uiState.value = DashboardUiState.Success
            } else {
                _uiState.value = DashboardUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _uiState.value = DashboardUiState.Idle
    }
}
