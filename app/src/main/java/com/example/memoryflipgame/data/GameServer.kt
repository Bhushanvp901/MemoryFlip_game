package com.example.memoryflipgame.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.BindException
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread


class ServerSocketManager(private val onMessageReceived: (String) -> Unit) {
    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var output: PrintWriter? = null
    private var input: BufferedReader? = null
    private var isRunning = false
    private var serverJob: Job? = null

    fun startServer(port: Int = 8888) {
        serverJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                serverSocket = ServerSocket(port)
                isRunning = true
                println("Server started on port $port. Waiting for client...")

                clientSocket = serverSocket!!.accept()
                println("Client connected from: ${clientSocket!!.remoteSocketAddress}")

                input = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
                output = PrintWriter(clientSocket!!.getOutputStream(), true)

                // Listen for messages
                while (isRunning && !clientSocket!!.isClosed) {
                    try {
                        val message = input!!.readLine()
                        if (message != null) {
                            onMessageReceived(message)
                        } else {
                            break // Client closed connection
                        }
                    } catch (e: Exception) {
                        if (isRunning) {
                            println("Error reading message: ${e.message}")
                        }
                        break
                    }
                }
            } catch (e: BindException) {
                println("Port $port is already in use: ${e.message}")
            } catch (e: Exception) {
                println("Server error: ${e.message}")
                e.printStackTrace()
            } finally {
                isRunning = false
                closeResources()
            }
        }
    }

    fun sendMessage(message: String) {
        if (!isRunning || output == null || clientSocket?.isClosed == true) {
            println("Cannot send message: No client connected")
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
            clientSocket?.close()
            serverSocket?.close()
        } catch (e: Exception) {
            println("Error closing resources: ${e.message}")
        }
    }

    fun close() {
        isRunning = false
        serverJob?.cancel()
        closeResources()
    }

    fun isRunning(): Boolean = isRunning
    fun hasClient(): Boolean = clientSocket != null && !clientSocket!!.isClosed
}
