package com.example.memoryflipgame.presentation

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryflipgame.R
import com.example.memoryflipgame.domain.model.MemoryCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MemoryFlipGameViewModel(
    private val context: Context
) : ViewModel() {


    private val _cards = mutableStateListOf<MemoryCard>()
    val cards: List<MemoryCard> get() = _cards

    private var firstCard: MemoryCard? = null
    var matchedCard = mutableIntStateOf(0)
    private var isBusy = false
    var attempts: Int = 0

    val isTimeRunning = mutableStateOf(false)
    val timeRemaining = mutableIntStateOf(60)
    val timeTaken = 60 - timeRemaining.intValue
    val isTimeOut = mutableStateOf(false)


    private val matchSound = MediaPlayer.create(context, R.raw.correct)
    private val wrongSound = MediaPlayer.create(context, R.raw.error)
    private val flipSound = MediaPlayer.create(context, R.raw.flipcard)
    private fun playSound(sound: MediaPlayer) {
        sound.seekTo(0)
        sound.start()
    }

    init {
        startMindFLipGame()
    }

    private fun startMindFLipGame() {
        val allImages = (1..20).toList()
        val selectedCards = allImages.shuffled().take(6)

        val cardPairs = selectedCards.flatMap {
            listOf(
                MemoryCard(id = Random.nextInt(), image = it.toString()),
                MemoryCard(id = Random.nextInt(), image = it.toString())
            )
        }.shuffled() // Shuffle the final list

        _cards.clear()
        _cards.addAll(cardPairs)
        isTimeRunning.value = true
    }


    fun onCardClicked(card: MemoryCard) {
        if (isBusy || card.isFaceUp || card.isMatched) return

        playSound(flipSound)

        val index = _cards.indexOf(card)
        _cards[index] = card.copy(isFaceUp = true)

        if (firstCard == null) {
            firstCard = _cards[index]
        } else {
            val secondCard = _cards[index]
            isBusy = true

            viewModelScope.launch {
                delay(700)

                if (firstCard?.image == secondCard.image) {
                    // Match
                    val index1 = _cards.indexOf(firstCard)
                    val index2 = _cards.indexOf(secondCard)
                    _cards[index1] = firstCard!!.copy(isMatched = true)
                    _cards[index2] = secondCard.copy(isMatched = true)
                    playSound(matchSound)
                    matchedCard.intValue++
                } else {
                    // Not matched
                    val index1 = _cards.indexOf(firstCard)
                    val index2 = _cards.indexOf(secondCard)
                    _cards[index1] = firstCard!!.copy(isFaceUp = false)
                    _cards[index2] = secondCard.copy(isFaceUp = false)
                    attempts += 1
                    playSound(wrongSound)
                }

                firstCard = null
                isBusy = false
            }
        }
    }


    fun onRetry() {
        startMindFLipGame()
        timeRemaining.intValue = 60
        isTimeRunning.value = true
        isTimeOut.value = false
        matchedCard.intValue = 0
        attempts = 0
    }
}