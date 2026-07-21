// Engineered by uncoalesced
package com.uncoalesced.impart.presentation.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uncoalesced.impart.domain.model.Contact
import com.uncoalesced.impart.presentation.theme.ImpartBackground
import com.uncoalesced.impart.presentation.theme.ImpartPanicRed
import com.uncoalesced.impart.presentation.theme.ImpartTextPrimary

@Composable
fun ContactMatrixScreen(viewModel: ContactMatrixViewModel) {
    val contacts by viewModel.contacts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ImpartBackground)
            .padding(16.dp)
    ) {
        items(contacts) { contact ->
            ContactRow(contact = contact, onRevoke = { viewModel.revokeContact(it) })
        }
    }
}

@Composable
fun ContactRow(contact: Contact, onRevoke: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = contact.nickname ?: contact.uuid.take(8),
            color = ImpartTextPrimary
        )
        Button(
            onClick = { onRevoke(contact.uuid) },
            colors = ButtonDefaults.buttonColors(
                containerColor = ImpartPanicRed,
                contentColor = ImpartTextPrimary
            )
        ) {
            Text("Revoke")
        }
    }
}

