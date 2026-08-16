package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AnimatedWalkingGirl
import com.example.ui.components.GirlWalkState
import com.example.ui.components.GirlyPrimaryButton
import com.example.ui.components.PetalParticleEffect
import com.example.ui.components.SoftCard
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftPinkSurfaceVariant
import com.example.ui.theme.SoftRose
import com.example.viewmodel.PeriodViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun AppLockScreen(
    viewModel: PeriodViewModel,
    onUnlocked: () -> Unit,
    isAmharic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val isPinConfigured = !userSettings?.appLockPin.isNullOrBlank()

    var loginMode by remember { mutableStateOf(0) } // 0: Runway PIN, 1: Fast Pass
    var enteredPin by remember { mutableStateOf("") }
    var inputName by remember { mutableStateOf(userSettings?.userName ?: "Selam") }

    var girlWalkState by remember { mutableStateOf(GirlWalkState.IDLE_WALK) }
    var walkSpeed by remember { mutableStateOf(1.0f) }
    var isError by remember { mutableStateOf(false) }
    var isCelebrating by remember { mutableStateOf(false) }

    val shakeOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    // Ambient floating glow transitions for soft gradient atmosphere
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glows")
    val glowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_phase"
    )

    val runwayGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "runway_glow"
    )

    fun triggerSuccessCatwalk() {
        isCelebrating = true
        girlWalkState = GirlWalkState.CELEBRATE_POSE
        walkSpeed = 1.4f
        coroutineScope.launch {
            delay(1400)
            onUnlocked()
        }
    }

    fun triggerError() {
        isError = true
        girlWalkState = GirlWalkState.ERROR_SHAKE
        coroutineScope.launch {
            shakeOffset.animateTo(20f, spring(dampingRatio = 0.2f, stiffness = 700f))
            shakeOffset.animateTo(-20f, spring(dampingRatio = 0.2f, stiffness = 700f))
            shakeOffset.animateTo(10f, spring(dampingRatio = 0.3f, stiffness = 600f))
            shakeOffset.animateTo(0f, spring(dampingRatio = 0.4f, stiffness = 500f))
            delay(400)
            enteredPin = ""
            isError = false
            girlWalkState = GirlWalkState.IDLE_WALK
            walkSpeed = 1.0f
        }
    }

    fun handleKeyPress(key: String) {
        if (isCelebrating || enteredPin.length >= 4) return

        val newPin = enteredPin + key
        enteredPin = newPin

        // Accelerate stride dynamically on key press
        girlWalkState = GirlWalkState.EXCITED_STRUT
        walkSpeed = 1.5f

        coroutineScope.launch {
            delay(450)
            if (!isCelebrating && !isError) {
                girlWalkState = GirlWalkState.IDLE_WALK
                walkSpeed = 1.0f
            }
        }

        if (newPin.length == 4) {
            if (viewModel.unlockApp(newPin)) {
                triggerSuccessCatwalk()
            } else {
                triggerError()
            }
        }
    }

    fun handleBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF0F5), // Lavender blush
                        Color(0xFFFDEEF2), // Soft rose mist
                        Color(0xFFF7ECF8), // Gentle lilac
                        Color(0xFFFFF7FA)  // Warm pearl white
                    )
                )
            )
            .testTag("app_lock_screen")
    ) {
        // 1. Ambient Dynamic Glow Orbs Canvas (soft composed lighting)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Orb 1: Soft Rose drifting orb
            val orb1X = w * 0.3f + sin(glowPhase.toDouble()).toFloat() * 40f
            val orb1Y = h * 0.25f + sin((glowPhase * 0.7).toDouble()).toFloat() * 30f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SoftRose.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(orb1X, orb1Y),
                    radius = 160.dp.toPx()
                ),
                center = Offset(orb1X, orb1Y),
                radius = 160.dp.toPx()
            )

            // Orb 2: Lavender mist orb
            val orb2X = w * 0.75f - sin((glowPhase * 0.8).toDouble()).toFloat() * 50f
            val orb2Y = h * 0.45f + sin(glowPhase.toDouble()).toFloat() * 40f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LavenderAccent.copy(alpha = 0.28f), Color.Transparent),
                    center = Offset(orb2X, orb2Y),
                    radius = 180.dp.toPx()
                ),
                center = Offset(orb2X, orb2Y),
                radius = 180.dp.toPx()
            )
        }

        // 2. Scenic boulevard backdrop with soft alpha & gradient blend
        Image(
            painter = painterResource(id = R.drawable.img_walk_backdrop),
            contentDescription = "Scenic Boulevard",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .alpha(0.65f)
        )

        // Gradient fade over the scenic backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 120f,
                        endY = 700f
                    )
                )
        )

        // 3. Floating Blossom Petals Overlay
        PetalParticleEffect(
            isActive = true,
            modifier = Modifier.fillMaxSize()
        )

        // 4. Main Scrollable Content Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Premium Sanctuary Badge Header
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(text = "🌸", fontSize = 14.sp)
                    Text(
                        text = if (isAmharic) "የሴቶች የግል ውበትና ጤና" else "GIRL RUNWAY SANCTUARY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            // 5. THE ANIMATED WALKING GIRL (GRACEFUL SLOW-PACED STRUT)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .testTag("animated_walking_girl_container")
            ) {
                // Illuminated Runway Platform Floor
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 22.dp)
                        .fillMaxWidth(0.85f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    SoftRose.copy(alpha = runwayGlow * 0.45f),
                                    LavenderAccent.copy(alpha = runwayGlow * 0.65f),
                                    SoftRose.copy(alpha = runwayGlow * 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Animated Girl Component with smooth physics
                AnimatedWalkingGirl(
                    state = girlWalkState,
                    walkSpeedMultiplier = walkSpeed,
                    size = 220.dp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Welcome Text
            Text(
                text = if (isAmharic) "እንኳን ደህና መጣሽ 🌸" else "Welcome Back, Beautiful ✨",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAmharic) "ወደ ግል ጤና እና ማስታወሻ ቦታሽ ግቢ።" else "Step into your private, caring wellness space.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TAB SWITCHER: Runway PIN vs Quick Strut
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Tab 0: PIN Mode
                    Surface(
                        onClick = { loginMode = 0 },
                        shape = RoundedCornerShape(16.dp),
                        color = if (loginMode == 0) MaterialTheme.colorScheme.surface else Color.Transparent,
                        border = if (loginMode == 0) androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isAmharic) "🔑 የይለፍ ቃል (PIN)" else "🔑 Secret PIN",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (loginMode == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (loginMode == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Tab 1: Fast Pass / Name Mode
                    Surface(
                        onClick = { loginMode = 1 },
                        shape = RoundedCornerShape(16.dp),
                        color = if (loginMode == 1) MaterialTheme.colorScheme.surface else Color.Transparent,
                        border = if (loginMode == 1) androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isAmharic) "👠 ቀጥታ ግቢ (Walk In)" else "👠 Fast Strut",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (loginMode == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (loginMode == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // TAB CONTENT
            if (loginMode == 0) {
                // ==================== MODE 0: RUNWAY PIN PAD ====================
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                ) {
                    // 4 Glowing Flower PIN Indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        for (i in 0..3) {
                            val isFilled = i < enteredPin.length
                            val bulbScale = if (isFilled) 1.25f else 1.0f

                            Surface(
                                shape = CircleShape,
                                color = when {
                                    isError -> MaterialTheme.colorScheme.error
                                    isFilled -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                },
                                border = androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    if (isFilled) MaterialTheme.colorScheme.primary else SoftPinkBorder
                                ),
                                modifier = Modifier
                                    .size(20.dp)
                                    .scale(bulbScale)
                            ) {
                                if (isFilled) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "🌸",
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isError) {
                        Text(
                            text = if (isAmharic) "የተሳሳተ የይለፍ ቃል! እንደገና ሞክሪ" else "Incorrect PIN! Please try again.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Text(
                            text = if (!isPinConfigured) (if (isAmharic) "ነባሪ PIN: 1234 ወይም የራስሽን አስገቢ" else "Default PIN: 1234 (or any 4 digits)")
                            else (if (isAmharic) "4 አሃዝ አስገቢ" else "Enter 4-digit secret passcode"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stylized Glass Keypad
                    val keypadRows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("✨", "0", "DEL")
                    )

                    keypadRows.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(22.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 5.dp)
                        ) {
                            row.forEach { key ->
                                when (key) {
                                    "" -> {
                                        Spacer(modifier = Modifier.size(62.dp))
                                    }
                                    "✨" -> {
                                        // Quick guest bypass / hint
                                        Surface(
                                            onClick = {
                                                if (!isPinConfigured) {
                                                    enteredPin = "1234"
                                                    triggerSuccessCatwalk()
                                                } else {
                                                    viewModel.resetAppLockPin()
                                                    triggerSuccessCatwalk()
                                                }
                                            },
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder),
                                            modifier = Modifier.size(62.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(text = "✨", fontSize = 20.sp)
                                            }
                                        }
                                    }
                                    "DEL" -> {
                                        IconButton(
                                            onClick = { handleBackspace() },
                                            modifier = Modifier.size(62.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.Backspace,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    else -> {
                                        Surface(
                                            onClick = { handleKeyPress(key) },
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surface,
                                            border = androidx.compose.foundation.BorderStroke(1.5.dp, SoftPinkBorder),
                                            shadowElevation = 2.dp,
                                            modifier = Modifier
                                                .size(62.dp)
                                                .testTag("pin_key_$key")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = key,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ==================== MODE 1: FAST WALK IN & WELLNESS PROFILE ====================
                SoftCard(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isAmharic) "የግል ስምሽ ወይም መጠሪያሽ" else "Your Wellness Handle / Name",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = inputName,
                            onValueChange = { inputName = it },
                            placeholder = { Text(if (isAmharic) "ለምሳሌ፦ ሰላም / ቤቲ" else "e.g. Selam / Betty") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = "Name",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = SoftPinkBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_name_input")
                        )

                        GirlyPrimaryButton(
                            text = if (isAmharic) "ወደ ውስጥ በኩራት ግቢ 👠✨" else "Strut into Sanctuary 👠✨",
                            onClick = {
                                if (inputName.isNotBlank()) {
                                    viewModel.updateUserName(inputName)
                                }
                                triggerSuccessCatwalk()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .testTag("fast_walk_in_button")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
