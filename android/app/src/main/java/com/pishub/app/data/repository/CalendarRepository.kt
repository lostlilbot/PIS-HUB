package com.pishub.app.data.repository

import com.google.firebase.database.DatabaseReference
import com.pishub.app.data.local.EventDao
import com.pishub.app.data.model.Event
import com.pishub.app.data.model.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class CalendarRepository(
    private val eventDao: EventDao,
    private val firebaseRef: DatabaseReference
) {
    fun getEventsInRange(startTime: Long, endTime: Long): Flow<List<Event>> {
        return eventDao.getEventsInRange(startTime, endTime)
    }

    fun getUpcomingEvents(currentTime: Long, limit: Int = 5): Flow<List<Event>> {
        return eventDao.getUpcomingEvents(currentTime, limit)
    }

    suspend fun getEventById(id: String): Event? {
        return eventDao.getEventById(id)
    }

    suspend fun createEvent(event: Event): Result<Unit> {
        return try {
            eventDao.insertEvent(event.copy(isSynced = false))
            
            try {
                firebaseRef.child(event.id).setValue(event.copy(isSynced = true)).await()
                eventDao.updateEvent(event.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEvent(event: Event): Result<Unit> {
        return try {
            eventDao.updateEvent(event.copy(isSynced = false, updatedAt = System.currentTimeMillis()))
            
            try {
                firebaseRef.child(event.id).setValue(event.copy(isSynced = true)).await()
                eventDao.updateEvent(event.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEvent(event: Event): Result<Unit> {
        return try {
            eventDao.deleteEvent(event)
            
            try {
                firebaseRef.child(event.id).removeValue().await()
            } catch (e: Exception) {
                // Continue
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Schedule methods
    fun getScheduleByClass(classId: String): Flow<List<Schedule>> {
        return eventDao.getScheduleByClass(classId)
    }

    fun getScheduleByTeacher(teacherId: String): Flow<List<Schedule>> {
        return eventDao.getScheduleByTeacher(teacherId)
    }

    fun getScheduleByClassAndDay(classId: String, dayOfWeek: Int): Flow<List<Schedule>> {
        return eventDao.getScheduleByClassAndDay(classId, dayOfWeek)
    }

    suspend fun createSchedule(schedule: Schedule): Result<Unit> {
        return try {
            eventDao.insertSchedule(schedule.copy(isSynced = false))
            
            try {
                firebaseRef.child("schedule").child(schedule.id).setValue(schedule.copy(isSynced = true)).await()
                eventDao.updateSchedule(schedule.copy(isSynced = true))
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
            val events = mutableListOf<Event>()
            
            for (child in snapshot.children) {
                val event = child.getValue(Event::class.java)
                if (event != null) {
                    events.add(event.copy(isSynced = true))
                }
            }
            
            if (events.isNotEmpty()) {
                eventDao.insertEvents(events)
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }
}
