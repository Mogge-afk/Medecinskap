package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.MedicineEntity
import com.example.ui.components.MedicationDetailSheet
import com.example.ui.theme.ExpiryExpired
import com.example.ui.theme.ExpiryNotice90
import com.example.ui.theme.ExpirySoon30
import com.example.ui.viewmodel.MedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpiringSoonScreen(
    viewModel: MedicineViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (MedicineEntity) -> Unit
) {
    BackHandler { onNavigateBack() }

    val expiredList by viewModel.expiredMedicines.collectAsStateWithLifecycle()
    val expiring30List by viewModel.expiringIn30Days.collectAsStateWithLifecycle()
    val expiring90List by viewModel.expiringIn90Days.collectAsStateWithLifecycle()

    var selectedTabIndex by remember {
        // Om det finns utgångna, visa flik 0; annars om det finns inom 30 dagar, visa flik 1; annars 2
        mutableIntStateOf(
            when {
                expiredList.isNotEmpty() -> 0
                expiring30List.isNotEmpty() -> 1
                else -> 0
            }
        )
    }

    var selectedMedicineForDetail by remember { mutableStateOf<MedicineEntity?>(null) }

    val tabs = listOf(
        Triple("Utgånget", expiredList.size, ExpiryExpired),
        Triple("Inom 30 dagar", expiring30List.size, ExpirySoon30),
        Triple("Inom 90 dagar", expiring90List.size, ExpiryNotice90)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Snart utgångna", fontWeight = FontWeight.Bold)
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
            ) {
                // Informationsbanner
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Beräknas utifrån dagens datum. Endast läkemedel med angivet utgångsdatum visas här.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Flikar för de 3 kategorierna enligt sektion 6
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    tabs.forEachIndexed { index, (label, count, indicatorColor) ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            modifier = Modifier.testTag("expiry_tab_$index"),
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = label,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (count > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = indicatorColor
                                        ) {
                                            Text(
                                                text = "$count",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                // Innehåll för vald flik
                val activeList = when (selectedTabIndex) {
                    0 -> expiredList
                    1 -> expiring30List
                    else -> expiring90List
                }

                val emptyMessage = when (selectedTabIndex) {
                    0 -> "Inga utgångna läkemedel i medicinskåpet!"
                    1 -> "Inga läkemedel som går ut inom 30 dagar."
                    else -> "Inga läkemedel som går ut inom 90 dagar."
                }

                if (activeList.isEmpty()) {
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
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Allt ser bra ut!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = emptyMessage,
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
                        items(activeList, key = { it.id }) { item ->
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

    // Detaljvisning vid klick
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
                selectedMedicineForDetail = selectedMedicineForDetail?.copy(packageCount = newCount)
            }
        )
    }
}
