package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.MedicineEntity
import com.example.util.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMedicationScreen(
    medicineToEdit: MedicineEntity? = null,
    initialBarcode: String? = null,
    initialName: String? = null,
    initialStrength: String? = null,
    initialForm: String? = null,
    initialNotes: String? = null,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val isEditing = medicineToEdit != null

    var name by remember { mutableStateOf(medicineToEdit?.name ?: initialName ?: "") }
    var nameError by remember { mutableStateOf<String?>(null) }

    var barcode by remember {
        mutableStateOf(medicineToEdit?.barcode ?: initialBarcode ?: "")
    }

    var strength by remember { mutableStateOf(medicineToEdit?.strength ?: initialStrength ?: "") }
    var form by remember { mutableStateOf(medicineToEdit?.form ?: initialForm ?: "") }
    var packageCount by remember { mutableIntStateOf(medicineToEdit?.packageCount ?: 1) }

    var expiryDate by remember { mutableStateOf(medicineToEdit?.expiryDate ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }

    var prescription by remember { mutableStateOf(medicineToEdit?.prescription ?: "") }
    var notes by remember { mutableStateOf(medicineToEdit?.notes ?: initialNotes ?: "") }

    val commonForms = listOf(
        "Tablett", "Kapsel", "Kräm", "Salva", "Lösning", "Spray", "Droppar", "Plåster"
    )

    val scrollState = rememberScrollState()

    // DatePicker State
    val initialDateMillis = remember(expiryDate) {
        if (DateUtils.isValidIsoDate(expiryDate)) {
            val localDate = LocalDate.parse(expiryDate)
            localDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        } else {
            System.currentTimeMillis()
        }
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Redigera läkemedel" else "Lägg till läkemedel",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tillbaka"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Namn (Obligatoriskt)
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = null
                    },
                    label = { Text("Läkemedlets namn *") },
                    placeholder = { Text("T.ex. Alvedon, Ipren, Omeprazol") },
                    isError = nameError != null,
                    supportingText = {
                        if (nameError != null) {
                            Text(nameError ?: "", color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Obligatoriskt fält")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("medicine_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Styrka (valfritt)
                OutlinedTextField(
                    value = strength,
                    onValueChange = { strength = it },
                    label = { Text("Styrka (valfritt)") },
                    placeholder = { Text("T.ex. 500 mg, 10 mg/ml, 1 %") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("medicine_strength_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Streckkod (valfritt)
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it.filter { char -> char.isDigit() || char.isLetter() } },
                    label = { Text("Streckkod / EAN (valfritt)") },
                    placeholder = { Text("T.ex. 7300009012345") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Streckkodsikon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (barcode.isNotEmpty()) {
                            IconButton(onClick = { barcode = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Rensa streckkod"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("medicine_barcode_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Läkemedelsform (valfritt)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Läkemedelsform (valfritt)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        commonForms.forEach { item ->
                            val isSelected = form.equals(item, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    form = if (isSelected) "" else item
                                },
                                label = { Text(item) },
                                modifier = Modifier.testTag("form_chip_$item")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = form,
                        onValueChange = { form = it },
                        placeholder = { Text("Eller skriv egen form...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Antal förpackningar (standard 1, positivt heltal)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Antal förpackningar",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Standardvärde: 1 förpackning",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (packageCount > 1) packageCount--
                                },
                                enabled = packageCount > 1,
                                modifier = Modifier.testTag("package_count_decrease")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Minska antal")
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = "$packageCount",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .testTag("package_count_display")
                                )
                            }

                            IconButton(
                                onClick = { packageCount++ },
                                modifier = Modifier.testTag("package_count_increase")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Öka antal")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Utgångsdatum (valfritt)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Utgångsdatum (valfritt)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = {
                            expiryDate = it
                        },
                        label = { Text("Datum (ÅÅÅÅ-MM-DD)") },
                        placeholder = { Text("T.ex. 2026-12-31") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Kalenderikon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (expiryDate.isNotEmpty()) {
                                    IconButton(onClick = { expiryDate = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Rensa datum")
                                    }
                                }
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        Icons.Default.CalendarMonth,
                                        contentDescription = "Öppna kalenderväljare",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        supportingText = {
                            if (expiryDate.isNotEmpty()) {
                                val info = DateUtils.getExpiryInfo(expiryDate)
                                Text(info.displayText, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                Text("Lämna tomt om datum saknas")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("medicine_expiry_input")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Min ordination (valfri fritext)
                OutlinedTextField(
                    value = prescription,
                    onValueChange = { prescription = it },
                    label = { Text("Min ordination (valfri fritext)") },
                    placeholder = { Text("T.ex. 1 tablett vid behov mot huvudvärk, max 4 tabletter per dygn enligt läkare.") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("medicine_prescription_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mina anteckningar (valfri fritext)
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Mina anteckningar (valfritt)") },
                    placeholder = { Text("T.ex. Förvaras i medicinlådan i kylskåpet, öppnad förpackning.") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("medicine_notes_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Ansvarsfriskrivningsruta
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Information",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Observera: Du ansvarar själv för att uppgifterna överensstämmer med din aktuella ordination. Appen tolkar eller ändrar inte dosering automatiskt.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Spara & Avbryt knappar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("cancel_button")
                    ) {
                        Text("Avbryt")
                    }

                    Button(
                        onClick = {
                            val cleanName = name.trim()
                            if (cleanName.isBlank()) {
                                nameError = "Vänligen ange läkemedlets namn"
                                return@Button
                            }

                            val cleanExpiry = if (expiryDate.isNotBlank() && DateUtils.isValidIsoDate(expiryDate)) {
                                expiryDate.trim()
                            } else {
                                null
                            }

                            val entity = MedicineEntity(
                                id = medicineToEdit?.id ?: 0L,
                                name = cleanName,
                                barcode = barcode.trim().ifEmpty { null },
                                strength = strength.trim().ifEmpty { null },
                                form = form.trim().ifEmpty { null },
                                packageCount = if (packageCount > 0) packageCount else 1,
                                expiryDate = cleanExpiry,
                                prescription = prescription.trim().ifEmpty { null },
                                notes = notes.trim().ifEmpty { null },
                                createdAt = medicineToEdit?.createdAt ?: System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                            )

                            // Save through callback
                            SaveHolder.lastEntity = entity
                            onSaveSuccess()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(52.dp)
                            .testTag("save_medicine_button")
                    ) {
                        Text(
                            text = if (isEditing) "Spara ändringar" else "Spara i skåpet",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedLocalDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            expiryDate = selectedLocalDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        }
                        showDatePicker = false
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("Välj datum")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Avbryt")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// Enkel överföring av sparad post till callback
object SaveHolder {
    var lastEntity: MedicineEntity? = null
}
