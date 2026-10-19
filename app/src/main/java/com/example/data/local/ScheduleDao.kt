package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedule_entries ORDER BY id ASC")
    fun getAllScheduleEntries(): Flow<List<ScheduleEntryEntity>>

    @Query("SELECT * FROM schedule_entries WHERE time_of_day = :timeOfDay ORDER BY id ASC")
    fun getScheduleEntriesByTime(timeOfDay: String): Flow<List<ScheduleEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleEntry(entry: ScheduleEntryEntity): Long

    @Update
    suspend fun updateScheduleEntry(entry: ScheduleEntryEntity)

    @Delete
    suspend fun deleteScheduleEntry(entry: ScheduleEntryEntity)

    @Query("DELETE FROM schedule_entries WHERE id = :id")
    suspend fun deleteScheduleEntryById(id: Long)

    @Query("DELETE FROM schedule_entries WHERE medicine_id = :medicineId")
    suspend fun deleteScheduleEntriesByMedicineId(medicineId: Long)
}
