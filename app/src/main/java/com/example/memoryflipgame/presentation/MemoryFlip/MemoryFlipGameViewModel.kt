package com.example.memoryflipgame.presentation.MemoryFlip

import androidx.lifecycle.ViewModel
import com.example.memoryflipgame.data.game_handler.OfflineMultiplayerHandler
import com.example.memoryflipgame.data.game_handler.SinglePlayerHandler
import com.example.memoryflipgame.data.service.CommunicationManager
import com.example.memoryflipgame.domain.GameModeHandler
import com.example.memoryflipgame.domain.GameUIState
import com.example.memoryflipgame.domain.model.MemoryCard
import kotlinx.coroutines.flow.StateFlow

class MemoryFlipGameViewModel(
    communicationManager: CommunicationManager
) : ViewModel() {


//    private val handler: GameModeHandler = SinglePlayerHandler()
    private val handler = OfflineMultiplayerHandler(false,communicationManager)
    var uiState: StateFlow<GameUIState> = handler.uiState

    init {
//        startMindFLipGame()
        communicationManager.onMessageReceived = { event ->
            handler.onReceiveEvent(event)
        }
    }

    fun startMindFLipGame() {
        handler.startGame()
    }

    fun onCardClicked(card: MemoryCard) {
        handler.handleCardClick(card)
    }

    fun onDismissDialog() {
        handler.dismissDialog()
    }

    fun onRetry() {
        handler.retryGame()
    }

}