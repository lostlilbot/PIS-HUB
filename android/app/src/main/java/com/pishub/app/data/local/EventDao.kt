package com.pishub.app.data.local

import androidx.room.*
import com.pishub.app.data.model.Event
import com.pishub.app.data.model.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    // Event queries
    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: String): Event?

    @Query("SELECT * FROM events WHERE startTime >= :startTime AND startTime <= :endTime ORDER BY startTime ASC")
    fun getEventsInRange(startTime: Long, endTime: Long): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE startTime >= :currentTime ORDER BY startTime ASC LIMIT :limit")
    fun getUpcomingEvents(currentTime: Long, limit: Int = 5): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE isSynced = 0")
    suspend fun getUnsyncedEvents(): List<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<Event>)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()

    // Schedule queries
    @Query("SELECT * FROM schedule WHERE id = :id")
    suspend fun getScheduleById(id: String): Schedule?

    @Query("SELECT * FROM schedule WHERE classId = :classId ORDER BY dayOfWeek, startTime")
    fun getScheduleByClass(classId: String): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule WHERE teacherId = :teacherId ORDER BY dayOfWeek, startTime")
    fun getScheduleByTeacher(teacherId: String): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule WHERE classId = :classId AND dayOfWeek = :dayOfWeek ORDER BY startTime")
    fun getScheduleByClassAndDay(classId: String, dayOfWeek: Int): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule WHERE isSynced = 0")
    suspend fun getUnsyncedSchedules(): List<Schedule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<Schedule>)

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("DELETE FROM schedule")
    suspend fun deleteAllSchedules()
}
