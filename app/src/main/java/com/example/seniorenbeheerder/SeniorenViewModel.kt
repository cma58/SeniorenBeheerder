package com.example.seniorenbeheerder

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.seniorenbeheerder.data.SeniorState
import com.example.seniorenbeheerder.sms.SmsSender

class SeniorenViewModel(context: Context) : ViewModel() {
    private val prefs = context.getSharedPreferences("senioren_beheerder", Context.MODE_PRIVATE)

    var state by mutableStateOf(SeniorState(
        phoneNumber = prefs.getString("phone_number", "") ?: "",
        isPrivacyAccepted = prefs.getBoolean("privacy_accepted", false)
    ))
        private set

    private val smsSender = SmsSender(context)

    fun acceptPrivacy() {
        state = state.copy(isPrivacyAccepted = true)
        prefs.edit().putBoolean("privacy_accepted", true).apply()
    }

    fun updatePhoneNumber(number: String) {
        state = state.copy(phoneNumber = number)
        prefs.edit().putString("phone_number", number).apply()
    }

    fun sendCommand(command: String) {
        Log.d("SeniorenViewModel", "Sending command: $command to ${state.phoneNumber}")
        state = state.copy(isSyncing = true)
        smsSender.sendSms(state.phoneNumber, command)
    }

    fun handleIncomingSms(body: String) {
        Log.d("SeniorenViewModel", "Handling incoming SMS: $body")
        var newState = state.copy(isSyncing = false)
        
        // Verwijder de "Sionro Remote:" prefix als die er is
        val cleanBody = if (body.startsWith("Sionro Remote:")) {
            body.substringAfter("Sionro Remote:").trim()
        } else {
            body.trim()
        }

        // Reset check
        if (cleanBody == "Locatie:") {
            newState = newState.copy(latitude = null, longitude = null)
        }

        // 1. Batterij, Volume en Stilte (kunnen in elk bericht zitten)
        if (cleanBody.contains("🔋")) {
            val battery = cleanBody.substringAfter("🔋").trim().substringBefore("%").trim().toIntOrNull()
            Log.d("SeniorenViewModel", "Parsed battery: $battery")
            if (battery != null) newState = newState.copy(batteryLevel = battery)
        }
        if (cleanBody.contains("🔊")) {
            val volumeText = cleanBody.substringAfter("🔊").trim().substringBefore("/").trim()
            val volume = volumeText.toIntOrNull()
            Log.d("SeniorenViewModel", "Parsed volume: $volume")
            if (volume != null) newState = newState.copy(volumeLevel = volume)
        }
        if (cleanBody.contains("🔕 Stil:")) {
            val silent = cleanBody.contains("🔕 Stil: JA")
            Log.d("SeniorenViewModel", "Parsed silent: $silent")
            newState = newState.copy(isSilentMode = silent)
        }

        // 2. Locatie en Coördinaten
        if (cleanBody.contains("maps.google.com") || cleanBody.contains("google.com/maps") || cleanBody.contains("query=") || cleanBody.contains("📍 Locatie:")) {
            Log.d("SeniorenViewModel", "Processing Location SMS")
            
            // Probeer coördinaten te extraheren (bijv. 51.0441737,3.7436598 of query=51.044,3.743)
            val coordsPattern = Regex("([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)")
            val match = coordsPattern.find(cleanBody)
            if (match != null) {
                val lat = match.groupValues[1].toDoubleOrNull()
                val lon = match.groupValues[2].toDoubleOrNull()
                Log.d("SeniorenViewModel", "Parsed coordinates: $lat, $lon")
                if (lat != null && lon != null) {
                    newState = newState.copy(latitude = lat, longitude = lon)
                }
            }

            if (cleanBody.startsWith("1&query=")) {
                val currentUrl = state.lastLocationUrl ?: ""
                newState = newState.copy(lastLocationUrl = currentUrl + cleanBody)
            } else {
                val url = cleanBody.split(" ").find { it.contains("http") } ?: cleanBody
                newState = newState.copy(lastLocationUrl = url)
            }
        }

        // 3. Privacy Status
        if (cleanBody.startsWith("Privacy Status:") || cleanBody.contains("GPS:") || cleanBody.contains("Permissions:")) {
            Log.d("SeniorenViewModel", "Processing Privacy SMS")
            newState = newState.copy(privacyReport = cleanBody.removePrefix("Privacy Status:").trim())
        }

        // 4. Systeem Info
        if (cleanBody.startsWith("Info:") || cleanBody.contains("Android:") || cleanBody.contains("Storage:")) {
            Log.d("SeniorenViewModel", "Processing System Info SMS")
            newState = newState.copy(systemInfo = cleanBody.removePrefix("Info:").trim())
        }

        // 5. Agenda en Wekkers
        if (cleanBody.startsWith("Agenda:") || cleanBody.contains("Afspraken:") || cleanBody.contains("Wekkers:")) {
            Log.d("SeniorenViewModel", "Processing Agenda/Wekkers SMS")
            val items = cleanBody.substringAfter(":").trim()
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            newState = newState.copy(agendaToday = items)
        }

        // 6. Netwerk Status
        if (cleanBody.startsWith("Netwerk:") || cleanBody.contains("Provider:")) {
            Log.d("SeniorenViewModel", "Processing Network Status SMS")
            newState = newState.copy(systemInfo = "Netwerk Status:\n$cleanBody")
        }

        // 7. Oproep Details
        if (cleanBody.startsWith("Oproep:") || cleanBody.contains("Laatste oproep:")) {
            Log.d("SeniorenViewModel", "Processing Call Details SMS")
            newState = newState.copy(systemInfo = "Oproep Details:\n$cleanBody")
        }

        // 8. Lege Agenda meldingen
        if (cleanBody.contains("Geen afspraken vandaag") || cleanBody.contains("Geen wekkers")) {
            Log.d("SeniorenViewModel", "Processing Empty Agenda SMS")
            newState = newState.copy(agendaToday = emptyList())
        }

        state = newState
    }
}
