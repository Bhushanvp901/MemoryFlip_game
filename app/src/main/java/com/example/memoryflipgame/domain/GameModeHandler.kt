package com.example.memoryflipgame.domain

import com.example.memoryflipgame.domain.model.MemoryCard
import kotlinx.coroutines.flow.StateFlow

interface GameModeHandler {
    val uiState: StateFlow<GameUIState>
    fun handleCardClick(card: MemoryCard)
    fun startGame()
    fun retryGame()
    fun dismissDialog()
}
