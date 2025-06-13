package com.example.memoryflipgame.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ConnectException
import java.net.Socket
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

class ClientSocketManager(private val onMessageReceived: (String) -> Unit) {
    private var socket: Socket? = null
    private var output: PrintWriter? = null
    private var input: BufferedReader? = null
    private var isConnected = false
    private var connectionJob: Job? = null

    fun connectToServer(serverIP: String, port: Int = 8888) {
        connectionJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                println("Attempting to connect to $serverIP:$port")
                socket = Socket(serverIP, port)
                isConnected = true
                println("Connected to server at $serverIP:$port")

                input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                output = PrintWriter(socket!!.getOutputStream(), true)

                // Listen for messages
                while (isConnected && !socket!!.isClosed) {
                    try {
                        val message = input!!.readLine()
                        if (message != null) {
                            onMessageReceived(message)
                        } else {
                            break // Server closed connection
                        }
                    } catch (e: Exception) {
                        if (isConnected) {
                            println("Error reading message: ${e.message}")
                        }
                        break
                    }
                }
            } catch (e: ConnectException) {
                println("Connection refused: ${e.message}")
                println("Make sure the server is running on $serverIP:$port")
            } catch (e: SocketTimeoutException) {
                println("Connection timeout: ${e.message}")
            } catch (e: Exception) {
                println("Connection error: ${e.message}")
                e.printStackTrace()
            } finally {
                isConnected = false
                closeResources()
            }
        }
    }

    fun sendMessage(message: String) {
        if (!isConnected || output == null) {
            println("Cannot send message: Not connected to server")
            return
        }

        try {
            output!!.println(message)
            println("Message sent: $message")
        } catch (e: Exception) {
            println("Error sending message: ${e.message}")
        }
    }

    private fun closeResources() {
        try {
            input?.close()
            output?.close()
            socket?.close()
        } catch (e: Exception) {
            println("Error closing resources: ${e.message}")
        }
    }

    fun close() {
        isConnected = false
        connectionJob?.cancel()
        closeResources()
    }

    fun isConnected(): Boolean = isConnected
}