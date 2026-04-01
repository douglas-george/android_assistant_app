package com.sweetbriarai.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sweetbriarai.mobile.data.api.RetrofitClient
import com.sweetbriarai.mobile.data.auth.AuthManager
import com.sweetbriarai.mobile.data.repository.MessageRepository
import kotlinx.coroutines.launch

@Composable
fun MessageDetailScreen(navController: NavController, messageId: Int) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val scope = rememberCoroutineScope()

    var message by remember { mutableStateOf<com.sweetbriarai.mobile.data.api.MobileMessage?>(null) }
    var isResponding by remember { mutableStateOf(false) }

    LaunchedEffect(messageId) {
        try {
            val apiService = RetrofitClient.create(authManager)
            val repository = MessageRepository(authManager, apiService)
            message = repository.getMessage(messageId)
        } catch (e: Exception) {
            // Handle error
        }
    }

    message?.let { msg ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(msg.sender_label, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(msg.message_text, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            when (msg.message_type) {
                "yes_no" -> {
                    Row {
                        Button(
                            onClick = { respond("yes") },
                            enabled = !isResponding,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Yes")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { respond("no") },
                            enabled = !isResponding,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("No")
                        }
                    }
                }
                "choice" -> {
                    msg.response_options?.forEach { option ->
                        Button(
                            onClick = { respond(option) },
                            enabled = !isResponding,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(option)
                        }
                    }
                }
                "info" -> {
                    // No actions
                }
            }

            if (msg.response_value != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Responded: ${msg.response_value}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    fun respond(response: String) {
        scope.launch {
            isResponding = true
            try {
                val apiService = RetrofitClient.create(authManager)
                val repository = MessageRepository(authManager, apiService)
                repository.respondToMessage(messageId, response)
                message = message?.copy(response_value = response)
            } catch (e: Exception) {
                // Handle error
            } finally {
                isResponding = false
            }
        }
    }
}