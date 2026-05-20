package com.example.seniorenbeheerder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seniorenbeheerder.SeniorenViewModel

import androidx.compose.ui.tooling.preview.Preview
import com.example.seniorenbeheerder.data.SeniorState
import com.example.seniorenbeheerder.ui.theme.SeniorenBeheerderTheme

@Composable
fun ZorgAgendaScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    ZorgAgendaContent(
        state = viewModel.state,
        onSendCommand = viewModel::sendCommand,
        onHandleIncomingSms = viewModel::handleIncomingSms,
        modifier = modifier
    )
}

@Composable
fun ZorgAgendaContent(
    state: SeniorState,
    onSendCommand: (String) -> Unit,
    onHandleIncomingSms: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var medicijnNaam by remember { mutableStateOf("") }
    var medicijnTijd by remember { mutableStateOf("08:00") }
    
    var voorraadNaam by remember { mutableStateOf("") }
    var voorraadAantal by remember { mutableStateOf("") }

    var agendaTekst by remember { mutableStateOf("") }
    var agendaDatum by remember { mutableStateOf("24-12") }
    var agendaTijd by remember { mutableStateOf("10:00") }

    var wekkerTijd by remember { mutableStateOf("07:30") }
    var wekkerLabel by remember { mutableStateOf("Opstaan") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Zorg & Agenda", style = MaterialTheme.typography.headlineMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onSendCommand("#AGENDA_VANDAAG") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Vandaag")
            }
            Button(
                onClick = { onSendCommand("#WEKKERS_LIJST") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Alarm, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Wekkers")
            }
        }

        if (state.agendaToday.isNotEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Overzicht (Agenda/Wekkers):", style = MaterialTheme.typography.titleMedium)
                    state.agendaToday.forEach { item ->
                        Text("• $item", style = MaterialTheme.typography.bodyMedium)
                    }
                    TextButton(
                        onClick = { onHandleIncomingSms("Agenda: ") },
                        modifier = Modifier.align(androidx.compose.ui.Alignment.End)
                    ) {
                        Text("Wissen")
                    }
                }
            }
        }

        // Medicatie
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Medicatie Toevoegen", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = medicijnNaam, onValueChange = { medicijnNaam = it }, label = { Text("Naam Medicijn") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = medicijnTijd, onValueChange = { medicijnTijd = it }, label = { Text("Tijd (bijv. 08:30)") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { onSendCommand("#MEDICIJN $medicijnTijd $medicijnNaam") }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                    Text("Remind")
                }
            }
        }

        // Voorraad
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Voorraad Bijwerken", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = voorraadNaam, onValueChange = { voorraadNaam = it }, label = { Text("Naam Pil/Item") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = voorraadAantal, onValueChange = { voorraadAantal = it }, label = { Text("Aantal over") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { onSendCommand("#VOORRAAD $voorraadNaam $voorraadAantal") }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                    Text("Update")
                }
            }
        }

        // Agenda Afspraak
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Nieuwe Afspraak", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = agendaTekst, onValueChange = { agendaTekst = it }, label = { Text("Wat: (bijv. Tandarts)") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = agendaDatum, onValueChange = { agendaDatum = it }, label = { Text("Wanneer: (DD-MM)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = agendaTijd, onValueChange = { agendaTijd = it }, label = { Text("Tijd: (HH:MM)") }, modifier = Modifier.weight(1f))
                }
                Button(onClick = { onSendCommand("#AGENDA $agendaDatum $agendaTijd $agendaTekst") }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                    Text("Inplannen")
                }
            }
        }

        // Wekker
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Nieuwe Wekker", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = wekkerTijd, onValueChange = { wekkerTijd = it }, label = { Text("Tijd (HH:MM)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = wekkerLabel, onValueChange = { wekkerLabel = it }, label = { Text("Label (bijv. Opstaan)") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { onSendCommand("#WEKKER $wekkerTijd $wekkerLabel") }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                    Text("Zet Wekker")
                }
            }
        }

        // Extra Tools
        Button(
            onClick = { onSendCommand("#RADIO_STOP") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
        ) {
            Icon(Icons.Default.Radio, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Stop Radio op afstand")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ZorgAgendaPreview() {
    SeniorenBeheerderTheme {
        ZorgAgendaContent(
            state = SeniorState(
                agendaToday = listOf("09:00: Wandelen", "12:30: Lunch", "18:00: Medicatie")
            ),
            onSendCommand = {},
            onHandleIncomingSms = {}
        )
    }
}

