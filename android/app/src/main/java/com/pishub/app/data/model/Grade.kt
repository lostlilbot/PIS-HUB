package com.pishub.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grades")
data class Grade(
    @PrimaryKey
    val id: String,
    val studentId: String,
    val studentName: String,
    val classId: String,
    val subjectId: String,
    val subjectName: String,
    val teacherId: String,
    val teacherName: String,
    val term: String, // e.g., "First Quarter", "Second Quarter"
    val score: Float,
    val maxScore: Float = 100f,
    val gradeType: String, // "Quiz", "Test", "Project", "Homework", "Exam"
    val description: String? = null,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class GradeReport(
    val studentId: String,
    val studentName: String,
    val grades: List<Grade>,
    val average: Float,
    val subjectAverages: Map<String, Float>,
    val term: String
)

data class SubjectAverage(
    val subjectId: String,
    val subjectName: String,
    val average: Float,
    val totalGrades: Int
)

data class GradeAnalytics(
    val studentId: String,
    val overallAverage: Float,
    val subjectAverages: List<SubjectAverage>,
    val highestGrade: Grade?,
    val lowestGrade: Grade?,
    val trend: GradeTrend // Improving, Declining, Stable
)

enum class GradeTrend {
    IMPROVING,
    DECLINING,
    STABLE
}
