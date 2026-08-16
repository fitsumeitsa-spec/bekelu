package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftPinkSurfaceVariant
import com.example.ui.theme.SoftRose
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class GirlWalkState {
    IDLE_WALK,
    EXCITED_STRUT,
    CELEBRATE_POSE,
    ERROR_SHAKE
}

data class FootstepSparkle(
    val id: Long,
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val maxLife: Float = 1f,
    var life: Float = 0f
)

@Composable
fun AnimatedWalkingGirl(
    state: GirlWalkState = GirlWalkState.IDLE_WALK,
    walkSpeedMultiplier: Float = 1.0f,
    modifier: Modifier = Modifier,
    size: Dp = 230.dp
) {
    // Graceful, slow-paced walk cycle timing (1600ms for graceful stroll)
    val cycleDuration = when (state) {
        GirlWalkState.EXCITED_STRUT -> 850
        GirlWalkState.CELEBRATE_POSE -> 1600
        GirlWalkState.ERROR_SHAKE -> 450
        GirlWalkState.IDLE_WALK -> 1600
    }

    val infiniteTransition = rememberInfiniteTransition(label = "graceful_walk_cycle")

    // Continuous smooth walk phase (0 to 2*PI radians)
    val walkPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (cycleDuration / walkSpeedMultiplier).toInt().coerceAtLeast(300),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "walk_phase"
    )

    // Gentle floating dress breeze animation
    val breezePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "breeze_phase"
    )

    // Natural eye blink animation (every 3.6s)
    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )
    val isBlinking = blinkProgress in 0.94f..0.98f

    // Soft glowing sparkles trail
    val sparkles = remember { mutableStateListOf<FootstepSparkle>() }

    // Generates delicate floral sparkles on graceful footsteps
    LaunchedEffect(walkPhase, state) {
        if (state == GirlWalkState.CELEBRATE_POSE) {
            if (sparkles.size < 24) {
                sparkles.add(
                    FootstepSparkle(
                        id = System.currentTimeMillis() + Random.nextLong(1000),
                        x = Random.nextFloat() * 180f - 90f,
                        y = Random.nextFloat() * 80f - 40f,
                        color = listOf(SoftRose, LavenderAccent, Color(0xFFFFD1DC), Color(0xFFFFF0F5), Color(0xFFFFE4B5)).random(),
                        size = Random.nextFloat() * 7f + 4f
                    )
                )
            }
        } else {
            val strideSine = sin(walkPhase.toDouble())
            if (abs(strideSine) > 0.92 && sparkles.size < 7) {
                sparkles.add(
                    FootstepSparkle(
                        id = System.currentTimeMillis() + Random.nextLong(1000),
                        x = if (strideSine > 0) 12f else -12f,
                        y = 60f + Random.nextFloat() * 6f,
                        color = listOf(
                            SoftRose.copy(alpha = 0.7f),
                            LavenderAccent.copy(alpha = 0.7f),
                            Color(0xFFFFD54F).copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.8f)
                        ).random(),
                        size = Random.nextFloat() * 5f + 3f
                    )
                )
            }
        }

        // Decay sparkles smoothly
        val toRemove = mutableListOf<FootstepSparkle>()
        sparkles.forEach { sp ->
            sp.life += 0.06f
            if (sp.life >= sp.maxLife) toRemove.add(sp)
        }
        sparkles.removeAll(toRemove)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = this.size.width
            val canvasH = this.size.height
            val centerX = canvasW / 2f
            val groundY = canvasH * 0.84f

            // Graceful vertical body bobbing (smooth organic curve)
            val verticalBounce = if (state == GirlWalkState.CELEBRATE_POSE) {
                sin(walkPhase * 2f) * 6f
            } else {
                abs(sin(walkPhase.toDouble()).toFloat()) * 4.5f
            }

            // Soft Ethereal Shadow on the ground
            val shadowWidth = 64.dp.toPx() * (1f - (verticalBounce / 40f))
            drawOval(
                color = Color(0xFF6D4A63).copy(alpha = 0.12f),
                topLeft = Offset(centerX - shadowWidth / 2f, groundY - 3.dp.toPx()),
                size = Size(shadowWidth, 7.dp.toPx())
            )

            // Draw Sparkling footstep trail
            sparkles.forEach { sp ->
                val alpha = (1f - sp.life / sp.maxLife).coerceIn(0f, 1f)
                val spY = groundY + sp.y - (sp.life * 20f)
                val spX = centerX + sp.x + (if (sp.x > 0) sp.life * 8f else -sp.life * 8f)

                drawCircle(
                    color = sp.color.copy(alpha = alpha),
                    radius = sp.size * (1f - sp.life * 0.35f),
                    center = Offset(spX, spY)
                )
            }

            // Draw the graceful character with vertical bobbing translation
            withTransform({
                translate(left = 0f, top = -verticalBounce)
            }) {
                drawGracefulWalkingGirl(
                    centerX = centerX,
                    groundY = groundY,
                    phase = walkPhase,
                    breezePhase = breezePhase,
                    state = state,
                    isBlinking = isBlinking
                )
            }
        }
    }
}

