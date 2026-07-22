// Engineered by uncoalesced
package com.uncoalesced.impart.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.uncoalesced.impart.core.security.CryptoManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioCaptureHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager
) {
    @SuppressLint("MissingPermission")
    suspend fun captureAndEncrypt(durationMs: Long = 10_000L) = withContext(Dispatchers.IO) {
        val tempFile = File.createTempFile("raw_audio_", ".aac", context.cacheDir)
        var recorder: MediaRecorder? = null

        try {
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(tempFile.absolutePath)
                prepare()
                start()
            }

            delay(durationMs)

            recorder.stop()
            recorder.release()
            recorder = null

            // Encrypt raw audio file content
            val rawBytes = tempFile.readBytes()
            val myPublicKey = cryptoManager.getPublicKey()
            val ciphertext = cryptoManager.encrypt(
                android.util.Base64.encodeToString(rawBytes, android.util.Base64.NO_WRAP),
                myPublicKey
            )

            // Save encrypted file to secure internal storage
            val captureDir = File(context.filesDir, "audio_captures").apply { mkdirs() }
            val outputFile = File(captureDir, "capture_${System.currentTimeMillis()}.enc")
            outputFile.writeText(ciphertext)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                recorder?.release()
            } catch (e: Exception) { }
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }
}
