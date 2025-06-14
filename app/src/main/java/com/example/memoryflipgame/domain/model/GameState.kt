package com.example.memoryflipgame.domain.model

data class GameState(
    val players: List<Player> = emptyList(),
    val currentPlayerId: String = "",
    val gameBoard: List<String> = emptyList(),
    val flippedCards: List<Int> = emptyList(),
    val matchedCards: List<Int> = emptyList(),
    val winner: Player? = null
)



