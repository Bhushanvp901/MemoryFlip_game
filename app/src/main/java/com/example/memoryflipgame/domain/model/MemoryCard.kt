package com.example.memoryflipgame.domain.model

data class MemoryCard (
    val id:Int,
    val image:String = "",
    val isFaceUp: Boolean = false,
    val isMatched:Boolean = false
)