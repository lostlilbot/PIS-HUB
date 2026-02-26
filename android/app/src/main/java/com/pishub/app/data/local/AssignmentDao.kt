package com.pishub.app.data.local

import androidx.room.*
import com.pishub.app.data.model.Assignment
import com.pishub.app.data.model.AssignmentSubmission
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {
    // Assignment queries
    @Query("SELECT * FROM assignments WHERE id = :id")
    suspend fun getAssignmentById(id: String): Assignment?

    @Query("SELECT * FROM assignments WHERE id = :id")
    fun observeAssignment(id: String): Flow<Assignment?>

    @Query("SELECT * FROM assignments WHERE classId = :classId ORDER BY dueDate ASC")
    fun getAssignmentsByClass(classId: String): Flow<List<Assignment>>

    @Query("SELECT * FROM assignments WHERE subjectId = :subjectId ORDER BY dueDate ASC")
    fun getAssignmentsBySubject(subjectId: String): Flow<List<Assignment>>

    @Query("SELECT * FROM assignments WHERE teacherId = :teacherId ORDER BY createdAt DESC")
    fun getAssignmentsByTeacher(teacherId: String): Flow<List<Assignment>>

    @Query("SELECT * FROM assignments WHERE isPinned = 1 ORDER BY dueDate ASC")
    fun getPinnedAssignments(): Flow<List<Assignment>>

    @Query("SELECT * FROM assignments WHERE dueDate >= :currentTime AND dueDate <= :futureTime ORDER BY dueDate ASC")
    fun getUpcomingAssignments(currentTime: Long, futureTime: Long): Flow<List<Assignment>>

    @Query("SELECT * FROM assignments WHERE isSynced = 0")
    suspend fun getUnsyncedAssignments(): List<Assignment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<Assignment>)

    @Update
    suspend fun updateAssignment(assignment: Assignment)

    @Delete
    suspend fun deleteAssignment(assignment: Assignment)

    // Submission queries
    @Query("SELECT * FROM assignment_submissions WHERE id = :id")
    suspend fun getSubmissionById(id: String): AssignmentSubmission?

    @Query("SELECT * FROM assignment_submissions WHERE assignmentId = :assignmentId")
    fun getSubmissionsByAssignment(assignmentId: String): Flow<List<AssignmentSubmission>>

    @Query("SELECT * FROM assignment_submissions WHERE studentId = :studentId")
    fun getSubmissionsByStudent(studentId: String): Flow<List<AssignmentSubmission>>

    @Query("SELECT * FROM assignment_submissions WHERE assignmentId = :assignmentId AND studentId = :studentId")
    suspend fun getSubmissionByAssignmentAndStudent(assignmentId: String, studentId: String): AssignmentSubmission?

    @Query("SELECT * FROM assignment_submissions WHERE isSynced = 0")
    suspend fun getUnsyncedSubmissions(): List<AssignmentSubmission>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: AssignmentSubmission)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmissions(submissions: List<AssignmentSubmission>)

    @Update
    suspend fun updateSubmission(submission: AssignmentSubmission)

    @Delete
    suspend fun deleteSubmission(submission: AssignmentSubmission)

    @Query("DELETE FROM assignments")
    suspend fun deleteAllAssignments()

    @Query("DELETE FROM assignment_submissions")
    suspend fun deleteAllSubmissions()
}
