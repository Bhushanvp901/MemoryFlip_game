package com.example.memoryflipgame.presentation.MemoryFlip

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryflipgame.R
import com.example.memoryflipgame.data.SinglePlayerHandler
import com.example.memoryflipgame.domain.GameModeHandler
import com.example.memoryflipgame.domain.GameUIState
import com.example.memoryflipgame.domain.model.MemoryCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MemoryFlipGameViewModel(
) : ViewModel() {


    private val handler: GameModeHandler = SinglePlayerHandler()
    var uiState: StateFlow<GameUIState> = handler.uiState

    init {
        startMindFLipGame()
    }

    private fun startMindFLipGame() {
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