package com.example.memoryflipgame.presentation.Multiplayer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryflipgame.data.service.ClientSocketService
import com.example.memoryflipgame.data.service.CommunicationManager
import com.example.memoryflipgame.data.service.ServerSocketService
import kotlinx.coroutines.launch


class MultiplayerViewModel () : ViewModel() {
    var connectionEstablished by mutableStateOf(false)
        private set

    fun startAsServer() {
        viewModelScope.launch {
            val socket = ServerSocketService.startServer()
            connectionEstablished = socket != null
            if (socket != null) {
                CommunicationManager.listenMessages(socket) {
                    // handle incoming messages (e.g., update board state)
                }
            }
        }
    }

    fun connectAsClient(ip: String) {
        viewModelScope.launch {
            val socket = ClientSocketService.connectToServer(ip)
            connectionEstablished = socket != null
            if (socket != null) {
                CommunicationManager.listenMessages(socket) {
                    // handle incoming messages
                }
            }
        }
    }

    fun sendMove(move: String) {
        val socket = ServerSocketService.clientSocket ?: ClientSocketService.socket
        CommunicationManager.sendMessage(socket, move)
    }
}