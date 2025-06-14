package com.example.memoryflipgame.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class GameEvent {
    @Serializable
    @SerialName("player_joined")
    data class PlayerJoined(
        val playerId: String,
        val playerName: String,
        val totalPlayers: Int
    ) : GameEvent()

    @Serializable
    @SerialName("game_started")
    data class GameStarted(
        val gameBoard: List<String>,
        val currentPlayerId: String
    ) : GameEvent()

    @Serializable
    @SerialName("game_ended")
    data class GameEnded(
        val winner: Player?,
        val finalScores: List<Player>,
        val reason: String = "Game completed"
    ) : GameEvent()

    @Serializable
    @SerialName("player")
    data class PlayerWon(val player: String, val score: Int) : GameEvent()

}

@Serializable
data class Player(
    val id: String,
    val name: String,
    val score: Int = 0,
    val isConnected: Boolean = true
)

enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, ERROR
}