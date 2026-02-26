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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunicationScreen(
    onNavigateToChat: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Communication") },
                actions = {
                    IconButton(onClick = { /* New Message */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "New Message")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Send Announcement */ }
            ) {
                Icon(Icons.Default.Campaign, contentDescription = "Announcement")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Messages") },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Announcements") },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = null) }
                )
            }
            
            when (selectedTab) {
                0 -> MessagesTab(onNavigateToChat)
                1 -> AnnouncementsTab()
            }
        }
    }
}

@Composable
fun MessagesTab(onNavigateToChat: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(10) { index ->
            MessageItem(
                name = listOf("John Smith", "Maria Garcia", "Teacher Johnson", "Principal Williams", "Parent Admin")[index % 5],
                message = "This is a sample message from ${listOf("John", "Maria", "Teacher Johnson", "Principal Williams", "Parent Admin")[index % 5]}...",
                time = "${index + 1}h ago",
                isUnread = index % 3 == 0,
                onClick = { onNavigateToChat("user_$index") }
            )
        }
    }
}

@Composable
fun MessageItem(
    name: String,
    message: String,
    time: String,
    isUnread: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (isUnread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isUnread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = time,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            if (isUnread) {
                Spacer(modifier = Modifier.width(8.dp))
                Badge { }
            }
        }
    }
}

@Composable
fun AnnouncementsTab() {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(5) { index ->
            AnnouncementCard(
                title = listOf(
                    "School Closure Notice",
                    "New Library Hours",
                    "Exam Schedule",
                    "Parent Meeting",
                    "Sports Day"
                )[index],
                content = "This is an important announcement that contains detailed information...",
                author = listOf("Principal", "Library", "Academic", "Administration", "Sports")[index],
                date = "${index + 1} day(s) ago",
                priority = when (index) {
                    0 -> "High"
                    1 -> "Normal"
                    else -> "Low"
                }
            )
        }
    }
}

@Composable
fun AnnouncementCard(
    title: String,
    content: String,
    author: String,
    date: String,
    priority: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (priority == "High") {
                    Icon(
                        Icons.Default.PriorityHigh,
                        contentDescription = "High Priority",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = { },
                    label = { Text(priority) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (priority) {
                            "High" -> MaterialTheme.colorScheme.errorContainer
                            "Normal" -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "By $author",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
