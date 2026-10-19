package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representerar ett registrerat läkemedel i Medicinskåpet.
 * All data lagras lokalt på enheten i en SQLite-databas via Room.
 */
@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "barcode")
    val barcode: String? = null,

    @ColumnInfo(name = "strength")
    val strength: String? = null,

    @ColumnInfo(name = "form")
    val form: String? = null,

    @ColumnInfo(name = "package_count")
    val packageCount: Int = 1,

    // Lagras konsekvent i formatet "YYYY-MM-DD" för exakt SQLite-sortering och jämförelse
    @ColumnInfo(name = "expiry_date")
    val expiryDate: String? = null,

    // Min ordination - fritext från användarens ordination
    @ColumnInfo(name = "prescription")
    val prescription: String? = null,

    // Mina anteckningar - användarens egna anteckningar
    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
