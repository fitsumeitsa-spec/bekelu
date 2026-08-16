package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyLogRecord
import com.example.ui.components.FlowSelector
import com.example.ui.components.GirlyPrimaryButton
import com.example.ui.components.MoodSelector
import com.example.ui.components.SoftCard
import com.example.ui.components.SymptomChip
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftPinkSurfaceVariant
import com.example.ui.theme.SoftRose
import com.example.util.EthiopianDate
import com.example.util.Localization
import com.example.viewmodel.PeriodViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyLogScreen(
    viewModel: PeriodViewModel,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val allLogs by viewModel.allDailyLogs.collectAsState()
    val isAmharic = userSettings?.language == "am"
    val isEthiopian = userSettings?.calendarSystem == "ETHIOPIAN"

    var activeDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateKey = activeDate.format(dateFormatter)
    val existingLog = allLogs.firstOrNull { it.date == dateKey }

    var selectedMood by remember(dateKey, existingLog) { mutableStateOf(existingLog?.mood) }
    var selectedFlow by remember(dateKey, existingLog) { mutableStateOf(existingLog?.flow ?: "None") }
    var selectedSymptoms by remember(dateKey, existingLog) { mutableStateOf(existingLog?.symptoms ?: emptyList()) }
    var selectedEnergy by remember(dateKey, existingLog) { mutableStateOf(existingLog?.energy ?: "Normal") }
    var hadSex by remember(dateKey, existingLog) { mutableStateOf(existingLog?.hadSex ?: false) }
    var selectedProtection by remember(dateKey, existingLog) { mutableStateOf(existingLog?.sexProtection ?: "Protected") }
    var hadOrgasm by remember(dateKey, existingLog) { mutableStateOf(existingLog?.sexOrgasm ?: false) }
    var journalNotes by remember(dateKey, existingLog) { mutableStateOf(existingLog?.notes ?: "") }

    var showSavedCelebration by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("daily_log_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Date Header with Quick Navigator
        item {
            SoftCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                borderColor = MaterialTheme.colorScheme.outline
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { activeDate = activeDate.minusDays(1) },
                        modifier = Modifier.testTag("log_prev_day")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = "Previous Day",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val formattedDate = if (isEthiopian) {
                            EthiopianDate.fromGregorian(activeDate).format(isAmharic)
                        } else {
                            activeDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
                        }
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        if (activeDate == LocalDate.now()) {
                            Text(
                                text = if (isAmharic) "ዛሬ" else "Today",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = { activeDate = activeDate.plusDays(1) },
                        modifier = Modifier.testTag("log_next_day")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = "Next Day",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Title Header
        item {
            Text(
                text = Localization.getHowAreYouFeeling(isAmharic),
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 1. Mood Section
        item {
            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAmharic) "የዛሬ ስሜትሽ" else "Today's Mood",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    MoodSelector(
                        moods = PeriodViewModel.ALL_MOODS,
                        selectedMoodKey = selectedMood,
                        onMoodSelected = { selectedMood = it },
                        isAmharic = isAmharic
                    )
                }
            }
        }

        // 2. Flow Section
        item {
            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🩸", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAmharic) "የወር አበባ ፍሰት መጠን" else "Menstrual Flow Intensity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowSelector(
                        selectedFlow = selectedFlow,
                        onFlowSelected = { selectedFlow = it },
                        isAmharic = isAmharic
                    )
                }
            }
        }

        // 3. Symptoms Section
        item {
            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌸", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAmharic) "ምልክቶችሽን መርጪ" else "Select Symptoms",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PeriodViewModel.ALL_SYMPTOMS.forEach { sym ->
                            val isSelected = selectedSymptoms.contains(sym.key)
                            SymptomChip(
                                symptom = sym,
                                isSelected = isSelected,
                                onClick = {
                                    selectedSymptoms = if (isSelected) {
                                        selectedSymptoms - sym.key
                                    } else {
                                        selectedSymptoms + sym.key
                                    }
                                },
                                isAmharic = isAmharic
                            )
                        }
                    }
                }
            }
        }

        // 4. Energy Section
        item {
            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⚡", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAmharic) "የጉልበት እና ሃይል ደረጃ" else "Energy Level",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PeriodViewModel.ALL_ENERGIES.forEach { energyItem ->
                            val isSelected = selectedEnergy == energyItem.key
                            Surface(
                                onClick = { selectedEnergy = energyItem.key },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("energy_${energyItem.key}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = energyItem.iconEmoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAmharic) energyItem.labelAm else energyItem.labelEn,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Intimacy & Sexual Activity Section
        item {
            SoftCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (hadSex) com.example.ui.theme.SoftIntimacyBg else MaterialTheme.colorScheme.surface,
                borderColor = if (hadSex) com.example.ui.theme.SoftIntimacyBadge else MaterialTheme.colorScheme.outline
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💞", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isAmharic) "የግብረ-ስጋ ግንኙነት" else "Sexual Activity & Intimacy",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isAmharic) "የዛሬውን ግንኙነት መዝግቢ" else "Log intimacy on this date",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Toggle Pill Button
                        Surface(
                            onClick = { hadSex = !hadSex },
                            shape = RoundedCornerShape(20.dp),
                            color = if (hadSex) com.example.ui.theme.SoftIntimacyHeart else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.testTag("toggle_had_sex_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (hadSex) "❤️ " + (if (isAmharic) "ተፈጽሟል" else "Logged") else (if (isAmharic) "+ መዝግብ" else "+ Add"),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hadSex) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (hadSex) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (isAmharic) "የግንኙነት አይነት" else "Protection & Type",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PeriodViewModel.ALL_INTIMACIES.forEach { item ->
                                val isSelected = selectedProtection == item.key
                                Surface(
                                    onClick = { selectedProtection = item.key },
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) com.example.ui.theme.SoftIntimacyBadge else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) com.example.ui.theme.SoftIntimacyHeart else MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier.testTag("intimacy_${item.key}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(text = item.iconEmoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isAmharic) item.labelAm else item.labelEn,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        // Orgasm Chip
                        Surface(
                            onClick = { hadOrgasm = !hadOrgasm },
                            shape = RoundedCornerShape(14.dp),
                            color = if (hadOrgasm) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (hadOrgasm) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.testTag("intimacy_orgasm_chip")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(text = "✨", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAmharic) "እርካታ ተገኝቷል (Orgasm)" else "Orgasm Reached",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (hadOrgasm) FontWeight.Bold else FontWeight.Normal,
                                    color = if (hadOrgasm) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Notes / Wellness Journal Text Area
        item {
            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📝", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAmharic) "የጤና እና ስሜት ማስታወሻ" else "Wellness & Thoughts Journal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = journalNotes,
                        onValueChange = { journalNotes = it },
                        placeholder = {
                            Text(
                                if (isAmharic) "የዛሬውን ቀን ስሜት፣ ምቾት ወይም ሀሳብ እዚህ ጻፊ..."
                                else "Write how your body feels, reflections, or notes..."
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("journal_notes_field")
                    )
                }
            }
        }

        // 7. Primary Save Action
        item {
            GirlyPrimaryButton(
                text = if (isAmharic) "የዛሬውን መዝግብ 🌸" else "Save Daily Check-in 🌸",
                icon = "✨",
                onClick = {
                    viewModel.saveDailyLog(
                        date = activeDate,
                        mood = selectedMood,
                        flow = selectedFlow,
                        symptoms = selectedSymptoms,
                        energy = selectedEnergy,
                        notes = journalNotes,
                        hadSex = hadSex,
                        sexProtection = if (hadSex) selectedProtection else null,
                        sexOrgasm = if (hadSex) hadOrgasm else false
                    )
                    if (selectedFlow != "None" && selectedFlow != null) {
                        viewModel.logPeriod(startDate = activeDate, flowIntensity = selectedFlow)
                    }
                    showSavedCelebration = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_daily_log_primary")
            )
        }

        // Celebration banner
        item {
            AnimatedVisibility(
                visible = showSavedCelebration,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🌸", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.getCheckedInCelebration(isAmharic),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
