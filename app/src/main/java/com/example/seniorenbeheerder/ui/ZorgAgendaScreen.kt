package com.example.seniorenbeheerder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seniorenbeheerder.SeniorenViewModel

@Composable
fun ZorgAgendaScreen(viewModel: SeniorenViewModel, modifier: Modifier = Modifier) {
    val state = viewModel.state
    var medicijnNaam by remember { mutableStateOf("") }
    var medicijnTijd by remember { mutableStateOf("08:00") }
    
    var voorraadNaam by remember { mutableStateOf("") }
    var voorraadAantal by remember { mutableStateOf("") }

    var agendaTekst by remember { mutableStateOf("") }
    var agendaDatum by remember { mutableStateOf("2023-12-01") }
    var agendaTijd by remember { mutableStateOf("10:00") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Zorg & Agenda", style = MaterialTheme.typography.headlineMedium)

        Button(
            onClick = { viewModel.sendCommand("#AGENDA_VANDAAG") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Bekijk Agenda Vandaag")
        }

        if (state.agendaToday.isNotEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Agenda voor vandaag:", style = MaterialTheme.typography.titleMedium)
                    state.agendaToday.forEach { item ->
                        Text("• $item", style = MaterialTheme.typography.bodyMedium)
                    }
                    TextButton(
                        onClick = { viewModel.handleIncomingSms("#AGENDA_RES ") },
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
                OutlinedTextField(value = medicijnTijd, onValueChange = { medicijnTijd = it }, label = { Text("Tijd (bijv. 08:00)") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { viewModel.sendCommand("#MEDICIJN $medicijnTijd $medicijnNaam") }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                    Text("Opslaan")
                }
            }
        }

        // Voorraad
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Voorraad Bijwerken", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = voorraadNaam, onValueChange = { voorraadNaam = it }, label = { Text("Naam Pil/Item") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = voorraadAantal, onValueChange = { voorraadAantal = it }, label = { Text("Aantal") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { viewModel.sendCommand("#VOORRAAD $voorraadNaam $voorraadAantal") }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                    Text("Update Voorraad")
                }
            }
        }

        // Agenda Afspraak
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Nieuwe Afspraak", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = agendaTekst, onValueChange = { agendaTekst = it }, label = { Text("Omschrijving") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = agendaDatum, onValueChange = { agendaDatum = it }, label = { Text("Datum") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = agendaTijd, onValueChange = { agendaTijd = it }, label = { Text("Tijd") }, modifier = Modifier.weight(1f))
                }
                Button(onClick = { viewModel.sendCommand("#AGENDA $agendaDatum $agendaTijd $agendaTekst") }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                    Text("Inplannen")
                }
            }
        }
    }
}
