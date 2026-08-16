package com.example.ui.screens

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
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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

/**
 * LoginScreen featuring a soft pastel gradient background, an integrated graceful
 * walking girl animation, a centered 'Welcome' header, and a prominent 'Get Started' action.
 */
@Composable
fun LoginScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PeriodViewModel? = null,
    isAmharic: Boolean = false
) {
    val userSettings = viewModel?.userSettings?.collectAsState()?.value
    var userNameInput by remember { mutableStateOf(userSettings?.userName ?: "") }
    var showNameField by remember { mutableStateOf(false) }

    var girlWalkState by remember { mutableStateOf(GirlWalkState.IDLE_WALK) }
    var walkSpeed by remember { mutableStateOf(1.0f) }
    var isCelebrating by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Smooth ambient background floating light transitions
    val infiniteTransition = rememberInfiniteTransition(label = "pastel_login_ambience")
    val glowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_glow_phase"
    )

    val runwayPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "runway_pulse"
    )

    fun handleGetStarted() {
        if (isCelebrating) return
        isCelebrating = true
        girlWalkState = GirlWalkState.CELEBRATE_POSE
        walkSpeed = 1.4f

        if (userNameInput.isNotBlank() && viewModel != null) {
            viewModel.updateUserName(userNameInput)
        }

        coroutineScope.launch {
            delay(1200)
            onGetStarted()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF0F5), // Lavender Blush
                        Color(0xFFFDEEF3), // Soft Rose Whisper
                        Color(0xFFF6ECF7), // Gentle Lilac
                        Color(0xFFFFF8FB), // Warm Pearl White
                        Color(0xFFFFF0F5)  // Soft Rose Base
                    )
                )
            )
            .testTag("login_screen")
    ) {
        // 1. Soft Ambient Radial Glow Atmosphere
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Floating soft rose orb
            val orb1X = w * 0.35f + sin(glowPhase.toDouble()).toFloat() * 35f
            val orb1Y = h * 0.22f + sin((glowPhase * 0.8).toDouble()).toFloat() * 25f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SoftRose.copy(alpha = 0.28f), Color.Transparent),
                    center = Offset(orb1X, orb1Y),
                    radius = 180.dp.toPx()
                ),
                center = Offset(orb1X, orb1Y),
                radius = 180.dp.toPx()
            )

            // Floating lavender mist orb
            val orb2X = w * 0.7f - sin((glowPhase * 0.9).toDouble()).toFloat() * 40f
            val orb2Y = h * 0.45f + sin(glowPhase.toDouble()).toFloat() * 30f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LavenderAccent.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(orb2X, orb2Y),
                    radius = 200.dp.toPx()
                ),
                center = Offset(orb2X, orb2Y),
                radius = 200.dp.toPx()
            )
        }

        // 2. Soft Scenic Boulevard Backdrop Overlay
        Image(
            painter = painterResource(id = R.drawable.img_walk_backdrop),
            contentDescription = "Scenic Sanctuary Backdrop",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .alpha(0.6f)
        )

        // Gradient Scrim blend
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.45f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 100f,
                        endY = 650f
                    )
                )
        )

        // 3. Floating Blossom Petal Particles
        PetalParticleEffect(
            isActive = true,
            modifier = Modifier.fillMaxSize()
        )

        // 4. Centered Main Interactive Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Badge Header
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(text = "🌸", fontSize = 14.sp)
                    Text(
                        text = if (isAmharic) "የሴቶች የጤናና ዑደት መጠበቂያ" else "FEMININE WELLNESS SANCTUARY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Graceful Walking Girl Animation Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .testTag("login_walking_girl_container")
            ) {
                // Illuminated Platform Glow Floor
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 22.dp)
                        .fillMaxWidth(0.85f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    SoftRose.copy(alpha = runwayPulse * 0.45f),
                                    LavenderAccent.copy(alpha = runwayPulse * 0.65f),
                                    SoftRose.copy(alpha = runwayPulse * 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Animated Walking Girl with Smooth Kinematic Physics
                AnimatedWalkingGirl(
                    state = girlWalkState,
                    walkSpeedMultiplier = walkSpeed,
                    size = 230.dp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Centered Welcome Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isAmharic) "እንኳን ደህና መጣሽ 🌸" else "Welcome ✨",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isAmharic)
                        "ወደ ግል የጤና፣ የውበትና የዑደት መከታተያ ቦታሽ በሰላም ግቢ።"
                    else
                        "Your graceful, private sanctuary for cycle tracking, wellness, and self-care.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Interactive Bottom Action Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AnimatedVisibility(
                    visible = showNameField,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut()
                ) {
                    OutlinedTextField(
                        value = userNameInput,
                        onValueChange = { userNameInput = it },
                        placeholder = {
                            Text(
                                if (isAmharic) "ስምሽን አስገቢ (ለምሳሌ፦ ሰላም)" else "Enter your name (e.g., Selam)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Name",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = SoftPinkBorder,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_name_text_field")
                    )
                }

                // Centered Prominent 'Get Started' Button
                GirlyPrimaryButton(
                    text = if (isAmharic) "ጀምሪ ✨" else "Get Started ✨",
                    onClick = { handleGetStarted() },
                    icon = "✨",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("login_get_started_button")
                )

                // Optional name personalization toggle
                if (!showNameField) {
                    TextButton(
                        onClick = { showNameField = true },
                        modifier = Modifier.testTag("personalize_login_button")
                    ) {
                        Text(
                            text = if (isAmharic) "✍️ ስም ማስተካከያ" else "✍️ Personalize with your name",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
