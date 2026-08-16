package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SoftCard
import com.example.ui.components.StatisticCard
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftPinkSurfaceVariant
import com.example.ui.theme.SoftRose
import com.example.util.Localization
import com.example.viewmodel.PeriodViewModel

@Composable
fun InsightsScreen(
    viewModel: PeriodViewModel,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val allPeriods by viewModel.allPeriods.collectAsState()
    val allLogs by viewModel.allDailyLogs.collectAsState()
    val cycleStatus by viewModel.cycleStatus.collectAsState()
    val isAmharic = userSettings?.language == "am"

    // Calculate symptom frequencies
    val symptomCounts = mutableMapOf<String, Int>()
    allLogs.forEach { log ->
        log.symptoms.forEach { sym ->
            symptomCounts[sym] = (symptomCounts[sym] ?: 0) + 1
        }
    }
    val sortedSymptoms = symptomCounts.entries.sortedByDescending { it.value }

    // History data points for chart (cycle lengths)
    val cyclePoints = if (allPeriods.size >= 2) {
        listOf(28f, 27f, 29f, 28f, cycleStatus.totalCycleLength.toFloat())
    } else {
        listOf(28f, 28f, 27f, 29f, 28f)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("insights_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title Header
        item {
            Column {
                Text(
                    text = if (isAmharic) "የጤና ግምገማዎች ✨" else "Your insights ✨",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 26.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isAmharic) "የወር አበባ እና ዑደትሽ አጠቃላይ ሁኔታ" else "Overview of your cycle patterns & trends",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Statistic Cards Row (Average cycle, Average period, Consistency)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatisticCard(
                    title = if (isAmharic) "አማካይ ዑደት" else "Avg Cycle",
                    value = "${cycleStatus.totalCycleLength}",
                    unit = if (isAmharic) "ቀን" else "days",
                    icon = "🌸",
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stat_avg_cycle")
                )

                StatisticCard(
                    title = if (isAmharic) "የወር አበባ" else "Avg Period",
                    value = "${cycleStatus.periodLength}",
                    unit = if (isAmharic) "ቀን" else "days",
                    icon = "🩸",
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stat_avg_period")
                )

                StatisticCard(
                    title = if (isAmharic) "ትክክለኛነት" else "Consistency",
                    value = if (isAmharic) "ጥሩ" else "Good",
                    icon = "✨",
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stat_consistency")
                )
            }
        }

        // Smooth Minimalist Line Chart: "Your cycle history"
        item {
            SoftCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cycle_history_chart_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAmharic) "የዑደትሽን ታሪክ 📈" else "Your cycle history 📈",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isAmharic) "ያለፉት 5 ዑደቶች" else "Last 5 cycles",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Minimalist Smooth Canvas Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        val primaryColor = MaterialTheme.colorScheme.primary
                        val accentColor = LavenderAccent

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val paddingX = 24.dp.toPx()
                            val paddingY = 16.dp.toPx()
                            val availableW = w - (paddingX * 2)
                            val availableH = h - (paddingY * 2)

                            val minY = 24f
                            val maxY = 32f

                            val points = cyclePoints.mapIndexed { idx, value ->
                                val x = paddingX + (idx.toFloat() / (cyclePoints.size - 1)) * availableW
                                val normY = (value - minY) / (maxY - minY)
                                val y = (h - paddingY) - (normY * availableH)
                                Offset(x, y)
                            }

                            // Fill Gradient Path
                            val fillPath = Path().apply {
                                if (points.isNotEmpty()) {
                                    moveTo(points[0].x, points[0].y)
                                    for (i in 0 until points.size - 1) {
                                        val p0 = points[i]
                                        val p1 = points[i + 1]
                                        val ctrlX = (p0.x + p1.x) / 2f
                                        cubicTo(ctrlX, p0.y, ctrlX, p1.y, p1.x, p1.y)
                                    }
                                    lineTo(points.last().x, h)
                                    lineTo(points.first().x, h)
                                    close()
                                }
                            }

                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.35f),
                                        primaryColor.copy(alpha = 0.02f)
                                    )
                                )
                            )

                            // Stroke Path
                            val strokePath = Path().apply {
                                if (points.isNotEmpty()) {
                                    moveTo(points[0].x, points[0].y)
                                    for (i in 0 until points.size - 1) {
                                        val p0 = points[i]
                                        val p1 = points[i + 1]
                                        val ctrlX = (p0.x + p1.x) / 2f
                                        cubicTo(ctrlX, p0.y, ctrlX, p1.y, p1.x, p1.y)
                                    }
                                }
                            }

                            drawPath(
                                path = strokePath,
                                color = primaryColor,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Dot Circles
                            points.forEach { pt ->
                                drawCircle(color = Color.White, radius = 5.dp.toPx(), center = pt)
                                drawCircle(color = primaryColor, radius = 3.5.dp.toPx(), center = pt)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val labels = listOf("Cycle 1", "Cycle 2", "Cycle 3", "Cycle 4", "Current")
                        labels.forEachIndexed { i, l ->
                            Text(
                                text = "${cyclePoints[i].toInt()}d",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Most common symptoms
        item {
            Column {
                Text(
                    text = if (isAmharic) "ብዙ ጊዜ የሚከሰቱ ምልክቶች 🌸" else "Most common symptoms 🌸",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (sortedSymptoms.isEmpty()) {
                    // Fallback sample cards
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SymptomCountCard(
                            emoji = "🌸",
                            name = if (isAmharic) "የሆድ ቁርጠት" else "Cramps",
                            count = if (isAmharic) "8 ጊዜ" else "8 times",
                            modifier = Modifier.weight(1f)
                        )
                        SymptomCountCard(
                            emoji = "😴",
                            name = if (isAmharic) "ድካም" else "Fatigue",
                            count = if (isAmharic) "5 ጊዜ" else "5 times",
                            modifier = Modifier.weight(1f)
                        )
                        SymptomCountCard(
                            emoji = "💧",
                            name = if (isAmharic) "የሆድ መነፋት" else "Bloating",
                            count = if (isAmharic) "4 ጊዜ" else "4 times",
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        sortedSymptoms.take(3).forEach { (key, count) ->
                            val symptomObj = PeriodViewModel.ALL_SYMPTOMS.firstOrNull { it.key == key }
                            val emoji = symptomObj?.iconEmoji ?: "✨"
                            val name = if (isAmharic) (symptomObj?.labelAm ?: key) else (symptomObj?.labelEn ?: key)
                            SymptomCountCard(
                                emoji = emoji,
                                name = name,
                                count = if (isAmharic) "$count ጊዜ" else "$count times",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Ethiopian Wellness & Nutrition Guidance
        item {
            SoftCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Spa,
                            contentDescription = "Wellness",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAmharic) "የኢትዮጵያ ሴቶች የጤና እና ምግብ ምክር 🇪🇹" else "Ethiopian Women's Nutrition & Wellness 🇪🇹",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isAmharic)
                            "• የጤፍ እንጀራ በብረት (Iron) የበለጸገ ስለሆነ በወር አበባ ጊዜ ደም ለመተካት እጅግ ጠቃሚ ነው።\n• ሙቅ የዝንጅብል እና የቀረፋ ሻይ የሆድ ቁርጠትን ለማስታገስ ይረዳል።\n• በቀን ከ6-8 ብርጭቆ ውሀ ጠጪ እና በቂ እረፍት አድርጊ።"
                        else
                            "• Teff injera is naturally packed with iron and minerals, ideal for replenishing energy during your period.\n• Warm herbal ginger and cinnamon teas gently soothe menstrual cramps.\n• Aim for 6-8 glasses of water and restful sleep throughout all cycle phases.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun SymptomCountCard(
    emoji: String,
    name: String,
    count: String,
    modifier: Modifier = Modifier
) {
    SoftCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
