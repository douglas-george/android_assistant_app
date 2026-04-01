package com.sweetbriarai.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sweetbriarai.mobile.ui.screens.MessageDetailScreen
import com.sweetbriarai.mobile.ui.screens.MessageListScreen
import com.sweetbriarai.mobile.ui.screens.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "message_list") {
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