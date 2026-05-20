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
import androidx.compose.ui.unit.dp
import com.example.seniorenbeheerder.SeniorenViewModel

import androidx.compose.ui.tooling.preview.Preview
import com.example.seniorenbeheerder.data.SeniorState
import com.example.seniorenbeheerder.ui.theme.SeniorenBeheerderTheme

@Composable
fun SafetyScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    SafetyContent(
        state = viewModel.state,
        onSendCommand = viewModel::sendCommand,
        onHandleIncomingSms = viewModel::handleIncomingSms,
        modifier = modifier
    )
}

@Composable
fun SafetyContent(
    state: SeniorState,
    onSendCommand: (String) -> Unit,
    onHandleIncomingSms: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSosDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var blockNumberInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Veiligheid & Beveiliging", style = MaterialTheme.typography.headlineMedium)

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleRow(
                    label = "Anti-Scam Modus",
                    checked = state.antiScamEnabled,
                    onCheckedChange = { onSendCommand("#VEILIG ${if (it) "ON" else "OFF"}") }
                )
                Text(
                    "Blokkeert automatisch oproepen van onbekende nummers die niet in de contactenlijst staan.",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Button(
                    onClick = { showBlockDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Block, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Specifiek nummer blokkeren")
                }
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleRow(
                    label = "Instellingen Vergrendelen",
                    checked = state.settingsLocked,
                    onCheckedChange = { onSendCommand("#SLOT ${if (it) "ON" else "OFF"}") }
                )
                Text(
                    "Voorkomt dat de senior per ongeluk belangrijke systeeminstellingen wijzigt.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text("Hulp & Welzijn op afstand", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onSendCommand("#PING") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Vraag: 'Gaat het?'")
            }
            Button(
                onClick = { onSendCommand("#SPEAKER") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Forceer Luidspreker")
            }
        }

        Text("Diagnose & Systeem", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onSendCommand("#PRIVACY") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Shield, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Privacy Status")
            }
            Button(
                onClick = { onSendCommand("#INFO_PLUS") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Info Plus")
            }
        }

        Button(
            onClick = { onSendCommand("#LAATSTE_OPROEP") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Call, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Details laatste oproep")
        }

        // Weergave van resultaten
        state.privacyReport?.let { report ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Privacy Rapport:", style = MaterialTheme.typography.titleSmall)
                    Text(report, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = { onHandleIncomingSms("Privacy Status: ") }, modifier = Modifier.align(Alignment.End)) {
                        Text("Sluiten")
                    }
                }
            }
        }

        state.systemInfo?.let { info ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Systeem Informatie:", style = MaterialTheme.typography.titleSmall)
                    Text(info, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = { onHandleIncomingSms("Info: ") }, modifier = Modifier.align(Alignment.End)) {
                        Text("Sluiten")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showSosDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            contentPadding = PaddingValues(20.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(12.dp))
            Text("FORCEER SOS NU", style = MaterialTheme.typography.headlineSmall)
        }
    }

    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            title = { Text("Bevestig SOS") },
            text = { Text("Weet u zeker dat u de SOS-modus op afstand wilt activeren? Dit zal noodcontacten waarschuwen en de sirene activeren.") },
            confirmButton = {
                Button(
                    onClick = {
                        onSendCommand("#SOS_NU")
                        showSosDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("JA, ACTIVEER")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSosDialog = false }) {
                    Text("Annuleren")
                }
            }
        )
    }

    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            title = { Text("Nummer blokkeren") },
            text = {
                OutlinedTextField(
                    value = blockNumberInput,
                    onValueChange = { blockNumberInput = it },
                    label = { Text("Telefoonnummer") },
                    placeholder = { Text("Bijv: 0484123456") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onSendCommand("#BLOKKEER $blockNumberInput")
                    showBlockDialog = false
                    blockNumberInput = ""
                }) {
                    Text("Blokkeer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) {
                    Text("Annuleren")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SafetyPreview() {
    SeniorenBeheerderTheme {
        SafetyContent(
            state = SeniorState(
                antiScamEnabled = true,
                settingsLocked = false,
                privacyReport = "GPS: OK\nSMS: OK\nBEL: NEE"
            ),
            onSendCommand = {},
            onHandleIncomingSms = {}
        )
    }
}

