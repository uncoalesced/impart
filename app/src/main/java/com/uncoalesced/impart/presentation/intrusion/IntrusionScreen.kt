// Engineered by uncoalesced
package com.uncoalesced.impart.presentation.intrusion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uncoalesced.impart.presentation.theme.ImpartBackground
import com.uncoalesced.impart.presentation.theme.ImpartPanicRed
import com.uncoalesced.impart.presentation.theme.ImpartTextPrimary

@Composable
fun IntrusionScreen(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ImpartPanicRed)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CRITICAL EMERGENCY",
            style = MaterialTheme.typography.displaySmall,
            color = ImpartTextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "INCOMING PANIC SIGNAL DETECTED",
            style = MaterialTheme.typography.titleLarge,
            color = ImpartTextPrimary
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(
                containerColor = ImpartBackground,
                contentColor = ImpartTextPrimary
            ),
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("ACKNOWLEDGE & DISMISS", style = MaterialTheme.typography.titleMedium)
        }
    }
}

