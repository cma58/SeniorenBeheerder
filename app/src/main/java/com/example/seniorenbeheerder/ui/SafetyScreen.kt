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

@Composable
fun SafetyScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    val state = viewModel.state
    var showSosDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Veiligheid & Beveiliging", style = MaterialTheme.typography.headlineMedium)

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                ToggleRow(
                    label = "Anti-Scam Modus",
                    checked = state.antiScamEnabled,
                    onCheckedChange = { viewModel.sendCommand("#VEILIG ${if (it) "AAN" else "UIT"}") }
                )
                Text(
                    "Blokkeert automatisch oproepen van onbekende nummers die niet in de contactenlijst staan.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                ToggleRow(
                    label = "Instellingen Vergrendelen",
                    checked = state.settingsLocked,
                    onCheckedChange = { viewModel.sendCommand("#SLOT ${if (it) "AAN" else "UIT"}") }
                )
                Text(
                    "Voorkomt dat de senior per ongeluk belangrijke systeeminstellingen wijzigt.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Text("Diagnose & Systeem", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.sendCommand("#PRIVACY") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Shield, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Privacy Check")
            }
            Button(
                onClick = { viewModel.sendCommand("#INFO_PLUS") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Systeem Info")
            }
        }

        // Weergave van resultaten
        state.privacyReport?.let { report ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Privacy Rapport:", style = MaterialTheme.typography.titleSmall)
                    Text(report, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = { viewModel.handleIncomingSms("#PRIVACY_RES ") }, modifier = Modifier.align(Alignment.End)) {
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
                    TextButton(onClick = { viewModel.handleIncomingSms("#INFO_RES ") }, modifier = Modifier.align(Alignment.End)) {
                        Text("Sluiten")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

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
                        viewModel.sendCommand("#SOS_NU")
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
}
