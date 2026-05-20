package com.example.seniorenbeheerder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seniorenbeheerder.SeniorenViewModel
import kotlin.math.roundToInt

import androidx.compose.ui.tooling.preview.Preview
import com.example.seniorenbeheerder.data.SeniorState
import com.example.seniorenbeheerder.ui.theme.SeniorenBeheerderTheme

@Composable
fun SettingsScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    SettingsContent(
        state = viewModel.state,
        onSendCommand = viewModel::sendCommand,
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    state: SeniorState,
    onSendCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var localVolume by remember(state.volumeLevel) { mutableStateOf(state.volumeLevel.toFloat()) }
    var localMediaVolume by remember(state.mediaVolumeLevel) { mutableStateOf(state.mediaVolumeLevel.toFloat()) }
    var localBrightness by remember(state.brightnessLevel) { mutableStateOf(state.brightnessLevel.toFloat()) }
    var localTextSize by remember(state.brightnessLevel) { mutableStateOf(3f) } // Placeholder

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    
    var showOpenAppDialog by remember { mutableStateOf(false) }
    var appNameInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Toestel Instellingen", style = MaterialTheme.typography.headlineMedium)

        // Volume Sliders
        SettingsCard(title = "Volume Beltoon", icon = Icons.Default.VolumeUp) {
            Slider(
                value = localVolume,
                onValueChange = { localVolume = it },
                onValueChangeFinished = { onSendCommand("#VOLUME ${localVolume.roundToInt()}") },
                valueRange = 0f..15f,
                steps = 14
            )
            Text("${localVolume.roundToInt()}/15", modifier = Modifier.align(Alignment.End))
        }

        SettingsCard(title = "Media Volume", icon = Icons.Default.MusicNote) {
            Slider(
                value = localMediaVolume,
                onValueChange = { localMediaVolume = it },
                onValueChangeFinished = { onSendCommand("#VOLUME_MEDIA ${localMediaVolume.roundToInt()}") },
                valueRange = 0f..10f,
                steps = 9
            )
            Text("${localMediaVolume.roundToInt()}/10", modifier = Modifier.align(Alignment.End))
        }

        SettingsCard(title = "Helderheid", icon = Icons.Default.BrightnessMedium) {
            Slider(
                value = localBrightness,
                onValueChange = { localBrightness = it },
                onValueChangeFinished = { onSendCommand("#HELDER ${localBrightness.roundToInt()}") },
                valueRange = 1f..10f,
                steps = 8
            )
            Text("${localBrightness.roundToInt()}/10", modifier = Modifier.align(Alignment.End))
        }

        // Toggles
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                ToggleRow(
                    label = "Wi-Fi",
                    checked = state.isWifiEnabled,
                    onCheckedChange = { onSendCommand("#WIFI ${if (it) "ON" else "OFF"}") }
                )
                HorizontalDivider()
                ToggleRow(
                    label = "Bluetooth",
                    checked = state.isBluetoothEnabled,
                    onCheckedChange = { onSendCommand("#BT ${if (it) "ON" else "OFF"}") }
                )
                HorizontalDivider()
                ToggleRow(
                    label = "Stille Modus",
                    checked = state.isSilentMode,
                    onCheckedChange = { onSendCommand("#STIL ${if (it) "ON" else "OFF"}") }
                )
            }
        }

        Text("UI & Weergave", style = MaterialTheme.typography.titleMedium)
        
        SettingsCard(title = "Tekstgrootte", icon = Icons.Default.TextFormat) {
            Slider(
                value = localTextSize,
                onValueChange = { localTextSize = it },
                onValueChangeFinished = { onSendCommand("#LETTER ${localTextSize.roundToInt()}") },
                valueRange = 1f..5f,
                steps = 3
            )
            val labels = listOf("Klein", "Normaal", "Groot", "Extra Groot", "Enorm")
            Text(labels[localTextSize.roundToInt() - 1], modifier = Modifier.align(Alignment.End))
        }

        SettingsCard(title = "Scherm Timeout", icon = Icons.Default.Timer) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("1", "2", "5", "MAX").forEach { min ->
                    OutlinedButton(
                        onClick = { onSendCommand("#SCHERM_TIJD $min") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${min}m")
                    }
                }
            }
        }

        Text("App Management", style = MaterialTheme.typography.titleMedium)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { showOpenAppDialog = true }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("App Openen")
                }
                Button(onClick = { onSendCommand("#APP_LIJST") }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("App Lijst")
                }
            }
            Button(
                onClick = { onSendCommand("#NOTIFICATIES_WEG") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.NotificationsOff, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Notificaties opschonen")
            }
        }

        Text("Systeem & Beveiliging", style = MaterialTheme.typography.titleMedium)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showPinDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("PIN Code wijzigen")
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onSendCommand("#UPDATE_CHECK") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(Icons.Default.SystemUpdate, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Check Update")
                }
                Button(
                    onClick = { onSendCommand("#RESTART") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Herstart App")
                }
            }
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("PIN wijzigen") },
            text = {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 4) pinInput = it },
                    label = { Text("Nieuwe 4-cijferige PIN") },
                    placeholder = { Text("Bijv: 1234") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onSendCommand("#PIN $pinInput")
                    showPinDialog = false
                    pinInput = ""
                }) {
                    Text("Opslaan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Annuleren")
                }
            }
        )
    }

    if (showOpenAppDialog) {
        AlertDialog(
            onDismissRequest = { showOpenAppDialog = false },
            title = { Text("App openen op afstand") },
            text = {
                OutlinedTextField(
                    value = appNameInput,
                    onValueChange = { appNameInput = it },
                    label = { Text("Naam van de app") },
                    placeholder = { Text("Bijv: Photos of Camera") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onSendCommand("#OPEN $appNameInput")
                    showOpenAppDialog = false
                    appNameInput = ""
                }) {
                    Text("Open")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOpenAppDialog = false }) {
                    Text("Annuleren")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SeniorenBeheerderTheme {
        SettingsContent(
            state = SeniorState(
                volumeLevel = 10,
                isWifiEnabled = true,
                isSilentMode = false
            ),
            onSendCommand = {}
        )
    }
}


@Composable
fun SettingsCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
