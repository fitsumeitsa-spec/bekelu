package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MoodItem
import com.example.model.SymptomItem
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftPinkSurfaceVariant
import com.example.ui.theme.SoftRose
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun GirlyPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String = "🌸",
    enabled: Boolean = true,
    testTag: String = "girly_primary_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        ),
        modifier = modifier
            .testTag(testTag)
            .scale(scale)
            .heightIn(min = 52.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = icon,
                fontSize = 18.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    elevation: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun CycleRing(
    currentDay: Int,
    totalDays: Int,
    phaseText: String,
    cycleDayLabel: String,
    modifier: Modifier = Modifier,
    size: Dp = 210.dp,
    strokeWidth: Dp = 12.dp,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    accentColor: Color = LavenderAccent,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
) {
    val progress = (currentDay.toFloat() / totalDays.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "cycle_progress"
    )

    // Animated counting integer for the center day number (e.g. 22)
    val animatedDayNumber by animateIntAsState(
        targetValue = currentDay,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "cycle_day_number"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val auraGlow by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring_aura_glow"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = this.size.minDimension
            val strokePx = strokeWidth.toPx()
            val radius = (canvasSize - strokePx) / 2f
            val center = Offset(canvasSize / 2f, canvasSize / 2f)

            // Ambient background aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = auraGlow * 0.35f),
                        accentColor.copy(alpha = auraGlow * 0.18f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.15f
                ),
                radius = radius * 1.15f,
                center = center
            )

            // Background Track
            drawCircle(
                color = backgroundColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Progress Arc with smooth gradient
            val sweepAngle = animatedProgress * 360f
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to primaryColor,
                    0.5f to accentColor,
                    1.0f to primaryColor,
                    center = center
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Glowing Indicator Dot at Arc Head
            if (animatedProgress > 0.01f) {
                val angleRad = Math.toRadians((sweepAngle - 90.0)).toFloat()
                val dotX = center.x + radius * cos(angleRad)
                val dotY = center.y + radius * sin(angleRad)

                // Outer glow
                drawCircle(
                    color = primaryColor.copy(alpha = 0.5f),
                    radius = strokePx * 0.8f,
                    center = Offset(dotX, dotY)
                )
                // Center white dot
                drawCircle(
                    color = Color.White,
                    radius = strokePx * 0.45f,
                    center = Offset(dotX, dotY)
                )
            }
        }

        // Central Content with Animated Transition Day Counter
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            AnimatedNumberTransition(
                number = currentDay,
                textStyle = MaterialTheme.typography.displayLarge.copy(fontSize = 38.sp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(pulseScale)
            )
            Text(
                text = cycleDayLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = phaseText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/**
 * AnimatedNumberTransition provides an ultra-smooth digit ticker transition using
 * Jetpack Compose's Transition API (AnimatedContent + spring physics) for premium numeric feel.
 */
@Composable
fun AnimatedNumberTransition(
    number: Int,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.displayLarge,
    color: Color = MaterialTheme.colorScheme.primary,
    fontWeight: FontWeight = FontWeight.Black
) {
    val numberString = number.toString()
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in numberString.indices) {
            val char = numberString[i]
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically(
                            animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f)
                        ) { fullHeight -> fullHeight } + fadeIn(
                            animationSpec = tween(220)
                        )).togetherWith(
                            slideOutVertically(
                                animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f)
                            ) { fullHeight -> -fullHeight } + fadeOut(
                                animationSpec = tween(180)
                            )
                        )
                    } else {
                        (slideInVertically(
                            animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f)
                        ) { fullHeight -> -fullHeight } + fadeIn(
                            animationSpec = tween(220)
                        )).togetherWith(
                            slideOutVertically(
                                animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f)
                            ) { fullHeight -> fullHeight } + fadeOut(
                                animationSpec = tween(180)
                            )
                        )
                    }
                },
                label = "digit_ticker_$i"
            ) { targetChar ->
                Text(
                    text = targetChar.toString(),
                    style = textStyle,
                    color = color,
                    fontWeight = fontWeight
                )
            }
        }
    }
}

/**
 * AnimatedCountdownSection renders the primary cycle countdown (e.g., "6 days / Expected Aug 22")
 * with dynamic number transition API, glowing aura breathing, and elegant typography transitions.
 */
