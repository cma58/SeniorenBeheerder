package com.example.seniorenbeheerder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.seniorenbeheerder.SeniorenViewModel
import com.example.seniorenbeheerder.data.SeniorState
import com.example.seniorenbeheerder.ui.theme.SeniorenBeheerderTheme

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun DashboardScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    DashboardContent(
        state = viewModel.state,
        onUpdatePhoneNumber = viewModel::updatePhoneNumber,
        onSendCommand = viewModel::sendCommand,
        onHandleIncomingSms = viewModel::handleIncomingSms,
        modifier = modifier
    )
}

@Composable
fun DashboardContent(
    state: SeniorState,
    onUpdatePhoneNumber: (String) -> Unit,
    onSendCommand: (String) -> Unit,
    onHandleIncomingSms: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPhoneDialog by remember { mutableStateOf(false) }
    var phoneNumberInput by remember { mutableStateOf(state.phoneNumber) }
    var popupMessage by remember { mutableStateOf("") }

    var showContactDialog by remember { mutableStateOf(false) }
    var contactNameInput by remember { mutableStateOf("") }
    var contactNumberInput by remember { mutableStateOf("") }

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
            Button(onClick = { onSendCommand("#STATUS") }) {
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
            StatusCard("Netwerk", "Check", Icons.Default.NetworkCheck, Modifier.weight(1f), onClick = { onSendCommand("#NETWERK") })
            StatusCard("Volume", "${state.volumeLevel}/15", Icons.Default.VolumeUp, Modifier.weight(1f))
        }

        // Acties
        Text("Snelle Acties", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
        
        ActionButton(
            text = "Locatie Opvragen",
            icon = Icons.Default.LocationOn,
            onClick = { onSendCommand("#WAAR") }
        )

        // OSM DIRECT VIEW
        if (state.latitude != null && state.longitude != null) {
            val geoPoint = GeoPoint(state.latitude, state.longitude)
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { context ->
                            MapView(context).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(15.0)
                                controller.setCenter(geoPoint)
                                
                                val marker = Marker(this)
                                marker.position = geoPoint
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.title = "Senior Locatie"
                                overlays.add(marker)
                            }
                        },
                        update = { view ->
                            view.controller.animateTo(geoPoint)
                            // Update marker position if needed
                            val marker = view.overlays.filterIsInstance<Marker>().firstOrNull()
                            marker?.position = geoPoint
                            view.invalidate()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Wissen knop
                    IconButton(
                        onClick = { onHandleIncomingSms("Locatie: ") },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kaart sluiten")
                    }
                }
            }
        } else if (state.lastLocationUrl != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Laatste Locatie Link:", style = MaterialTheme.typography.titleSmall)
                    Text(state.lastLocationUrl, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onSendCommand("LAUN_ZOEK") }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Roepen")
            }
            Button(onClick = { onSendCommand("#BEL_TERUG") }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Call, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Bel mij")
            }
        }

        // Flashlight Tools
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Zaklamp Controle", style = MaterialTheme.typography.titleSmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { onSendCommand("#LAMP ON") }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.FlashlightOn, contentDescription = "Aan")
                    }
                    IconButton(onClick = { onSendCommand("#LAMP OFF") }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.FlashlightOff, contentDescription = "Uit")
                    }
                    Button(onClick = { onSendCommand("#KNIPPER") }, modifier = Modifier.weight(2f)) {
                        Icon(Icons.Default.FlashOn, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Knipper")
                    }
                }
            }
        }

        // Contacts
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Snelkeuze Contacten", style = MaterialTheme.typography.titleSmall)
                Button(onClick = { showContactDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Contact toevoegen")
                }
            }
        }

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
                        onSendCommand("#BERICHT $popupMessage")
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
                    onUpdatePhoneNumber(phoneNumberInput)
                    showPhoneDialog = false
                }) {
                    Text("Opslaan")
                }
            }
        )
    }

    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text("Nieuw Snelkeuze Contact") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = contactNameInput, onValueChange = { contactNameInput = it }, label = { Text("Naam") })
                    OutlinedTextField(value = contactNumberInput, onValueChange = { contactNumberInput = it }, label = { Text("Telefoonnummer") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSendCommand("#CONTACT $contactNameInput $contactNumberInput")
                    showContactDialog = false
                    contactNameInput = ""
                    contactNumberInput = ""
                }) {
                    Text("Toevoegen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text("Annuleren")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    SeniorenBeheerderTheme {
        DashboardContent(
            state = SeniorState(
                phoneNumber = "+31612345678",
                batteryLevel = 85,
                isWifiEnabled = true,
                volumeLevel = 10,
                lastLocationUrl = "https://maps.google.com/?q=52.3676,4.9041"
            ),
            onUpdatePhoneNumber = {},
            onSendCommand = {},
            onHandleIncomingSms = {}
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(modifier = modifier, onClick = onClick) {
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
