package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representerar ett schemalagt läkemedelsintag under dagen.
 * Tidsindelning: MORNING (Morgon), NOON (Middag/Lunch), EVENING (Kväll), NIGHT (Natt/Sänggående).
 */
@Entity(tableName = "schedule_entries")
data class ScheduleEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "medicine_id")
    val medicineId: Long? = null,

    @ColumnInfo(name = "medicine_name")
    val medicineName: String,

    @ColumnInfo(name = "time_of_day")
    val timeOfDay: String, // "MORNING", "NOON", "EVENING", "NIGHT"

    @ColumnInfo(name = "dose")
    val dose: String, // t.ex. "1 tablett", "2 spray", "10 ml"

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