@Composable
fun AnimatedCountdownSection(
    daysRemaining: Int,
    daysLabel: String,
    expectedDateText: String,
    titleText: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "countdown_breathing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "countdown_pulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "countdown_glow"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Text(
            text = titleText.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.2.sp
        )

        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            // Ambient soft glowing backdrop for the prominent number
            Canvas(
                modifier = Modifier
                    .size(width = 90.dp, height = 56.dp)
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SoftRose.copy(alpha = glowAlpha),
                            LavenderAccent.copy(alpha = glowAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(36.dp.toPx(), 28.dp.toPx()),
                        radius = 45.dp.toPx()
                    ),
                    radius = 45.dp.toPx(),
                    center = Offset(36.dp.toPx(), 28.dp.toPx())
                )
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AnimatedNumberTransition(
                    number = daysRemaining,
                    textStyle = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 50.sp,
                        lineHeight = 50.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.scale(pulseScale)
                )
                Text(
                    text = daysLabel,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }

        // Expected Date with clean calendar styling
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "🌸", fontSize = 12.sp)
                Text(
                    text = expectedDateText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * AnimatedCycleDayProgress renders the animated cycle progress ("Cycle day 22 of 28")
 * with dynamic count-up, glowing linear progress track, and regularity indicator.
 */
@Composable
fun AnimatedCycleDayProgress(
    currentDay: Int,
    totalDays: Int,
    cycleDayText: String,
    regularityText: String,
    phaseDescription: String,
    modifier: Modifier = Modifier
) {
    val progress = (currentDay.toFloat() / totalDays.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "linear_cycle_progress"
    )

    val animatedDay by animateIntAsState(
        targetValue = currentDay,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "linear_cycle_day"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cycleDayText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Regularity badge
            Surface(
                shape = RoundedCornerShape(50),
                color = com.example.ui.theme.SoftSuccess.copy(alpha = 0.14f),
                border = BorderStroke(
                    1.dp,
                    com.example.ui.theme.SoftSuccess.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "$regularityText ✨",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = com.example.ui.theme.SoftSuccess,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // Animated Gradient Linear Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                LavenderAccent,
                                SoftRose,
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    )
            )
        }

        // Phase insight message card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = phaseDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
fun MoodSelector(
    moods: List<MoodItem>,
    selectedMoodKey: String?,
    onMoodSelected: (String) -> Unit,
    isAmharic: Boolean,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(moods) { mood ->
            val isSelected = mood.key == selectedMoodKey
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.08f else 1.0f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
                label = "mood_scale"
            )
            val containerColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                label = "mood_color"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                label = "mood_border"
            )

            Surface(
                onClick = { onMoodSelected(mood.key) },
                shape = RoundedCornerShape(18.dp),
                color = containerColor,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier
                    .scale(scale)
                    .testTag("mood_${mood.key}")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = mood.emoji,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAmharic) mood.labelAm else mood.labelEn,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun FlowSelector(
    selectedFlow: String?,
    onFlowSelected: (String) -> Unit,
    isAmharic: Boolean,
    modifier: Modifier = Modifier
) {
    val flows = listOf(
        Triple("None", if (isAmharic) "ምንም" else "None", "⚪"),
        Triple("Light", if (isAmharic) "ቀላል" else "Light", "💧"),
        Triple("Medium", if (isAmharic) "መካከለኛ" else "Medium", "🩸"),
        Triple("Heavy", if (isAmharic) "ከባድ" else "Heavy", "🩸🩸")
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        flows.forEach { (key, label, icon) ->
            val isSelected = selectedFlow == key
            val containerColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                label = "flow_bg"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                label = "flow_border"
            )

            Surface(
                onClick = { onFlowSelected(key) },
                shape = RoundedCornerShape(16.dp),
                color = containerColor,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 58.dp)
                    .testTag("flow_$key")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                ) {
                    Text(text = icon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SymptomChip(
    symptom: SymptomItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    isAmharic: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.04f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "chip_scale"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        label = "chip_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        label = "chip_border"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .scale(scale)
            .testTag("symptom_${symptom.key}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text = symptom.iconEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isAmharic) symptom.labelAm else symptom.labelEn,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String,
    unit: String = "",
    icon: String = "✨",
    modifier: Modifier = Modifier
) {
    SoftCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(text = icon, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PrivacyCard(
    isAmharic: Boolean,
    onLearnMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    SoftCard(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        borderColor = MaterialTheme.colorScheme.outline
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isAmharic) "መረጃሽ በእጅሽ ብቻ ነው 🔐" else "Your data stays with you 🔐",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isAmharic) "የወር አበባ መረጃሽ በስልክሽ ላይ በግል ይቀመጣል።" else "Your cycle information is stored locally on your device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (isAmharic) "ስለ ግላዊነት ጥበቃ የበለጠ እወቂ →" else "Learn about privacy & security →",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onLearnMore)
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun PetalParticleEffect(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    if (!isActive) return

    val particles = remember {
        List(14) {
            Triple(
                Random.nextFloat(), // x
                Random.nextFloat() * 0.4f, // initial y
                Random.nextInt(16, 26) // size
            )
        }
    }

    var animFraction by remember { mutableStateOf(0f) }

    LaunchedEffect(isActive) {
        val startTime = System.currentTimeMillis()
        val duration = 1200L
        while (System.currentTimeMillis() - startTime < duration) {
            animFraction = (System.currentTimeMillis() - startTime).toFloat() / duration
            delay(16)
        }
        onAnimationEnd()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEachIndexed { i, (px, py, pSize) ->
            val curY = (py * h) + (animFraction * (h * 0.7f)) + (i * 8)
            val curX = (px * w) + sin((animFraction * 6f + i).toDouble()).toFloat() * 30f
            val alpha = (1f - animFraction).coerceIn(0f, 1f)

            drawCircle(
                color = SoftRose.copy(alpha = alpha * 0.75f),
                radius = pSize.toFloat(),
                center = Offset(curX, curY)
            )
            drawCircle(
                color = LavenderAccent.copy(alpha = alpha * 0.5f),
                radius = pSize * 0.6f,
                center = Offset(curX - 4f, curY - 4f)
            )
        }
    }
}
