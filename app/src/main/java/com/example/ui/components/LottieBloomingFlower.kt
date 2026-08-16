package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.R
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.SoftRose
import kotlin.math.cos
import kotlin.math.sin

/**
 * LottieBloomingFlower displays a graceful blooming lotus/rose flower animation using Lottie
 * with an atmospheric pastel aura and organic petal breathing.
 */
@Composable
fun LottieBloomingFlower(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    onAnimationEnd: (() -> Unit)? = null
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.blooming_flower)
    )

    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            isPlaying = true
        }
    }

    val lottieAnimState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying && isVisible,
        iterations = 1,
        speed = 0.85f,
        restartOnPlay = false
    )

    // Gentle breathing pulse once bloomed
    val infiniteTransition = rememberInfiniteTransition(label = "flower_breathing")
    val breathingPulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bloom_pulse"
    )

    val auraGlow by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_glow"
    )

    val enterAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "flower_enter_alpha"
    )

    val enterScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.4f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "flower_enter_scale"
    )

    if (enterAlpha > 0.01f) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(size)
                .alpha(enterAlpha)
                .scale(enterScale * if (lottieAnimState.isAtEnd) breathingPulse else 1f)
        ) {
            // Ambient Aura Halo behind flower
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val radius = this.size.minDimension / 2f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = auraGlow * 0.5f),
                            LavenderAccent.copy(alpha = auraGlow * 0.35f),
                            Color(0xFFFFD1DC).copy(alpha = auraGlow * 0.2f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius * 1.15f
                    ),
                    center = center,
                    radius = radius * 1.15f
                )
            }

            // Lottie Blooming Animation
            if (composition != null) {
                LottieAnimation(
                    composition = composition,
                    progress = { lottieAnimState.progress },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Graceful fallback procedural vector blooming flower
                ProceduralBloomingFlower(
                    progress = if (isVisible) 1f else 0f,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Procedural fallback blooming flower composed of layered petals and golden pistil.
 */
@Composable
fun ProceduralBloomingFlower(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension * 0.42f * progress.coerceIn(0f, 1f)

        if (maxRadius <= 1f) return@Canvas

        // Outer petals (8 petals)
        for (i in 0 until 8) {
            val angle = i * 45f
            rotate(angle, pivot = center) {
                drawOval(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            LavenderAccent.copy(alpha = 0.85f),
                            SoftRose.copy(alpha = 0.9f)
                        ),
                        startY = center.y - maxRadius,
                        endY = center.y
                    ),
                    topLeft = Offset(center.x - maxRadius * 0.28f, center.y - maxRadius * 0.95f),
                    size = androidx.compose.ui.geometry.Size(maxRadius * 0.56f, maxRadius * 0.95f)
                )
            }
        }

        // Inner petals (6 petals)
        for (i in 0 until 6) {
            val angle = i * 60f + 22.5f
            rotate(angle, pivot = center) {
                drawOval(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFB6C1),
                            SoftRose
                        ),
                        startY = center.y - maxRadius * 0.7f,
                        endY = center.y
                    ),
                    topLeft = Offset(center.x - maxRadius * 0.22f, center.y - maxRadius * 0.7f),
                    size = androidx.compose.ui.geometry.Size(maxRadius * 0.44f, maxRadius * 0.7f)
                )
            }
        }

        // Golden luminous pistil center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF3B0),
                    Color(0xFFFFD54F),
                    Color(0xFFFFA000)
                ),
                center = center,
                radius = maxRadius * 0.26f
            ),
            center = center,
            radius = maxRadius * 0.26f
        )
    }
}
