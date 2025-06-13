package com.example.memoryflipgame.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

object ServerSocketService {
    private var serverSocket: ServerSocket? = null
    var clientSocket: Socket? = null

    suspend fun startServer(port: Int = 8888): Socket? = withContext(Dispatchers.IO) {
        try {
            serverSocket = ServerSocket(port)
            println("Waiting for client connection...")
            clientSocket = serverSocket!!.accept()
            println("Client connected: ${clientSocket!!.inetAddress}")
            clientSocket
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun stopServer() {
        serverSocket?.close()
        clientSocket?.close()
    }
}