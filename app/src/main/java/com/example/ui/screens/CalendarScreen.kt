package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyLogRecord
import com.example.ui.components.FlowSelector
import com.example.ui.components.GirlyPrimaryButton
import com.example.ui.components.MoodSelector
import com.example.ui.components.SoftCard
import com.example.ui.components.SymptomChip
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.SoftFertile
import com.example.ui.theme.SoftIntimacyBadge
import com.example.ui.theme.SoftIntimacyBg
import com.example.ui.theme.SoftIntimacyHeart
import com.example.ui.theme.SoftPeriodLight
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftRose
import com.example.util.CalendarSystem
import com.example.util.CycleEngine
import com.example.util.EthiopianCalendarUtils
import com.example.util.EthiopianDate
import com.example.util.Localization
import com.example.viewmodel.PeriodViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: PeriodViewModel,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val allPeriods by viewModel.allPeriods.collectAsState()
    val allLogs by viewModel.allDailyLogs.collectAsState()
    val cycleStatus by viewModel.cycleStatus.collectAsState()

    val isAmharic = userSettings?.language == "am"
    var calendarMode by remember(userSettings?.calendarSystem) {
        mutableStateOf(userSettings?.calendarSystem ?: "ETHIOPIAN")
    }

    val today = LocalDate.now()
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var currentEthDate by remember { mutableStateOf(EthiopianDate.now()) }

    var selectedDateForSheet by remember { mutableStateOf<LocalDate?>(null) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Calendar Toggle Switcher
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isAmharic) "የቀን መቁጠሪያ 🌸" else "Calendar 🌸",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 26.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Calendar System Toggle Tabs
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        val isEth = calendarMode == "ETHIOPIAN"
                        Surface(
                            onClick = {
                                calendarMode = "ETHIOPIAN"
                                viewModel.updateCalendarSystem("ETHIOPIAN")
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isEth) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier.testTag("tab_ethiopian_calendar")
                        ) {
                            Text(
                                text = if (isAmharic) "የኢትዮጵያ 🇪🇹" else "Ethiopian 🇪🇹",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isEth) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            onClick = {
                                calendarMode = "GREGORIAN"
                                viewModel.updateCalendarSystem("GREGORIAN")
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (!isEth) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier.testTag("tab_gregorian_calendar")
                        ) {
                            Text(
                                text = if (isAmharic) "ጎርጎሪያን" else "Gregorian",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (!isEth) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Calendar Card
        item {
            SoftCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calendar_card"),
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Month Navigation Header & Today Quick Jump
                    val monthHeaderTitle = if (calendarMode == "ETHIOPIAN") {
                        val mName = currentEthDate.getMonthName(isAmharic)
                        if (isAmharic) "$mName ${currentEthDate.year} ዓ.ም" else "$mName ${currentEthDate.year} (EC)"
                    } else {
                        val mName = currentYearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
                        "$mName ${currentYearMonth.year}"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (calendarMode == "ETHIOPIAN") {
                                    val prevMonth = if (currentEthDate.month == 1) 13 else currentEthDate.month - 1
                                    val prevYear = if (currentEthDate.month == 1) currentEthDate.year - 1 else currentEthDate.year
                                    currentEthDate = EthiopianDate(prevYear, prevMonth, 1)
                                } else {
                                    currentYearMonth = currentYearMonth.minusMonths(1)
                                }
                            },
                            modifier = Modifier.testTag("calendar_prev_month")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                                contentDescription = "Previous Month",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = monthHeaderTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // Quick Jump to Current Month
                            TextButton(
                                onClick = {
                                    currentEthDate = EthiopianDate.now()
                                    currentYearMonth = YearMonth.now()
                                },
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text(
                                    text = if (isAmharic) "ወደ ዛሬ ተመለስ 🌸" else "Jump to Today 🌸",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (calendarMode == "ETHIOPIAN") {
                                    val nextMonth = if (currentEthDate.month == 13) 1 else currentEthDate.month + 1
                                    val nextYear = if (currentEthDate.month == 13) currentEthDate.year + 1 else currentEthDate.year
                                    currentEthDate = EthiopianDate(nextYear, nextMonth, 1)
                                } else {
                                    currentYearMonth = currentYearMonth.plusMonths(1)
                                }
                            },
                            modifier = Modifier.testTag("calendar_next_month")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = "Next Month",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Day Names Header (M, T, W, T, F, S, S or ሰ, ማ, ረ, ሐ, አ, ቅ, እ)
                    val dayNames = if (isAmharic) EthiopianDate.ETHIOPIAN_DAYS_SHORT_AM else EthiopianDate.DAYS_SHORT_EN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        dayNames.forEach { dayName ->
                            Text(
                                text = dayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(38.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Days Grid
                    if (calendarMode == "ETHIOPIAN") {
                        val firstDayGregorian = currentEthDate.copy(day = 1).toGregorianLocalDate()
                        val dayOfWeekOffset = (firstDayGregorian.dayOfWeek.value - 1) % 7
                        val daysInMonth = currentEthDate.maxDaysInMonth
                        val totalCells = dayOfWeekOffset + daysInMonth
                        val rows = (totalCells + 6) / 7

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (r in 0 until rows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    for (c in 0 until 7) {
                                        val cellIndex = r * 7 + c
                                        val dayNumber = cellIndex - dayOfWeekOffset + 1
                                        if (dayNumber in 1..daysInMonth) {
                                            val cellEthDate = currentEthDate.copy(day = dayNumber)
                                            val cellLocalDate = cellEthDate.toGregorianLocalDate()
                                            val isToday = cellLocalDate == today
                                            val isSelected = selectedDateForSheet == cellLocalDate
                                            val isPeriod = CycleEngine.isDateInPeriod(cellLocalDate, allPeriods)
                                            val isPredicted = !isPeriod && CycleEngine.isDatePredictedPeriod(
                                                cellLocalDate,
                                                cycleStatus.lastPeriodStartDate,
                                                cycleStatus.totalCycleLength,
                                                cycleStatus.periodLength
                                            )
                                            val isFertile = !isPeriod && !isPredicted && CycleEngine.isDateFertile(
                                                cellLocalDate,
                                                cycleStatus.lastPeriodStartDate,
                                                cycleStatus.totalCycleLength
                                            )
                                            val matchingLog = allLogs.firstOrNull { it.date == cellLocalDate.format(dateFormatter) }
                                            val hasLog = matchingLog != null
                                            val hadSex = matchingLog?.hadSex == true

                                            CalendarDayCell(
                                                dayText = "$dayNumber",
                                                secondaryDayText = "${cellLocalDate.dayOfMonth}",
                                                isToday = isToday,
                                                isSelected = isSelected,
                                                isPeriod = isPeriod,
                                                isPredicted = isPredicted,
                                                isFertile = isFertile,
                                                hasLog = hasLog,
                                                hadSex = hadSex,
                                                onClick = {
                                                    selectedDateForSheet = cellLocalDate
                                                }
                                            )
                                        } else {
                                            Box(modifier = Modifier.size(42.dp))
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Gregorian Grid
                        val firstDay = currentYearMonth.atDay(1)
                        val dayOfWeekOffset = (firstDay.dayOfWeek.value - 1) % 7
                        val daysInMonth = currentYearMonth.lengthOfMonth()
                        val totalCells = dayOfWeekOffset + daysInMonth
                        val rows = (totalCells + 6) / 7

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (r in 0 until rows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    for (c in 0 until 7) {
                                        val cellIndex = r * 7 + c
                                        val dayNumber = cellIndex - dayOfWeekOffset + 1
                                        if (dayNumber in 1..daysInMonth) {
                                            val cellLocalDate = currentYearMonth.atDay(dayNumber)
                                            val cellEthDate = EthiopianDate.fromGregorian(cellLocalDate)
                                            val isToday = cellLocalDate == today
                                            val isSelected = selectedDateForSheet == cellLocalDate
                                            val isPeriod = CycleEngine.isDateInPeriod(cellLocalDate, allPeriods)
                                            val isPredicted = !isPeriod && CycleEngine.isDatePredictedPeriod(
                                                cellLocalDate,
                                                cycleStatus.lastPeriodStartDate,
                                                cycleStatus.totalCycleLength,
                                                cycleStatus.periodLength
                                            )
                                            val isFertile = !isPeriod && !isPredicted && CycleEngine.isDateFertile(
                                                cellLocalDate,
                                                cycleStatus.lastPeriodStartDate,
                                                cycleStatus.totalCycleLength
                                            )
                                            val matchingLog = allLogs.firstOrNull { it.date == cellLocalDate.format(dateFormatter) }
                                            val hasLog = matchingLog != null
                                            val hadSex = matchingLog?.hadSex == true

                                            CalendarDayCell(
                                                dayText = "$dayNumber",
                                                secondaryDayText = "${cellEthDate.day}",
                                                isToday = isToday,
                                                isSelected = isSelected,
                                                isPeriod = isPeriod,
                                                isPredicted = isPredicted,
                                                isFertile = isFertile,
                                                hasLog = hasLog,
                                                hadSex = hadSex,
                                                onClick = {
                                                    selectedDateForSheet = cellLocalDate
                                                }
                                            )
                                        } else {
                                            Box(modifier = Modifier.size(42.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // CALENDAR LEGEND
        item {
            SoftCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calendar_legend_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(
                        color = SoftRose,
                        isFilled = true,
                        label = if (isAmharic) "የወር አበባ" else "Period"
                    )
                    LegendItem(
                        color = SoftRose,
                        isDotted = true,
                        label = if (isAmharic) "የሚጠበቅ" else "Expected"
                    )
                    LegendItem(
                        color = LavenderAccent,
                        isRing = true,
                        label = if (isAmharic) "ዛሬ" else "Today"
                    )
                    LegendItem(
                        color = SoftFertile,
                        isFilled = true,
                        label = if (isAmharic) "የመራባት" else "Fertile"
                    )
                    LegendItem(
                        color = SoftIntimacyHeart,
                        isHeart = true,
                        label = if (isAmharic) "ግንኙነት" else "Sex (❤️)"
                    )
                }
            }
        }

        // NEXT CYCLE FORECAST SUMMARY CARD (DUAL CALENDAR FORMATTED)
        item {
            SoftCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calendar_cycle_forecast_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "✨", fontSize = 18.sp)
                        Text(
                            text = if (isAmharic) "የሚቀጥለው ዑደት ትንበያ" else "Next Cycle Forecast",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val nextPeriodDate = cycleStatus.expectedNextPeriodDate
                    val nextPeriodDisplay = if (nextPeriodDate != null) {
                        if (calendarMode == "ETHIOPIAN") {
                            val ethNext = EthiopianDate.fromGregorian(nextPeriodDate)
                            ethNext.format(isAmharic)
                        } else {
                            nextPeriodDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                        }
                    } else {
                        if (isAmharic) "በቅርቡ ይሰላል" else "Calculating..."
                    }

                    val daysUntil = cycleStatus.daysUntilNextPeriod
                    val daysUntilText = when {
                        daysUntil > 0 -> if (isAmharic) "ከ $daysUntil ቀናት በኋላ" else "in $daysUntil days"
                        daysUntil == 0 -> if (isAmharic) "ዛሬ ይጠበቃል" else "Expected today"
                        else -> if (isAmharic) "${-daysUntil} ቀናት አልፈዋል" else "${-daysUntil} days late"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAmharic) "የሚቀጥለው የወር አበባ" else "Next Predicted Period",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = nextPeriodDisplay,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SoftRose
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, SoftPinkBorder)
                        ) {
                            Text(
                                text = daysUntilText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Day Detail / Quick Check-In Bottom Sheet
    selectedDateForSheet?.let { date ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val dateStr = date.format(dateFormatter)
        val currentLog = allLogs.firstOrNull { it.date == dateStr }
        val isDatePeriod = CycleEngine.isDateInPeriod(date, allPeriods)

        var mood by remember(dateStr) { mutableStateOf(currentLog?.mood) }
        var flow by remember(dateStr) { mutableStateOf(currentLog?.flow ?: if (isDatePeriod) "Medium" else "None") }
        var symptoms by remember(dateStr) { mutableStateOf(currentLog?.symptoms ?: emptyList()) }
        var energy by remember(dateStr) { mutableStateOf(currentLog?.energy ?: "Normal") }
        var hadSex by remember(dateStr) { mutableStateOf(currentLog?.hadSex ?: false) }
        var selectedProtection by remember(dateStr) { mutableStateOf(currentLog?.sexProtection ?: "Protected") }
        var hadOrgasm by remember(dateStr) { mutableStateOf(currentLog?.sexOrgasm ?: false) }
        var notes by remember(dateStr) { mutableStateOf(currentLog?.notes ?: "") }

        ModalBottomSheet(
            onDismissRequest = { selectedDateForSheet = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp)
                    .testTag("day_detail_sheet"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val ethDate = EthiopianDate.fromGregorian(date)
                val gregStr = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                val ethStr = ethDate.format(isAmharic)

                val primaryHeader = if (calendarMode == "ETHIOPIAN") ethStr else gregStr
                val secondaryHeader = if (calendarMode == "ETHIOPIAN") "Gregorian: $gregStr" else "Ethiopian: $ethStr"

                Text(
                    text = primaryHeader,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = secondaryHeader,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Sexual Activity & Intimacy Section
                SoftCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (hadSex) SoftIntimacyBg else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    borderColor = if (hadSex) SoftIntimacyBadge else MaterialTheme.colorScheme.outline
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
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
                                        text = if (isAmharic) "የግብረ-ስጋ ግንኙነት" else "Sexual Activity / Sex",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isAmharic) "ቀኑን በቀይ/ሮዝ ምልክት ያሳያል" else "Displays colored badge on calendar",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Toggle Button
                            Surface(
                                onClick = { hadSex = !hadSex },
                                shape = RoundedCornerShape(20.dp),
                                color = if (hadSex) SoftIntimacyHeart else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (hadSex) SoftIntimacyHeart else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.testTag("sheet_toggle_sex_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (hadSex) "❤️ " + (if (isAmharic) "ተፈጽሟል" else "Occurred") else (if (isAmharic) "+ መዝግብ" else "+ Add Sex"),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hadSex) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        if (hadSex) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                PeriodViewModel.ALL_INTIMACIES.take(3).forEach { item ->
                                    val isSelected = selectedProtection == item.key
                                    Surface(
                                        onClick = { selectedProtection = item.key },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) SoftIntimacyBadge else MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) SoftIntimacyHeart else MaterialTheme.colorScheme.outline
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                                        ) {
                                            Text(text = item.iconEmoji, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isAmharic) item.labelAm else item.labelEn,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Flow
                Text(
                    text = if (isAmharic) "🩸 የወር አበባ ፍሰት" else "🩸 Flow Intensity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowSelector(
                    selectedFlow = flow,
                    onFlowSelected = { flow = it },
                    isAmharic = isAmharic
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mood
                Text(
                    text = if (isAmharic) "🌸 ስሜት" else "🌸 Mood",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                MoodSelector(
                    moods = PeriodViewModel.ALL_MOODS,
                    selectedMoodKey = mood,
                    onMoodSelected = { mood = it },
                    isAmharic = isAmharic
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save button
                GirlyPrimaryButton(
                    text = if (isAmharic) "መዝግብ 🌸" else "Save Log 🌸",
                    icon = "✨",
                    onClick = {
                        viewModel.saveDailyLog(
                            date = date,
                            mood = mood,
                            flow = flow,
                            symptoms = symptoms,
                            energy = energy,
                            notes = notes,
                            hadSex = hadSex,
                            sexProtection = if (hadSex) selectedProtection else null,
                            sexOrgasm = if (hadSex) hadOrgasm else false
                        )
                        if (flow != "None" && flow != null && !isDatePeriod) {
                            viewModel.logPeriod(startDate = date, flowIntensity = flow ?: "Medium")
                        }
                        selectedDateForSheet = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_day_log_button")
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    dayText: String,
    secondaryDayText: String? = null,
    isToday: Boolean,
    isSelected: Boolean = false,
    isPeriod: Boolean,
    isPredicted: Boolean,
    isFertile: Boolean,
    hasLog: Boolean,
    hadSex: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("day_cell_$dayText")
    ) {
        Canvas(modifier = Modifier.size(38.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f - 2f

            // Base fills / borders for cycle states
            when {
                isPeriod -> {
                    // Soft rose solid circle
                    drawCircle(color = SoftRose, radius = radius, center = center)
                }
                isPredicted -> {
                    // Dotted rose outline + subtle soft pink background
                    drawCircle(color = SoftRose.copy(alpha = 0.12f), radius = radius, center = center)
                    drawCircle(
                        color = SoftRose,
                        radius = radius,
                        center = center,
                        style = Stroke(
                            width = 1.8.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f), 0f)
                        )
                    )
                }
                isFertile -> {
                    // Soft fertile purple translucent fill + gentle border
                    drawCircle(color = SoftFertile.copy(alpha = 0.28f), radius = radius, center = center)
                    drawCircle(
                        color = SoftFertile.copy(alpha = 0.5f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
                hadSex -> {
                    // Sexual intercourse background aura if not on period
                    drawCircle(color = SoftIntimacyBg, radius = radius, center = center)
                    drawCircle(
                        color = SoftIntimacyBadge,
                        radius = radius,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }

            // Selection highlight
            if (isSelected) {
                drawCircle(
                    color = SoftRose,
                    radius = radius + 1f,
                    center = center,
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }

            // Today indicator: prominent glowing lavender ring on top
            if (isToday) {
                drawCircle(
                    color = LavenderAccent,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Indicator dots at bottom
            if (hadSex) {
                // Vibrant Romantic Coral-Pink dot / heart color for intercourse
                drawCircle(
                    color = if (isPeriod) Color.White else SoftIntimacyHeart,
                    radius = 2.5.dp.toPx(),
                    center = Offset(center.x, size.height - 2.5.dp.toPx())
                )
            } else if (hasLog) {
                drawCircle(
                    color = if (isPeriod) Color.White else LavenderAccent,
                    radius = 2.dp.toPx(),
                    center = Offset(center.x, size.height - 2.5.dp.toPx())
                )
            }
        }

        // Mini heart badge at top right if sexual activity occurred
        if (hadSex) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 1.dp, end = 2.dp)
            ) {
                Text(
                    text = "♥",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPeriod) Color.White else SoftIntimacyHeart
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = if (secondaryDayText != null) 13.sp else 14.sp,
                    lineHeight = if (secondaryDayText != null) 14.sp else 18.sp
                ),
                fontWeight = if (isToday || isPeriod || hadSex || isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isPeriod) Color.White else if (hadSex) SoftIntimacyHeart else MaterialTheme.colorScheme.onSurface
            )
            if (secondaryDayText != null) {
                Text(
                    text = secondaryDayText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        lineHeight = 9.sp
                    ),
                    color = if (isPeriod) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                )
            }
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String,
    isFilled: Boolean = false,
    isDotted: Boolean = false,
    isRing: Boolean = false,
    isHeart: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (isHeart) {
            Text(
                text = "❤️",
                fontSize = 11.sp
            )
        } else {
            Box(
                modifier = Modifier.size(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.width / 2f
                    val center = Offset(radius, radius)
                    when {
                        isFilled -> drawCircle(color = color, radius = radius, center = center)
                        isDotted -> drawCircle(
                            color = color,
                            radius = radius - 1f,
                            center = center,
                            style = Stroke(
                                width = 1.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 3f), 0f)
                            )
                        )
                        isRing -> drawCircle(
                            color = color,
                            radius = radius - 1f,
                            center = center,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
