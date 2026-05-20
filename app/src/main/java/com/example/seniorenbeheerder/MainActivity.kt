package com.example.seniorenbeheerder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import com.example.seniorenbeheerder.ui.DashboardScreen
import com.example.seniorenbeheerder.ui.SafetyScreen
import com.example.seniorenbeheerder.ui.SettingsScreen
import com.example.seniorenbeheerder.ui.ZorgAgendaScreen
import com.example.seniorenbeheerder.ui.theme.SeniorenBeheerderTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: SeniorenViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = SeniorenViewModel(this)
        
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                Configuration.getInstance().userAgentValue = "SeniorenBeheerder/1.0"
                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            }
            SeniorenBeheerderTheme {
                SetupHandler(viewModel)
                SmsListener(viewModel)
                SeniorenBeheerderApp(viewModel)
            }
        }
    }
}

@Composable
fun SetupHandler(viewModel: SeniorenViewModel) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showPhoneSetupDialog by remember { mutableStateOf(false) }

    val requiredPermissions = remember {
        mutableListOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(context, "Machtigingen zijn essentieel voor de werking van de app.", Toast.LENGTH_LONG).show()
        }
        // Check phone number after permissions
        if (viewModel.state.phoneNumber.isEmpty()) {
            showPhoneSetupDialog = true
        }
    }

    LaunchedEffect(Unit) {
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            showPermissionDialog = true
        } else if (viewModel.state.phoneNumber.isEmpty()) {
            showPhoneSetupDialog = true
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Machtigingen Nodig", fontWeight = FontWeight.Bold) },
            text = { Text("Voor een goede werking heeft deze app toegang nodig tot SMS (lezen/verzenden) en meldingen. Dit is nodig om de senior op afstand te beheren.") },
            confirmButton = {
                Button(onClick = {
                    launcher.launch(requiredPermissions.toTypedArray())
                    showPermissionDialog = false
                }) {
                    Text("Machtigingen Geven")
                }
            }
        )
    }

    if (showPhoneSetupDialog) {
        var tempNumber by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { /* Don't dismiss without action */ },
            title = { Text("Telefoonnummer Instellen", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Voer het telefoonnummer in van de senior die u wilt beheren.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = tempNumber,
                        onValueChange = { tempNumber = it },
                        label = { Text("Telefoonnummer") },
                        placeholder = { Text("+316...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempNumber.isNotEmpty()) {
                            viewModel.updatePhoneNumber(tempNumber)
                            showPhoneSetupDialog = false
                        } else {
                            Toast.makeText(context, "Voer een geldig nummer in", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Opslaan")
                }
            }
        )
    }
}

@Composable
fun SmsListener(viewModel: SeniorenViewModel) {
    val context = LocalContext.current
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("MainActivity", "Received SMS_UPDATED broadcast")
                val body = intent?.getStringExtra("body") ?: ""
                viewModel.handleIncomingSms(body)
            }
        }
        val filter = IntentFilter("com.example.seniorenbeheerder.SMS_UPDATED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}

@Composable
fun SeniorenBeheerderApp(viewModel: SeniorenViewModel) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppDestinations.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (currentDestination) {
            AppDestinations.DASHBOARD -> DashboardScreen(viewModel, modifier)
            AppDestinations.ZORG -> ZorgAgendaScreen(viewModel, modifier)
            AppDestinations.INSTELLINGEN -> SettingsScreen(viewModel, modifier)
            AppDestinations.VEILIGHEID -> SafetyScreen(viewModel, modifier)
        }
    }
}

enum class AppDestinations(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Home),
    ZORG("Zorg", Icons.Default.DateRange),
    INSTELLINGEN("Instellingen", Icons.Default.Settings),
    VEILIGHEID("Veiligheid", Icons.Default.Lock)
}
