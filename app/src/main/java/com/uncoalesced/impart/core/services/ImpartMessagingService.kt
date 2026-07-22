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
import com.uncoalesced.impart.core.util.AudioCaptureHelper
import com.uncoalesced.impart.presentation.intrusion.IntrusionActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class ImpartMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var cryptoManager: CryptoManager

    @Inject
    lateinit var powerManager: PowerManager

    @Inject
    lateinit var audioCaptureHelper: AudioCaptureHelper

    companion object {
        private const val CHANNEL_PANIC = "impart_panic_channel"
        private const val CHANNEL_ACK = "impart_ack_channel"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "Impart::IntrusionWakeLock"
        ).apply { acquire(30_000L) }

        try {
            val ciphertext = message.data["ciphertext"] ?: return
            
            val plaintext = try {
                cryptoManager.decrypt(ciphertext)
            } catch (e: Exception) {
                return
            }
            
            val json = JSONObject(plaintext)
            val type = json.optString("type", "PANIC")
            
            if (type == "PANIC") {
                val senderFcmToken = json.optString("senderFcmToken")
                val senderPublicKey = json.optString("senderPublicKey")
                val lat = if (json.has("lat")) json.getDouble("lat") else null
                val lng = if (json.has("lng")) json.getDouble("lng") else null
                
                // Silent ambient audio recording in background on panic intrusion
                CoroutineScope(Dispatchers.IO).launch {
                    audioCaptureHelper.captureAndEncrypt(10_000L)
                }

                fireIntrusionIntent(senderFcmToken, senderPublicKey, lat, lng)
            } else if (type == "ACK") {
                fireAckNotification()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }

    private fun fireIntrusionIntent(
        senderFcmToken: String, 
        senderPublicKey: String, 
        lat: Double?, 
        lng: Double?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_PANIC,
                "Panic Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intrusionIntent = Intent(this, IntrusionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("senderFcmToken", senderFcmToken)
            putExtra("senderPublicKey", senderPublicKey)
            if (lat != null && lng != null) {
                putExtra("lat", lat)
                putExtra("lng", lng)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intrusionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_PANIC)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("IMPART — PANIC")
            .setContentText("Incoming Panic Signal Detected")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun fireAckNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ACK,
                "Acknowledgment Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ACK)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("IMPART — ACKNOWLEDGED")
            .setContentText("Your panic signal was acknowledged by a trusted node.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
