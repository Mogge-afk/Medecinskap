package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ScheduleEntryEntity
import com.example.ui.theme.PineTealPrimary
import com.example.ui.viewmodel.MedicineViewModel

enum class TimePeriod(val id: String, val title: String, val subtitle: String, val icon: ImageVector, val accentColor: Color) {
    MORNING("MORNING", "Morgon", "Kl. 07:00 – 10:00 (Frukost)", Icons.Default.WbSunny, Color(0xFFE65100)),
    NOON("NOON", "Middag / Lunch", "Kl. 11:30 – 14:00 (Mitt på dagen)", Icons.Default.WbSunny, Color(0xFFF57F17)),
    EVENING("EVENING", "Kväll", "Kl. 17:00 – 20:00 (Middagstid)", Icons.Default.WbTwilight, Color(0xFF6A1B9A)),
    NIGHT("NIGHT", "Natt / Sänggående", "Kl. 21:00 – 23:00 (Före sömn)", Icons.Default.Bedtime, Color(0xFF1565C0))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: MedicineViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val allScheduleEntries by viewModel.allScheduleEntries.collectAsStateWithLifecycle()
    val allMedicines by viewModel.allMedicines.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var preselectedPeriod by remember { mutableStateOf(TimePeriod.MORNING) }
    var entryToDelete by remember { mutableStateOf<ScheduleEntryEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Doseringsschema", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("schedule_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tillbaka till startsidan"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            preselectedPeriod = TimePeriod.MORNING
                            showAddDialog = true
                        },
                        modifier = Modifier.testTag("schedule_top_add_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Lägg till i schemat",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    preselectedPeriod = TimePeriod.MORNING
                    showAddDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("schedule_fab_add")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lägg till dos", fontWeight = FontWeight.Bold)
                }
            }
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
                    .widthIn(max = 600.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Info- och sammanfattningskort
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PineTealPrimary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dagligt intag & dosering",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (allScheduleEntries.isEmpty())
                                    "Planera vilka läkemedel du tar på morgonen, mitt på dagen, kvällen och natten."
                                else
                                    "Totalt ${allScheduleEntries.size} inlagda intag fördelade över dygnet.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // De fyra tidssektionerna
                TimePeriod.entries.forEach { period ->
                    val entriesForPeriod = allScheduleEntries.filter { it.timeOfDay == period.id }

                    TimePeriodSection(
                        period = period,
                        entries = entriesForPeriod,
                        onAddClick = {
                            preselectedPeriod = period
                            showAddDialog = true
                        },
                        onDeleteClick = { entry ->
                            entryToDelete = entry
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(72.dp)) // Luft för FAB
            }
        }
    }

    // Bekräfta borttagning
    if (entryToDelete != null) {
        val entry = entryToDelete!!
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text("Ta bort från schemat?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Vill du ta bort ${entry.medicineName} (${entry.dose}) från schemat? Läkemedlet finns kvar i ditt medicinskåp.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteScheduleEntry(entry)
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ta bort")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { entryToDelete = null }) {
                    Text("Avbryt")
                }
            }
        )
    }

    // Dialog för att lägga till i schemat
    if (showAddDialog) {
        AddScheduleBottomSheet(
            availableMedicines = allMedicines.map { it.name + (it.strength?.let { s -> " $s" } ?: "") },
            initialPeriod = preselectedPeriod,
            onDismiss = { showAddDialog = false },
            onSave = { name, period, dose, notes ->
                viewModel.saveScheduleEntry(
                    ScheduleEntryEntity(
                        medicineName = name,
                        timeOfDay = period.id,
                        dose = dose,
                        notes = notes.ifBlank { null }
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TimePeriodSection(
    period: TimePeriod,
    entries: List<ScheduleEntryEntity>,
    onAddClick: () -> Unit,
    onDeleteClick: (ScheduleEntryEntity) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Sektionsrubrik
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = period.accentColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = period.icon,
                                contentDescription = null,
                                tint = period.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = period.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = period.subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                TextButton(
                    onClick = onAddClick,
                    modifier = Modifier.testTag("add_to_${period.id.lowercase()}")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lägg till")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (entries.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Inga doser inlagda för ${period.title.lowercase()}.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    entries.forEach { entry ->
                        ScheduleEntryItem(
                            entry = entry,
                            onDelete = { onDeleteClick(entry) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleEntryItem(
    entry: ScheduleEntryEntity,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.medicineName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PineTealPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = entry.dose,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PineTealPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                if (!entry.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.notes,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("delete_entry_${entry.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Ta bort dos",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddScheduleBottomSheet(
    availableMedicines: List<String>,
    initialPeriod: TimePeriod,
    onDismiss: () -> Unit,
    onSave: (name: String, period: TimePeriod, dose: String, notes: String) -> Unit
) {
    var medicineName by remember { mutableStateOf("") }
    var selectedPeriod by remember { mutableStateOf(initialPeriod) }
    var dose by remember { mutableStateOf("1 tablett") }
    var notes by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    val quickDoses = listOf("1 tablett", "2 tabletter", "1 kapsel", "1 dospåse", "10 ml", "2 spray", "1 plåster")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Lägg till i doseringsschemat",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Val av läkemedel från skåpet (om det finns registrerade)
            if (availableMedicines.isNotEmpty()) {
                Text(
                    text = "Välj från ditt medicinskåp:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableMedicines.forEach { name ->
                        FilterChip(
                            selected = medicineName == name,
                            onClick = {
                                medicineName = name
                                nameError = false
                            },
                            label = { Text(name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Textfält för läkemedelsnamn
            OutlinedTextField(
                value = medicineName,
                onValueChange = {
                    medicineName = it
                    nameError = false
                },
                label = { Text("Läkemedelsnamn *") },
                placeholder = { Text("T.ex. Alvedon 500 mg") },
                isError = nameError,
                supportingText = if (nameError) {
                    { Text("Ange läkemedlets namn") }
                } else null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_medicine_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Val av tidpunkt
            Text(
                text = "Tidpunkt på dygnet:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TimePeriod.entries.forEach { period ->
                    val isSelected = selectedPeriod == period
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) period.accentColor.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, period.accentColor) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedPeriod = period }
                            .padding(vertical = 10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = period.icon,
                                contentDescription = null,
                                tint = if (isSelected) period.accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = period.title.split(" ")[0], // "Morgon", "Middag", "Kväll", "Natt"
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) period.accentColor else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dos
            OutlinedTextField(
                value = dose,
                onValueChange = { dose = it },
                label = { Text("Dosering") },
                placeholder = { Text("T.ex. 1 tablett") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_dose_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Snabbval för dos
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickDoses.forEach { quick ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { dose = quick }
                    ) {
                        Text(
                            text = quick,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Notering / intagsinstruktion
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Instruktion / Notering (valfritt)") },
                placeholder = { Text("T.ex. Tas efter frukost med ett glas vatten") },
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_notes_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (medicineName.isBlank()) {
                        nameError = true
                    } else {
                        onSave(medicineName.trim(), selectedPeriod, dose.ifBlank { "1 dos" }, notes.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("schedule_save_button")
            ) {
                Text("Spara i schemat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
