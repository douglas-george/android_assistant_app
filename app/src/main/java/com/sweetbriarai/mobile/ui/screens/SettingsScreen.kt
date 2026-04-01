package com.sweetbriarai.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sweetbriarai.mobile.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val isRegistering by viewModel.isRegistering.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val registrationComplete by viewModel.registrationComplete.collectAsState()

    var apiUrl by remember { mutableStateOf(viewModel.authManager.apiUrl) }
    var bearerToken by remember { mutableStateOf(viewModel.authManager.bearerToken) }

    LaunchedEffect(registrationComplete) {
        if (registrationComplete) {
            navController.navigate("message_list") {
                popUpTo("settings") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = apiUrl,
            onValueChange = { apiUrl = it },
            label = { Text("API URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = bearerToken,
            onValueChange = { bearerToken = it },
            label = { Text("Bearer Token") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.testConnection(apiUrl, bearerToken) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Connection")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.registerDevice(apiUrl, bearerToken) },
            enabled = !isRegistering,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRegistering) "Registering..." else "Register Device")
        }

        statusMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                it,
                color = if (it.contains("failed")) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            )
        }
    }
}
