package com.example.memoryflipgame.presentation.MemoryFlip

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
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
fun MemoryFlipGameScreen2(
    gameState: GameUIState,
    onFlipCard: (MemoryCard) -> Unit,
    onRetry: () -> Unit,
    onDismissDialog: () -> Unit
) {

    var previousMatch by remember { mutableStateOf<Boolean?>(null) }

    val context = LocalContext.current

    LaunchedEffect(gameState.flipCard, gameState.lastMatchSuccessful, gameState.isGameLoose) {
        if (gameState.flipCard) {
            playSound(context, R.raw.flipcard)
        }

        // Handle match result sound
        gameState.lastMatchSuccessful?.let { isSuccessful ->
            if (isSuccessful != previousMatch) {
                previousMatch = isSuccessful
                val soundId = if (isSuccessful) R.raw.correct else R.raw.error
                playSound(context, soundId)
            }
        }

        // Handle game lose sound
        if (gameState.isGameLoose) {
            playSound(context, R.raw.lost)
        }
    }


    // Animation states for enhanced UI
    var screenVisible by remember { mutableStateOf(false) }
    val headerAlpha by animateFloatAsState(
        targetValue = if (screenVisible) 1f else 0f,
        animationSpec = tween(800), label = ""
    )
    val cardsScale by animateFloatAsState(
        targetValue = if (screenVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    LaunchedEffect(Unit) {
        screenVisible = true
    }


    Box(
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
    ) {
        repeat(8) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "")
            val offsetY by infiniteTransition.animateFloat(
                initialValue = (-100).sdp.value,
                targetValue = (LocalConfiguration.current.screenHeightDp + 100).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween((3000..6000).random(), easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ), label = ""
            )
            val offsetX by infiniteTransition.animateFloat(
                initialValue = (index * 50).sdp.value,
                targetValue = (index * 50 + (-20..20).random()).sdp.value,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )

            Box(
                modifier = Modifier
                    .offset(x = offsetX.sdp, y = offsetY.sdp)
                    .size((3..8).random().sdp)
                    .background(
                        Color.White.copy(alpha = (0.1f)),
                        CircleShape
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.sdp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Enhanced Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.sdp)
                    .graphicsLayer { alpha = headerAlpha },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glowing title with shadow effect
//                Box(
//                    modifier = Modifier
//                        .background(
//                            brush = Brush.horizontalGradient(
//                                colors = listOf(
//                                    Color(0xFF6366F1).copy(alpha = 0.2f),
//                                    Color(0xFF8B5CF6).copy(alpha = 0.2f),
//                                    Color(0xFFEC4899).copy(alpha = 0.2f)
//                                )
//                            ),
//                            shape = RoundedCornerShape(20.sdp)
//                        )
//                        .padding(horizontal = 24.sdp, vertical = 12.sdp)
//                ) {
//                    Text(
//                        text = "✨ Memory Match ✨",
//                        style = MaterialTheme.typography.headlineMedium.copy(
//                            color = Color.White,
//                            fontWeight = FontWeight.Bold,
//                            shadow = Shadow(
//                                color = Color(0xFF6366F1).copy(alpha = 0.5f),
//                                offset = Offset(0f, 4f),
//                                blurRadius = 8f
//                            )
//                        ),
//                        fontSize = 28.ssp
//                    )

                Text(
                    text = "✨Memory Match✨",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = whiteColor,
                        fontWeight = FontWeight.Bold
                    ),
                    fontSize = 28.ssp,
                    modifier = Modifier.padding(bottom = 24.sdp)
                )
//                }

                Spacer(modifier = Modifier.height(24.sdp))

                // Enhanced Stats Row with glassmorphism effect
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.sdp)
                ) {
                    // Attempts Counter with gradient border
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .drawBehind {
                                drawRoundRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF6366F1),
                                            Color(0xFF8B5CF6)
                                        )
                                    ),
                                    cornerRadius = CornerRadius(12.dp.toPx()),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.sdp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.sdp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.15f),
                                            Color.White.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                                .padding(16.sdp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎯 Attempts",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Medium
                                ),
                                fontSize = 11.ssp
                            )
                            Spacer(modifier = Modifier.height(4.sdp))
                            Text(
                                text = "${gameState.attempts}/${gameState.pairCount * 2}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = if (gameState.attempts > 8) Color(0xFFFF6B6B) else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.3f),
                                        offset = Offset(0f, 2f),
                                        blurRadius = 4f
                                    )
                                ),
                                fontSize = 18.ssp
                            )
                        }
                    }

                    // Timer with pulsing effect when low
                    val timerColor = when {
                        gameState.time <= 10 -> Color(0xFFFF6B6B)
                        gameState.time <= 30 -> Color(0xFFFFD93D)
                        else -> Color.White
                    }

                    val pulseScale by animateFloatAsState(
                        targetValue = if (gameState.time <= 10) 1.1f else 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                scaleX =
                                    if (gameState.time <= 10) pulseScale else 1f
                                scaleY =
                                    if (gameState.time <= 10) pulseScale else 1f
                            }
                            .drawBehind {
                                drawRoundRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(timerColor, timerColor.copy(alpha = 0.7f))
                                    ),
                                    cornerRadius = CornerRadius(12.dp.toPx()),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = timerColor.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.sdp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.sdp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.15f),
                                            Color.White.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                                .padding(16.sdp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⏰ Time",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Medium
                                ),
                                fontSize = 11.ssp
                            )
                            Spacer(modifier = Modifier.height(4.sdp))
                            Text(
                                text = formatTime(gameState.time),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = timerColor,
                                    fontWeight = FontWeight.Bold,
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.3f),
                                        offset = Offset(0f, 2f),
                                        blurRadius = 4f
                                    )
                                ),
                                fontSize = 18.ssp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.sdp))

                // Progress bar for matched cards
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 24.sdp),
//                    colors = CardDefaults.cardColors(
//                        containerColor = Color.White.copy(alpha = 0.1f)
//                    ),
//                    shape = RoundedCornerShape(20.sdp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(12.sdp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(
//                            text = "Progress: ${viewModel.matchedCard.intValue}/6 pairs",
//                            style = MaterialTheme.typography.bodySmall.copy(
//                                color = Color.White.copy(alpha = 0.8f)
//                            ),
//                            fontSize = 10.ssp
//                        )
//                        Spacer(modifier = Modifier.height(6.sdp))
//                        LinearProgressIndicator(
//                            progress = viewModel.matchedCard.intValue / 6f,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(6.sdp)
//                                .clip(RoundedCornerShape(3.sdp)),
//                            color = Color(0xFF4ECDC4),
//                            trackColor = Color.White.copy(alpha = 0.2f)
//                        )
//                    }
//                }
            }

            // Enhanced Game Grid with better animations
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.sdp)
                    .graphicsLayer {
                        scaleX = cardsScale
                        scaleY = cardsScale
                    },
                contentAlignment = Alignment.Center
            ) {
//                Card(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(8.sdp),
//                    colors = CardDefaults.cardColors(
//                        containerColor = Color.White.copy(alpha = 0.05f)
//                    ),
//                    shape = RoundedCornerShape(20.sdp),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 12.sdp)
//                ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 12.sdp, horizontal = 5.sdp),
                    verticalArrangement = Arrangement.spacedBy(8.sdp)
                ) {
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

                                    if (!card.isMatched) {
                                        FlipCard(
                                            cardFace = if (card.isFaceUp) CardFace.Front else CardFace.Back,
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f),
                                            back = {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            brush = Brush.radialGradient(
                                                                colors = listOf(
                                                                    Color(0xFF6366F1),
                                                                    Color(0xFF4338CA),
                                                                    Color(0xFF3730A3)
                                                                )
                                                            ),
                                                            shape = RoundedCornerShape(8.sdp)
                                                        )
                                                        .border(
                                                            2.sdp,
                                                            brush = Brush.horizontalGradient(
                                                                colors = listOf(
                                                                    Color.White.copy(alpha = 0.3f),
                                                                    Color.Transparent
                                                                )
                                                            ),
                                                            RoundedCornerShape(12.sdp)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "❓",
                                                        fontSize = 32.ssp,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        style = TextStyle(
                                                            shadow = Shadow(
                                                                color = Color.Black.copy(alpha = 0.5f),
                                                                offset = Offset(0f, 2f),
                                                                blurRadius = 4f
                                                            )
                                                        )
                                                    )
                                                }
                                            },
                                            front = {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            brush = Brush.verticalGradient(
                                                                colors = listOf(
                                                                    Color.White,
                                                                    Color(0xFFF8FAFC)
                                                                )
                                                            ),
                                                            shape = RoundedCornerShape(8.sdp)
                                                        )
                                                        .border(
                                                            2.sdp,
                                                            Color(0xFF4ECDC4).copy(alpha = 0.6f),
                                                            RoundedCornerShape(8.sdp)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = card.image,
                                                        fontSize = 28.ssp,
                                                        style = TextStyle(
                                                            shadow = Shadow(
                                                                color = Color.Gray.copy(alpha = 0.3f),
                                                                offset = Offset(0f, 1f),
                                                                blurRadius = 2f
                                                            )
                                                        )
                                                    )
                                                }
                                            },
                                            onClick = {
                                                onFlipCard(card)
                                            }
                                        )
                                    } else {
                                        // Enhanced matched card with celebration effect
                                        val infiniteTransition = rememberInfiniteTransition()
                                        val glowAlpha by infiniteTransition.animateFloat(
                                            initialValue = 0.3f,
                                            targetValue = 0.8f,
                                            animationSpec = infiniteRepeatable(
                                                animation = tween(1000),
                                                repeatMode = RepeatMode.Reverse
                                            ), label = ""
                                        )

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .background(
                                                    brush = Brush.radialGradient(
                                                        colors = listOf(
                                                            Color(0xFF4ECDC4).copy(alpha = glowAlpha),
                                                            Color(0xFF44A08D).copy(alpha = 0.3f)
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(12.sdp)
                                                )
                                                .border(
                                                    2.sdp,
                                                    Color(0xFF4ECDC4),
                                                    RoundedCornerShape(12.sdp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            androidx.compose.material3.Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Matched",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.sdp)
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

            }
        }
    }

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


fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}


class GameNetworkHandler(private val communicationManager: CommunicationManager) {
    var onGameEvent: ((GameEvent) -> Unit)? = null

    init {
        communicationManager.onMessageReceived = { event ->
            onGameEvent?.invoke(event)
        }
    }
}


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
        println("sound exception $e")
        // Handle exception silently
    }
}
