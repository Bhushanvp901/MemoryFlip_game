package com.example.memoryflipgame.data.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

object CommunicationManager {
    fun sendMessage(socket: Socket?, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                socket?.getOutputStream()?.write((message + "\n").toByteArray())
                socket?.getOutputStream()?.flush()
            } catch (e: Exception) {
                println("Exception $e")
                e.printStackTrace()
            }
        }
    }

    fun listenMessages(socket: Socket?, onMessage: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
                while (true) {
                    val message = reader.readLine() ?: break
                    onMessage(message)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
