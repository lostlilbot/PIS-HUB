package com.pishub.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val startTime: Long,
    val endTime: Long,
    val location: String? = null,
    val organizerId: String,
    val organizerName: String,
    val targetRoles: List<UserRole> = emptyList(),
    val targetClasses: List<String> = emptyList(),
    val targetStudents: List<String> = emptyList(),
    val hasReminder: Boolean = false,
    val reminderMinutesBefore: Int = 30,
    val isAllDay: Boolean = false,
    val color: Int? = null,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "schedule")
data class Schedule(
    @PrimaryKey
    val id: String,
    val classId: String,
    val subjectId: String,
    val subjectName: String,
    val teacherId: String,
    val teacherName: String,
    val dayOfWeek: Int, // 1-7 (Monday-Sunday)
    val startTime: String, // "08:00"
    val endTime: String, // "08:45"
    val room: String? = null,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class ClassInfo(
    val id: String,
    val name: String,
    val grade: Int,
    val section: String,
    val teacherId: String,
    val subjectIds: List<String> = emptyList()
)

data class Subject(
    val id: String,
    val name: String,
    val code: String,
    val teacherIds: List<String> = emptyList()
)
