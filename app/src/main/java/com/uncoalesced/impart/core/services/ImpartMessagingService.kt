// Engineered by uncoalesced
package com.uncoalesced.impart.core.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.uncoalesced.impart.core.security.CryptoManager
import com.uncoalesced.impart.presentation.intrusion.IntrusionActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImpartMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var cryptoManager: CryptoManager

    @Inject
    lateinit var powerManager: PowerManager

    companion object {
        private const val CHANNEL_ID = "impart_panic_channel"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // 1. Acquire WakeLock to survive Doze
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "Impart::IntrusionWakeLock"
        ).apply { acquire(30_000L) }

        try {
            // 2. Extract Ciphertext
            val ciphertext = message.data["ciphertext"]
            if (ciphertext == null) {
                // Not a valid Impart payload; ignore
                return
            }

            // 3. Decrypt Payload via Tink
            try {
                cryptoManager.decrypt(ciphertext)
            } catch (e: Exception) {
                // Decryption failed (keys mismatch or tampered payload). Fail closed.
                return
            }

            // Validated and Decrypted. Proceed to OS Intrusion Pipeline.
            fireIntrusionIntent()
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }

    private fun fireIntrusionIntent() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Panic Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intrusionIntent = Intent(this, IntrusionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intrusionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("IMPART — PANIC")
            .setContentText("Incoming Panic Signal")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
