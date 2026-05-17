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

@Composable
fun SettingsScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    val state = viewModel.state
    var localVolume by remember(state.volumeLevel) { mutableStateOf(state.volumeLevel.toFloat()) }
    var localMediaVolume by remember(state.mediaVolumeLevel) { mutableStateOf(state.mediaVolumeLevel.toFloat()) }
    var localBrightness by remember(state.brightnessLevel) { mutableStateOf(state.brightnessLevel.toFloat()) }

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
                onValueChangeFinished = { viewModel.sendCommand("#VOLUME ${localVolume.roundToInt()}") },
                valueRange = 0f..10f,
                steps = 9
            )
            Text("${localVolume.roundToInt()}/10", modifier = Modifier.align(Alignment.End))
        }

        SettingsCard(title = "Media Volume", icon = Icons.Default.MusicNote) {
            Slider(
                value = localMediaVolume,
                onValueChange = { localMediaVolume = it },
                onValueChangeFinished = { viewModel.sendCommand("#VOLUME_MEDIA ${localMediaVolume.roundToInt()}") },
                valueRange = 0f..10f,
                steps = 9
            )
            Text("${localMediaVolume.roundToInt()}/10", modifier = Modifier.align(Alignment.End))
        }

        SettingsCard(title = "Helderheid", icon = Icons.Default.BrightnessMedium) {
            Slider(
                value = localBrightness,
                onValueChange = { localBrightness = it },
                onValueChangeFinished = { viewModel.sendCommand("#HELDER ${localBrightness.roundToInt()}") },
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
                    onCheckedChange = { viewModel.sendCommand("#WIFI ${if (it) "AAN" else "UIT"}") }
                )
                HorizontalDivider()
                ToggleRow(
                    label = "Bluetooth",
                    checked = state.isBluetoothEnabled,
                    onCheckedChange = { viewModel.sendCommand("#BT ${if (it) "AAN" else "UIT"}") }
                )
                HorizontalDivider()
                ToggleRow(
                    label = "Stille Modus",
                    checked = state.isSilentMode,
                    onCheckedChange = { viewModel.sendCommand("#STIL ${if (it) "AAN" else "UIT"}") }
                )
            }
        }
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
