package com.example.memoryflipgame.presentation.Multiplayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memoryflipgame.data.ClientSocketManager
import com.example.memoryflipgame.data.ClientSocketService
import com.example.memoryflipgame.data.CommunicationManager
import com.example.memoryflipgame.data.MultiplayerViewModel
import com.example.memoryflipgame.data.ServerSocketManager
import com.example.memoryflipgame.data.ServerSocketService
import network.chaintech.sdpcomposemultiplatform.sdp


@Composable
fun ConnectSocketScreen(serverManager: ServerSocketManager, clientManager: ClientSocketManager) {

    var receivedMessage by remember { mutableStateOf("") }
    var onSendHello by remember { mutableStateOf(false) }

    LaunchedEffect(onSendHello) {
        val socket = ServerSocketService.clientSocket ?: ClientSocketService.socket

        if (onSendHello) {
            CommunicationManager.listenMessages(socket) { message ->
                receivedMessage = message
                println("Received: $message") // Optional: log in Logcat
            }
        }

    }



    val viewModel = remember { MultiplayerViewModel() }

    var isServer by remember { mutableStateOf(false) }
    var ip by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }

    if (!viewModel.connectionEstablished) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Button(onClick = {
                isServer = true
                viewModel.startAsServer()
            }) {
                Text("Host Game")
            }

            Spacer(Modifier.height(8.dp))

            TextField(value = ip, onValueChange = { ip = it }, label = { Text("Enter Host IP") })

            Button(onClick = {
                isServer = false
                viewModel.connectAsClient(ip)
            }) {
                Text("Join Game")
            }


        }
    } else {
        Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Text("Connected 🎉", fontSize = 20.sp)
            TextField(value = msg, onValueChange = { msg = it }, label = { Text("Enter Host IP") })
            Button(onClick = {
                onSendHello = true
                sendHello(msg)
            }) {
                Text("Join Game")
            }
            Text("Received: $receivedMessage")
        }

        // Display your game here and use viewModel.sendMove("...") to communicate moves
    }
}

fun sendHello(message:String) {
    val socket = ServerSocketService.clientSocket ?: ClientSocketService.socket
    CommunicationManager.sendMessage(socket, message)
}
