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
import com.example.memoryflipgame.domain.model.GameEvent
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
    val communicationManager = remember { CommunicationManager() }

    val viewModel = remember { MemoryFlipGameViewModel(communicationManager) }
    val gameState by viewModel.uiState.collectAsState()



    NavHost(navController = navController, startDestination = "socket") {

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
                onStartGame = {viewModel.startMindFLipGame()},
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


