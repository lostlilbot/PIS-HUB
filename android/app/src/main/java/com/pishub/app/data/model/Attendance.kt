package com.pishub.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE,
    EXCUSED
}

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey
    val id: String,
    val studentId: String,
    val studentName: String,
    val classId: String,
    val subjectId: String,
    val teacherId: String,
    val date: Long, // Date in millis
    val status: AttendanceStatus,
    val notes: String? = null,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class AttendanceRecord(
    val attendance: Attendance,
    val student: User? = null
)

data class AttendanceSummary(
    val totalDays: Int,
    val present: Int,
    val absent: Int,
    val late: Int,
    val excused: Int,
    val attendancePercentage: Float
)
