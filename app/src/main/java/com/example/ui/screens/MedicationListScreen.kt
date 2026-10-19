package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.MedicineEntity
import com.example.ui.components.MedicationDetailSheet
import com.example.ui.theme.ExpiryExpired
import com.example.ui.theme.ExpiryExpiredContainer
import com.example.ui.theme.ExpiryNotice90
import com.example.ui.theme.ExpiryNotice90Container
import com.example.ui.theme.ExpirySoon30
import com.example.ui.theme.ExpirySoon30Container
import com.example.ui.theme.ExpiryValid
import com.example.ui.theme.ExpiryValidContainer
import com.example.ui.viewmodel.MedicineViewModel
import com.example.ui.viewmodel.SortOption
import com.example.util.DateUtils
import com.example.util.ExpiryStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationListScreen(
    viewModel: MedicineViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (MedicineEntity) -> Unit
) {
    BackHandler { onNavigateBack() }

    val medicines by viewModel.filteredMedicines.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentSort by viewModel.sortOption.collectAsStateWithLifecycle()

    var selectedMedicineForDetail by remember { mutableStateOf<MedicineEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mina läkemedel", fontWeight = FontWeight.Bold)
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_medicine_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Lägg till läkemedel")
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
                    .widthIn(max = 640.dp)
            ) {
                // Sökfält
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Sök på läkemedelsnamn, form eller kod...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Sök",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Rensa sökning")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("search_medicine_input")
                )

                // Sorteringsalternativ (Chips)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Sortera",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )

                    SortOption.entries.forEach { option ->
                        FilterChip(
                            selected = currentSort == option,
                            onClick = { viewModel.onSortOptionChanged(option) },
                            label = { Text(option.label) },
                            modifier = Modifier.testTag("sort_chip_${option.name}")
                        )
                    }
                }

                // Läkemedelslista
                if (medicines.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Inventory2,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "Inga läkemedel matchade din sökning" else "Inga läkemedel registrerade än",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "Prova att söka på något annat namn." else "Tryck på plusknappen nere till höger för att registrera ditt första läkemedel.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(medicines, key = { it.id }) { item ->
                            MedicationListItemCard(
                                medicine = item,
                                onClick = { selectedMedicineForDetail = item },
                                onQuickAddPackage = {
                                    viewModel.updatePackageCount(item.id, item.packageCount + 1)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Detaljblad när användaren klickar på ett kort
    selectedMedicineForDetail?.let { medicine ->
        MedicationDetailSheet(
            medicine = medicine,
            onDismiss = { selectedMedicineForDetail = null },
            onEdit = { med ->
                selectedMedicineForDetail = null
                onNavigateToEdit(med)
            },
            onDelete = { med ->
                selectedMedicineForDetail = null
                viewModel.deleteMedicine(med)
            },
            onUpdateQuantity = { id, newCount ->
                viewModel.updatePackageCount(id, newCount)
                // Uppdatera även lokalt state i detaljbladet
                selectedMedicineForDetail = selectedMedicineForDetail?.copy(packageCount = newCount)
            }
        )
    }
}

@Composable
fun MedicationListItemCard(
    medicine: MedicineEntity,
    onClick: () -> Unit,
    onQuickAddPackage: () -> Unit
) {
    val expiryInfo = remember(medicine.expiryDate) {
        DateUtils.getExpiryInfo(medicine.expiryDate)
    }

    val (badgeFg, badgeBg) = when (expiryInfo.status) {
        ExpiryStatus.EXPIRED -> Pair(ExpiryExpired, ExpiryExpiredContainer)
        ExpiryStatus.WITHIN_30_DAYS -> Pair(ExpirySoon30, ExpirySoon30Container)
        ExpiryStatus.WITHIN_90_DAYS -> Pair(ExpiryNotice90, ExpiryNotice90Container)
        ExpiryStatus.VALID -> Pair(ExpiryValid, ExpiryValidContainer)
        ExpiryStatus.NONE -> Pair(MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("medication_card_${medicine.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Styrka och Form om angivet
                val subtitleParts = listOfNotNull(medicine.strength, medicine.form)
                if (subtitleParts.isNotEmpty()) {
                    Text(
                        text = subtitleParts.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Förpackningar och Utgångsdatum i rad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Förpackningsantal
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "${medicine.packageCount} st",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Utgångsdatum badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = if (medicine.expiryDate.isNullOrBlank()) "Saknar utgångsdatum" else expiryInfo.badgeLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = badgeFg,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Snabbknapp för att lägga till ytterligare förpackning
            IconButton(
                onClick = onQuickAddPackage,
                modifier = Modifier.testTag("quick_add_package_${medicine.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Lägg till ytterligare förpackning av ${medicine.name}",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
