package com.example.seniorenbeheerder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
            SeniorenBeheerderTheme {
                SmsListener(viewModel)
                SeniorenBeheerderApp(viewModel)
            }
        }
    }
}

@Composable
fun SmsListener(viewModel: SeniorenViewModel) {
    val context = LocalContext.current
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val body = intent?.getStringExtra("body") ?: ""
                viewModel.handleIncomingSms(body)
            }
        }
        val filter = IntentFilter("com.example.seniorenbeheerder.SMS_UPDATED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
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
