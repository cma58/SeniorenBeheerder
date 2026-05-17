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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.seniorenbeheerder.SeniorenViewModel

@Composable
fun DashboardScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    val state = viewModel.state
    var showPhoneDialog by remember { mutableStateOf(false) }
    var phoneNumberInput by remember { mutableStateOf(state.phoneNumber) }
    var popupMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Beheer van:", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = if (state.phoneNumber.isBlank()) "Niet ingesteld" else state.phoneNumber,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            IconButton(onClick = { showPhoneDialog = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Telefoonnummer aanpassen")
            }
            Button(onClick = { viewModel.sendCommand("#STATUS") }) {
                Text("Sync")
            }
        }

        if (state.isSyncing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text("Synchroniseren...", style = MaterialTheme.typography.bodySmall)
        }

        // Widgets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusCard("Batterij", "${state.batteryLevel}%", Icons.Default.BatteryFull, Modifier.weight(1f))
            StatusCard("Wi-Fi", if (state.isWifiEnabled) "AAN" else "UIT", Icons.Default.Wifi, Modifier.weight(1f))
            StatusCard("Volume", "${state.volumeLevel}/10", Icons.Default.VolumeUp, Modifier.weight(1f))
        }

        // Acties
        Text("Snelle Acties", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
        
        ActionButton(
            text = "Locatie Opvragen",
            icon = Icons.Default.LocationOn,
            onClick = { viewModel.sendCommand("#WAAR") }
        )

        state.lastLocationUrl?.let { url ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Laatste Locatie:", style = MaterialTheme.typography.titleSmall)
                    Text(url, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        ActionButton(
            text = "Laat telefoon roepen",
            icon = Icons.Default.NotificationsActive,
            onClick = { viewModel.sendCommand("LAUN_ZOEK") }
        )

        ActionButton(
            text = "Bel mij terug",
            icon = Icons.Default.Call,
            onClick = { viewModel.sendCommand("#BEL_TERUG") }
        )

        // Popup Bericht
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Stuur popup bericht", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(
                    value = popupMessage,
                    onValueChange = { popupMessage = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Typ bericht...") }
                )
                Button(
                    onClick = { 
                        viewModel.sendCommand("#BERICHT $popupMessage")
                        popupMessage = ""
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Verstuur")
                }
            }
        }
    }

    if (showPhoneDialog) {
        AlertDialog(
            onDismissRequest = { showPhoneDialog = false },
            title = { Text("Instellen telefoonnummer") },
            text = {
                OutlinedTextField(
                    value = phoneNumberInput,
                    onValueChange = { phoneNumberInput = it },
                    label = { Text("Telefoonnummer Senior") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updatePhoneNumber(phoneNumberInput)
                    showPhoneDialog = false
                }) {
                    Text("Opslaan")
                }
            }
        )
    }
}

@Composable
fun StatusCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null)
            Text(title, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}
