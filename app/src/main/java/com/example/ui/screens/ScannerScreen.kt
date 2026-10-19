package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.camera.BarcodeAnalyzer
import com.example.data.local.MedicineEntity
import com.example.ui.theme.PineTealPrimary
import com.example.ui.viewmodel.MedicineViewModel
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: MedicineViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRegisterNew: (barcode: String, suggestedName: String?, suggestedStrength: String?, suggestedForm: String?, suggestedNotes: String?) -> Unit,
    onNavigateToEdit: (MedicineEntity) -> Unit
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Kamera-inställningar (blixt, kamera-instans för autofokus)
    var cameraInstance by remember { mutableStateOf<Camera?>(null) }
    var isTorchOn by remember { mutableStateOf(false) }

    // Streckkodshantering
    var manualBarcodeInput by remember { mutableStateOf("") }
    var activeScannedCode by remember { mutableStateOf<String?>(null) }
    var matchedMedicine by remember { mutableStateOf<MedicineEntity?>(null) }
    var showScanResultSheet by remember { mutableStateOf(false) }
    var packageAddedSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Inbyggd svensk katalog & onlinesökning
    var catalogResult by remember { mutableStateOf<com.example.data.catalog.MedicineCatalogItem?>(null) }
    var isSearchingOnline by remember { mutableStateOf(false) }
    var onlineLookupResult by remember { mutableStateOf<com.example.data.remote.ProductLookupResult?>(null) }

    fun triggerHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(100)
            }
        } catch (_: Exception) {
            // Ignorera om vibration inte stöds
        }
    }

    fun processBarcode(barcode: String) {
        val clean = barcode.trim()
        if (clean.isEmpty()) return
        triggerHapticFeedback()
        activeScannedCode = clean
        onlineLookupResult = null
        catalogResult = null
        viewModel.findMedicineByBarcode(clean) { found ->
            matchedMedicine = found
            if (found != null) {
                isSearchingOnline = false
                showScanResultSheet = true
            } else {
                // Steg 2: Sök i den inbyggda svenska katalogen (blixtsnabb offline-träff)
                val inCatalog = viewModel.findInCatalog(clean)
                if (inCatalog != null) {
                    catalogResult = inCatalog
                    isSearchingOnline = false
                    showScanResultSheet = true
                } else {
                    // Steg 3: Sök i öppna register på nätet som backup
                    isSearchingOnline = true
                    showScanResultSheet = true
                    viewModel.lookupBarcodeOnline(clean) { result ->
                        isSearchingOnline = false
                        onlineLookupResult = result
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skanna läkemedel", fontWeight = FontWeight.Bold) },
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
                actions = {
                    if (hasCameraPermission && cameraInstance?.cameraInfo?.hasFlashUnit() == true) {
                        IconButton(
                            onClick = {
                                isTorchOn = !isTorchOn
                                cameraInstance?.cameraControl?.enableTorch(isTorchOn)
                            }
                        ) {
                            Icon(
                                imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = if (isTorchOn) "Släck lampa" else "Tänd lampa",
                                tint = if (isTorchOn) Color(0xFFFFD600) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Kamera-vy och sökram
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    // Live Camera Preview via CameraX
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraExecutor = Executors.newSingleThreadExecutor()
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                            // Tryck för att fokusera (Tap-to-focus)
                            previewView.setOnTouchListener { view, motionEvent ->
                                if (motionEvent.action == android.view.MotionEvent.ACTION_UP) {
                                    val factory = previewView.meteringPointFactory
                                    val point = factory.createPoint(motionEvent.x, motionEvent.y)
                                    val action = FocusMeteringAction.Builder(point).build()
                                    cameraInstance?.cameraControl?.startFocusAndMetering(action)
                                    view.performClick()
                                }
                                true
                            }

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also { analysis ->
                                        analysis.setAnalyzer(
                                            cameraExecutor,
                                            BarcodeAnalyzer { detectedCode ->
                                                previewView.post {
                                                    processBarcode(detectedCode)
                                                }
                                            }
                                        )
                                    }

                                try {
                                    cameraProvider.unbindAll()
                                    val boundCamera = cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        imageAnalysis
                                    )
                                    cameraInstance = boundCamera
                                } catch (_: Exception) {
                                    // Fallback om bakkamera inte finns
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Sökram (Viewfinder) och animerad skanningslinje
                    val transition = rememberInfiniteTransition(label = "scan_line")
                    val lineOffsetY by transition.animateFloat(
                        initialValue = 0f,
                        targetValue = 180f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1400, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "laser_y"
                    )

                    Box(
                        modifier = Modifier
                            .size(280.dp, 190.dp)
                            .border(3.dp, PineTealPrimary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        // Cyan skanningslinje
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .offset(y = lineOffsetY.dp)
                                .background(Color(0xFF00E5FF))
                        )
                    }

                    // Tydlig instruktionstext under sökramen
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp)
                    ) {
                        Text(
                            text = "Rikta kameran mot streckkoden på förpackningen",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                } else {
                    // Om kameratillstånd saknas
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.DarkGray,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Kameratillstånd behövs",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Appen behöver tillgång till kameran för att skanna streckkoder. Du kan även mata in koder manuellt nedan.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            modifier = Modifier.testTag("request_camera_permission_button")
                        ) {
                            Text("Ge tillstånd för kamera")
                        }
                    }
                }
            }

            // Manuell inmatning och testsektion
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Mata in streckkod manuellt",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = manualBarcodeInput,
                            onValueChange = { manualBarcodeInput = it.filter { c -> c.isDigit() || c.isLetter() } },
                            placeholder = { Text("T.ex. 7300009012345") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    processBarcode(manualBarcodeInput)
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("manual_barcode_input")
                        )

                        Button(
                            onClick = { processBarcode(manualBarcodeInput) },
                            enabled = manualBarcodeInput.isNotBlank(),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("process_manual_barcode_button")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Sök streckkod")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Snabbknappar för att testa vanliga läkemedels-EAN
                    Text(
                        text = "Testkoder (klicka för att prova direkt):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                manualBarcodeInput = "7300009012345"
                                processBarcode("7300009012345")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_barcode_1")
                        ) {
                            Text("Alvedon", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                manualBarcodeInput = "7350035021008"
                                processBarcode("7350035021008")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_barcode_2")
                        ) {
                            Text("Ipren", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                manualBarcodeInput = "7391515001142"
                                processBarcode("7391515001142")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_barcode_3")
                        ) {
                            Text("Treo", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                manualBarcodeInput = "7391515011011"
                                processBarcode("7391515011011")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_barcode_4")
                        ) {
                            Text("Nezeril", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet när en streckkod har lästs av
    if (showScanResultSheet && activeScannedCode != null) {
        val currentCode = activeScannedCode ?: ""
        val existingMedicine = matchedMedicine

        ModalBottomSheet(
            onDismissRequest = {
                showScanResultSheet = false
                activeScannedCode = null
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val headerIcon = when {
                    existingMedicine != null -> Icons.Default.CheckCircle
                    catalogResult != null -> Icons.Default.CheckCircle
                    else -> Icons.Default.QrCode
                }
                val headerBg = when {
                    existingMedicine != null -> PineTealPrimary.copy(alpha = 0.15f)
                    catalogResult != null -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                val headerTint = when {
                    existingMedicine != null -> PineTealPrimary
                    catalogResult != null -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                val headerTitle = when {
                    existingMedicine != null -> "Läkemedel finns i skåpet!"
                    catalogResult != null -> "Hittades i läkemedelskatalogen!"
                    else -> "Ny streckkod avläst"
                }

                Surface(
                    shape = CircleShape,
                    color = headerBg,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = headerIcon,
                            contentDescription = null,
                            tint = headerTint,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = headerTitle,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Kod: $currentCode",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (existingMedicine != null) {
                    // Koden finns redan registrerad
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = existingMedicine.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            val details = listOfNotNull(existingMedicine.strength, existingMedicine.form).joinToString(" • ")
                            if (details.isNotEmpty()) {
                                Text(
                                    text = details,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nuvarande antal i skåpet: ${existingMedicine.packageCount} förpackningar",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Erbjud användaren att lägga till fler förpackningar (+1)
                    Button(
                        onClick = {
                            val newCount = existingMedicine.packageCount + 1
                            viewModel.updatePackageCount(existingMedicine.id, newCount)
                            packageAddedSuccessMessage = "Lade till 1 förpackning av ${existingMedicine.name}. Totalt nu: $newCount st."
                            showScanResultSheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("add_package_from_scanner_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lägg till ytterligare förpackning (+1)")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            showScanResultSheet = false
                            onNavigateToEdit(existingMedicine)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("open_medicine_from_scanner_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Öppna och redigera läkemedlet")
                    }
                } else if (catalogResult != null) {
                    val catItem = catalogResult!!
                    // Träff i den inbyggda svenska läkemedelskatalogen!
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = PineTealPrimary,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "SVENSK LÄKEMEDELSKATALOG",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = catItem.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            val details = listOfNotNull(catItem.strength, catItem.form, catItem.packageSize).joinToString(" • ")
                            if (details.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = details,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            if (!catItem.activeSubstance.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Aktiv substans: ${catItem.activeSubstance}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Knapp 1: Lägg till i mitt skåp (förifyllt)
                    Button(
                        onClick = {
                            showScanResultSheet = false
                            val autoNotes = listOfNotNull(
                                catItem.packageSize?.let { "Förpackningsstorlek: $it" },
                                catItem.activeSubstance?.let { "Aktiv substans: $it" }
                            ).joinToString(". ")
                            onNavigateToRegisterNew(
                                currentCode,
                                catItem.name,
                                catItem.strength,
                                catItem.form,
                                if (autoNotes.isNotEmpty()) autoNotes else null
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("add_catalog_medicine_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lägg till i mitt skåp", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Knapp 2: Skriv in manuellt istället
                    OutlinedButton(
                        onClick = {
                            showScanResultSheet = false
                            onNavigateToRegisterNew(currentCode, null, null, null, null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("manual_entry_override_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Skriv in manuellt istället")
                    }
                } else {
                    // Koden finns inte i skåpet eller den inbyggda katalogen
                    if (isSearchingOnline) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Söker produktnamn på nätet...",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                showScanResultSheet = false
                                onNavigateToRegisterNew(currentCode, null, null, null, null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Hoppa över och skriv in manuellt")
                        }
                    } else if (onlineLookupResult != null) {
                        val suggestion = onlineLookupResult!!
                        // Förslag från nätet hittat!
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                            ) {
                                Text(
                                    text = "FÖRSLAG FRÅN ÖPPNA REGISTER",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = suggestion.productName,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                val details = listOfNotNull(suggestion.brand, suggestion.strengthOrQuantity).joinToString(" • ")
                                if (details.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = details,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Knapp 1: Använd förslaget
                        Button(
                            onClick = {
                                showScanResultSheet = false
                                onNavigateToRegisterNew(
                                    currentCode,
                                    suggestion.productName,
                                    suggestion.strengthOrQuantity,
                                    null,
                                    null
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("use_online_suggestion_button")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Använd detta förslag", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Knapp 2: Skriv eget namn
                        OutlinedButton(
                            onClick = {
                                showScanResultSheet = false
                                onNavigateToRegisterNew(currentCode, null, null, null, null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("write_manual_name_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Skriv in eget namn istället")
                        }
                    } else {
                        // Ingen träff på nätet
                        Text(
                            text = "Denna streckkod finns inte i ditt medicinskåp och ingen träff hittades i katalogen eller på nätet. Registrera produkten manuellt.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                showScanResultSheet = false
                                onNavigateToRegisterNew(currentCode, null, null, null, null)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("register_new_from_scanner_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Registrera manuellt med denna kod")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = {
                        showScanResultSheet = false
                        activeScannedCode = null
                    }
                ) {
                    Text("Avbryt / Skanna annan")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Feedback dialog vid lyckad tillägg av förpackning
    packageAddedSuccessMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { packageAddedSuccessMessage = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PineTealPrimary
                )
            },
            title = { Text("Förpackning tillagd") },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = { packageAddedSuccessMessage = null }) {
                    Text("Klar")
                }
            }
        )
    }
}
