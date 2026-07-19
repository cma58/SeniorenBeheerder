package com.example.seniorenbeheerder.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages == null || messages.isEmpty()) return

            // Meerdelige (multipart) SMS: één broadcast bevat ALLE delen van hetzelfde bericht.
            // We voegen ze samen tot één body i.p.v. elk deel apart te verwerken; anders werd
            // een lang statusantwoord van de Launcher half/verkeerd ingelezen.
            val sender = messages.first().displayOriginatingAddress
            val body = messages.joinToString("") { it.displayMessageBody ?: "" }

            Log.d("SmsReceiver", "Ontvangen van $sender: $body")

            // Stuur één samengevoegde broadcast naar de app om de UI te updaten.
            val updateIntent = Intent("com.example.seniorenbeheerder.SMS_UPDATED")
            updateIntent.putExtra("sender", sender)
            updateIntent.putExtra("body", body)
            updateIntent.setPackage(context.packageName)
            context.sendBroadcast(updateIntent)
        }
    }
}
