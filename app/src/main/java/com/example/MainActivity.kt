package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.navigation.Screen
import com.example.ui.screens.AddEditMedicationScreen
import com.example.ui.screens.ExpiringSoonScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MedicationListScreen
import com.example.ui.screens.SaveHolder
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.theme.MedicinskapetTheme
import com.example.ui.viewmodel.MedicineViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MedicineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicinskapetTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MedicinskapetApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MedicinskapetApp(viewModel: MedicineViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    when (val screen = currentScreen) {
        is Screen.Home -> {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToScanner = { currentScreen = Screen.Scanner },
                onNavigateToList = { currentScreen = Screen.MedicationList },
                onNavigateToSchedule = { currentScreen = Screen.Schedule },
                onNavigateToAdd = { currentScreen = Screen.AddEdit() },
                onNavigateToExpiring = { currentScreen = Screen.ExpiringSoon }
            )
        }

        is Screen.MedicationList -> {
            MedicationListScreen(
                viewModel = viewModel,
                onNavigateBack = { currentScreen = Screen.Home },
                onNavigateToAdd = { currentScreen = Screen.AddEdit() },
                onNavigateToEdit = { medicine ->
                    currentScreen = Screen.AddEdit(medicineToEdit = medicine)
                }
            )
        }

        is Screen.Schedule -> {
            ScheduleScreen(
                viewModel = viewModel,
                onNavigateBack = { currentScreen = Screen.Home }
            )
        }

        is Screen.AddEdit -> {
            AddEditMedicationScreen(
                medicineToEdit = screen.medicineToEdit,
                initialBarcode = screen.initialBarcode,
                initialName = screen.initialName,
                initialStrength = screen.initialStrength,
                initialForm = screen.initialForm,
                initialNotes = screen.initialNotes,
                onNavigateBack = { currentScreen = Screen.Home },
                onSaveSuccess = {
                    SaveHolder.lastEntity?.let { entity ->
                        viewModel.saveMedicine(entity)
                    }
                    currentScreen = Screen.MedicationList
                }
            )
        }

        is Screen.ExpiringSoon -> {
            ExpiringSoonScreen(
                viewModel = viewModel,
                onNavigateBack = { currentScreen = Screen.Home },
                onNavigateToEdit = { medicine ->
                    currentScreen = Screen.AddEdit(medicineToEdit = medicine)
                }
            )
        }

        is Screen.Scanner -> {
            ScannerScreen(
                viewModel = viewModel,
                onNavigateBack = { currentScreen = Screen.Home },
                onNavigateToRegisterNew = { barcode, suggestedName, suggestedStrength, suggestedForm, suggestedNotes ->
                    currentScreen = Screen.AddEdit(
                        initialBarcode = barcode,
                        initialName = suggestedName,
                        initialStrength = suggestedStrength,
                        initialForm = suggestedForm,
                        initialNotes = suggestedNotes
                    )
                },
                onNavigateToEdit = { medicine ->
                    currentScreen = Screen.AddEdit(medicineToEdit = medicine)
                }
            )
        }
    }
}
