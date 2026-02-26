package com.pishub.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_lessons")
data class EmergencyLesson(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val teacherId: String,
    val teacherName: String,
    val subject: String,
    val classId: String,
    val scheduledTime: Long,
    val duration: Int = 45, // minutes
    val meetingLink: String? = null,
    val materials: List<String> = emptyList(), // URLs
    val notified: Boolean = false,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
