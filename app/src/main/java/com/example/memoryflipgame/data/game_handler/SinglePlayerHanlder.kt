package com.example.memoryflipgame.data.game_handler

import com.example.memoryflipgame.domain.GameEngine
import com.example.memoryflipgame.domain.GameModeHandler
import com.example.memoryflipgame.domain.GameUIState
import com.example.memoryflipgame.domain.model.MemoryCard
import kotlinx.coroutines.flow.StateFlow

class SinglePlayerHandler : GameModeHandler {

    private val engine = GameEngine()
    override val uiState: StateFlow<GameUIState> = engine.uiState

    override fun handleCardClick(card: MemoryCard) {
        engine.onCardClicked(card)
    }

    override fun startGame() {
        engine.startGame()
    }

    override fun retryGame() {
        engine.retryGame()
    }

    override fun dismissDialog() {
        engine.dismissDialog()
    }
}
