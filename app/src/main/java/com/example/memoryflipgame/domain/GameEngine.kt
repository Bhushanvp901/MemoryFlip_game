package com.example.memoryflipgame.domain

import com.example.memoryflipgame.domain.model.MemoryCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameEngine(private val pairCount: Int = 10, private val maxTime: Int = 120) {

    private var firstCard: MemoryCard? = null
    private var matchedCardCount = 0
    private var flipAttempts = 0
    private var _cards: List<MemoryCard> = emptyList()
    private var isBusy = false

    private val _uiState = MutableStateFlow(GameUIState())
    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    private var timerScope: CoroutineScope? = null


    fun startGame() {
        val allImages = (1..20).toList()
        val selectedCards = allImages.shuffled().take(pairCount)

        val cardPairs = selectedCards.flatMap {
            listOf(
                MemoryCard(id = it * 1000, image = it.toString()),
                MemoryCard(id = it * 1000 + 1, image = it.toString())
            )
        }.shuffled()

        _cards = cardPairs
        matchedCardCount = 0
        flipAttempts = 0
        firstCard = null

        _uiState.value = GameUIState(
            cards = _cards,
            isGameWon = false,
            isGameLoose = false,
            currentScore = 0,
            attempts = 0,
            time = maxTime,
            pairCount = pairCount,
            lastMatchSuccessful = null
        )

        startTimer()
    }

    private fun startTimer() {
        timerScope?.let { return }

        timerScope = CoroutineScope(Dispatchers.Default)
        timerScope?.launch {
            while (_uiState.value.time > 0 && !_uiState.value.isGameWon) {
                delay(1000)
                val newTime = _uiState.value.time - 1
                val isLoose = newTime <= 0 && !_uiState.value.isGameWon

                _uiState.value = _uiState.value.copy(
                    time = newTime,
                    isGameLoose = isLoose
                )
            }
        }
    }


    fun retryGame() {
        timerScope = null
        startGame()
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(
            isGameWon = false,
            isGameLoose = false
        )
    }


    fun onCardClicked(card: MemoryCard) {
        if (isBusy || card.isFaceUp || card.isMatched || uiState.value.isGameLoose) return

        val index = _cards.indexOf(card)
        if (index == -1) return

        val updatedCards = _cards.toMutableList()
        updatedCards[index] = card.copy(isFaceUp = true)
        _cards = updatedCards

        if (firstCard == null) {
            firstCard = updatedCards[index]
            _uiState.value = _uiState.value.copy(
                cards = _cards,
                lastMatchSuccessful = null,
                flipCard = true
            )
        } else {
            isBusy = true
            val secondCard = updatedCards[index]
            val firstIndex = updatedCards.indexOfFirst { it.id == firstCard?.id }

            _uiState.value = _uiState.value.copy(cards = _cards)

            CoroutineScope(Dispatchers.Default).launch {
                delay(500)

                val isMatch = firstCard?.image == secondCard.image

                if (isMatch) {
                    updatedCards[firstIndex] = updatedCards[firstIndex].copy(isMatched = true)
                    updatedCards[index] = secondCard.copy(isMatched = true)
                    matchedCardCount++
                } else {
                    updatedCards[firstIndex] = updatedCards[firstIndex].copy(isFaceUp = false)
                    updatedCards[index] = secondCard.copy(isFaceUp = false)
                    if (flipAttempts <= pairCount * 2) flipAttempts++
                    checkGameStatus(flipAttempts)
                }

                _cards = updatedCards
                firstCard = null

                val isGameWon = matchedCardCount >= pairCount
                val score = calculateScore()

                _uiState.value = _uiState.value.copy(
                    cards = _cards,
                    isGameWon = isGameWon,
                    currentScore = score,
                    attempts = flipAttempts,
                    lastMatchSuccessful = isMatch,
                    isCardMatch = isMatch,
                    flipCard = false
                )
                isBusy = false
            }
        }
    }

    private fun calculateScore(): Int = (matchedCardCount * 100) - (flipAttempts * 10)
    private fun checkGameStatus(flipAttempts: Int) {
        if (flipAttempts == uiState.value.pairCount * 2) {
            _uiState.value = _uiState.value.copy(
                isGameWon = false,
                isGameLoose = true
            )
        }
    }
}


data class GameUIState(
    val cards: List<MemoryCard> = emptyList(),
    val isGameWon: Boolean = false,
    val isGameLoose: Boolean = false,
    val pairCount: Int = (cards.size / 2),
    val currentScore: Int = 0,
    val attempts: Int = 0,
    val time: Int = 0,
    val lastMatchSuccessful: Boolean? = null,

    val isCardMatch:Boolean = false,
    val flipCard: Boolean = false
)

