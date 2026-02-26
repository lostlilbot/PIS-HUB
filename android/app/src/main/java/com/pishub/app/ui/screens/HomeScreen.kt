package com.pishub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToDirectory: () -> Unit,
    onNavigateToWebcams: () -> Unit
) {
    var userName by remember { mutableStateOf("User") }
    var userRole by remember { mutableStateOf("Student") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Welcome, $userName",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = userRole,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Weather Widget
            item {
                WeatherCard()
            }
            
            // Quick Actions
            item {
                QuickActionsCard(
                    userRole = userRole,
                    onNavigateToDirectory = onNavigateToDirectory,
                    onNavigateToWebcams = onNavigateToWebcams
                )
            }
            
            // Local News
            item {
                LocalNewsCard()
            }
            
            // Upcoming Events
            item {
                UpcomingEventsCard()
            }
            
            // Recent Announcements
            item {
                RecentAnnouncementsCard()
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun WeatherCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "El Progreso, Honduras",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "28°C",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "Partly Cloudy",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Weather",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun QuickActionsCard(
    userRole: String,
    onNavigateToDirectory: () -> Unit,
    onNavigateToWebcams: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Person,
                    label = "Directory",
                    onClick = onNavigateToDirectory
                )
                if (userRole == "Admin") {
                    QuickActionButton(
                        icon = Icons.Default.Videocam,
                        label = "Webcams",
                        onClick = onNavigateToWebcams
                    )
                }
                QuickActionButton(
                    icon = Icons.Default.Assignment,
                    label = "Tasks",
                    onClick = { }
                )
                QuickActionButton(
                    icon = Icons.Default.Chat,
                    label = "Messages",
                    onClick = { }
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(icon, contentDescription = label)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun LocalNewsCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Local News",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { }) {
                    Text("See All")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sample news items
            NewsItem(
                title = "Schools in El Progreso resume classes after holidays",
                time = "2 hours ago"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            NewsItem(
                title = "Weather alert: Light rain expected this week",
                time = "5 hours ago"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            NewsItem(
                title = "New cultural event at Progreso International School",
                time = "1 day ago"
            )
        }
    }
}

@Composable
fun NewsItem(title: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UpcomingEventsCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Events",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { }) {
                    Text("Calendar")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            EventItem(
                title = "Parent-Teacher Meeting",
                date = "Tomorrow, 2:00 PM"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            EventItem(
                title = "Science Fair",
                date = "Friday, 9:00 AM"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            EventItem(
                title = "Final Exams Begin",
                date = "Next Monday"
            )
        }
    }
}

@Composable
fun EventItem(title: String, date: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Event,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = date,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecentAnnouncementsCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Recent Announcements",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            AnnouncementItem(
                title = "School Closure Notice",
                description = "School will be closed next Friday for maintenance...",
                priority = "High"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            AnnouncementItem(
                title = "New Library Hours",
                description = "Library will now be open from 7 AM to 6 PM...",
                priority = "Normal"
            )
        }
    }
}

@Composable
fun AnnouncementItem(
    title: String,
    description: String,
    priority: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        if (priority == "High") {
            Icon(
                imageVector = Icons.Default.PriorityHigh,
                contentDescription = "High Priority",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
