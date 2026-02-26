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
fun GradesScreen() {
    var selectedTerm by remember { mutableStateOf("First Quarter") }
    val terms = listOf("First Quarter", "Second Quarter", "Third Quarter", "Fourth Quarter")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grades") },
                actions = {
                    IconButton(onClick = { /* Generate Report */ }) {
                        Icon(Icons.Default.Assessment, contentDescription = "Generate Report")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Term selector
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedTerm,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Term") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    terms.forEach { term ->
                        DropdownMenuItem(
                            text = { Text(term) },
                            onClick = {
                                selectedTerm = term
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Overall Average
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Overall Average",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "85%",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subject Grades
            Text(
                text = "Subject Grades",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(6) { index ->
                    SubjectGradeItem(
                        subject = getSubjectName(index),
                        grade = (70..100).random(),
                        trend = when (index % 3) {
                            0 -> "Improving"
                            1 -> "Stable"
                            else -> "Declining"
                        }
                    )
                }
            }
        }
    }
}

fun getSubjectName(index: Int): String {
    return listOf(
        "Mathematics",
        "English",
        "Science",
        "History",
        "Art",
        "Physical Education"
    )[index]
}

@Composable
fun SubjectGradeItem(
    subject: String,
    grade: Int,
    trend: String
) {
    val gradeColor = when {
        grade >= 90 -> MaterialTheme.colorScheme.primary
        grade >= 80 -> MaterialTheme.colorScheme.tertiary
        grade >= 70 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }
    
    val trendIcon = when (trend) {
        "Improving" -> Icons.Default.TrendingUp
        "Declining" -> Icons.Default.TrendingDown
        else -> Icons.Default.TrendingFlat
    }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = trend,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$grade%",
                style = MaterialTheme.typography.headlineSmall,
                color = gradeColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = trendIcon,
                contentDescription = trend,
                tint = when (trend) {
                    "Improving" -> MaterialTheme.colorScheme.primary
                    "Declining" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
