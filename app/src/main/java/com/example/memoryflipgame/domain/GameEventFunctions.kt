package com.example.memoryflipgame.domain

import com.example.memoryflipgame.data.service.CommunicationManager
import com.example.memoryflipgame.domain.model.GameEvent
import com.example.memoryflipgame.domain.model.Player

fun sendGameEnded(winner: Player?,  reason: String = "Game completed",communicationManager: CommunicationManager) {
    communicationManager.sendMessage(GameEvent.GameEnded(winner, reason))
}