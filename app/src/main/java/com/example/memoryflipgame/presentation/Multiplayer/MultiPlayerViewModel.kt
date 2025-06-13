package com.example.memoryflipgame.presentation.Multiplayer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memoryflipgame.data.service.ClientSocketService
import com.example.memoryflipgame.data.service.CommunicationManager
import com.example.memoryflipgame.data.service.ServerSocketService
import kotlinx.coroutines.launch


//class MultiplayerViewModel () : ViewModel() {
//    var connectionEstablished by mutableStateOf(false)
//        private set
//
//    val communicationManager = CommunicationManager()
//
//    fun startAsServer() {
//        viewModelScope.launch {
//            val socket = ServerSocketService.startServer()
//            connectionEstablished = socket != null
//            if (socket != null) {
//                communicationManager.listenMessages(socket) {
//                    println("Received messageV $it")
//                }
//            }
//        }
//    }
//
//    fun connectAsClient(ip: String) {
//        viewModelScope.launch {
//            val socket = ClientSocketService.connectToServer(ip)
//            connectionEstablished = socket != null
//            if (socket != null) {
//                communicationManager.listenMessages(socket) {
//                    // handle incoming messages
//                }
//            }
//        }
//    }
//
//    fun sendMessage(){
//        viewModelScope.launch {  }c()
//    }
//}