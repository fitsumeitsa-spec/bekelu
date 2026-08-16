package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PeriodRecord
import com.example.ui.components.FlowSelector
import com.example.ui.components.GirlyPrimaryButton
import com.example.ui.components.SoftCard
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftPinkSurfaceVariant
import com.example.ui.theme.SoftRose
import com.example.util.EthiopianDate
import com.example.util.Localization
import com.example.viewmodel.PeriodViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: PeriodViewModel,
    onLogPeriodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val allPeriods by viewModel.allPeriods.collectAsState()
    val isAmharic = userSettings?.language == "am"
    val isEthiopian = userSettings?.calendarSystem == "ETHIOPIAN"
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var periodToDelete by remember { mutableStateOf<PeriodRecord?>(null) }
    var periodToEdit by remember { mutableStateOf<PeriodRecord?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("history_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isAmharic) "የወር አበባ ታሪክ 🩸" else "Period History 🩸",
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 26.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isAmharic) "የተመዘገቡ የወር አበባ ቀናት እና ዑደቶች" else "Logged periods and cycle durations",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (allPeriods.isNotEmpty()) {
                    IconButton(
                        onClick = onLogPeriodClick,
                        modifier = Modifier.testTag("history_add_period_btn")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "+",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Empty State if no periods
        if (allPeriods.isEmpty()) {
            item {
                SoftCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                        .testTag("history_empty_state")
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🌱", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isAmharic) "የዑደትሽ ታሪክ እዚህ ይጀምራል።" else "Your cycle story starts here.",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isAmharic) "ክትትል ለመጀመር የመጀመሪያሽን የወር አበባ መዝግቢ።" else "Log your first period to begin tracking your cycle patterns.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        GirlyPrimaryButton(
                            text = Localization.getLogPeriodButton(isAmharic),
                            icon = "🩸",
                            onClick = onLogPeriodClick,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .testTag("empty_state_log_button")
                        )
                    }
                }
            }
        } else {
            // List of Chronological Period Cards
            items(allPeriods) { period ->
                val startDate = try { LocalDate.parse(period.startDate, dateFormatter) } catch (e: Exception) { LocalDate.now() }
                val endDate = period.endDate?.let { try { LocalDate.parse(it, dateFormatter) } catch (e: Exception) { null } }

                val formattedRange = if (isEthiopian) {
                    val ethStart = EthiopianDate.fromGregorian(startDate)
                    if (endDate != null) {
                        val ethEnd = EthiopianDate.fromGregorian(endDate)
                        "${ethStart.format(isAmharic)} – ${ethEnd.format(isAmharic)}"
                    } else {
                        "${ethStart.format(isAmharic)} (ቀጣይ)"
                    }
                } else {
                    if (endDate != null) {
                        val startFmt = startDate.format(DateTimeFormatter.ofPattern("MMM d"))
                        val endFmt = endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                        "$startFmt – $endFmt"
                    } else {
                        val startFmt = startDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                        "$startFmt (Ongoing)"
                    }
                }

                val durationDays = if (endDate != null) {
                    ChronoUnit.DAYS.between(startDate, endDate) + 1
                } else {
                    ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1
                }

                SoftCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("period_item_${period.id}")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🩸", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formattedRange,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = { periodToEdit = period },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { periodToDelete = period },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = if (isAmharic) "$durationDays ቀናት ፍሰት" else "$durationDays days flow",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = Localization.getFlowLabel(period.flowIntensity, isAmharic),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            if (period.notes.isNotBlank()) {
                                Text(
                                    text = "📝 ${period.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    periodToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { periodToDelete = null },
            title = {
                Text(
                    text = if (isAmharic) "የወር አበባ መዝገብ ይሰረዝ?" else "Delete Period Record?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isAmharic)
                        "ይህንን የወር አበባ መዝገብ ማስወገድ ይፈልጋሉ? ይህ ክንውን የዑደትሽን ስሌት ሊለውጠው ይችላል።"
                    else
                        "Are you sure you want to remove this period entry? This will update your cycle analytics.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePeriod(target.id)
                        periodToDelete = null
                    },
                    modifier = Modifier.testTag("confirm_delete_period")
                ) {
                    Text(
                        text = if (isAmharic) "ሰርዝ" else "Delete",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { periodToDelete = null }) {
                    Text(if (isAmharic) "ተመለስ" else "Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Edit Period Sheet
    periodToEdit?.let { target ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var flow by remember(target) { mutableStateOf(target.flowIntensity) }
        var notes by remember(target) { mutableStateOf(target.notes) }

        ModalBottomSheet(
            onDismissRequest = { periodToEdit = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp)
                    .testTag("edit_period_sheet"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isAmharic) "የወር አበባ መዝገብ አስተካክይ 🩸" else "Edit Period Record 🩸",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                FlowSelector(
                    selectedFlow = flow,
                    onFlowSelected = { flow = it },
                    isAmharic = isAmharic
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isAmharic) "ማስታወሻ" else "Notes") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                GirlyPrimaryButton(
                    text = if (isAmharic) "አስቀምጪ 🌸" else "Save Changes 🌸",
                    icon = "✨",
                    onClick = {
                        viewModel.logPeriod(
                            startDate = LocalDate.parse(target.startDate, dateFormatter),
                            endDate = target.endDate?.let { LocalDate.parse(it, dateFormatter) },
                            flowIntensity = flow,
                            notes = notes
                        )
                        periodToEdit = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_edit_period_button")
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
