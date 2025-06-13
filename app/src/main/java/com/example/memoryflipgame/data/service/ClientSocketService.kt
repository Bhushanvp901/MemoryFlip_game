package com.example.memoryflipgame.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.Socket

object ClientSocketService {
    var socket: Socket? = null

    suspend fun connectToServer(ip: String, port: Int = 8888): Socket? = withContext(Dispatchers.IO) {
        try {
            socket = Socket(ip, port)
            println("Connected to server")
            socket
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun closeConnection() {
        socket?.close()
    }
}
