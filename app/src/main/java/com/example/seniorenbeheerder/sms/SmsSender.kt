package com.example.seniorenbeheerder.sms

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.concurrent.atomic.AtomicInteger

/** Status van een verzonden SMS-opdracht, zodat de UI feedback kan tonen. */
enum class SmsStatus { SENT, DELIVERED, FAILED }

class SmsSender(private val context: Context) {

    // SmsManager is pas vanaf Android 12 (S) een echte systeemdienst; op oudere toestellen
    // (minSdk 23) geeft getSystemService(SmsManager::class.java) null terug -> gebruik daar
    // de (gedeprecieerde) getDefault(). Zonder deze splitsing crasht verzenden op < Android 12.
    private val smsManager: SmsManager =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

    /**
     * Verstuurt een SMS en meldt het resultaat terug (verzonden / afgeleverd / mislukt) via
     * een Toast en de optionele [onStatus]-callback. "Afgeleverd" hangt af van de provider:
     * niet elk netwerk stuurt een afleverrapport, dus die melding kan uitblijven.
     */
    fun sendSms(phoneNumber: String, message: String, onStatus: ((SmsStatus) -> Unit)? = null) {
        if (phoneNumber.isBlank()) {
            Toast.makeText(context, "Stel eerst een telefoonnummer in", Toast.LENGTH_SHORT).show()
            onStatus?.invoke(SmsStatus.FAILED)
            return
        }
        try {
            val id = counter.incrementAndGet()
            val sentAction = "$ACTION_SENT.$id"
            val deliveredAction = "$ACTION_DELIVERED.$id"
            registerResultReceivers(sentAction, deliveredAction, onStatus)

            val appCtx = context.applicationContext
            val parts = smsManager.divideMessage(message)
            if (parts.size <= 1) {
                smsManager.sendTextMessage(
                    phoneNumber, null, message,
                    pendingIntent(appCtx, sentAction, id),
                    pendingIntent(appCtx, deliveredAction, -id)
                )
            } else {
                // Bij een meerdelig bericht koppelen we het "echte" resultaat-intent alleen aan
                // het LAATSTE deel (zodat we één keer "verzonden/afgeleverd" tonen). De overige
                // delen krijgen een intent op een actie waar geen ontvanger op luistert.
                val sentPIs = ArrayList<PendingIntent>()
                val deliveredPIs = ArrayList<PendingIntent>()
                for (i in parts.indices) {
                    val last = i == parts.size - 1
                    val sAction = if (last) sentAction else "$sentAction.part$i"
                    val dAction = if (last) deliveredAction else "$deliveredAction.part$i"
                    val code = if (last) id else id * 1000 + i
                    sentPIs.add(pendingIntent(appCtx, sAction, code))
                    deliveredPIs.add(pendingIntent(appCtx, dAction, -code))
                }
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, sentPIs, deliveredPIs)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Fout bij verzenden: ${e.message}", Toast.LENGTH_SHORT).show()
            onStatus?.invoke(SmsStatus.FAILED)
        }
    }

    private fun pendingIntent(appCtx: Context, action: String, requestCode: Int): PendingIntent {
        val intent = Intent(action).setPackage(appCtx.packageName)
        return PendingIntent.getBroadcast(
            appCtx, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun registerResultReceivers(
        sentAction: String,
        deliveredAction: String,
        onStatus: ((SmsStatus) -> Unit)?
    ) {
        val appCtx = context.applicationContext

        val sentReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, i: Intent?) {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(appCtx, "✅ Opdracht verzonden", Toast.LENGTH_SHORT).show()
                    onStatus?.invoke(SmsStatus.SENT)
                } else {
                    val reason = when (resultCode) {
                        SmsManager.RESULT_ERROR_NO_SERVICE -> "geen netwerk"
                        SmsManager.RESULT_ERROR_RADIO_OFF -> "radio uit (vliegtuigmodus?)"
                        SmsManager.RESULT_ERROR_NULL_PDU -> "leeg bericht"
                        else -> "onbekende fout"
                    }
                    Toast.makeText(appCtx, "❌ Verzenden mislukt: $reason", Toast.LENGTH_LONG).show()
                    onStatus?.invoke(SmsStatus.FAILED)
                }
                try { appCtx.unregisterReceiver(this) } catch (_: Exception) {}
            }
        }

        val deliveredReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, i: Intent?) {
                Toast.makeText(appCtx, "📬 Afgeleverd op de telefoon van de senior", Toast.LENGTH_SHORT).show()
                onStatus?.invoke(SmsStatus.DELIVERED)
                try { appCtx.unregisterReceiver(this) } catch (_: Exception) {}
            }
        }

        ContextCompat.registerReceiver(appCtx, sentReceiver, IntentFilter(sentAction), ContextCompat.RECEIVER_NOT_EXPORTED)
        ContextCompat.registerReceiver(appCtx, deliveredReceiver, IntentFilter(deliveredAction), ContextCompat.RECEIVER_NOT_EXPORTED)

        // Veiligheidsnet: meld de ontvangers na 60s af als er nooit een resultaat komt
        // (bv. providers zonder afleverrapport), zodat er geen ontvangers blijven hangen.
        Handler(Looper.getMainLooper()).postDelayed({
            try { appCtx.unregisterReceiver(sentReceiver) } catch (_: Exception) {}
            try { appCtx.unregisterReceiver(deliveredReceiver) } catch (_: Exception) {}
        }, 60_000)
    }

    companion object {
        private const val ACTION_SENT = "com.example.seniorenbeheerder.SMS_SENT"
        private const val ACTION_DELIVERED = "com.example.seniorenbeheerder.SMS_DELIVERED"
        private val counter = AtomicInteger(0)
    }
}