private fun DrawScope.drawGracefulWalkingGirl(
    centerX: Float,
    groundY: Float,
    phase: Float,
    breezePhase: Float,
    state: GirlWalkState,
    isBlinking: Boolean
) {
    val phaseSin = sin(phase.toDouble()).toFloat()
    val phaseCos = cos(phase.toDouble()).toFloat()
    val breezeSin = sin(breezePhase.toDouble()).toFloat()

    // Premium Soft Feminine Color Palette
    val skinTone = Color(0xFFFDE5D4)
    val skinShadow = Color(0xFFF0CCA8)
    val hairColor = Color(0xFF38231E) // Rich dark espresso
    val dressColor = SoftRose
    val dressAccent = Color(0xFFD66986)
    val dressGlow = Color(0xFFFFF0F5)
    val bootsColor = Color(0xFF533B4A)
    val handbagColor = LavenderAccent
    val beretColor = SoftRose

    // Key anatomical coordinates
    val hipY = groundY - 48.dp.toPx()
    val waistY = hipY - 14.dp.toPx()
    val shoulderY = waistY - 24.dp.toPx()
    val neckY = shoulderY - 6.dp.toPx()
    val headCenterY = neckY - 18.dp.toPx()
    val headRadius = 15.dp.toPx()

    // Smooth hip sway and gentle torso tilt
    val hipSway = if (state == GirlWalkState.CELEBRATE_POSE) sin(phase * 2f) * 3f else phaseSin * 4.5f
    val torsoTilt = if (state == GirlWalkState.ERROR_SHAKE) phaseSin * 5f else phaseCos * 2.0f

    // -------------------------------------------------------------
    // 1. BACK LEG (Gracefully moving behind skirt)
    // -------------------------------------------------------------
    val backLegAngle = if (state == GirlWalkState.CELEBRATE_POSE) {
        -12f + sin(phase) * 8f
    } else {
        -phaseSin * 24f
    }
    drawLeg(
        startX = centerX - 3.5.dp.toPx(),
        startY = hipY,
        angleDeg = backLegAngle,
        length = 42.dp.toPx(),
        skinColor = skinShadow,
        shoeColor = bootsColor
    )

    // -------------------------------------------------------------
    // 2. BACK ARM & SWINGING HANDBAG
    // -------------------------------------------------------------
    val backArmAngle = if (state == GirlWalkState.CELEBRATE_POSE) {
        -135f + sin(phase * 2f) * 12f
    } else {
        phaseSin * 22f
    }
    drawArmWithHandbag(
        startX = centerX - 6.dp.toPx(),
        startY = shoulderY + 2.dp.toPx(),
        angleDeg = backArmAngle,
        skinColor = skinShadow,
        sleeveColor = dressColor,
        handbagColor = handbagColor,
        hasHandbag = true,
        phase = phase
    )

    // -------------------------------------------------------------
    // 3. FLOWING HAIR (Silky Multi-Strand Back Layer)
    // -------------------------------------------------------------
    val hairSway = if (state == GirlWalkState.CELEBRATE_POSE) sin(phase * 3f) * 10f else (-phaseSin * 8f + breezeSin * 4f)
    drawSilkyHair(
        headX = centerX + hipSway * 0.3f,
        headY = headCenterY,
        sway = hairSway,
        color = hairColor
    )

    // -------------------------------------------------------------
    // 4. TORSO & ELEGANT DRESS
    // -------------------------------------------------------------
    val bodyPath = Path().apply {
        moveTo(centerX - 9.5.dp.toPx() + torsoTilt, shoulderY)
        lineTo(centerX + 9.5.dp.toPx() + torsoTilt, shoulderY)
        lineTo(centerX + 6.5.dp.toPx() + hipSway, waistY)
        lineTo(centerX - 6.5.dp.toPx() + hipSway, waistY)
        close()
    }
    drawPath(path = bodyPath, color = dressColor)

    // Belt / Waistline band
    drawRoundRect(
        color = bootsColor,
        topLeft = Offset(centerX - 7.5.dp.toPx() + hipSway, waistY - 2.dp.toPx()),
        size = Size(15.dp.toPx(), 4.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
    )
    // Delicate gold jewel buckle
    drawCircle(
        color = Color(0xFFFFD54F),
        radius = 2.dp.toPx(),
        center = Offset(centerX + hipSway, waistY)
    )

    // Flowing Flared Skirt (swings with step momentum and gentle breeze)
    val skirtSwing = if (state == GirlWalkState.CELEBRATE_POSE) sin(phase * 2f) * 6f else (phaseSin * 5f + breezeSin * 3f)
    val skirtBottomY = hipY + 12.dp.toPx()
    val skirtPath = Path().apply {
        moveTo(centerX - 6.5.dp.toPx() + hipSway, waistY)
        lineTo(centerX + 6.5.dp.toPx() + hipSway, waistY)
        // Flared bottom edge with curved hem
        cubicTo(
            centerX + 12.dp.toPx() + skirtSwing, waistY + 6.dp.toPx(),
            centerX + 18.dp.toPx() + skirtSwing, skirtBottomY - 2.dp.toPx(),
            centerX + 17.dp.toPx() + skirtSwing, skirtBottomY
        )
        lineTo(centerX - 17.dp.toPx() + skirtSwing, skirtBottomY)
        cubicTo(
            centerX - 18.dp.toPx() + skirtSwing, skirtBottomY - 2.dp.toPx(),
            centerX - 12.dp.toPx() + skirtSwing, waistY + 6.dp.toPx(),
            centerX - 6.5.dp.toPx() + hipSway, waistY
        )
        close()
    }
    drawPath(path = skirtPath, color = dressColor)

    // Skirt pleat fold lines
    drawLine(
        color = dressAccent.copy(alpha = 0.7f),
        start = Offset(centerX - 2.dp.toPx() + hipSway, waistY),
        end = Offset(centerX - 6.dp.toPx() + skirtSwing, skirtBottomY),
        strokeWidth = 1.5.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        color = dressAccent.copy(alpha = 0.7f),
        start = Offset(centerX + 2.dp.toPx() + hipSway, waistY),
        end = Offset(centerX + 6.dp.toPx() + skirtSwing, skirtBottomY),
        strokeWidth = 1.5.dp.toPx(),
        cap = StrokeCap.Round
    )

    // -------------------------------------------------------------
    // 5. FRONT LEG (Graceful forward stride)
    // -------------------------------------------------------------
    val frontLegAngle = if (state == GirlWalkState.CELEBRATE_POSE) {
        10f + cos(phase) * 6f
    } else {
        phaseSin * 24f
    }
    drawLeg(
        startX = centerX + 3.5.dp.toPx(),
        startY = hipY,
        angleDeg = frontLegAngle,
        length = 42.dp.toPx(),
        skinColor = skinTone,
        shoeColor = bootsColor
    )

    // -------------------------------------------------------------
    // 6. NECK, FACE, ROSY CHEEKS & CHIC BERET
    // -------------------------------------------------------------
    // Neck
    drawLine(
        color = skinTone,
        start = Offset(centerX + torsoTilt, shoulderY),
        end = Offset(centerX + torsoTilt, neckY),
        strokeWidth = 5.dp.toPx(),
        cap = StrokeCap.Round
    )

    val headX = centerX + torsoTilt + hipSway * 0.2f
    // Face
    drawCircle(
        color = skinTone,
        radius = headRadius,
        center = Offset(headX, headCenterY)
    )

    // Soft Rosy Cheeks
    drawCircle(
        color = SoftRose.copy(alpha = 0.55f),
        radius = 3.5.dp.toPx(),
        center = Offset(headX - 6.dp.toPx(), headCenterY + 4.dp.toPx())
    )
    drawCircle(
        color = SoftRose.copy(alpha = 0.55f),
        radius = 3.5.dp.toPx(),
        center = Offset(headX + 6.dp.toPx(), headCenterY + 4.dp.toPx())
    )

    // Expressive Eyes (Blinking, Sparkling, or Smiling)
    if (isBlinking) {
        // Happy closed eyes
        drawArc(
            color = hairColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(headX - 8.dp.toPx(), headCenterY - 2.dp.toPx()),
            size = Size(5.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = hairColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(headX + 3.dp.toPx(), headCenterY - 2.dp.toPx()),
            size = Size(5.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )
    } else if (state == GirlWalkState.CELEBRATE_POSE) {
        // Joyful wink
        drawCircle(
            color = hairColor,
            radius = 2.2.dp.toPx(),
            center = Offset(headX - 5.dp.toPx(), headCenterY)
        )
        drawArc(
            color = hairColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(headX + 3.dp.toPx(), headCenterY - 1.dp.toPx()),
            size = Size(5.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )
    } else {
        // Big glossy gentle anime eyes with white catchlight highlights
        drawCircle(
            color = hairColor,
            radius = 2.2.dp.toPx(),
            center = Offset(headX - 5.dp.toPx(), headCenterY)
        )
        drawCircle(
            color = Color.White,
            radius = 0.8.dp.toPx(),
            center = Offset(headX - 5.8.dp.toPx(), headCenterY - 0.8.dp.toPx())
        )

        drawCircle(
            color = hairColor,
            radius = 2.2.dp.toPx(),
            center = Offset(headX + 5.dp.toPx(), headCenterY)
        )
        drawCircle(
            color = Color.White,
            radius = 0.8.dp.toPx(),
            center = Offset(headX + 4.2.dp.toPx(), headCenterY - 0.8.dp.toPx())
        )
    }

    // Soft Gentle Smile
    drawArc(
        color = Color(0xFF8D3B4F),
        startAngle = 10f,
        sweepAngle = 160f,
        useCenter = false,
        topLeft = Offset(headX - 3.dp.toPx(), headCenterY + 4.dp.toPx()),
        size = Size(6.dp.toPx(), 3.5.dp.toPx()),
        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
    )

    // Bangs / Hair Fringe
    val bangsPath = Path().apply {
        moveTo(headX - headRadius, headCenterY - 2.dp.toPx())
        cubicTo(
            headX - 6.dp.toPx(), headCenterY + 4.dp.toPx(),
            headX, headCenterY - 4.dp.toPx(),
            headX + headRadius, headCenterY - 2.dp.toPx()
        )
        lineTo(headX + headRadius, headCenterY - headRadius)
        lineTo(headX - headRadius, headCenterY - headRadius)
        close()
    }
    drawPath(path = bangsPath, color = hairColor)

    // French Chic Beret
    rotate(degrees = -10f + torsoTilt * 0.5f, pivot = Offset(headX, headCenterY - headRadius)) {
        drawOval(
            color = beretColor,
            topLeft = Offset(headX - headRadius * 1.25f, headCenterY - headRadius * 1.35f),
            size = Size(headRadius * 2.5f, headRadius * 0.95f)
        )
        // Stalk
        drawLine(
            color = beretColor,
            start = Offset(headX, headCenterY - headRadius * 1.35f),
            end = Offset(headX - 1.dp.toPx(), headCenterY - headRadius * 1.6f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        // Golden flower pin on beret
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = 2.dp.toPx(),
            center = Offset(headX + 8.dp.toPx(), headCenterY - headRadius * 0.9f)
        )
    }

    // -------------------------------------------------------------
    // 7. FRONT ARM (In front of body)
    // -------------------------------------------------------------
    val frontArmAngle = if (state == GirlWalkState.CELEBRATE_POSE) {
        -125f - sin(phase * 2f) * 12f
    } else {
        -phaseSin * 22f
    }
    drawArmWithHandbag(
        startX = centerX + 6.dp.toPx(),
        startY = shoulderY + 2.dp.toPx(),
        angleDeg = frontArmAngle,
        skinColor = skinTone,
        sleeveColor = dressColor,
        handbagColor = handbagColor,
        hasHandbag = false,
        phase = phase
    )
}

private fun DrawScope.drawLeg(
    startX: Float,
    startY: Float,
    angleDeg: Float,
    length: Float,
    skinColor: Color,
    shoeColor: Color
) {
    val angleRad = Math.toRadians((angleDeg + 90).toDouble())
    val thighLen = length * 0.52f
    val calfLen = length * 0.48f

    val kneeX = startX + (cos(angleRad) * thighLen).toFloat()
    val kneeY = startY + (sin(angleRad) * thighLen).toFloat()

    val calfAngleRad = if (angleDeg > 0) {
        angleRad - 0.22
    } else {
        angleRad + 0.12
    }
    val footX = kneeX + (cos(calfAngleRad) * calfLen).toFloat()
    val footY = kneeY + (sin(calfAngleRad) * calfLen).toFloat()

    // Thigh
    drawLine(
        color = skinColor,
        start = Offset(startX, startY),
        end = Offset(kneeX, kneeY),
        strokeWidth = 6.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Calf
    drawLine(
        color = skinColor,
        start = Offset(kneeX, kneeY),
        end = Offset(footX, footY),
        strokeWidth = 5.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Chic Boot / Heel
    val bootTipX = footX + 8.dp.toPx()
    val bootPath = Path().apply {
        moveTo(footX - 3.dp.toPx(), footY - 6.dp.toPx())
        lineTo(footX + 3.dp.toPx(), footY - 6.dp.toPx())
        lineTo(bootTipX, footY + 2.dp.toPx())
        lineTo(footX - 4.dp.toPx(), footY + 2.dp.toPx())
        close()
    }
    drawPath(path = bootPath, color = shoeColor)
}

private fun DrawScope.drawArmWithHandbag(
    startX: Float,
    startY: Float,
    angleDeg: Float,
    skinColor: Color,
    sleeveColor: Color,
    handbagColor: Color,
    hasHandbag: Boolean,
    phase: Float
) {
    val angleRad = Math.toRadians((angleDeg + 90).toDouble())
    val armLength = 32.dp.toPx()
    val elbowLength = armLength * 0.5f

    val elbowX = startX + (cos(angleRad) * elbowLength).toFloat()
    val elbowY = startY + (sin(angleRad) * elbowLength).toFloat()

    val handX = startX + (cos(angleRad) * armLength).toFloat()
    val handY = startY + (sin(angleRad) * armLength).toFloat()

    // Sleeve
    drawLine(
        color = sleeveColor,
        start = Offset(startX, startY),
        end = Offset(elbowX, elbowY),
        strokeWidth = 5.5.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Forearm
    drawLine(
        color = skinColor,
        start = Offset(elbowX, elbowY),
        end = Offset(handX, handY),
        strokeWidth = 4.5.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Hand
    drawCircle(
        color = skinColor,
        radius = 2.5.dp.toPx(),
        center = Offset(handX, handY)
    )

    // Chic Handbag
    if (hasHandbag) {
        val bagSwingAngle = sin(phase.toDouble() - 0.4).toFloat() * 15f
        rotate(degrees = bagSwingAngle, pivot = Offset(handX, handY)) {
            // Strap
            drawLine(
                color = Color(0xFF6D4A63),
                start = Offset(handX, handY),
                end = Offset(handX - 2.dp.toPx(), handY + 12.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )
            // Bag body
            drawRoundRect(
                color = handbagColor,
                topLeft = Offset(handX - 9.dp.toPx(), handY + 12.dp.toPx()),
                size = Size(14.dp.toPx(), 11.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
            // Gold clasp
            drawCircle(
                color = Color(0xFFFFD54F),
                radius = 1.5.dp.toPx(),
                center = Offset(handX - 2.dp.toPx(), handY + 16.dp.toPx())
            )
        }
    }
}

private fun DrawScope.drawSilkyHair(
    headX: Float,
    headY: Float,
    sway: Float,
    color: Color
) {
    // Flowing ponytail path with dual curve
    val ponytailPath = Path().apply {
        moveTo(headX - 8.dp.toPx(), headY - 4.dp.toPx())
        cubicTo(
            headX - 24.dp.toPx() + sway, headY + 4.dp.toPx(),
            headX - 20.dp.toPx() + sway * 1.3f, headY + 24.dp.toPx(),
            headX - 12.dp.toPx() + sway * 0.8f, headY + 34.dp.toPx()
        )
        cubicTo(
            headX - 16.dp.toPx() + sway * 0.5f, headY + 20.dp.toPx(),
            headX - 14.dp.toPx(), headY + 8.dp.toPx(),
            headX - 4.dp.toPx(), headY
        )
        close()
    }
    drawPath(path = ponytailPath, color = color)

    // Ribbon scrunchie
    drawCircle(
        color = SoftRose,
        radius = 3.dp.toPx(),
        center = Offset(headX - 8.dp.toPx(), headY - 3.dp.toPx())
    )
}
