package com.sweetbriarai.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sweetbriarai.mobile.ui.screens.MessageDetailScreen
import com.sweetbriarai.mobile.ui.screens.MessageListScreen
import com.sweetbriarai.mobile.ui.screens.SettingsScreen

@Composable
fun AppNavigation(
    startDestination: String = "message_list",
    deepLinkMessageId: Int? = null
) {
    val navController = rememberNavController()

    // Handle deep link from notification tap
    LaunchedEffect(deepLinkMessageId) {
        if (deepLinkMessageId != null) {
            navController.navigate("message_detail/$deepLinkMessageId")
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("settings") {
            SettingsScreen(navController)
        }
        composable("message_list") {
            MessageListScreen(navController)
        }
        composable("message_detail/{messageId}") { backStackEntry ->
            val messageId = backStackEntry.arguments?.getString("messageId")?.toIntOrNull()
            if (messageId != null) {
                MessageDetailScreen(navController, messageId)
            }
        }
    }
}
