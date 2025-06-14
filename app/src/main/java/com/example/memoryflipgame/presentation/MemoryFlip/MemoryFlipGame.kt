package com.example.memoryflipgame.presentation.MemoryFlip


import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.memoryflipgame.R
import com.example.memoryflipgame.data.service.CommunicationManager
import com.example.memoryflipgame.domain.GameUIState
import com.example.memoryflipgame.domain.model.GameEvent
import com.example.memoryflipgame.domain.model.MemoryCard
import com.example.memoryflipgame.presentation.MemoryFlip.components.CardFace
import com.example.memoryflipgame.presentation.MemoryFlip.components.FlipCard
import com.example.memoryflipgame.presentation.MemoryFlip.components.LoseDialog
import com.example.memoryflipgame.presentation.MemoryFlip.components.WinDialog
import com.example.memoryflipgame.ui.theme.whiteColor
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp


@Composable
fun MemoryFlipGameScreen(
    gameState: GameUIState,
    onFlipCard: (MemoryCard) -> Unit,
    onStartGame:()->Unit,
    onRetry: () -> Unit,
    onDismissDialog: () -> Unit
) {
    var previousMatch by remember { mutableStateOf<Boolean?>(null) }
    val context = LocalContext.current

    LaunchedEffect (Unit){
        onStartGame()
    }

    LaunchedEffect(gameState.lastMatchSuccessful, gameState.isGameLoose) {
        try {
            if (gameState.lastMatchSuccessful == true) {
                previousMatch = true
                val soundId = R.raw.correct
                playSound(context, soundId)
            } else if (gameState.lastMatchSuccessful == false && !gameState.isCardMatch ){
                previousMatch = false
                val soundId = R.raw.error
                playSound(context, soundId)
            }
            // Handle game lose sound
            if (gameState.isGameLoose) {
                playSound(context, R.raw.lost)
            }
        } catch (e: Exception) {
            // Silently handle any audio errors
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
            .padding(16.sdp),
        verticalArrangement = Arrangement.spacedBy(16.sdp)
    ) {

        // Header Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.sdp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game Title
            Text(
                text = "✨ Memory Match ✨",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                fontSize = 25.ssp,
                modifier = Modifier.padding(bottom = 20.sdp)
            )

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.sdp)
            ) {
                // Attempts Counter
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.sdp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.sdp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.sdp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎯 Attempts",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            ),
                            fontSize = 12.ssp
                        )
                        Spacer(modifier = Modifier.height(4.sdp))
                        Text(
                            text = "${gameState.attempts}/${gameState.pairCount * 2}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = if (gameState.attempts > 8) Color(0xFFFF6B6B) else Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            fontSize = 18.ssp
                        )
                    }
                }

                // Timer
                val timerColor = when {
                    gameState.time <= 10 -> Color(0xFFFF6B6B)
                    gameState.time <= 30 -> Color(0xFFFFD93D)
                    else -> Color.White
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = timerColor.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.sdp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.sdp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.sdp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⏰ Time",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            ),
                            fontSize = 12.ssp
                        )
                        Spacer(modifier = Modifier.height(4.sdp))
                        Text(
                            text = formatTime(gameState.time),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = timerColor,
                                fontWeight = FontWeight.Bold
                            ),
                            fontSize = 18.ssp
                        )
                    }
                }
            }

            // Progress Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.sdp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.sdp)
            ) {
                Column(
                    modifier = Modifier.padding(16.sdp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val matchedPairs = gameState.cards.count { it.isMatched } / 2
                    val totalPairs = gameState.pairCount

                    Text(
                        text = "Progress: $matchedPairs/$totalPairs pairs matched",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        ),
                        fontSize = 14.ssp
                    )

                    Spacer(modifier = Modifier.height(8.sdp))

                    LinearProgressIndicator(
                        progress = { matchedPairs.toFloat() / totalPairs.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.sdp)
                            .clip(RoundedCornerShape(4.sdp)),
                        color = Color(0xFF4ECDC4),
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }
            }
        }

        // Game Grid
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.08f)
            ),
            shape = RoundedCornerShape(16.sdp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.sdp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.sdp),
                verticalArrangement = Arrangement.spacedBy(8.sdp)
            ) {
                // Create 5 rows of 4 cards each
                repeat(5) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.sdp)
                    ) {
                        repeat(4) { col ->
                            val cardIndex = row * 4 + col
                            if (cardIndex < gameState.cards.size) {
                                val card = gameState.cards[cardIndex]
                                GameCard(
                                    card = card,
                                    onFlipCard = onFlipCard,
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )



                            } else {
                                // Empty space for layout consistency
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    WinDialog(
        isVisible = gameState.isGameWon,
        onDismiss = onDismissDialog,
        onPlayAgain = onRetry,
        attempts = gameState.attempts,
        timeElapsed = formatTime(gameState.time),
        score = gameState.currentScore
    )

    LoseDialog(
        isVisible = gameState.isGameLoose,
        onDismiss = onDismissDialog,
        onTryAgain = onRetry,
        attempts = gameState.attempts,
        reason = ""
    )
}

@Composable
fun GameCard(
    card: MemoryCard,
    onFlipCard: (MemoryCard) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        card.isMatched -> {
            // Matched card - show success state
            Card(
                modifier = modifier,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4ECDC4).copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(12.sdp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.sdp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Matched",
                        tint = Color.White,
                        modifier = Modifier.size(32.sdp)
                    )
                }
            }
        }



        card.isFaceUp -> {
            // Face up card - show the image
            Card(
                modifier = modifier
                    .clickable { onFlipCard(card) },
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.sdp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.sdp),
                border = BorderStroke(2.sdp, Color(0xFF4ECDC4))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = card.image,
                        fontSize = 32.ssp,
                        color = Color.Black
                    )
                }
            }
        }

        else -> {

//             Face down card - show back
            Card(
                modifier = modifier
                    .clickable { onFlipCard(card) },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF6366F1)
                ),
                shape = RoundedCornerShape(12.sdp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.sdp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF8B5CF6),
                                    Color(0xFF6366F1),
                                    Color(0xFF4338CA)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "❓",
                        fontSize = 32.ssp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

