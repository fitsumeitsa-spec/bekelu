package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Flare
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyLogRecord
import com.example.ui.components.CycleRing
import com.example.ui.components.FlowSelector
import com.example.ui.components.GirlyPrimaryButton
import com.example.ui.components.MoodSelector
import com.example.ui.components.PrivacyCard
import com.example.ui.components.SoftCard
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.SoftFertile
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftPinkSurfaceVariant
import com.example.ui.theme.SoftRose
import com.example.util.AppLanguage
import com.example.util.CalendarSystem
import com.example.util.CycleEngine
import com.example.util.EthiopianDate
import com.example.util.Localization
import com.example.viewmodel.PeriodViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PeriodViewModel,
    onNavigateToCalendar: () -> Unit,
    onNavigateToDailyLog: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val cycleStatus by viewModel.cycleStatus.collectAsState()
    val allLogs by viewModel.allDailyLogs.collectAsState()
    val today = LocalDate.now()
    val todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val todayLog = allLogs.firstOrNull { it.date == todayStr }

    val isAmharic = userSettings?.language == "am"
    val isEthiopianCalendar = userSettings?.calendarSystem == "ETHIOPIAN"

    var showLogPeriodSheet by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val hour = java.time.LocalTime.now().hour
    val greeting = Localization.getGreeting(hour, isAmharic)
    val subtitle = Localization.getDailySubtitle(isAmharic)

    // Formatted expected date
    val expectedDateDisplay = if (isEthiopianCalendar) {
        val ethDate = EthiopianDate.fromGregorian(cycleStatus.expectedNextPeriodDate)
        ethDate.format(isAmharic)
    } else {
        cycleStatus.expectedNextPeriodDate.format(DateTimeFormatter.ofPattern("MMM d"))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top App Bar / Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 26.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Girl Walk Style Lock / Login Trigger Button
                    Surface(
                        onClick = { viewModel.lockAppForced() },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.testTag("girl_walk_lock_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = "👠", fontSize = 14.sp)
                            Text(
                                text = if (isAmharic) "ቆልፊ" else "Lock",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Quick Calendar Mode indicator badge
                    Surface(
                        onClick = {
                            val newSys = if (isEthiopianCalendar) "GREGORIAN" else "ETHIOPIAN"
                            viewModel.updateCalendarSystem(newSys)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.testTag("calendar_toggle_badge")
                    ) {
                        Text(
                            text = if (isEthiopianCalendar) "🇪🇹" else "📅",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("settings_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // NEXT PERIOD CARD (Bold Typography Theme)
        item {
            SoftCard(
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("next_period_card"),
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Top row: Bold Countdown on Left, Cycle Ring on Right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Column: Bold Typography Countdown
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = Localization.getNextPeriodTitle(isAmharic).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.2.sp
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${cycleStatus.daysUntilNextPeriod}",
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontSize = 48.sp,
                                        lineHeight = 48.sp
                                    ),
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (isAmharic) "ቀናት" else "days",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            Text(
                                text = Localization.getExpectedDateText(expectedDateDisplay, isAmharic),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }

                        // Right: Cycle Ring visual
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(112.dp)
                        ) {
                            CycleRing(
                                currentDay = cycleStatus.currentCycleDay,
                                totalDays = cycleStatus.totalCycleLength,
                                phaseText = Localization.getPhaseName(cycleStatus.phase.rawName, isAmharic),
                                cycleDayLabel = Localization.getCycleDayLabel(isAmharic),
                                size = 112.dp,
                                strokeWidth = 8.dp,
                                modifier = Modifier.testTag("cycle_ring")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Card Bottom Divider & Status Summary
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Localization.getCycleDayText(
                                cycleStatus.currentCycleDay,
                                cycleStatus.totalCycleLength,
                                isAmharic
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Regularity badge
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = com.example.ui.theme.SoftSuccess.copy(alpha = 0.14f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                com.example.ui.theme.SoftSuccess.copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                text = Localization.getCycleRegularityText(cycleStatus.regularity, isAmharic) + " ✨",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = com.example.ui.theme.SoftSuccess,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Phase insight message
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = Localization.getPhaseDescription(cycleStatus.phase.rawName, isAmharic),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        // TODAY'S MOOD SECTION (Bold Typography Theme)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Localization.getHowAreYouFeeling(isAmharic),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isAmharic) "ዛሬ" else "HOW ARE YOU?",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    )
                }

                MoodSelector(
                    moods = PeriodViewModel.ALL_MOODS,
                    selectedMoodKey = todayLog?.mood,
                    onMoodSelected = { moodKey ->
                        viewModel.updateMood(moodKey)
                    },
                    isAmharic = isAmharic,
                    modifier = Modifier.testTag("mood_selector_row")
                )
            }
        }

        // TODAY'S TRACKING / CHECK-IN CARD
        item {
            SoftCard(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("today_checkin_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Localization.getTodayCheckIn(isAmharic).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )

                        TextButton(
                            onClick = onNavigateToDailyLog,
                            modifier = Modifier.testTag("edit_full_log_button")
                        ) {
                            Text(
                                text = if (isAmharic) "ሙሉ ማስታወሻ →" else "Open Journal →",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Flow Row Container
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "🩸", fontSize = 18.sp)
                                    Text(
                                        text = if (isAmharic) "የወር አበባ ፍሰት" else "Flow",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                    )
                                ) {
                                    Text(
                                        text = todayLog?.flow ?: (if (isAmharic) "ምንም" else "None"),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowSelector(
                                selectedFlow = todayLog?.flow,
                                onFlowSelected = { selectedFlow ->
                                    viewModel.saveDailyLog(
                                        date = today,
                                        mood = todayLog?.mood,
                                        flow = selectedFlow,
                                        symptoms = todayLog?.symptoms ?: emptyList(),
                                        energy = todayLog?.energy,
                                        notes = todayLog?.notes ?: ""
                                    )
                                },
                                isAmharic = isAmharic,
                                modifier = Modifier.testTag("home_flow_selector")
                            )
                        }
                    }

                    // Symptoms Row Container
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.background,
                        onClick = onNavigateToDailyLog,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "🌸", fontSize = 18.sp)
                                Column {
                                    Text(
                                        text = if (isAmharic) "ምልክቶች" else "Symptoms",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val symptomsText = if (todayLog?.symptoms.isNullOrEmpty()) {
                                        if (isAmharic) "ምልክት ጨምሪ" else "Add symptoms"
                                    } else {
                                        todayLog!!.symptoms.joinToString(", ") { sym ->
                                            Localization.getSymptomLabel(sym, isAmharic)
                                        }
                                    }
                                    Text(
                                        text = symptomsText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Symptoms",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // PRIMARY ACTION BUTTON: Log Period
        item {
            GirlyPrimaryButton(
                text = Localization.getLogPeriodButton(isAmharic),
                icon = "🩸",
                onClick = { showLogPeriodSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("log_period_primary_button")
            )
        }

        // Privacy Guarantee Card
        item {
            PrivacyCard(
                isAmharic = isAmharic,
                onLearnMore = { showPrivacyDialog = true },
                modifier = Modifier.testTag("home_privacy_card")
            )
        }
    }

    // Modal Bottom Sheet to Log Period
    if (showLogPeriodSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var selectedFlow by remember { mutableStateOf("Medium") }
        var periodNotes by remember { mutableStateOf("") }
        var isOngoing by remember { mutableStateOf(true) }

        ModalBottomSheet(
            onDismissRequest = { showLogPeriodSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .testTag("log_period_sheet"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isAmharic) "የወር አበባሽን መዝግቢ 🩸" else "Log Your Period 🩸",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                val datePrompt = if (isEthiopianCalendar) {
                    EthiopianDate.fromGregorian(today).format(isAmharic)
                } else {
                    today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
                }
                Text(
                    text = datePrompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Flow selection
                Text(
                    text = if (isAmharic) "የፍሰት መጠን ምረጪ" else "Select Flow Intensity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowSelector(
                    selectedFlow = selectedFlow,
                    onFlowSelected = { selectedFlow = it },
                    isAmharic = isAmharic,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Notes Field
                OutlinedTextField(
                    value = periodNotes,
                    onValueChange = { periodNotes = it },
                    label = { Text(if (isAmharic) "ማስታወሻ (አማራጭ)" else "Notes (Optional)") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("period_notes_input")
                )

                Spacer(modifier = Modifier.height(24.dp))

                GirlyPrimaryButton(
                    text = if (isAmharic) "መዝግብ 🌸" else "Save Period 🌸",
                    icon = "✨",
                    onClick = {
                        viewModel.logPeriod(
                            startDate = today,
                            endDate = if (isOngoing) null else today,
                            flowIntensity = selectedFlow,
                            notes = periodNotes
                        )
                        showLogPeriodSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_log_period_button")
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Privacy",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAmharic) "የግላዊነት ዋስትና 🔐" else "Privacy & Security 🔐",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = if (isAmharic)
                            "• ሁሉም የወር አበባ እና የጤና መረጃዎችሽ በስልክሽ ውስጥ ብቻ ነው የሚቀመጡት።\n• ምንም አይነት መረጃ ወደ ውጪ ሰርቨር አይላክም።\n• የይለፍ ቃል (App Lock PIN) በማዘጋጀት ስልክሽን ማንም እንዳይከፍት መቆለፍ ትችያለሽ።"
                        else
                            "• All period, mood, and symptom records are stored strictly on your local device.\n• No health information is ever uploaded to external cloud servers.\n• You can configure an App Lock PIN in Settings to protect your private space.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showPrivacyDialog = false },
                    modifier = Modifier.testTag("dismiss_privacy_dialog")
                ) {
                    Text(if (isAmharic) "ገባኝ 🌸" else "Understood 🌸", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(22.dp)
        )
    }
}
