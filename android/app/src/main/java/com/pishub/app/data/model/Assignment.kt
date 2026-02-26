package com.pishub.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AssignmentStatus {
    PENDING,
    SUBMITTED,
    GRADED,
    LATE,
    MISSING
}

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val classId: String,
    val subjectId: String,
    val subjectName: String,
    val teacherId: String,
    val teacherName: String,
    val assignedTo: List<String>, // Student IDs or class ID
    val dueDate: Long,
    val points: Float = 100f,
    val attachments: List<String> = emptyList(), // URLs
    val isPinned: Boolean = false,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "assignment_submissions")
data class AssignmentSubmission(
    @PrimaryKey
    val id: String,
    val assignmentId: String,
    val studentId: String,
    val studentName: String,
    val content: String? = null,
    val attachments: List<String> = emptyList(), // URLs
    val status: AssignmentStatus = AssignmentStatus.SUBMITTED,
    val grade: Float? = null,
    val feedback: String? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val gradedAt: Long? = null,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class AssignmentWithSubmissions(
    val assignment: Assignment,
    val submissions: List<AssignmentSubmission> = emptyList(),
    val submissionCount: Int = 0,
    val gradedCount: Int = 0
)
