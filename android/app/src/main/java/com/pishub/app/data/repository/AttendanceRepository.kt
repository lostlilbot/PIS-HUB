package com.pishub.app.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.pishub.app.data.local.AttendanceDao
import com.pishub.app.data.model.Attendance
import com.pishub.app.data.model.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class AttendanceRepository(
    private val attendanceDao: AttendanceDao,
    private val firebaseRef: DatabaseReference
) {
    fun getAttendanceByStudent(studentId: String): Flow<List<Attendance>> {
        return attendanceDao.getAttendanceByStudent(studentId)
    }

    fun getAttendanceByClassAndDate(classId: String, date: Long): Flow<List<Attendance>> {
        return attendanceDao.getAttendanceByClassAndDate(classId, date)
    }

    suspend fun getAttendanceByClassSubjectAndDate(classId: String, subjectId: String, date: Long): List<Attendance> {
        return attendanceDao.getAttendanceByClassSubjectAndDate(classId, subjectId, date)
    }

    fun getAttendanceByStudentAndDateRange(studentId: String, startDate: Long, endDate: Long): Flow<List<Attendance>> {
        return attendanceDao.getAttendanceByStudentAndDateRange(studentId, startDate, endDate)
    }

    suspend fun markAttendance(attendance: Attendance): Result<Unit> {
        return try {
            // Save locally first
            attendanceDao.insertAttendance(attendance.copy(isSynced = false))
            
            // Try to sync to Firebase
            try {
                firebaseRef.child(attendance.id).setValue(attendance.copy(isSynced = true)).await()
                attendanceDao.updateAttendance(attendance.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced, will sync later
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncFromFirebase(classId: String, date: Long) {
        try {
            val snapshot = firebaseRef.orderByChild("classId").equalTo(classId).get().await()
            val attendances = mutableListOf<Attendance>()
            
            for (child in snapshot.children) {
                val attendance = child.getValue(Attendance::class.java)
                if (attendance != null && isSameDay(attendance.date, date)) {
                    attendances.add(attendance.copy(isSynced = true))
                }
            }
            
            if (attendances.isNotEmpty()) {
                attendanceDao.insertAttendances(attendances)
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    suspend fun syncUnsyncedData() {
        val unsynced = attendanceDao.getUnsyncedAttendance()
        for (attendance in unsynced) {
            try {
                firebaseRef.child(attendance.id).setValue(attendance.copy(isSynced = true)).await()
                attendanceDao.updateAttendance(attendance.copy(isSynced = true))
            } catch (e: Exception) {
                // Continue with next
            }
        }
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }
}