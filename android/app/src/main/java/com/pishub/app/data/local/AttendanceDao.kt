package com.pishub.app.data.local

import androidx.room.*
import com.pishub.app.data.model.Attendance
import com.pishub.app.data.model.AttendanceStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE id = :id")
    suspend fun getAttendanceById(id: String): Attendance?

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE classId = :classId AND date = :date")
    fun getAttendanceByClassAndDate(classId: String, date: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE classId = :classId AND subjectId = :subjectId AND date = :date")
    suspend fun getAttendanceByClassSubjectAndDate(classId: String, subjectId: String, date: Long): List<Attendance>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND date BETWEEN :startDate AND :endDate")
    fun getAttendanceByStudentAndDateRange(studentId: String, startDate: Long, endDate: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE isSynced = 0")
    suspend fun getUnsyncedAttendance(): List<Attendance>

    @Query("SELECT * FROM attendance WHERE status = :status")
    fun getAttendanceByStatus(status: AttendanceStatus): Flow<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendances: List<Attendance>)

    @Update
    suspend fun updateAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("DELETE FROM attendance")
    suspend fun deleteAllAttendance()
}
