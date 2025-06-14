package com.example.memoryflipgame.presentation.Multiplayer

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memoryflipgame.data.service.CommunicationManager

import com.example.memoryflipgame.domain.model.ConnectionState
import com.example.memoryflipgame.domain.model.GameEvent
import com.example.memoryflipgame.domain.model.Player
import com.example.memoryflipgame.domain.playerWin
import com.example.memoryflipgame.domain.sendGameEnded
import com.example.memoryflipgame.presentation.MemoryFlip.MemoryFlipGameScreen
import com.example.memoryflipgame.presentation.MemoryFlip.MemoryFlipGameViewModel


@Composable
fun ConnectSocketScreen(
    communicationManager: CommunicationManager,
    onNavigateTo: (String) -> Unit
) {

    val context = LocalContext.current
    var receivedMessage by remember { mutableStateOf("") }
    val onSendHello by remember { mutableStateOf(false) }
//    val communicationManager = remember { CommunicationManager() }
    var connected by remember { mutableStateOf(false) }

    val viewModel = remember { MemoryFlipGameViewModel(communicationManager) }
    val gameState by viewModel.uiState.collectAsState()

    LaunchedEffect(onSendHello) {
//        communicationManager.onMessageReceived = { message ->
//            receivedMessage = message.toString()
//            println("Received: $message")
//        }
        communicationManager.onConnectionStateChanged = { state ->
            if (state == ConnectionState.CONNECTED) {
                connected = true
            } else if (state == ConnectionState.ERROR || state == ConnectionState.DISCONNECTED) {
                connected = false
            }
        }
    }

    LaunchedEffect(gameState.isGameLoose) {
        if (gameState.isGameLoose) {
            Toast.makeText(context, "You Loose", Toast.LENGTH_SHORT).show()
        }
    }


    LaunchedEffect(
        gameState.isGameWon
    ) {
        if (gameState.isGameWon) {
            playerWin(
                score = 0,
                player= "Bhushan Wins",
                communicationManager = communicationManager
            )
        }
    }

    var isServer by remember { mutableStateOf(false) }
    var ip by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }

    if (!connected) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                isServer = true
                communicationManager.startServer()
            }) {
                Text("Host Game")
            }

            Spacer(Modifier.height(8.dp))

            TextField(value = ip, onValueChange = { ip = it }, label = { Text("Enter Host IP") })

            Button(onClick = {
                isServer = false
                communicationManager.connectToServer(ip)
            }) {
                Text("Join Game")
            }
        }
    } else {

//        viewModel.startMindFLipGame()
//        Button(
//            onClick = {
//                playerWin(
//                    player = "Bhushan Wins",
//                    score = 10,
//                    communicationManager = communicationManager
//                )
//            }
//        ) { }

        MemoryFlipGameScreen(
            gameState = gameState,
            onRetry = { viewModel.onRetry() },
            onStartGame = {viewModel.startMindFLipGame()},
            onFlipCard = { viewModel.onCardClicked(it) },
            onDismissDialog = { viewModel.onDismissDialog() }
        )
    }
}

//
//class GameNetworkHandler(private val communicationManager: CommunicationManager) {
//    var onGameEvent: ((GameEvent) -> Unit)? = null
//
//    init {
//        communicationManager.onMessageReceived = { event ->
//            onGameEvent?.invoke(event)
//        }
//    }
//}

