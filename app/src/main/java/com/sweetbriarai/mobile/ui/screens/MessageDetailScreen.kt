package com.sweetbriarai.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sweetbriarai.mobile.ui.viewmodel.MessageViewModel

@Composable
fun MessageDetailScreen(
    navController: NavController,
    messageId: Int,
    viewModel: MessageViewModel = viewModel()
) {
    val message by viewModel.selectedMessage.collectAsState()
    val isResponding by viewModel.isResponding.collectAsState()

    LaunchedEffect(messageId) {
        viewModel.loadMessage(messageId)
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

            if (msg.response_value != null) {
                Text("Responded: ${msg.response_value}", style = MaterialTheme.typography.bodyMedium)
            } else {
                when (msg.message_type) {
                    "yes_no" -> {
                        Row {
                            Button(
                                onClick = { viewModel.respondToMessage(messageId, "yes") },
                                enabled = !isResponding,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Yes")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.respondToMessage(messageId, "no") },
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
                                onClick = { viewModel.respondToMessage(messageId, option) },
                                enabled = !isResponding,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(option)
                            }
                        }
                    }
                }
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
