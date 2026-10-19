package com.example.data.repository

import com.example.data.local.MedicineDao
import com.example.data.local.MedicineEntity
import com.example.data.local.ScheduleDao
import com.example.data.local.ScheduleEntryEntity
import kotlinx.coroutines.flow.Flow

class MedicineRepository(
    private val medicineDao: MedicineDao,
    private val scheduleDao: ScheduleDao? = null
) {

    val allMedicines: Flow<List<MedicineEntity>> = medicineDao.getAllMedicines()
    val medicineCount: Flow<Int> = medicineDao.getMedicineCount()

    suspend fun getMedicineById(id: Long): MedicineEntity? {
        return medicineDao.getMedicineById(id)
    }

    suspend fun getMedicineByBarcode(barcode: String): MedicineEntity? {
        return medicineDao.getMedicineByBarcode(barcode.trim())
    }

    suspend fun saveMedicine(medicine: MedicineEntity): Long {
        val updated = medicine.copy(updatedAt = System.currentTimeMillis())
        return medicineDao.insertMedicine(updated)
    }

    suspend fun updatePackageCount(id: Long, newCount: Int) {
        if (newCount > 0) {
            medicineDao.updatePackageCount(id, newCount)
        }
    }

    suspend fun deleteMedicine(medicine: MedicineEntity) {
        medicineDao.deleteMedicine(medicine)
        scheduleDao?.deleteScheduleEntriesByMedicineId(medicine.id)
    }

    suspend fun deleteMedicineById(id: Long) {
        medicineDao.deleteMedicineById(id)
        scheduleDao?.deleteScheduleEntriesByMedicineId(id)
    }

    // Schema-operationer
    val allScheduleEntries: Flow<List<ScheduleEntryEntity>> =
        scheduleDao?.getAllScheduleEntries() ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveScheduleEntry(entry: ScheduleEntryEntity): Long {
        return scheduleDao?.insertScheduleEntry(entry) ?: 0L
    }

    suspend fun deleteScheduleEntry(entry: ScheduleEntryEntity) {
        scheduleDao?.deleteScheduleEntry(entry)
    }

    suspend fun deleteScheduleEntryById(id: Long) {
        scheduleDao?.deleteScheduleEntryById(id)
    }
}
