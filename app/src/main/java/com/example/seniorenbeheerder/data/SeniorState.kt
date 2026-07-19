package com.example.seniorenbeheerder.data

data class SeniorState(
    val phoneNumber: String = "",
    val batteryLevel: Int = 0,
    val isWifiEnabled: Boolean = false,
    val volumeLevel: Int = 0,
    val mediaVolumeLevel: Int = 0,
    val brightnessLevel: Int = 5,
    val isBluetoothEnabled: Boolean = false,
    val isSilentMode: Boolean = false,
    val lastLocationUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isSyncing: Boolean = false,
    val antiScamEnabled: Boolean = false,
    val settingsLocked: Boolean = false,
    val privacyReport: String? = null,
    val systemInfo: String? = null,
    val agendaToday: List<String> = emptyList(),
    val isPrivacyAccepted: Boolean = false,
    // De beveiligingscode (PIN) van de telefoon van de senior. De Launcher vereist deze
    // als eerste argument bij gevoelige opdrachten (locatie, SOS, slot, herstart, ...).
    // Standaard 1234, gelijk aan de standaard-PIN van de Launcher.
    val pinCode: String = "1234"
)
