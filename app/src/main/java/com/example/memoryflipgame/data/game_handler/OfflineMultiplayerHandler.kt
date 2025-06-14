package com.example.memoryflipgame.data.game_handler

import com.example.memoryflipgame.data.service.CommunicationManager
import com.example.memoryflipgame.domain.GameEngine
import com.example.memoryflipgame.domain.GameModeHandler
import com.example.memoryflipgame.domain.GameUIState
import com.example.memoryflipgame.domain.model.GameEvent
import com.example.memoryflipgame.domain.model.MemoryCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OfflineMultiplayerHandler(
    private val isHost: Boolean,
    private val communicator: CommunicationManager
) : GameModeHandler {
    private val engine = GameEngine(maxTime = 180)
    private val _uiState = MutableStateFlow(GameUIState())
    override val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    private var currentTurn = if (isHost) "Player 1" else "Player 2"

    init {
        observeEngineUpdates()
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

    override fun handleCardClick(card: MemoryCard) {
        engine.onCardClicked(card)
    }

    fun onReceiveEvent(event: GameEvent) {
        when (event) {
            is GameEvent.GameEnded -> {

            }
            is GameEvent.GameStarted -> {}
            is GameEvent.PlayerJoined -> {}
            is GameEvent.PlayerWon -> {
                _uiState.value = _uiState.value.copy(isGameLoose = true)
            }
        }
    }

    private fun observeEngineUpdates() {
        CoroutineScope(Dispatchers.Default).launch {
            engine.uiState.collect { state ->
                _uiState.value = state
                if (state.isGameWon) {
                    communicator.sendMessage(
                        GameEvent.PlayerWon(
                            player = "Bhushan",
                            score = uiState.value.currentScore
                        )
                    )
                }
            }
        }
    }

    fun getCurrentPlayer(): String = currentTurn
}
