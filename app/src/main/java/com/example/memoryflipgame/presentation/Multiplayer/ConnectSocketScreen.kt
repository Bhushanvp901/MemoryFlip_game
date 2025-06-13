package com.example.memoryflipgame.presentation.Multiplayer

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memoryflipgame.data.service.CommunicationManager

import com.example.memoryflipgame.domain.model.ConnectionState
import com.example.memoryflipgame.domain.model.GameEvent


@Composable
fun ConnectSocketScreen(communicationManager: CommunicationManager,onNavigateTo:(String)->Unit) {

    var receivedMessage by remember { mutableStateOf("") }
    var onSendHello by remember { mutableStateOf(false) }
//    val communicationManager = remember { CommunicationManager() }
    var connected by remember { mutableStateOf(false) }


    LaunchedEffect(onSendHello) {
        communicationManager.onMessageReceived = { message ->
            receivedMessage = message.toString()
            println("Received: $message")
        }

        communicationManager.onConnectionStateChanged = { state ->
            if (state == ConnectionState.CONNECTED) {
                connected = true
            } else if (state == ConnectionState.ERROR || state == ConnectionState.DISCONNECTED) {
                connected = false
            }
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

        onNavigateTo("game")
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text("Connected 🎉", fontSize = 20.sp)
//            TextField(value = msg, onValueChange = { msg = it }, label = { Text("Enter Host IP") })
//            Button(onClick = {
//                onSendHello = true
//                communicationManager.sendMessage(event = GameEvent.CardFlipped(msg,1,"5"))
//            }) {
//                Text("Join Game")
//            }
//            Text("Received: $receivedMessage")
//        }

    }
}


