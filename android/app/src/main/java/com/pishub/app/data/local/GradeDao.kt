package com.pishub.app.data.local

import androidx.room.*
import com.pishub.app.data.model.Grade
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {
    @Query("SELECT * FROM grades WHERE id = :id")
    suspend fun getGradeById(id: String): Grade?

    @Query("SELECT * FROM grades WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getGradesByStudent(studentId: String): Flow<List<Grade>>

    @Query("SELECT * FROM grades WHERE studentId = :studentId AND subjectId = :subjectId")
    fun getGradesByStudentAndSubject(studentId: String, subjectId: String): Flow<List<Grade>>

    @Query("SELECT * FROM grades WHERE studentId = :studentId AND term = :term")
    fun getGradesByStudentAndTerm(studentId: String, term: String): Flow<List<Grade>>

    @Query("SELECT * FROM grades WHERE teacherId = :teacherId")
    fun getGradesByTeacher(teacherId: String): Flow<List<Grade>>

    @Query("SELECT * FROM grades WHERE classId = :classId AND subjectId = :subjectId")
    fun getGradesByClassAndSubject(classId: String, subjectId: String): Flow<List<Grade>>

    @Query("SELECT AVG(score/maxScore * 100) FROM grades WHERE studentId = :studentId")
    suspend fun getStudentAverage(studentId: String): Float?

    @Query("SELECT AVG(score/maxScore * 100) FROM grades WHERE studentId = :studentId AND subjectId = :subjectId")
    suspend fun getStudentSubjectAverage(studentId: String, subjectId: String): Float?

    @Query("SELECT * FROM grades WHERE isSynced = 0")
    suspend fun getUnsyncedGrades(): List<Grade>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: Grade)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrades(grades: List<Grade>)

    @Update
    suspend fun updateGrade(grade: Grade)

    @Delete
    suspend fun deleteGrade(grade: Grade)

    @Query("DELETE FROM grades")
    suspend fun deleteAllGrades()
}
