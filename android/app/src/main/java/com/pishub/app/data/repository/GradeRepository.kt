package com.pishub.app.data.repository

import com.google.firebase.database.DatabaseReference
import com.pishub.app.data.local.GradeDao
import com.pishub.app.data.model.Grade
import com.pishub.app.data.model.GradeAnalytics
import com.pishub.app.data.model.GradeReport
import com.pishub.app.data.model.GradeTrend
import com.pishub.app.data.model.SubjectAverage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class GradeRepository(
    private val gradeDao: GradeDao,
    private val firebaseRef: DatabaseReference
) {
    fun getGradesByStudent(studentId: String): Flow<List<Grade>> {
        return gradeDao.getGradesByStudent(studentId)
    }

    fun getGradesByStudentAndSubject(studentId: String, subjectId: String): Flow<List<Grade>> {
        return gradeDao.getGradesByStudentAndSubject(studentId, subjectId)
    }

    fun getGradesByStudentAndTerm(studentId: String, term: String): Flow<List<Grade>> {
        return gradeDao.getGradesByStudentAndTerm(studentId, term)
    }

    fun getGradesByTeacher(teacherId: String): Flow<List<Grade>> {
        return gradeDao.getGradesByTeacher(teacherId)
    }

    suspend fun getStudentAverage(studentId: String): Float {
        return gradeDao.getStudentAverage(studentId) ?: 0f
    }

    suspend fun getStudentSubjectAverage(studentId: String, subjectId: String): Float {
        return gradeDao.getStudentSubjectAverage(studentId, subjectId) ?: 0f
    }

    suspend fun inputGrade(grade: Grade): Result<Unit> {
        return try {
            gradeDao.insertGrade(grade.copy(isSynced = false))
            
            try {
                firebaseRef.child(grade.id).setValue(grade.copy(isSynced = true)).await()
                gradeDao.updateGrade(grade.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGradeAnalytics(studentId: String): GradeAnalytics {
        val grades = gradeDao.getGradesByStudent(studentId).first()
        
        if (grades.isEmpty()) {
            return GradeAnalytics(
                studentId = studentId,
                overallAverage = 0f,
                subjectAverages = emptyList(),
                highestGrade = null,
                lowestGrade = null,
                trend = GradeTrend.STABLE
            )
        }

        val overallAverage = getStudentAverage(studentId)
        
        // Calculate subject averages
        val subjectGroups = grades.groupBy { it.subjectId }
        val subjectAverages = subjectGroups.map { (subjectId, subjectGrades) ->
            val avg = subjectGrades.map { (it.score / it.maxScore) * 100 }.average().toFloat()
            SubjectAverage(
                subjectId = subjectId,
                subjectName = subjectGrades.first().subjectName,
                average = avg,
                totalGrades = subjectGrades.size
            )
        }

        val highestGrade = grades.maxByOrNull { it.score / it.maxScore }
        val lowestGrade = grades.minByOrNull { it.score / it.maxScore }

        // Calculate trend
        val trend = calculateTrend(grades)

        return GradeAnalytics(
            studentId = studentId,
            overallAverage = overallAverage,
            subjectAverages = subjectAverages,
            highestGrade = highestGrade,
            lowestGrade = lowestGrade,
            trend = trend
        )
    }

    private fun calculateTrend(grades: List<Grade>): GradeTrend {
        val sortedGrades = grades.sortedBy { it.createdAt }
        if (sortedGrades.size < 3) return GradeTrend.STABLE

        val firstHalf = sortedGrades.take(sortedGrades.size / 2)
        val secondHalf = sortedGrades.takeLast(sortedGrades.size / 2)

        val firstAvg = firstHalf.map { it.score / it.maxScore }.average()
        val secondAvg = secondHalf.map { it.score / it.maxScore }.average()

        return when {
            secondAvg - firstAvg > 0.1 -> GradeTrend.IMPROVING
            firstAvg - secondAvg > 0.1 -> GradeTrend.DECLINING
            else -> GradeTrend.STABLE
        }
    }

    suspend fun generateReport(studentId: String, term: String): GradeReport {
        val grades = gradeDao.getGradesByStudentAndTerm(studentId, term).first()
        val average = if (grades.isNotEmpty()) grades.map { (it.score / it.maxScore) * 100 }.average().toFloat() else 0f
        
        val subjectGroups = grades.groupBy { it.subjectId }
        val subjectAverages = subjectGroups.mapValues { (_, subjectGrades) ->
            subjectGrades.map { (it.score / it.maxScore) * 100 }.average().toFloat()
        }

        return GradeReport(
            studentId = studentId,
            studentName = grades.firstOrNull()?.studentName ?: "",
            grades = grades,
            average = average,
            subjectAverages = subjectAverages,
            term = term
        )
    }

    suspend fun syncFromFirebase() {
        try {
            val snapshot = firebaseRef.get().await()
            val grades = mutableListOf<Grade>()
            
            for (child in snapshot.children) {
                val grade = child.getValue(Grade::class.java)
                if (grade != null) {
                    grades.add(grade.copy(isSynced = true))
                }
            }
            
            if (grades.isNotEmpty()) {
                gradeDao.insertGrades(grades)
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    suspend fun syncUnsyncedData() {
        val unsynced = gradeDao.getUnsyncedGrades()
        for (grade in unsynced) {
            try {
                firebaseRef.child(grade.id).setValue(grade.copy(isSynced = true)).await()
                gradeDao.updateGrade(grade.copy(isSynced = true))
            } catch (e: Exception) {
                // Continue with next
            }
        }
    }
}
