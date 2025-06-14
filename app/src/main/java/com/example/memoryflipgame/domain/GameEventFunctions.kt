package com.example.memoryflipgame.domain

import com.example.memoryflipgame.data.service.CommunicationManager
import com.example.memoryflipgame.domain.model.GameEvent
import com.example.memoryflipgame.domain.model.Player

fun sendGameEnded(
    winner: Player?,
    finalScore: List<Player>,
    reason: String = "Game completed",
    communicationManager: CommunicationManager
) {
    communicationManager.sendMessage(GameEvent.GameEnded(winner, finalScore, reason))
}

fun playerWin(
    player: String,
    score: Int,
    communicationManager: CommunicationManager
) {
    communicationManager.sendMessage(GameEvent.PlayerWon(player, score))
}