package com.example.memoryflipgame.data.service

import com.example.memoryflipgame.domain.model.ConnectionState
import com.example.memoryflipgame.domain.model.GameEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import java.io.BufferedReader
import java.io.BufferedWriter
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean


class CommunicationManager{

    private var clientSocket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null
    private var job: Job? = null
    val isConnected = AtomicBoolean(false)

    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        serializersModule = SerializersModule {
            polymorphic(GameEvent::class) {
                subclass(GameEvent.CardFlipped::class)
            }
        }
    }

    var onConnectionStateChanged: ((ConnectionState) -> Unit)? = null
    var onMessageReceived: ((GameEvent) -> Unit)? = null
    var onError: ((Exception) -> Unit)? = null

    fun startServer(port: Int = 9999): Boolean {
        return try {
            onConnectionStateChanged?.invoke(ConnectionState.CONNECTING)
            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    serverSocket = ServerSocket(port)
                    println("Server started on port $port, waiting for connections...")

                    clientSocket = serverSocket!!.accept()
                    println("Client connected: ${clientSocket!!.remoteSocketAddress}")

                    setupStreams()
                    isConnected.set(true)
                    onConnectionStateChanged?.invoke(ConnectionState.CONNECTED)
//                    startHeartbeat()

                } catch (e: Exception) {
                    handleError(e)
                }
            }
            true
        } catch (e: Exception) {
            handleError(e)
            false
        }
    }

    fun connectToServer(ip: String, port: Int = 9999): Boolean {
        return try {
            onConnectionStateChanged?.invoke(ConnectionState.CONNECTING)

            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    clientSocket = Socket(ip, port)
                    println("Connected to server: $ip:$port")

                    setupStreams()
                    isConnected.set(true)
                    onConnectionStateChanged?.invoke(ConnectionState.CONNECTED)
//                    startHeartbeat()

                } catch (e: Exception) {
                    handleError(e)
                }
            }
            true
        } catch (e: Exception) {
            handleError(e)
            false
        }
    }

    private fun setupStreams() {
        try {
            reader = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
            writer = BufferedWriter(OutputStreamWriter(clientSocket!!.getOutputStream()))
            listenForMessages()
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun handleError(exception: Exception) {
        println("Communication error: ${exception.message}")
        exception.printStackTrace()
        isConnected.set(false)
        onConnectionStateChanged?.invoke(ConnectionState.ERROR)
        onError?.invoke(exception)
    }

    private fun listenForMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                while (isConnected.get() && !Thread.currentThread().isInterrupted) {
                    val message = reader?.readLine()
                    if (message.isNullOrEmpty()) {
                        println("Received null/empty message, connection might be closed")
                        break
                    }

                    try {
                        val gameEvent = json.decodeFromString<GameEvent>(message)

                        // Handle ping/pong internally
                        when (gameEvent) {
//                            is GameEvent.Ping -> {
//                                sendMessage(GameEvent.Pong())
//                            }
//                            is GameEvent.Pong -> {
//                                // Connection is alive
//                            }
                            else -> {
                                withContext(Dispatchers.Main) {
                                    onMessageReceived?.invoke(gameEvent)
                                }
                            }
                        }
                    } catch (e: SerializationException) {
                        println("Failed to parse message: $message")
                        onError?.invoke(e)
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                disconnect()
            }
        }
    }


    fun sendMessage(event: GameEvent): Boolean {
        if (!isConnected.get()) {
            println("Cannot send message: not connected")
            return false
        }

        return try {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val jsonMessage = json.encodeToString(event)
                    writer?.write(jsonMessage + "\n")
                    writer?.flush()
                } catch (e: Exception) {
                    handleError(e)
                }
            }
            true
        } catch (e: Exception) {
            handleError(e)
            false
        }
    }

    fun disconnect() {
        isConnected.set(false)

        job?.cancel()
//        heartbeatJob?.cancel()

        try {
            reader?.close()
            writer?.close()
            clientSocket?.close()
            serverSocket?.close()
        } catch (e: Exception) {
            println("Error during disconnect: ${e.message}")
        }

        reader = null
        writer = null
        clientSocket = null
        serverSocket = null

        onConnectionStateChanged?.invoke(ConnectionState.DISCONNECTED)
    }
}