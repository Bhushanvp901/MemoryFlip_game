package com.example.memoryflipgame.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.memoryflipgame.data.service.CommunicationManager
import com.example.memoryflipgame.presentation.Home.HomeScreen
import com.example.memoryflipgame.presentation.MemoryFlip.MemoryFlipGameScreen
import com.example.memoryflipgame.presentation.MemoryFlip.MemoryFlipGameViewModel
import com.example.memoryflipgame.presentation.Multiplayer.ConnectSocketScreen

@Composable
fun Navigation() {

    fun handleMessage(message: String) {
        println("Received: $message")
    }

    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = remember { MemoryFlipGameViewModel() }
    val gameState by viewModel.uiState.collectAsState()

    val communicationManager = remember { CommunicationManager() }


    NavHost(navController = navController, startDestination = "game") {

        composable("home") {
            HomeScreen() {
                navController.navigate(it)
            }
        }

        composable("game") {
            MemoryFlipGameScreen(
                gameState = gameState,
                onRetry = { viewModel.onRetry() },
                onFlipCard = { viewModel.onCardClicked(it) },
                onDismissDialog = { viewModel.onDismissDialog() }
            )
        }


        composable("socket") {
            ConnectSocketScreen(communicationManager) {
                navController.navigate(it)
            }
        }

    }

}