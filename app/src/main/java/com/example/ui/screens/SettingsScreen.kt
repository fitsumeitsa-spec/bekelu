package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserSettingsEntity
import com.example.ui.components.GirlyPrimaryButton
import com.example.ui.components.SoftCard
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftPinkSurfaceVariant
import com.example.ui.theme.SoftRose
import com.example.util.Localization
import com.example.viewmodel.PeriodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PeriodViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val isAmharic = userSettings?.language == "am"
    val isEthiopian = userSettings?.calendarSystem == "ETHIOPIAN"

    var showPinDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showDisclaimerDialog by remember { mutableStateOf(false) }

    var inputPin by remember { mutableStateOf("") }
    var importJsonText by remember { mutableStateOf("") }

    val cycleLength = userSettings?.avgCycleLength ?: 28
    val periodLength = userSettings?.avgPeriodLength ?: 5

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = if (isAmharic) "ቅንብሮች 🌸" else "Settings 🌸",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 26.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isAmharic) "የመተግበሪያውን ምርጫዎች እና ግላዊነት አስተካክይ" else "Customize your preferences and privacy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section 1: Preferences
        item {
            Text(
                text = if (isAmharic) "ምርጫዎች (Preferences)" else "Preferences",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Language Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAmharic) "ቋንቋ" else "Language",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isAmharic) "አማርኛ / English" else "English / አማርኛ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                onClick = { viewModel.updateLanguage("en") },
                                shape = RoundedCornerShape(12.dp),
                                color = if (!isAmharic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.testTag("lang_en_btn")
                            ) {
                                Text(
                                    text = "EN",
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isAmharic) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            Surface(
                                onClick = { viewModel.updateLanguage("am") },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isAmharic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.testTag("lang_am_btn")
                            ) {
                                Text(
                                    text = "አማ",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAmharic) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Calendar Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAmharic) "የቀን መቁጠሪያ" else "Calendar System",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isEthiopian) "የኢትዮጵያ 🇪🇹" else "Gregorian",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                onClick = { viewModel.updateCalendarSystem("ETHIOPIAN") },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isEthiopian) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.testTag("cal_eth_btn")
                            ) {
                                Text(
                                    text = "🇪🇹 Eth",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEthiopian) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Surface(
                                onClick = { viewModel.updateCalendarSystem("GREGORIAN") },
                                shape = RoundedCornerShape(12.dp),
                                color = if (!isEthiopian) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.testTag("cal_greg_btn")
                            ) {
                                Text(
                                    text = "Greg",
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isEthiopian) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Cycle Length Stepper
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAmharic) "አማካይ የዑደት ርዝመት" else "Typical Cycle Length",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isAmharic) "ከመጀመሪያ ቀን እስከ ቀጣዩ የወር አበባ" else "Days between period starts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.updateCycleLengths(cycleLength - 1, periodLength) }) {
                                Text(text = "−", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                text = "$cycleLength ${if (isAmharic) "ቀን" else "days"}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { viewModel.updateCycleLengths(cycleLength + 1, periodLength) }) {
                                Text(text = "+", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Reminders & Notifications
        item {
            Text(
                text = if (isAmharic) "ማስታወሻዎች (Reminders)" else "Gentle Reminders",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Period Reminder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAmharic) "የወር አበባ መቃረቢያ ማስታወሻ" else "Period Approaching Reminder",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isAmharic) "ከወር አበባ በፊት ረጋ ያለ ማስታወሻ ይሰጣል" else "Gentle notification before period starts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = userSettings?.isPeriodReminderEnabled ?: true,
                            onCheckedChange = {
                                viewModel.updateReminderSettings(
                                    periodReminder = it,
                                    daysBefore = userSettings?.periodReminderDaysBefore ?: 2,
                                    dailyCheckIn = userSettings?.isDailyCheckInReminderEnabled ?: true,
                                    dailyTime = userSettings?.dailyReminderTime ?: "20:00",
                                    fertileReminder = userSettings?.isFertileReminderEnabled ?: true,
                                    hydrationReminder = userSettings?.isHydrationReminderEnabled ?: false
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("period_reminder_switch")
                        )
                    }

                    if (userSettings?.isPeriodReminderEnabled != false) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAmharic) "ከስንት ቀን በፊት፦" else "Days before:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            listOf(1, 2, 3, 5).forEach { days ->
                                val isSelected = (userSettings?.periodReminderDaysBefore ?: 2) == days
                                Surface(
                                    onClick = {
                                        viewModel.updateReminderSettings(
                                            periodReminder = true,
                                            daysBefore = days,
                                            dailyCheckIn = userSettings?.isDailyCheckInReminderEnabled ?: true,
                                            dailyTime = userSettings?.dailyReminderTime ?: "20:00",
                                            fertileReminder = userSettings?.isFertileReminderEnabled ?: true,
                                            hydrationReminder = userSettings?.isHydrationReminderEnabled ?: false
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.testTag("days_before_$days")
                                ) {
                                    Text(
                                        text = "$days ${if (isAmharic) "ቀን" else "d"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Daily Check-in Reminder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAmharic) "የእለት ተዕለት የጤና ማስታወሻ" else "Daily Wellness Check-in",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isAmharic) "ስሜትንና ምልክቶችን ለመመዝገብ ዕለታዊ ማስታወሻ" else "Daily reminder to log mood & symptoms",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = userSettings?.isDailyCheckInReminderEnabled ?: true,
                            onCheckedChange = {
                                viewModel.updateReminderSettings(
                                    periodReminder = userSettings?.isPeriodReminderEnabled ?: true,
                                    daysBefore = userSettings?.periodReminderDaysBefore ?: 2,
                                    dailyCheckIn = it,
                                    dailyTime = userSettings?.dailyReminderTime ?: "20:00",
                                    fertileReminder = userSettings?.isFertileReminderEnabled ?: true,
                                    hydrationReminder = userSettings?.isHydrationReminderEnabled ?: false
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("daily_checkin_switch")
                        )
                    }

                    if (userSettings?.isDailyCheckInReminderEnabled != false) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAmharic) "ሰዓት፦" else "Time:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            listOf("08:00" to "8 AM", "13:00" to "1 PM", "20:00" to "8 PM", "21:30" to "9:30 PM").forEach { (timeVal, timeLabel) ->
                                val isSelected = (userSettings?.dailyReminderTime ?: "20:00") == timeVal
                                Surface(
                                    onClick = {
                                        viewModel.updateReminderSettings(
                                            periodReminder = userSettings?.isPeriodReminderEnabled ?: true,
                                            daysBefore = userSettings?.periodReminderDaysBefore ?: 2,
                                            dailyCheckIn = true,
                                            dailyTime = timeVal,
                                            fertileReminder = userSettings?.isFertileReminderEnabled ?: true,
                                            hydrationReminder = userSettings?.isHydrationReminderEnabled ?: false
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.testTag("reminder_time_$timeVal")
                                ) {
                                    Text(
                                        text = timeLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Fertile Window Alert
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAmharic) "የመራባት ቀናት ማሳሰቢያ 💜" else "Fertile Window Alert 💜",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isAmharic) "የመራባት ቀናት ሲጀምሩ ያሳውቃል" else "Notifies when fertile window begins",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = userSettings?.isFertileReminderEnabled ?: true,
                            onCheckedChange = {
                                viewModel.updateReminderSettings(
                                    periodReminder = userSettings?.isPeriodReminderEnabled ?: true,
                                    daysBefore = userSettings?.periodReminderDaysBefore ?: 2,
                                    dailyCheckIn = userSettings?.isDailyCheckInReminderEnabled ?: true,
                                    dailyTime = userSettings?.dailyReminderTime ?: "20:00",
                                    fertileReminder = it,
                                    hydrationReminder = userSettings?.isHydrationReminderEnabled ?: false
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("fertile_reminder_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Private Notifications Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAmharic) "ሚስጥራዊ ማስታወቂያዎች (Private)" else "Private Notifications",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isAmharic) "ስሜታዊ ቃላትን ደብቆ አጠቃላይ ማስታወሻ ያሳያል" else "Discreet preview on lock screen",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = userSettings?.isPrivateNotifications ?: false,
                            onCheckedChange = { viewModel.updatePrivacySettings(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("private_notifs_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Test Notification Button
                    Surface(
                        onClick = {
                            viewModel.triggerTestNotification()
                            Toast.makeText(
                                context,
                                if (isAmharic) "ማሳሰቢያ ተልኳል! 🔔" else "Test notification sent! 🔔",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_notification_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔔", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAmharic) "የማስታወሻ ሙከራ ላኪ (Test Notification)" else "Send Test Notification 🔔",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Privacy & App Lock
        item {
            Text(
                text = if (isAmharic) "ግላዊነት እና ደህንነት (Privacy & Lock)" else "Privacy & Security",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // App Lock Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAmharic) "መተግበሪያ መቆለፊያ (App Lock)" else "App Lock PIN 🔐",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (userSettings?.isAppLockEnabled == true)
                                    (if (isAmharic) "የይለፍ ቃል ነቅቷል" else "PIN protection active")
                                else
                                    (if (isAmharic) "ያልነቃ" else "Disabled"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = userSettings?.isAppLockEnabled ?: false,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    showPinDialog = true
                                } else {
                                    viewModel.setAppLock(false, "")
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Catwalk Girl Walk Login Preview Button
                    Surface(
                        onClick = { viewModel.lockAppForced() },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("preview_girl_walk_login_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = "👠", fontSize = 22.sp)
                                Column {
                                    Text(
                                        text = if (isAmharic) "የ catwalk ልጃገረድ መግቢያ ሞክሪ" else "Animated Girl Walk Login",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isAmharic) "ቆንጆ የ catwalk አኒሜሽን እና መቆለፊያ" else "Runway strut animation & PIN entrance",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = "→",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Export Data
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAmharic) "መረጃሽን ኮፒ አድርጊ (Backup)" else "Export Backup Data",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isAmharic) "የወር አበባ መረጃዎችን በJSON ቅጂ" else "Copy cycle data as JSON",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(
                            onClick = {
                                val json = viewModel.exportDataJson()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("PeriodData", json)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(
                                    context,
                                    if (isAmharic) "መረጃው ተገልብጧል 🌸" else "Data copied to clipboard 🌸",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.testTag("export_data_button")
                        ) {
                            Text(
                                text = if (isAmharic) "ኮፒ አድርግ" else "Export",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Import Data
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAmharic) "መረጃ መልሰሽ ጫኚ (Restore)" else "Import & Restore Data",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isAmharic) "የተቀመጠ የJSON መረጃ ወደ መተግበሪያው አስገቢ" else "Restore saved JSON backup",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(
                            onClick = { showImportDialog = true },
                            modifier = Modifier.testTag("import_data_button")
                        ) {
                            Text(
                                text = if (isAmharic) "አስገባ" else "Import",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Delete All Data
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAmharic) "ሁሉንም መረጃ አጥፊ" else "Delete All Data",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = if (isAmharic) "ሁሉንም የወር አበባ እና የጤና መዝገቦች ያስወግዳል" else "Permanently wipe all records",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(
                            onClick = { showDeleteAllDialog = true },
                            modifier = Modifier.testTag("delete_all_data_button")
                        ) {
                            Text(
                                text = if (isAmharic) "አጥፋ" else "Wipe",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Section 4: About & Legal
        item {
            Text(
                text = if (isAmharic) "ስለ መተግበሪያው (About)" else "About & Legal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Privacy Policy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAmharic) "የግላዊነት ፖሊሲ (Privacy Policy)" else "Privacy Policy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(onClick = { showPrivacyPolicyDialog = true }) {
                            Text(text = "→", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Medical Disclaimer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAmharic) "የህክምና ማሳሰቢያ (Medical Disclaimer)" else "Medical Disclaimer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(onClick = { showDisclaimerDialog = true }) {
                            Text(text = "→", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Version
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAmharic) "የመተግበሪያ እትም" else "App Version",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "1.0.0 (Mela)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    // Set PIN Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = {
                Text(
                    text = if (isAmharic) "የይለፍ ቃል (PIN) አዘጋጂ 🔐" else "Set 4-Digit PIN 🔐",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isAmharic) "መተግበሪያውን ለመክፈት የምትጠቀሚበትን 4 አሃዝ የይለፍ ቃል አስገቢ።"
                        else "Enter a 4-digit numeric passcode to secure your period planner.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputPin,
                        onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) inputPin = it },
                        label = { Text("PIN") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pin_setup_input")
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (inputPin.length == 4) {
                            viewModel.setAppLock(true, inputPin)
                            showPinDialog = false
                            inputPin = ""
                            Toast.makeText(context, if (isAmharic) "የይለፍ ቃል ተዘጋጅቷል 🌸" else "PIN successfully configured 🌸", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("save_pin_button")
                ) {
                    Text(if (isAmharic) "አስቀምጪ" else "Save PIN", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text(if (isAmharic) "ተመለስ" else "Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Import JSON Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Text(
                    text = if (isAmharic) "መረጃ መልሰሽ ጫኚ" else "Restore Backup",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isAmharic) "የተገለበጠውን የJSON መረጃ እዚህ ለጥፊ፦" else "Paste your exported JSON data string below:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        minLines = 4,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (viewModel.importDataJson(importJsonText)) {
                            Toast.makeText(context, if (isAmharic) "መረጃው ተመልሷል 🌸" else "Data restored successfully 🌸", Toast.LENGTH_SHORT).show()
                            showImportDialog = false
                            importJsonText = ""
                        } else {
                            Toast.makeText(context, if (isAmharic) "የተሳሳተ የJSON ቅርጸት" else "Invalid JSON format", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(if (isAmharic) "ጫን" else "Import", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text(if (isAmharic) "ተመለስ" else "Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Delete All Dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = {
                Text(
                    text = if (isAmharic) "ሁሉንም መረጃ ማጥፋት እርግጠኛ ነሽ?" else "Erase All Data?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = if (isAmharic)
                        "ይህ ክንውን የተመዘገቡትን የወር አበባ፣ ስሜት እና ምልክቶች በሙሉ ያስወግዳል። ይህ ክንውን ሊመለስ አይችልም።"
                    else
                        "This will permanently remove all period records, daily logs, and customized settings from your device. This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showDeleteAllDialog = false
                        Toast.makeText(context, if (isAmharic) "ሁሉም መረጃ ተሰርዟል" else "All data wiped", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(if (isAmharic) "ሁሉንም አጥፋ" else "Erase All", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text(if (isAmharic) "ተመለስ" else "Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Privacy Policy Modal
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = {
                Text(
                    text = if (isAmharic) "የግላዊነት ጥበቃ ፖሊሲ 🔐" else "Privacy Policy 🔐",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isAmharic)
                        "የግል መረጃሽ ለእኛ ከሁሉም በላይ አስፈላጊ ነው።\n\n1. ሁሉም መረጃዎችሽ በስልክሽ ማከማቻ (Room SQLite DB) ውስጥ ብቻ ነው የሚቀመጡት።\n2. ምንም አይነት የግል ጤና መረጃ ወደ ውጪ አገልጋዮች (Cloud Servers) አይተላለፍም።\n3. መረጃሽን በማንኛውም ጊዜ ኮፒ ማድረግ ወይም ሙሉ በሙሉ ማጥፋት ትችያለሽ።"
                    else
                        "Your privacy and peace of mind are our highest priorities.\n\n1. All health and cycle records remain 100% on your local device.\n2. No personal wellness entries are collected, sold, or synced with external servers.\n3. You have complete control to export or permanently wipe your data at any time.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                    Text(if (isAmharic) "እሺ" else "Close", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Medical Disclaimer Modal
    if (showDisclaimerDialog) {
        AlertDialog(
            onDismissRequest = { showDisclaimerDialog = false },
            title = {
                Text(
                    text = if (isAmharic) "የህክምና ማሳሰቢያ 🩺" else "Medical Disclaimer 🩺",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isAmharic)
                        "ይህ መተግበሪያ ለግል ዑደት ክትትል እና ለጤና መረጃ ብቻ የተዘጋጀ ነው። የባለሙያ የህክምና ምክርን፣ ምርመራን ወይም ህክምናን አይተካም። ለማንኛውም የጤና ጥያቄ ሁልጊዜ የጤና ባለሙያሽን አማክሪ።"
                    else
                        "This app is intended solely for personal wellness tracking and informational cycle planning. It is not a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of your qualified healthcare provider with any medical questions.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showDisclaimerDialog = false }) {
                    Text(if (isAmharic) "እሺ" else "Close", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
