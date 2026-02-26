package com.pishub.app.data.local

import androidx.room.*
import com.pishub.app.data.model.EmergencyLesson
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyLessonDao {
    @Query("SELECT * FROM emergency_lessons WHERE id = :id")
    suspend fun getEmergencyLessonById(id: String): EmergencyLesson?

    @Query("SELECT * FROM emergency_lessons ORDER BY scheduledTime ASC")
    fun getAllEmergencyLessons(): Flow<List<EmergencyLesson>>

    @Query("SELECT * FROM emergency_lessons WHERE scheduledTime >= :startTime AND scheduledTime <= :endTime ORDER BY scheduledTime ASC")
    fun getEmergencyLessonsByDateRange(startTime: Long, endTime: Long): Flow<List<EmergencyLesson>>

    @Query("SELECT * FROM emergency_lessons WHERE scheduledTime >= :currentTime ORDER BY scheduledTime ASC")
    fun getUpcomingEmergencyLessons(currentTime: Long): Flow<List<EmergencyLesson>>

    @Query("SELECT * FROM emergency_lessons WHERE classId = :classId ORDER BY scheduledTime ASC")
    fun getEmergencyLessonsByClass(classId: String): Flow<List<EmergencyLesson>>

    @Query("SELECT * FROM emergency_lessons WHERE teacherId = :teacherId ORDER BY scheduledTime ASC")
    fun getEmergencyLessonsByTeacher(teacherId: String): Flow<List<EmergencyLesson>>

    @Query("SELECT * FROM emergency_lessons WHERE isSynced = 0")
    suspend fun getUnsyncedEmergencyLessons(): List<EmergencyLesson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyLesson(emergencyLesson: EmergencyLesson)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyLessons(emergencyLessons: List<EmergencyLesson>)

    @Update
    suspend fun updateEmergencyLesson(emergencyLesson: EmergencyLesson)

    @Delete
    suspend fun deleteEmergencyLesson(emergencyLesson: EmergencyLesson)

    @Query("DELETE FROM emergency_lessons")
    suspend fun deleteAllEmergencyLessons()

    @Query("DELETE FROM emergency_lessons WHERE scheduledTime < :currentTime")
    suspend fun deletePastEmergencyLessons(currentTime: Long)
}
