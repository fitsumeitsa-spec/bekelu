package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GirlyPrimaryButton
import com.example.ui.components.SoftCard
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftPinkSurfaceVariant
import com.example.ui.theme.SoftRose
import com.example.viewmodel.PeriodViewModel

@Composable
fun OnboardingScreen(
    viewModel: PeriodViewModel,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var selectedLanguage by remember { mutableStateOf("en") }
    var selectedCalendar by remember { mutableStateOf("ETHIOPIAN") }
    var cycleLength by remember { mutableIntStateOf(28) }
    var periodLength by remember { mutableIntStateOf(5) }

    val isAmharic = selectedLanguage == "am"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("onboarding_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Skip (for steps < 4)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Progress Dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 0..4) {
                        val isActive = i == currentStep
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (isActive) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isActive) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                )
                        )
                    }
                }

                if (currentStep < 4) {
                    TextButton(
                        onClick = { currentStep = 4 },
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = if (isAmharic) "እለፍ" else "Skip",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            // Step Content Animated
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "onboarding_content"
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (step) {
                        0 -> {
                            // Screen 1: Hero cycle illustration
                            Image(
                                painter = painterResource(id = R.drawable.img_onboarding_cycle),
                                contentDescription = "Cycle Illustration",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(240.dp)
                                    .clip(RoundedCornerShape(32.dp))
                                    .border(1.dp, SoftPinkBorder, RoundedCornerShape(32.dp))
                            )
                            Spacer(modifier = Modifier.height(28.dp))
                            Text(
                                text = if (isAmharic) "የዑደትሽ ጊዜ። የአንቺ የግል ቦታ 🌸" else "Your cycle. Your space. 🌸",
                                style = MaterialTheme.typography.displaySmall.copy(fontSize = 26.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isAmharic) "የወር አበባሽን በቀላሉ እና በውበት ለመረዳት የተዘጋጀ የግል ማስታወሻ።" else "A simple, gentle and beautiful way to understand your cycle.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        1 -> {
                            // Screen 2: Calendar illustration
                            Image(
                                painter = painterResource(id = R.drawable.img_onboarding_calendar),
                                contentDescription = "Calendar Illustration",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(240.dp)
                                    .clip(RoundedCornerShape(32.dp))
                                    .border(1.dp, SoftPinkBorder, RoundedCornerShape(32.dp))
                            )
                            Spacer(modifier = Modifier.height(28.dp))
                            Text(
                                text = if (isAmharic) "ዑደትሽን በትክክል እወቂ 🌷" else "Know your cycle 🌷",
                                style = MaterialTheme.typography.displaySmall.copy(fontSize = 26.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isAmharic) "የወር አበባሽን በመመዝገብ የዑደትሽን ሂደት እና የመራባት ቀናትሽን ተረጂ።" else "Track your periods, fertile window, and understand your natural rhythm.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        2 -> {
                            // Screen 3: Privacy illustration
                            Image(
                                painter = painterResource(id = R.drawable.img_onboarding_privacy),
                                contentDescription = "Privacy Illustration",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(240.dp)
                                    .clip(RoundedCornerShape(32.dp))
                                    .border(1.dp, SoftPinkBorder, RoundedCornerShape(32.dp))
                            )
                            Spacer(modifier = Modifier.height(28.dp))
                            Text(
                                text = if (isAmharic) "ግላዊነቱ የተጠበቀ 🔐" else "Private by design 🔐",
                                style = MaterialTheme.typography.displaySmall.copy(fontSize = 26.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isAmharic) "የጤና እና የዑደት መረጃዎችሽ በስልክሽ ላይ በሙሉ ሚስጥራዊነት ይቀመጣሉ።" else "Your personal cycle information stays safely on your own device.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        3 -> {
                            // Screen 4: Gentle reminders
                            Surface(
                                shape = RoundedCornerShape(32.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.size(240.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Text(text = "🌷", fontSize = 54.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (isAmharic) "የቀን ማስታወሻዎች" else "Gentle Reminders",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(28.dp))
                            Text(
                                text = if (isAmharic) "አትዘነጊም 🌷" else "Never forget 🌷",
                                style = MaterialTheme.typography.displaySmall.copy(fontSize = 26.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isAmharic) "የወር አበባሽ ሲቃረብ ረጋ ያሉ ማስታወሻዎችን በግል ተቀበይ።" else "Get gentle, discreet reminders when your period is approaching.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        4 -> {
                            // Screen 5: Preferences Setup (Language, Calendar, Cycle length)
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isAmharic) "ምርጫዎችሽን አስተካክይ ✨" else "Personalize Your Space ✨",
                                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 24.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(18.dp))

                                // Language Selector
                                SoftCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = if (isAmharic) "ቋንቋ / Language" else "Language / ቋንቋ",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Surface(
                                                onClick = { selectedLanguage = "en" },
                                                shape = RoundedCornerShape(14.dp),
                                                color = if (selectedLanguage == "en") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.weight(1f).height(44.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "English",
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (selectedLanguage == "en") Color.White else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                            Surface(
                                                onClick = { selectedLanguage = "am" },
                                                shape = RoundedCornerShape(14.dp),
                                                color = if (selectedLanguage == "am") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.weight(1f).height(44.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "አማርኛ (Amharic)",
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (selectedLanguage == "am") Color.White else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Calendar System Selector
                                SoftCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = if (isAmharic) "የቀን መቁጠሪያ" else "Calendar System",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Surface(
                                                onClick = { selectedCalendar = "ETHIOPIAN" },
                                                shape = RoundedCornerShape(14.dp),
                                                color = if (selectedCalendar == "ETHIOPIAN") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.weight(1f).height(44.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = if (isAmharic) "የኢትዮጵያ 🇪🇹" else "Ethiopian 🇪🇹",
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (selectedCalendar == "ETHIOPIAN") Color.White else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                            Surface(
                                                onClick = { selectedCalendar = "GREGORIAN" },
                                                shape = RoundedCornerShape(14.dp),
                                                color = if (selectedCalendar == "GREGORIAN") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.weight(1f).height(44.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = if (isAmharic) "ጎርጎሪያን" else "Gregorian",
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (selectedCalendar == "GREGORIAN") Color.White else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Cycle Steppers
                                SoftCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = if (isAmharic) "የዑደት ርዝመት" else "Cycle Length",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (isAmharic) "በተለምዶ 28 ቀናት" else "Usually 28 days",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = { if (cycleLength > 21) cycleLength-- }) {
                                                Text(text = "−", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            }
                                            Text(
                                                text = "$cycleLength ${if (isAmharic) "ቀን" else "days"}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            IconButton(onClick = { if (cycleLength < 45) cycleLength++ }) {
                                                Text(text = "+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    TextButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.testTag("onboarding_back_button")
                    ) {
                        Text(
                            text = if (isAmharic) "ወደኋላ" else "Back",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(60.dp))
                }

                if (currentStep < 4) {
                    GirlyPrimaryButton(
                        text = if (isAmharic) "ቀጥይ 🌸" else "Next 🌸",
                        icon = "→",
                        onClick = { currentStep++ },
                        modifier = Modifier
                            .widthIn(min = 140.dp)
                            .testTag("onboarding_next_button")
                    )
                } else {
                    GirlyPrimaryButton(
                        text = if (isAmharic) "ጀምሪ 🌸" else "Get Started 🌸",
                        icon = "✨",
                        onClick = {
                            viewModel.completeOnboarding(
                                language = selectedLanguage,
                                calendarSystem = selectedCalendar,
                                cycleLength = cycleLength,
                                periodLength = periodLength
                            )
                            onFinished()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .testTag("onboarding_finish_button")
                    )
                }
            }
        }
    }
}
