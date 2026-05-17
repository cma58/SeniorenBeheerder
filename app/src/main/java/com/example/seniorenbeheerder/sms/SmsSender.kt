package com.example.seniorenbeheerder.sms

import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast

class SmsSender(private val context: Context) {
    private val smsManager: SmsManager = context.getSystemService(SmsManager::class.java)

    fun sendSms(phoneNumber: String, message: String) {
        if (phoneNumber.isBlank()) {
            Toast.makeText(context, "Stel eerst een telefoonnummer in", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } catch (e: Exception) {
            Toast.makeText(context, "Fout bij verzenden: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
