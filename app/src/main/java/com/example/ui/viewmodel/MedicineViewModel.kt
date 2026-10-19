package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MedicineEntity
import com.example.data.repository.MedicineRepository
import com.example.util.DateUtils
import com.example.util.ExpiryStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    NAME_ASC("A till Ö"),
    NAME_DESC("Ö till A"),
    EXPIRY_SOONEST("Snabbast utgående"),
    EXPIRY_LATEST("Senast utgående")
}

class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedicineRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = MedicineRepository(database.medicineDao(), database.scheduleDao())
    }

    val allMedicines: StateFlow<List<MedicineEntity>> = repository.allMedicines
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allScheduleEntries: StateFlow<List<com.example.data.local.ScheduleEntryEntity>> = repository.allScheduleEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val medicineCount: StateFlow<Int> = repository.medicineCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val searchQuery = MutableStateFlow("")
    val sortOption = MutableStateFlow(SortOption.NAME_ASC)

    // Filtrerad och sorterad lista för "Mina läkemedel"
    val filteredMedicines: StateFlow<List<MedicineEntity>> = combine(
        allMedicines,
        searchQuery,
        sortOption
    ) { list, query, sort ->
        val filtered = if (query.isBlank()) {
            list
        } else {
            val q = query.trim().lowercase()
            list.filter {
                it.name.lowercase().contains(q) ||
                    (it.strength?.lowercase()?.contains(q) == true) ||
                    (it.form?.lowercase()?.contains(q) == true) ||
                    (it.barcode?.contains(q) == true)
            }
        }

        when (sort) {
            SortOption.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
            SortOption.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
            SortOption.EXPIRY_SOONEST -> filtered.sortedWith(
                compareBy(
                    // Läkemedel utan datum hamnar sist
                    { it.expiryDate == null || it.expiryDate.isBlank() },
                    { it.expiryDate }
                )
            )
            SortOption.EXPIRY_LATEST -> filtered.sortedWith(
                compareBy(
                    { it.expiryDate == null || it.expiryDate.isBlank() },
                    { it.expiryDate?.let { d -> Long.MAX_VALUE - d.hashCode() } }
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Kategoriserade listor för "Snart utgångna" (endast läkemedel MED registrerat utgångsdatum)
    val expiredMedicines: StateFlow<List<MedicineEntity>> = allMedicines.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { medicine ->
            if (medicine.expiryDate.isNullOrBlank()) return@filter false
            val info = DateUtils.getExpiryInfo(medicine.expiryDate)
            info.status == ExpiryStatus.EXPIRED
        }.sortedBy { it.expiryDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expiringIn30Days: StateFlow<List<MedicineEntity>> = allMedicines.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { medicine ->
            if (medicine.expiryDate.isNullOrBlank()) return@filter false
            val info = DateUtils.getExpiryInfo(medicine.expiryDate)
            info.status == ExpiryStatus.WITHIN_30_DAYS
        }.sortedBy { it.expiryDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expiringIn90Days: StateFlow<List<MedicineEntity>> = allMedicines.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { medicine ->
            if (medicine.expiryDate.isNullOrBlank()) return@filter false
            val info = DateUtils.getExpiryInfo(medicine.expiryDate)
            info.status == ExpiryStatus.WITHIN_90_DAYS
        }.sortedBy { it.expiryDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun onSortOptionChanged(newSort: SortOption) {
        sortOption.value = newSort
    }

    fun saveMedicine(medicine: MedicineEntity, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.saveMedicine(medicine)
            onComplete(id)
        }
    }

    fun updatePackageCount(id: Long, newCount: Int) {
        if (newCount > 0) {
            viewModelScope.launch {
                repository.updatePackageCount(id, newCount)
            }
        }
    }

    fun deleteMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            repository.deleteMedicine(medicine)
        }
    }

    fun deleteMedicineById(id: Long) {
        viewModelScope.launch {
            repository.deleteMedicineById(id)
        }
    }

    private val barcodeLookupService = com.example.data.remote.BarcodeLookupService()

    fun findMedicineByBarcode(barcode: String, onResult: (MedicineEntity?) -> Unit) {
        viewModelScope.launch {
            val match = repository.getMedicineByBarcode(barcode)
            onResult(match)
        }
    }

    fun lookupBarcodeOnline(barcode: String, onResult: (com.example.data.remote.ProductLookupResult?) -> Unit) {
        viewModelScope.launch {
            val result = barcodeLookupService.lookupBarcode(barcode)
            onResult(result)
        }
    }

    fun findInCatalog(barcode: String): com.example.data.catalog.MedicineCatalogItem? {
        return com.example.data.catalog.SwedishMedicineCatalog.findByBarcode(barcode)
    }

    fun saveScheduleEntry(entry: com.example.data.local.ScheduleEntryEntity, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.saveScheduleEntry(entry)
            onComplete(id)
        }
    }

    fun deleteScheduleEntry(entry: com.example.data.local.ScheduleEntryEntity) {
        viewModelScope.launch {
            repository.deleteScheduleEntry(entry)
        }
    }

    fun deleteScheduleEntryById(id: Long) {
        viewModelScope.launch {
            repository.deleteScheduleEntryById(id)
        }
    }
}
