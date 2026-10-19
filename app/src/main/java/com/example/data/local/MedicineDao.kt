package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    @Query("SELECT * FROM medicines ORDER BY name COLLATE NOCASE ASC")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE id = :id LIMIT 1")
    suspend fun getMedicineById(id: Long): MedicineEntity?

    @Query("SELECT * FROM medicines WHERE barcode = :barcode LIMIT 1")
    suspend fun getMedicineByBarcode(barcode: String): MedicineEntity?

    @Query("SELECT COUNT(*) FROM medicines")
    fun getMedicineCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity): Long

    @Update
    suspend fun updateMedicine(medicine: MedicineEntity)

    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Long)

    @Query("UPDATE medicines SET package_count = :count, updated_at = :updatedAt WHERE id = :id")
    suspend fun updatePackageCount(id: Long, count: Int, updatedAt: Long = System.currentTimeMillis())
}
