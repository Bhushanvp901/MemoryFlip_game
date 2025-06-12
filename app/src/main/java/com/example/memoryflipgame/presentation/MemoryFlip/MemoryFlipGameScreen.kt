package com.example.memoryflipgame.presentation.MemoryFlip

import android.widget.Toast
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
import com.example.memoryflipgame.presentation.MemoryFlipGameViewModel
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.delay
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp

@Composable
fun MemoryFlipGameScreen(viewModel: MemoryFlipGameViewModel) {

    val context = LocalContext.current

    // Animation states for enhanced UI
    var screenVisible by remember { mutableStateOf(false) }
    val headerAlpha by animateFloatAsState(
        targetValue = if (screenVisible) 1f else 0f,
        animationSpec = tween(800)
    )
    val cardsScale by animateFloatAsState(
        targetValue = if (screenVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(Unit) {
        screenVisible = true
    }

    LaunchedEffect(viewModel.attempts, viewModel.matchedCard) {
        if (viewModel.attempts > 12 || viewModel.matchedCard.intValue == 6) {
            viewModel.timeRemaining.intValue = 0
        }
    }

    LaunchedEffect(viewModel.timeRemaining.intValue, viewModel.isTimeRunning.value) {
        if (viewModel.isTimeRunning.value && viewModel.timeRemaining.intValue > 0) {
            delay(1000)
            viewModel.timeRemaining.intValue--
        } else if (viewModel.timeRemaining.intValue == 0) {
            viewModel.isTimeOut.value = true
            Toast.makeText(context, "Time Out", Toast.LENGTH_SHORT).show()
        }
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
        // Animated background particles
        repeat(15) { index ->
            val infiniteTransition = rememberInfiniteTransition()
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
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6366F1).copy(alpha = 0.2f),
                                    Color(0xFF8B5CF6).copy(alpha = 0.2f),
                                    Color(0xFFEC4899).copy(alpha = 0.2f)
                                )
                            ),
                            shape = RoundedCornerShape(20.sdp)
                        )
                        .padding(horizontal = 24.sdp, vertical = 12.sdp)
                ) {
                    Text(
                        text = "✨ Memory Match ✨",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(
                                color = Color(0xFF6366F1).copy(alpha = 0.5f),
                                offset = Offset(0f, 4f),
                                blurRadius = 8f
                            )
                        ),
                        fontSize = 28.ssp
                    )
                }

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
                                text = "${viewModel.attempts}/12",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = if (viewModel.attempts > 8) Color(0xFFFF6B6B) else Color.White,
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
                        viewModel.timeRemaining.intValue <= 10 -> Color(0xFFFF6B6B)
                        viewModel.timeRemaining.intValue <= 30 -> Color(0xFFFFD93D)
                        else -> Color.White
                    }

                    val pulseScale by animateFloatAsState(
                        targetValue = if (viewModel.timeRemaining.intValue <= 10) 1.1f else 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                scaleX = if (viewModel.timeRemaining.intValue <= 10) pulseScale else 1f
                                scaleY = if (viewModel.timeRemaining.intValue <= 10) pulseScale else 1f
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
                                text = formatTime(viewModel.timeRemaining.value),
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.sdp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(20.sdp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.sdp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Progress: ${viewModel.matchedCard.intValue}/6 pairs",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            ),
                            fontSize = 10.ssp
                        )
                        Spacer(modifier = Modifier.height(6.sdp))
                        LinearProgressIndicator(
                            progress = viewModel.matchedCard.intValue / 6f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.sdp)
                                .clip(RoundedCornerShape(3.sdp)),
                            color = Color(0xFF4ECDC4),
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
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
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.sdp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(20.sdp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.sdp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.sdp),
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
                                    if (cardIndex < viewModel.cards.size) {
                                        val card = viewModel.cards[cardIndex]

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
                                                                shape = RoundedCornerShape(12.sdp)
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
                                                                shape = RoundedCornerShape(12.sdp)
                                                            )
                                                            .border(
                                                                2.sdp,
                                                                Color(0xFF4ECDC4).copy(alpha = 0.6f),
                                                                RoundedCornerShape(12.sdp)
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
                                                    viewModel.onCardClicked(card)
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
                                                )
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
    }

    // Your existing dialogs
    WinDialog(
        isVisible = (viewModel.matchedCard.intValue == 6),
        onDismiss = { viewModel.matchedCard.intValue = 0 },
        onPlayAgain = {
            viewModel.onRetry()
        },
        attempts = viewModel.attempts,
        timeElapsed = formatTime(viewModel.timeTaken),
        score = calculateScore(viewModel.attempts, viewModel.timeRemaining.intValue)
    )

    LoseDialog(
        isVisible = (viewModel.isTimeOut.value || viewModel.attempts == 12),
        onDismiss = {
            viewModel.isTimeOut.value = false
            viewModel.attempts = 0
        },
        onTryAgain = {
            viewModel.onRetry()
        },
        attempts = viewModel.attempts,
        reason = if (viewModel.isTimeOut.value) "Time's up! Retry again" else "Attempts exceeds!!"
    )
}


fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}