package com.example.memoryflipgame.presentation.MemoryFlip.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.memoryflipgame.R
import com.example.memoryflipgame.ui.theme.whiteColor
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun WinDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onPlayAgain: () -> Unit,
    attempts: Int,
    timeElapsed: String,
    score: Int = 100
) {
    if (isVisible) {
        GameDialog(
            icon = R.drawable.baseline_emoji_events_24,
            title = "\uD83C\uDF89Congratulation!!",
            message = "Your Score",
            backgroundColor = listOf(
                Color(0xFF4CAF50),
                Color(0xFF8BC34A),
                Color(0xFFCDDC39)
            ),
            iconColor = Color(0xFFFFD700),
            stats = listOf(
                "Time: $timeElapsed",
                "Attempts: $attempts",
//                "Score: $score pts"
            ),
            primaryButtonText = "Play Again",
            secondaryButtonText = "Exit",
            onPrimaryClick = onPlayAgain,
            onSecondaryClick = onDismiss,
            celebrationAnimation = true
        )
    }
}

@Composable
fun LoseDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onTryAgain: () -> Unit,
    attempts: Int,
    reason: String = "Time's up!"
) {
    if (isVisible) {
        GameDialog(
            icon = R.drawable.baseline_sentiment_dissatisfied_24,
            title = "😔 Game Over",
            message = reason,
            backgroundColor = listOf(
                Color(0xFFF44336),
                Color(0xFFE91E63),
                Color(0xFF9C27B0)
            ),
            iconColor = Color(0xFFFFB74D),
            stats = listOf(
                "Attempts made: $attempts",
                "Don't give up!",
//                "Try again to improve"
            ),
            primaryButtonText = "Try Again",
            secondaryButtonText = "Exit",
            onPrimaryClick = onTryAgain,
            onSecondaryClick = onDismiss,
            celebrationAnimation = false
        )
    }
}

@Composable
private fun GameDialog(
    icon: Int,
    title: String,
    message: String,
    backgroundColor: List<Color>,
    iconColor: Color,
    stats: List<String>,
    primaryButtonText: String,
    secondaryButtonText: String,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    celebrationAnimation: Boolean
) {
    // Animation states
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300)
    )

    // Celebration particles animation
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val celebrationOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    Dialog(
        onDismissRequest = onSecondaryClick,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    },
                shape = RoundedCornerShape(20.sdp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.sdp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box {
                    // Background gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.sdp)
                            .background(
                                Brush.horizontalGradient(backgroundColor),
                                RoundedCornerShape(topStart = 16.sdp, topEnd = 16.sdp)
                            )
                    )

                    // Celebration particles (only for win dialog)
                    if (celebrationAnimation) {
                        repeat(8) { index ->
                            val angle = (celebrationOffset + index * 45f) * (Math.PI / 180f)
                            val radius = 30.sdp
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = (radius.value * kotlin.math.cos(angle)).sdp + 93.sdp,
                                        y = (radius.value * kotlin.math.sin(angle)).sdp + 60.sdp
                                    )
                                    .size(8.sdp)
                                    .background(
                                        Color.Yellow.copy(alpha = 0.8f),
                                        CircleShape
                                    )
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.sdp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icon with animation
                        val iconScale by animateFloatAsState(
                            targetValue = if (visible) 1f else 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                        )

                        Box(
                            modifier = Modifier
                                .size(80.sdp)
                                .background(
                                    iconColor.copy(alpha = 0.2f),
                                    CircleShape
                                )
                                .graphicsLayer {
                                    scaleX = iconScale
                                    scaleY = iconScale
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(icon),
                                contentDescription = null,
                                modifier = Modifier.size(40.sdp),
                                tint = iconColor
                            )
                        }

                        Spacer(modifier = Modifier.height(25.sdp))

                        // Title with slide animation
                        AnimatedVisibility(
                            visible = visible,
                            enter = slideInVertically(
                                initialOffsetY = { -40 },
                                animationSpec = tween(500, delayMillis = 300)
                            ) + fadeIn(animationSpec = tween(500, delayMillis = 300))
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(8.sdp))

                        // Message with fade animation
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(500, delayMillis = 400))
                        ) {
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF666666)
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.sdp))

                        // Stats cards with staggered animation
                        stats.forEachIndexed { index, stat ->
                            AnimatedVisibility(
                                visible = visible,
                                enter = slideInHorizontally(
                                    initialOffsetX = { if (index % 2 == 0) -300 else 300 },
                                    animationSpec = tween(400, delayMillis = 500 + index * 100)
                                ) + fadeIn(animationSpec = tween(400, delayMillis = 500 + index * 100))
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.sdp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = backgroundColor.first().copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(12.sdp)
                                ) {
                                    Text(
                                        text = stat,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.sdp),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF444444)
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.sdp))

                        // Buttons with slide animation
                        AnimatedVisibility(
                            visible = visible,
                            enter = slideInVertically(
                                initialOffsetY = { 60 },
                                animationSpec = tween(500, delayMillis = 800)
                            ) + fadeIn(animationSpec = tween(500, delayMillis = 800))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.sdp)
                            ) {
                                // Secondary button
                                OutlinedButton(
                                    onClick = onSecondaryClick,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.sdp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = backgroundColor.first()
                                    ),
                                    border = BorderStroke(2.sdp, backgroundColor.first().copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = secondaryButtonText,
                                        modifier = Modifier.padding(vertical = 4.sdp),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // Primary button
                                Button(
                                    onClick = onPrimaryClick,
                                    modifier = Modifier,
                                    shape = RoundedCornerShape(12.sdp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = backgroundColor.first()
                                    )
                                ) {
                                    Text(
                                        text = primaryButtonText,
                                        modifier = Modifier.padding(vertical = 4.sdp),
                                        fontWeight = FontWeight.Bold,
                                        color = whiteColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Usage Example in your MemoryFlipGameScreen
//@Composable
//fun MemoryFlipGameScreenWithDialogs() {
//    val viewModel: MemoryFlipGameViewModel = viewModel()
//
//    // Your existing game UI here...
//
//    // Win Dialog
//    WinDialog(
//        isVisible = viewModel.isGameComplete,
//        onDismiss = { /* Handle exit */ },
//        onPlayAgain = { viewModel.startGame() },
//        attempts = viewModel.attempts,
//        timeElapsed = viewModel.formattedTime,
//        score = calculateScore(viewModel.attempts, viewModel.timeInSeconds)
//    )
//
//    // Lose Dialog
//    LoseDialog(
//        isVisible = viewModel.isTimeOut.value,
//        onDismiss = { /* Handle exit */ },
//        onTryAgain = { viewModel.startGame() },
//        attempts = viewModel.attempts,
//        reason = "Time's up! Better luck next time."
//    )
//}

// Helper function to calculate score
fun calculateScore(attempts: Int, timeRemaining: Int): Int {
    val baseScore = 100
    val timeBonus = timeRemaining * 2
    val attemptPenalty = maxOf(0, (attempts - 10) * 5)
    return maxOf(10, baseScore + timeBonus - attemptPenalty)
}