//    if (!card.isMatched) {
//        FlipCard(
//            cardFace = if (card.isFaceUp) CardFace.Front else CardFace.Back,
//            modifier = modifier
//                .aspectRatio(1f),
//            back = {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(
//                            brush = Brush.radialGradient(
//                                colors = listOf(
//                                    Color(0xFF6366F1),
//                                    Color(0xFF4338CA),
//                                    Color(0xFF3730A3)
//                                )
//                            ),
//                            shape = RoundedCornerShape(8.sdp)
//                        )
//                        .border(
//                            2.sdp,
//                            brush = Brush.horizontalGradient(
//                                colors = listOf(
//                                    Color.White.copy(alpha = 0.3f),
//                                    Color.Transparent
//                                )
//                            ),
//                            RoundedCornerShape(12.sdp)
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "❓",
//                        fontSize = 32.ssp,
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold,
//                        style = TextStyle(
//                            shadow = Shadow(
//                                color = Color.Black.copy(alpha = 0.5f),
//                                offset = Offset(0f, 2f),
//                                blurRadius = 4f
//                            )
//                        )
//                    )
//                }
//            },
//            front = {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(
//                            brush = Brush.verticalGradient(
//                                colors = listOf(
//                                    Color.White,
//                                    Color(0xFFF8FAFC)
//                                )
//                            ),
//                            shape = RoundedCornerShape(8.sdp)
//                        )
//                        .border(
//                            2.sdp,
//                            Color(0xFF4ECDC4).copy(alpha = 0.6f),
//                            RoundedCornerShape(8.sdp)
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = card.image,
//                        fontSize = 28.ssp,
//                        style = TextStyle(
//                            shadow = Shadow(
//                                color = Color.Gray.copy(alpha = 0.3f),
//                                offset = Offset(0f, 1f),
//                                blurRadius = 2f
//                            )
//                        )
//                    )
//                }
//            },
//            onClick = {
//                onFlipCard(card)
//            }
//        )
//    }else{
//        Box(
//            modifier = modifier
//                .aspectRatio(1f)
//                .background(
//                    brush = Brush.radialGradient(
//                        colors = listOf(
//                            Color(0xFF4ECDC4).copy(alpha = 0.5f),
//                            Color(0xFF44A08D).copy(alpha = 0.3f)
//                        )
//                    ),
//                    shape = RoundedCornerShape(12.sdp)
//                )
//                .border(
//                    2.sdp,
//                    Color(0xFF4ECDC4),
//                    RoundedCornerShape(12.sdp)
//                ),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                imageVector = Icons.Default.Check,
//                contentDescription = "Matched",
//                tint = Color.White,
//                modifier = Modifier.size(24.sdp)
//            )
//        }
//    }
}

// Helper function for safe sound playing
private fun playSound(context: Context, soundResId: Int) {
    try {
        val mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer?.let { player ->
            player.setOnCompletionListener {
                it.release()
            }
            player.setOnErrorListener { mp, _, _ ->
                mp.release()
                true
            }
            player.start()
        }
    } catch (e: Exception) {
        // Silently handle any audio errors to prevent crashes
    }
}