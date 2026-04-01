package com.sweetbriarai.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.messaging.FirebaseMessaging
import com.sweetbriarai.mobile.data.api.RegisterDeviceRequest
import com.sweetbriarai.mobile.data.api.RetrofitClient
import com.sweetbriarai.mobile.data.auth.AuthManager
import com.sweetbriarai.mobile.data.repository.MessageRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val scope = rememberCoroutineScope()

    var apiUrl by remember { mutableStateOf(authManager.apiUrl) }
    var bearerToken by remember { mutableStateOf(authManager.bearerToken) }
    var isRegistering by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

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
            onClick = {
                scope.launch {
                    try {
                        statusMessage = "Testing connection..."
                        authManager.apiUrl = apiUrl
                        authManager.bearerToken = bearerToken
                        val apiService = RetrofitClient.create(authManager)
                        val repository = MessageRepository(authManager, apiService)
                        repository.health()
                        statusMessage = "Connection successful!"
                    } catch (e: Exception) {
                        statusMessage = "Connection failed: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Connection")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    isRegistering = true
                    statusMessage = null
                    try {
                        authManager.apiUrl = apiUrl
                        authManager.bearerToken = bearerToken
                        val fcmToken = FirebaseMessaging.getInstance().token.await()
                        val request = RegisterDeviceRequest(
                            device_name = android.os.Build.MODEL,
                            fcm_token = fcmToken,
                            app_version = "1.0"
                        )
                        val apiService = RetrofitClient.create(authManager)
                        val repository = MessageRepository(authManager, apiService)
                        val response = repository.registerDevice(request)
                        authManager.deviceId = response.device_id
                        statusMessage = "Device registered successfully!"
                        navController.navigate("message_list")
                    } catch (e: Exception) {
                        statusMessage = "Registration failed: ${e.message}"
                    } finally {
                        isRegistering = false
                    }
                }
            },
            enabled = !isRegistering,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRegistering) "Registering..." else "Register Device")
        }

        statusMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = if (it.contains("failed")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        }
    }
}