package com.example.seniorenbeheerder

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.seniorenbeheerder.data.SeniorState
import com.example.seniorenbeheerder.sms.SmsSender

class SeniorenViewModel(context: Context) : ViewModel() {
    var state by mutableStateOf(SeniorState())
        private set

    private val smsSender = SmsSender(context)

    fun updatePhoneNumber(number: String) {
        state = state.copy(phoneNumber = number)
    }

    fun sendCommand(command: String) {
        state = state.copy(isSyncing = true)
        smsSender.sendSms(state.phoneNumber, command)
    }

    fun handleIncomingSms(body: String) {
        state = state.copy(isSyncing = false)
        
        when {
            body.startsWith("#STATUS") -> {
                val battery = body.substringAfter("BATT:").substringBefore(" ").toIntOrNull() ?: state.batteryLevel
                val wifi = body.contains("WIFI:AAN")
                val bluetooth = body.contains("BT:AAN")
                val volume = body.substringAfter("VOL:").substringBefore(" ").toIntOrNull() ?: state.volumeLevel
                val media = body.substringAfter("MEDIA:").substringBefore(" ").toIntOrNull() ?: state.mediaVolumeLevel
                val silent = body.contains("STIL:AAN")
                
                state = state.copy(
                    batteryLevel = battery,
                    isWifiEnabled = wifi,
                    isBluetoothEnabled = bluetooth,
                    volumeLevel = volume,
                    mediaVolumeLevel = media,
                    isSilentMode = silent
                )
            }
            body.contains("maps.google.com") -> {
                state = state.copy(lastLocationUrl = body)
            }
            body.startsWith("#PRIVACY_RES") -> {
                state = state.copy(privacyReport = body.removePrefix("#PRIVACY_RES").trim())
            }
            body.startsWith("#INFO_RES") -> {
                state = state.copy(systemInfo = body.removePrefix("#INFO_RES").trim())
            }
            body.startsWith("#AGENDA_RES") -> {
                val items = body.removePrefix("#AGENDA_RES").split("|").map { it.trim() }.filter { it.isNotEmpty() }
                state = state.copy(agendaToday = items)
            }
        }
    }
}
