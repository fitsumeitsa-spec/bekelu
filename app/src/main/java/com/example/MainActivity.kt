package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PetalParticleEffect
import com.example.ui.screens.AppLockScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DailyLogScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SoftPinkBorder
import com.example.ui.theme.SoftRose
import com.example.util.Localization
import com.example.viewmodel.PeriodViewModel
import kotlinx.coroutines.delay

enum class ScreenTab(
    val route: String,
    val titleEn: String,
    val titleAm: String,
    val icon: ImageVector
) {
    TODAY("today", "Today", "ዛሬ", Icons.Rounded.Spa),
    CALENDAR("calendar", "Calendar", "ቀን", Icons.Rounded.CalendarMonth),
    TRACK("track", "Log", "መዝግብ", Icons.Rounded.EditNote),
    INSIGHTS("insights", "Insights", "ግምገማ", Icons.Rounded.Insights),
    YOU("you", "You", "እኔ", Icons.Rounded.Person)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.example.util.NotificationHelper.createNotificationChannels(this)
        setContent {
            MyApplicationTheme {
                MainAppRoot()
            }
        }
    }
}

@Composable
fun MainAppRoot(viewModel: PeriodViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val isAppLocked by viewModel.isAppLocked.collectAsState()
    val showCelebration by viewModel.showCelebration.collectAsState()

    val isAmharic = userSettings?.language == "am"
    val isOnboardingComplete = userSettings?.isOnboardingCompleted ?: false

    var currentScreen by remember { mutableStateOf(ScreenTab.TODAY) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Auto-dismiss celebration effect after 3.5 seconds
    LaunchedEffect(showCelebration) {
        if (showCelebration) {
            delay(3500)
            viewModel.dismissCelebration()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // 1. App Lock Screen
            isAppLocked -> {
                AppLockScreen(
                    viewModel = viewModel,
                    onUnlocked = { /* unlocked state in VM */ },
                    isAmharic = isAmharic
                )
            }

            // 2. Onboarding Flow (if first time launch)
            !isOnboardingComplete -> {
                OnboardingScreen(
                    viewModel = viewModel,
                    onFinished = {
                        currentScreen = ScreenTab.TODAY
                    }
                )
            }

            // 3. Main Application Flow
            else -> {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        GirlyBottomNavigationBar(
                            currentTab = currentScreen,
                            onTabSelected = { currentScreen = it },
                            isAmharic = isAmharic
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            ScreenTab.TODAY -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToCalendar = { currentScreen = ScreenTab.CALENDAR },
                                    onNavigateToDailyLog = { currentScreen = ScreenTab.TRACK },
                                    onNavigateToSettings = { currentScreen = ScreenTab.YOU }
                                )
                            }
                            ScreenTab.CALENDAR -> {
                                CalendarScreen(
                                    viewModel = viewModel
                                )
                            }
                            ScreenTab.TRACK -> {
                                DailyLogScreen(
                                    viewModel = viewModel
                                )
                            }
                            ScreenTab.INSIGHTS -> {
                                InsightsScreen(
                                    viewModel = viewModel
                                )
                            }
                            ScreenTab.YOU -> {
                                SettingsScreen(
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Petal Celebration Overlay
        PetalParticleEffect(
            isActive = showCelebration,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun GirlyBottomNavigationBar(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    isAmharic: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(72.dp)
        ) {
            val tabs = listOf(
                ScreenTab.TODAY,
                ScreenTab.CALENDAR,
                ScreenTab.TRACK,
                ScreenTab.INSIGHTS,
                ScreenTab.YOU
            )

            tabs.forEach { tab ->
                val isSelected = currentTab == tab
                val isCenterTrack = tab == ScreenTab.TRACK

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        if (isCenterTrack) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                                shadowElevation = 3.dp,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "+",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        } else {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.titleEn,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = if (isAmharic) tab.titleAm else tab.titleEn,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        indicatorColor = if (isCenterTrack) Color.Transparent else MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_${tab.route}")
                )
            }
        }
    }
}
