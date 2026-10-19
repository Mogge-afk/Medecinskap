package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.theme.ExpiryExpired
import com.example.ui.theme.ExpiryExpiredContainer
import com.example.ui.theme.ExpirySoon30
import com.example.ui.theme.ExpirySoon30Container
import com.example.ui.theme.PineTealPrimary
import com.example.ui.viewmodel.MedicineViewModel

@Composable
fun HomeScreen(
    viewModel: MedicineViewModel,
    onNavigateToScanner: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToExpiring: () -> Unit
) {
    val medicineCount by viewModel.medicineCount.collectAsStateWithLifecycle()
    val allMedicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val expiredList by viewModel.expiredMedicines.collectAsStateWithLifecycle()
    val expiring30List by viewModel.expiringIn30Days.collectAsStateWithLifecycle()
    val scheduleEntries by viewModel.allScheduleEntries.collectAsStateWithLifecycle()

    val totalPackages = allMedicines.sumOf { it.packageCount }
    val expiredCount = expiredList.size
    val soonCount = expiring30List.size

    var showInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PineTealPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Säkerhet och lokal lagring",
                                    tint = PineTealPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "100 % lokal lagring",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "Version 1.6",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { showInfoDialog = true }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("footer_about_button")
                        ) {
                            Text(
                                text = "Om appen",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
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
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Rejält skyddat toppavstånd mot telefonens statusfält, klocka och kamerahål
                Spacer(modifier = Modifier.height(10.dp))

            // Header med appnamn och Info-knapp
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = "Medicinskåpet ikon",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Medicinskåpet",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            text = "Privat inventering i hemmet",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Info-knapp ("Readme / Om appen")
                IconButton(
                    onClick = { showInfoDialog = true },
                    modifier = Modifier.testTag("home_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Om appen och information",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Statuskort med antal läkemedel
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$medicineCount",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (medicineCount == 1) "registrerat läkemedel" else "registrerade läkemedel",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        if (medicineCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = "$totalPackages förpackningar",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Varningsbanner om utgångna finns
                    if (expiredCount > 0 || soonCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (expiredCount > 0) ExpiryExpiredContainer else ExpirySoon30Container
                                )
                                .clickable { onNavigateToExpiring() }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (expiredCount > 0) ExpiryExpired else ExpirySoon30,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val alertMsg = when {
                                expiredCount > 0 && soonCount > 0 ->
                                    "$expiredCount utgånget, $soonCount går ut inom 30 dagar"
                                expiredCount > 0 ->
                                    "$expiredCount läkemedel har gått ut"
                                else ->
                                    "$soonCount läkemedel går ut inom 30 dagar"
                            }
                            Text(
                                text = alertMsg,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (expiredCount > 0) ExpiryExpired else ExpirySoon30
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Grupp 1: De 3 huvudsakliga inventeringsknapparna (tydligt större text och ikoner)
            HomeActionButton(
                title = "Skanna läkemedel",
                description = "Läs av streckkod (EAN-13) med mobilkameran",
                icon = Icons.Default.QrCodeScanner,
                iconBackgroundColor = PineTealPrimary,
                testTag = "scan_medicine_button",
                onClick = onNavigateToScanner
            )

            Spacer(modifier = Modifier.height(12.dp))

            HomeActionButton(
                title = "Mina läkemedel",
                description = "Överblicka, sök, redigera och sortera i skåpet ($medicineCount st)",
                icon = Icons.Default.Inventory2,
                iconBackgroundColor = MaterialTheme.colorScheme.secondary,
                testTag = "my_medicines_button",
                onClick = onNavigateToList
            )

            Spacer(modifier = Modifier.height(12.dp))

            HomeActionButton(
                title = "Doseringsschema",
                description = "Daglig översikt för morgon, middag, kväll och natt med doser",
                icon = Icons.Default.Schedule,
                iconBackgroundColor = Color(0xFF3F51B5),
                badgeCount = if (scheduleEntries.isNotEmpty()) scheduleEntries.size else null,
                testTag = "schedule_button",
                onClick = onNavigateToSchedule
            )

            Spacer(modifier = Modifier.height(12.dp))

            HomeActionButton(
                title = "Lägg till manuellt",
                description = "Registrera produkt utan att behöva skanna",
                icon = Icons.Default.Add,
                iconBackgroundColor = MaterialTheme.colorScheme.tertiary,
                testTag = "add_manual_button",
                onClick = onNavigateToAdd
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Subtil avgränsare för att placera "Snart utgångna" lite avskilt
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                Text(
                    text = "DATUM & VARNINGAR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grupp 2: Avskild knapp för Snart utgångna
            HomeActionButton(
                title = "Snart utgångna",
                description = "Håll koll på bäst-före-datum (inom 30/90 dagar eller utgånget)",
                icon = Icons.Default.NotificationsActive,
                iconBackgroundColor = if (expiredCount > 0) ExpiryExpired else ExpirySoon30,
                badgeCount = if (expiredCount + soonCount > 0) (expiredCount + soonCount) else null,
                testTag = "expiring_soon_button",
                onClick = onNavigateToExpiring
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

    // Info-dialog / "Readme" som samlar all text och information
    if (showInfoDialog) {
        AboutInfoDialog(onDismiss = { showInfoDialog = false })
    }
}

@Composable
private fun HomeActionButton(
    title: String,
    description: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    testTag: String,
    badgeCount: Int? = null,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = iconBackgroundColor,
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            if (badgeCount != null) {
                Surface(
                    shape = CircleShape,
                    color = ExpiryExpired,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "$badgeCount",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Om Medicinskåpet",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                InfoSection(
                    title = "Syfte",
                    body = "Medicinskåpet är en enkel, privat app för att hålla ordning på vilka läkemedel och förpackningar som finns hemma samt uppmärksamma när utgångsdatum närmar sig."
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoSection(
                    title = "Integritet & Lokal lagring",
                    body = "All information du registrerar sparas uteslutande lokalt i telefonens minne (SQLite/Room). Inget konto krävs, ingen molnsynkning sker och ingen analys- eller annonsspårning förekommer."
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoSection(
                    title = "Streckkoder & Svensk Läkemedelskatalog",
                    body = "Kameran används för att läsa EAN-koder på läkemedelsförpackningar. Appen har en inbyggd svensk läkemedelskatalog (offline) som direkt känner igen Sveriges vanligaste läkemedel och förutfyller namn, styrka, form och förpackning – precis som i Apotekets app! Vid övriga streckkoder kan appen söka efter produktnamn i öppna register som backup."
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoSection(
                    title = "Doseringsschema",
                    body = "Organisera dina dagliga läkemedelsintag fördelade över morgon, middag, kväll och natt så du har full översikt över dosering och tidpunkter."
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoSection(
                    title = "Ansvarsfriskrivning",
                    body = "Appen är ett privat inventeringsverktyg och ersätter inte läkares eller apotekets ordination eller rådgivning. Du ansvarar själv för att sparade uppgifter stämmer med din ordination."
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Version 1.6",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Stäng")
            }
        }
    )
}

@Composable
private fun InfoSection(title: String, body: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
