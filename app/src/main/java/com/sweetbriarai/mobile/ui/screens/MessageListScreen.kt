package com.sweetbriarai.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sweetbriarai.mobile.data.api.MobileMessage
import com.sweetbriarai.mobile.data.api.RetrofitClient
import com.sweetbriarai.mobile.data.auth.AuthManager
import com.sweetbriarai.mobile.data.repository.MessageRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessageListScreen(navController: NavController) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val scope = rememberCoroutineScope()

    var messages by remember { mutableStateOf<List<MobileMessage>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(isRefreshing, {
        scope.launch {
            isRefreshing = true
            try {
                val apiService = RetrofitClient.create(authManager)
                val repository = MessageRepository(authManager, apiService)
                val response = repository.getMessages()
                messages = response.messages
            } catch (e: Exception) {
                // Handle error
            } finally {
                isRefreshing = false
            }
        }
    })

    LaunchedEffect(Unit) {
        pullRefreshState.onRefresh()
    }

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        if (messages.isEmpty()) {
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
        PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
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