package com.example.ui.navigation

import com.example.data.local.MedicineEntity

sealed class Screen {
    data object Home : Screen()
    data object MedicationList : Screen()
    data class AddEdit(
        val medicineToEdit: MedicineEntity? = null,
        val initialBarcode: String? = null,
        val initialName: String? = null,
        val initialStrength: String? = null,
        val initialForm: String? = null,
        val initialNotes: String? = null
    ) : Screen()
    data object ExpiringSoon : Screen()
    data object Scanner : Screen()
    data object Schedule : Screen()
}
