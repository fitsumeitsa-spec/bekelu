package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.WaterDrop
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AnimatedWalkingGirl
import com.example.ui.components.GirlWalkState
import com.example.ui.components.GirlyPrimaryButton
import com.example.ui.components.LottieBloomingFlower
import com.example.ui.components.PetalParticleEffect
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftPinkSurfaceVariant
import com.example.ui.theme.SoftRose
import com.example.viewmodel.PeriodViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Enhanced Login & Welcome Screen with modern luxury styling, soft atmospheric glow,
 * language switcher, rich feature highlights, kinematic character animation, and smooth transitions.
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
    var showNameField by remember { mutableStateOf(userSettings?.userName?.isNotBlank() == true) }

    var girlWalkState by remember { mutableStateOf(GirlWalkState.IDLE_WALK) }
    var walkSpeed by remember { mutableStateOf(1.0f) }
    var isCelebrating by remember { mutableStateOf(false) }
    var isFlowerBloomed by remember { mutableStateOf(false) }

    // Graceful sequence: entrance walk concludes into full lotus flower bloom
    LaunchedEffect(Unit) {
        delay(1800)
        isFlowerBloomed = true
    }

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
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "runway_pulse"
    )

    val badgeShimmer by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badge_shimmer"
    )

    fun handleGetStarted() {
        if (isCelebrating) return
        isCelebrating = true
        girlWalkState = GirlWalkState.CELEBRATE_POSE
        walkSpeed = 1.4f

        if (userNameInput.isNotBlank() && viewModel != null) {
            viewModel.updateUserName(userNameInput.trim())
        }

        coroutineScope.launch {
            delay(1100)
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
                        Color(0xFFFDEEF4), // Soft Rose Whisper
                        Color(0xFFF7ECF8), // Gentle Lilac
                        Color(0xFFFFF8FB), // Warm Pearl White
                        Color(0xFFFFF0F5)  // Soft Rose Base
                    )
                )
            )
            .testTag("login_screen")
    ) {
        // 1. Ambient Radial Glow Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Floating soft rose orb
            val orb1X = w * 0.35f + sin(glowPhase.toDouble()).toFloat() * 35f
            val orb1Y = h * 0.22f + sin((glowPhase * 0.8).toDouble()).toFloat() * 25f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SoftRose.copy(alpha = 0.32f), Color.Transparent),
                    center = Offset(orb1X, orb1Y),
                    radius = 200.dp.toPx()
                ),
                center = Offset(orb1X, orb1Y),
                radius = 200.dp.toPx()
            )

            // Floating lavender mist orb
            val orb2X = w * 0.72f - sin((glowPhase * 0.9).toDouble()).toFloat() * 40f
            val orb2Y = h * 0.48f + sin(glowPhase.toDouble()).toFloat() * 30f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LavenderAccent.copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(orb2X, orb2Y),
                    radius = 220.dp.toPx()
                ),
                center = Offset(orb2X, orb2Y),
                radius = 220.dp.toPx()
            )
        }

        // 2. Soft Scenic Boulevard Backdrop
        Image(
            painter = painterResource(id = R.drawable.img_walk_backdrop),
            contentDescription = "Scenic Sanctuary Backdrop",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .alpha(0.55f)
        )

        // Gradient Scrim blend
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.40f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.90f),
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 80f,
                        endY = 620f
                    )
                )
        )

        // 3. Floating Petal Particle Effect
        PetalParticleEffect(
            isActive = true,
            modifier = Modifier.fillMaxSize()
        )

        // 4. Main Scrollable Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Navigation & Branding Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Brand Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🌸", fontSize = 14.sp)
                        Text(
                            text = "MELA",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.8.sp
                        )
                    }
                }

                // Quick Language Switcher Pill
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // English option
                        val isEng = !isAmharic
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isEng) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { viewModel?.updateLanguage("en") }
                        ) {
                            Text(
                                text = "EN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isEng) FontWeight.Bold else FontWeight.Medium,
                                color = if (isEng) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Amharic option
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isAmharic) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { viewModel?.updateLanguage("am") }
                        ) {
                            Text(
                                text = "አማ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isAmharic) FontWeight.Bold else FontWeight.Medium,
                                color = if (isAmharic) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Animated Walking Stage
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .testTag("login_walking_girl_container")
            ) {
                // Illuminated Platform Floor
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(0.85f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    SoftRose.copy(alpha = runwayPulse * 0.45f),
                                    LavenderAccent.copy(alpha = runwayPulse * 0.70f),
                                    SoftRose.copy(alpha = runwayPulse * 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Lottie Graceful Blooming Flower Aura
                LottieBloomingFlower(
                    isVisible = isFlowerBloomed,
                    size = 175.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .testTag("blooming_flower_lottie")
                )

                // Animated Walking Girl
                AnimatedWalkingGirl(
                    state = girlWalkState,
                    walkSpeedMultiplier = walkSpeed,
                    size = 220.dp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Glassmorphic Welcome Card
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 460.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Sub-badge
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = if (isAmharic) "የጤናና ዑደት መጠበቂያ ✨" else "Holistic Cycle & Wellness ✨",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title
                    Text(
                        text = if (isAmharic) "እንኳን ደህና መጡ 🌸" else "Welcome to Mela ✨",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subtitle
                    Text(
                        text = if (isAmharic)
                            "የወር አበባ ዑደትዎን በኢትዮጵያና ግሪጎሪያን ዘመን አቆጣጠር በምቾት እና በሚስጥር ይከታተሉ።"
                        else
                            "Your private, graceful sanctuary for cycle tracking, Ethiopian & Gregorian calendars, and daily wellness.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Feature Pill Highlights Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FeatureChip(
                            icon = "🩸",
                            title = if (isAmharic) "ዑደት ትንበያ" else "Cycle Sync"
                        )
                        FeatureChip(
                            icon = "📅",
                            title = if (isAmharic) "ባለሁለት አቆጣጠር" else "Dual Calendar"
                        )
                        FeatureChip(
                            icon = "🔒",
                            title = if (isAmharic) "100% ሚስጥራዊ" else "100% Private"
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Name Personalization Input (Smooth Expandable)
                    AnimatedVisibility(
                        visible = showNameField,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = userNameInput,
                                onValueChange = { userNameInput = it },
                                placeholder = {
                                    Text(
                                        if (isAmharic) "ስምዎን ያስገቡ (ለምሳሌ፦ ሰላም)" else "Enter your name (e.g. Selam)",
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
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_name_text_field")
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Get Started Button
                    GirlyPrimaryButton(
                        text = if (isAmharic) "ይጀምሩ ✨" else "Get Started ✨",
                        onClick = { handleGetStarted() },
                        icon = "✨",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("login_get_started_button")
                    )

                    // Personalize Name button toggle
                    if (!showNameField) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(
                            onClick = { showNameField = true },
                            modifier = Modifier.testTag("personalize_login_button")
                        ) {
                            Text(
                                text = if (isAmharic) "✍️ ስም ማበጀት (Personalize)" else "✍️ Personalize with your name",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom On-device Security Assurance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Security",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (isAmharic)
                        "መረጃዎ በስልክዎ ላይ ብቻ ደህንነቱ ተጠብቆ ይቆያል"
                    else
                        "100% offline & stored securely on your device",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun FeatureChip(
    icon: String,
    title: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder.copy(alpha = 0.5f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
