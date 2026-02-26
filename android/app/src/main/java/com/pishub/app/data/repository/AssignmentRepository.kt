package com.pishub.app.data.repository

import com.google.firebase.database.DatabaseReference
import com.pishub.app.data.local.AssignmentDao
import com.pishub.app.data.model.Assignment
import com.pishub.app.data.model.AssignmentSubmission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class AssignmentRepository(
    private val assignmentDao: AssignmentDao,
    private val firebaseRef: DatabaseReference
) {
    fun getAssignmentsByClass(classId: String): Flow<List<Assignment>> {
        return assignmentDao.getAssignmentsByClass(classId)
    }

    fun getAssignmentsBySubject(subjectId: String): Flow<List<Assignment>> {
        return assignmentDao.getAssignmentsBySubject(subjectId)
    }

    fun getAssignmentsByTeacher(teacherId: String): Flow<List<Assignment>> {
        return assignmentDao.getAssignmentsByTeacher(teacherId)
    }

    fun getPinnedAssignments(): Flow<List<Assignment>> {
        return assignmentDao.getPinnedAssignments()
    }

    fun getUpcomingAssignments(currentTime: Long, futureTime: Long): Flow<List<Assignment>> {
        return assignmentDao.getUpcomingAssignments(currentTime, futureTime)
    }

    suspend fun getAssignmentById(id: String): Assignment? {
        return assignmentDao.getAssignmentById(id)
    }

    suspend fun createAssignment(assignment: Assignment): Result<Unit> {
        return try {
            assignmentDao.insertAssignment(assignment.copy(isSynced = false))
            
            try {
                firebaseRef.child(assignment.id).setValue(assignment.copy(isSynced = true)).await()
                assignmentDao.updateAssignment(assignment.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAssignment(assignment: Assignment): Result<Unit> {
        return try {
            assignmentDao.updateAssignment(assignment.copy(isSynced = false, updatedAt = System.currentTimeMillis()))
            
            try {
                firebaseRef.child(assignment.id).setValue(assignment.copy(isSynced = true)).await()
                assignmentDao.updateAssignment(assignment.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun pinAssignment(assignmentId: String, isPinned: Boolean): Result<Unit> {
        val assignment = assignmentDao.getAssignmentById(assignmentId) ?: return Result.failure(Exception("Assignment not found"))
        return updateAssignment(assignment.copy(isPinned = isPinned))
    }

    suspend fun deleteAssignment(assignment: Assignment): Result<Unit> {
        return try {
            assignmentDao.deleteAssignment(assignment)
            
            try {
                firebaseRef.child(assignment.id).removeValue().await()
            } catch (e: Exception) {
                // Continue
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Submission methods
    fun getSubmissionsByAssignment(assignmentId: String): Flow<List<AssignmentSubmission>> {
        return assignmentDao.getSubmissionsByAssignment(assignmentId)
    }

    fun getSubmissionsByStudent(studentId: String): Flow<List<AssignmentSubmission>> {
        return assignmentDao.getSubmissionsByStudent(studentId)
    }

    suspend fun getSubmissionByAssignmentAndStudent(assignmentId: String, studentId: String): AssignmentSubmission? {
        return assignmentDao.getSubmissionByAssignmentAndStudent(assignmentId, studentId)
    }

    suspend fun submitAssignment(submission: AssignmentSubmission): Result<Unit> {
        return try {
            assignmentDao.insertSubmission(submission.copy(isSynced = false))
            
            try {
                firebaseRef.child("submissions").child(submission.id).setValue(submission.copy(isSynced = true)).await()
                assignmentDao.updateSubmission(submission.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun gradeSubmission(submission: AssignmentSubmission): Result<Unit> {
        return try {
            val gradedSubmission = submission.copy(
                gradedAt = System.currentTimeMillis(),
                isSynced = false
            )
            assignmentDao.updateSubmission(gradedSubmission)
            
            try {
                firebaseRef.child("submissions").child(gradedSubmission.id).setValue(gradedSubmission.copy(isSynced = true)).await()
                assignmentDao.updateSubmission(gradedSubmission.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncFromFirebase() {
        try {
            val snapshot = firebaseRef.get().await()
            val assignments = mutableListOf<Assignment>()
            
            for (child in snapshot.children) {
                val assignment = child.getValue(Assignment::class.java)
                if (assignment != null) {
                    assignments.add(assignment.copy(isSynced = true))
                }
            }
            
            if (assignments.isNotEmpty()) {
                assignmentDao.insertAssignments(assignments)
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    suspend fun syncUnsyncedData() {
        val unsyncedAssignments = assignmentDao.getUnsyncedAssignments()
        for (assignment in unsyncedAssignments) {
            try {
                firebaseRef.child(assignment.id).setValue(assignment.copy(isSynced = true)).await()
                assignmentDao.updateAssignment(assignment.copy(isSynced = true))
            } catch (e: Exception) {
                // Continue with next
            }
        }

        val unsyncedSubmissions = assignmentDao.getUnsyncedSubmissions()
        for (submission in unsyncedSubmissions) {
            try {
                firebaseRef.child("submissions").child(submission.id).setValue(submission.copy(isSynced = true)).await()
                assignmentDao.updateSubmission(submission.copy(isSynced = true))
            } catch (e: Exception) {
                // Continue with next
            }
        }
    }
}
