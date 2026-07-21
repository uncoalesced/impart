// Engineered by uncoalesced
package com.uncoalesced.impart.presentation.intrusion

import android.app.KeyguardManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.uncoalesced.impart.presentation.theme.ImpartTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntrusionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)

        setContent {
            ImpartTheme {
                IntrusionScreen(onDismiss = { finish() })
            }
        }
    }
}
