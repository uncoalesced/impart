// Engineered by uncoalesced
package com.uncoalesced.impart.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uncoalesced.impart.domain.model.Contact
import com.uncoalesced.impart.presentation.theme.*
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val contacts by viewModel.activeContacts.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is DashboardUiState.Success) {
            delay(3000)
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ImpartBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "IMPART DASHBOARD",
            color = ImpartTextPrimary,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp, top = 16.dp)
        )

        when (val state = uiState) {
            is DashboardUiState.Loading -> {
                CircularProgressIndicator(color = ImpartPanicRed)
            }
            is DashboardUiState.Success -> {
                Text(
                    text = "Panic Dispatched Successfully", 
                    color = ImpartTextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            is DashboardUiState.Error -> {
                Text("Error: ${state.message}", color = ImpartPanicRed)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.resetState() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImpartPanicRed,
                        contentColor = ImpartTextPrimary
                    )
                ) {
                    Text("Retry", color = ImpartTextPrimary)
                }
            }
            is DashboardUiState.Idle -> {
                if (contacts.isEmpty()) {
                    Text("No trusted contacts found. Add a contact first.", color = ImpartSecondary)
                } else {
                    Text("Select a contact to INITIATE PANIC:", color = ImpartSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(contacts) { contact ->
                            PanicButton(contact = contact) {
                                viewModel.initiatePanic(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PanicButton(contact: Contact, onPanic: (Contact) -> Unit) {
    Button(
        onClick = { onPanic(contact) },
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ImpartPanicRed,
            contentColor = ImpartTextPrimary
        )
    ) {
        Text(
            text = "INITIATE PANIC: ${contact.nickname ?: contact.uuid.take(8)}",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

