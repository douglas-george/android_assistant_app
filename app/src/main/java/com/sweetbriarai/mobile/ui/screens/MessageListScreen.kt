package com.sweetbriarai.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sweetbriarai.mobile.data.api.MobileMessage
import com.sweetbriarai.mobile.ui.viewmodel.MessageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageListScreen(navController: NavController, viewModel: MessageViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshMessages()
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshMessages() },
        modifier = Modifier.fillMaxSize()
    ) {
        if (messages.isEmpty() && !isRefreshing) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No messages")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(messages) { message ->
                    MessageCard(message) {
                        navController.navigate("message_detail/${message.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun MessageCard(message: MobileMessage, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(message.sender_label, style = MaterialTheme.typography.titleMedium)
            Text(message.message_text, style = MaterialTheme.typography.bodyMedium)
            Text(message.created_at, style = MaterialTheme.typography.bodySmall)
            if (message.response_value != null) {
                Text("Response: ${message.response_value}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
