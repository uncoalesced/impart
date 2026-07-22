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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncoalesced.impart.presentation.theme.ImpartBackground
import com.uncoalesced.impart.presentation.theme.ImpartPanicRed
import com.uncoalesced.impart.presentation.theme.ImpartTextPrimary

@Composable
fun IntrusionScreen(
    lat: Double?,
    lng: Double?,
    onDismiss: () -> Unit
) {
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
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (lat != null && lng != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ImpartBackground.copy(alpha = 0.5f))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TARGET COORDINATES",
                    color = ImpartTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$lat, $lng",
                    color = ImpartTextPrimary,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        
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
