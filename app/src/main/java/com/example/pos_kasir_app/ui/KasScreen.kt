package com.example.pos_kasir_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pos_kasir_app.model.Kas
import com.example.pos_kasir_app.viewmodel.KasUiState
import com.example.pos_kasir_app.viewmodel.KasViewModel
import java.text.NumberFormat
import java.util.Locale

// Local colors (aligned to black + orange design system)
private val SubtleGray = Color(0xFF9E9E9E)
private val SoftWhite = Color(0xFFF8F9FB)
private val MutedDark = Color(0xFF3A3A3A)
private val OrangeLight = Color(0xFFFF8A65)

enum class KasFilter { ACTIVE, INACTIVE, ALL }

@Composable
fun KasScreen(
    onNavigateBack: () -> Unit,
    viewModel: KasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedKasToEdit by remember { mutableStateOf<Kas?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Kas?>(null) }
    var currentFilter by remember { mutableStateOf(KasFilter.ACTIVE) }

    Scaffold(
        containerColor = LightGrayBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedKasToEdit = null
                    showAddEditDialog = true
                },
                containerColor = OrangeBrand,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Kas")
            }
        },
        modifier = Modifier.safeDrawingPadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // ═══ DARK HEADER ═══
            KasHeader(onBackClick = onNavigateBack, kasList = (uiState as? KasUiState.Success)?.kasList)

            // ═══ FILTER SECTION ═══
            if (uiState is KasUiState.Success) {
                FilterSection(
                    selectedFilter = currentFilter,
                    onFilterSelected = { currentFilter = it }
                )
            }

            // ═══ CONTENT ═══
            when (uiState) {
                is KasUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = OrangeBrand)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Memuat data kas...", color = SubtleGray, fontSize = 14.sp)
                        }
                    }
                }

                is KasUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Surface(
                            color = Color.White, shape = RoundedCornerShape(20.dp),
                            shadowElevation = 2.dp, modifier = Modifier.padding(32.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(Icons.Outlined.ErrorOutline, null, tint = OrangeBrand, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Gagal Memuat Data", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text((uiState as KasUiState.Error).message, color = SubtleGray, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { viewModel.fetchKas() },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangeBrand),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Outlined.Refresh, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Coba Lagi")
                                }
                            }
                        }
                    }
                }

                is KasUiState.Success -> {
                    val fullList = (uiState as KasUiState.Success).kasList
                    val filteredList = when (currentFilter) {
                        KasFilter.ACTIVE -> fullList.filter { it.isActive }
                        KasFilter.INACTIVE -> fullList.filter { !it.isActive }
                        KasFilter.ALL -> fullList
                    }

                    if (filteredList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.Inbox, null, tint = SubtleGray, modifier = Modifier.size(56.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                val emptyMessage = when(currentFilter) {
                                    KasFilter.ACTIVE -> "Belum Ada Kas Aktif"
                                    KasFilter.INACTIVE -> "Belum Ada Kas Nonaktif"
                                    KasFilter.ALL -> "Belum Ada Data Kas"
                                }
                                Text(emptyMessage, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Tekan + untuk menambah kas baru", color = SubtleGray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredList) { kas ->
                                KasCard(
                                    kas = kas,
                                    onEditClick = { selectedKasToEdit = kas; showAddEditDialog = true },
                                    onDeleteClick = { showDeleteConfirmDialog = kas }
                                )
                            }
                            // Bottom spacer for FAB
                            item { Spacer(modifier = Modifier.height(72.dp)) }
                        }
                    }
                }
            }
        }

        // ═══ ADD/EDIT DIALOG ═══
        if (showAddEditDialog) {
            KasFormDialog(
                kas = selectedKasToEdit,
                onDismiss = { showAddEditDialog = false },
                onSave = { namaKas, saldoKas, keteranganKas, isActive ->
                    if (selectedKasToEdit == null) {
                        viewModel.addKas(namaKas, saldoKas, keteranganKas, isActive)
                    } else {
                        selectedKasToEdit?.kasId?.let { id ->
                            viewModel.updateKas(id, namaKas, saldoKas, keteranganKas, isActive)
                        }
                    }
                    showAddEditDialog = false
                }
            )
        }

        // ═══ DELETE CONFIRM DIALOG ═══
        showDeleteConfirmDialog?.let { kasToDelete ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = null },
                shape = RoundedCornerShape(20.dp),
                icon = {
                    Surface(color = OrangeBrand.copy(alpha = 0.1f), shape = CircleShape) {
                        Icon(Icons.Outlined.DeleteForever, null, tint = OrangeBrand,
                            modifier = Modifier.padding(12.dp).size(28.dp))
                    }
                },
                title = {
                    Text("Hapus Kas?", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                text = {
                    Text("Kas \"${kasToDelete.namaKas}\" akan dihapus secara permanen.",
                        color = SubtleGray, fontSize = 14.sp)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            kasToDelete.kasId?.let { viewModel.deleteKas(it) }
                            showDeleteConfirmDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Hapus") }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDeleteConfirmDialog = null },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Batal") }
                }
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// HEADER
// ═══════════════════════════════════════════════════

@Composable
private fun KasHeader(onBackClick: () -> Unit, kasList: List<Kas>?) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val totalSaldo = kasList?.filter { it.isActive }?.sumOf { it.saldoKas } ?: 0.0
    val totalKas = kasList?.size ?: 0
    val activeKas = kasList?.count { it.isActive } ?: 0

    Surface(
        color = DarkBackground,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
    ) {
        Box {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp)
                    .background(Brush.verticalGradient(listOf(Color(0xFF1A1B23), DarkBackground)))
            )
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                // Back + Title
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Kelola Kas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Total saldo card
                Surface(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Saldo Aktif", color = Color.LightGray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatter.format(totalSaldo),
                            color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            KasStatChip(label = "Total", value = "$totalKas", color = Color.White)
                            KasStatChip(label = "Aktif", value = "$activeKas", color = OrangeBrand)
                            KasStatChip(label = "Nonaktif", value = "${totalKas - activeKas}", color = OrangeLight)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KasStatChip(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text("$value $label", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

// ═══════════════════════════════════════════════════
// KAS ITEM CARD
// ═══════════════════════════════════════════════════

@Composable
private fun KasCard(kas: Kas, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Icon box
                Surface(
                    color = if (kas.isActive) OrangeBrand.copy(alpha = 0.12f) else DarkBackground.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = if (kas.isActive) OrangeBrand else MutedDark,
                        modifier = Modifier.padding(10.dp).size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Name + status
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = kas.namaKas,
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                        color = if (kas.isActive) DarkBackground else SubtleGray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape)
                            .background(if (kas.isActive) OrangeBrand else SubtleGray))
                        Text(
                            text = if (kas.isActive) "Aktif" else "Nonaktif",
                            fontSize = 12.sp, color = if (kas.isActive) OrangeBrand else SubtleGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Action buttons
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Edit, "Edit", tint = OrangeBrand, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Delete, "Hapus", tint = MutedDark, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(12.dp))

            // Saldo row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Saldo", fontSize = 13.sp, color = SubtleGray)
                Text(
                    text = formatter.format(kas.saldoKas),
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OrangeBrand
                )
            }

            // Keterangan
            kas.keteranganKas?.let {
                if (it.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(color = SoftWhite, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.StickyNote2, null, tint = SubtleGray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = it, fontSize = 13.sp, color = Color(0xFF666666))
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// ADD/EDIT DIALOG (styled)
// ═══════════════════════════════════════════════════

@Composable
private fun KasFormDialog(
    kas: Kas?,
    onDismiss: () -> Unit,
    onSave: (namaKas: String, saldoKas: Double, keteranganKas: String, isActive: Boolean) -> Unit
) {
    var namaKas by remember { mutableStateOf(kas?.namaKas ?: "") }
    var saldoKasStr by remember { mutableStateOf(kas?.saldoKas?.toString() ?: "") }
    var keteranganKas by remember { mutableStateOf(kas?.keteranganKas ?: "") }
    var isActive by remember { mutableStateOf(kas?.isActive ?: true) }
    val isEdit = kas != null

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Surface(
                color = OrangeBrand.copy(alpha = 0.1f), shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isEdit) Icons.Outlined.Edit else Icons.Outlined.AddCircleOutline,
                    contentDescription = null, tint = OrangeBrand,
                    modifier = Modifier.padding(12.dp).size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = if (isEdit) "Edit Kas" else "Tambah Kas Baru",
                fontWeight = FontWeight.Bold, fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = namaKas, onValueChange = { namaKas = it },
                    label = { Text("Nama Kas") },
                    leadingIcon = { Icon(Icons.Outlined.Label, null, tint = OrangeBrand, modifier = Modifier.size(20.dp)) },
                    singleLine = true, shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeBrand, unfocusedBorderColor = Color(0xFFE8E8E8),
                        focusedContainerColor = SoftWhite, unfocusedContainerColor = SoftWhite,
                        focusedLabelColor = OrangeBrand, cursorColor = OrangeBrand
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = saldoKasStr, onValueChange = { saldoKasStr = it },
                    label = { Text("Saldo Awal (Rp)") },
                    leadingIcon = { Icon(Icons.Outlined.AttachMoney, null, tint = OrangeBrand, modifier = Modifier.size(20.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true, shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeBrand, unfocusedBorderColor = Color(0xFFE8E8E8),
                        focusedContainerColor = SoftWhite, unfocusedContainerColor = SoftWhite,
                        focusedLabelColor = OrangeBrand, cursorColor = OrangeBrand
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = keteranganKas, onValueChange = { keteranganKas = it },
                    label = { Text("Keterangan (opsional)") },
                    leadingIcon = { Icon(Icons.Outlined.StickyNote2, null, tint = OrangeBrand, modifier = Modifier.size(20.dp)) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeBrand, unfocusedBorderColor = Color(0xFFE8E8E8),
                        focusedContainerColor = SoftWhite, unfocusedContainerColor = SoftWhite,
                        focusedLabelColor = OrangeBrand, cursorColor = OrangeBrand
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Active toggle
                Surface(color = SoftWhite, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.ToggleOn, null, tint = if (isActive) OrangeBrand else SubtleGray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Status Aktif", modifier = Modifier.weight(1f), fontSize = 14.sp, color = Color(0xFF444444))
                        Switch(
                            checked = isActive, onCheckedChange = { isActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White, checkedTrackColor = OrangeBrand,
                                uncheckedThumbColor = Color.White, uncheckedTrackColor = Color(0xFFE0E0E0)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val saldo = saldoKasStr.toDoubleOrNull() ?: 0.0
                    if (namaKas.isNotBlank()) onSave(namaKas, saldo, keteranganKas, isActive)
                },
                enabled = namaKas.isNotBlank() && saldoKasStr.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeBrand),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Simpan", fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Batal")
            }
        }
    )
}

// ═══════════════════════════════════════════════════
// FILTER SECTION
// ═══════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    selectedFilter: KasFilter,
    onFilterSelected: (KasFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(KasFilter.ACTIVE, KasFilter.INACTIVE, KasFilter.ALL).forEach { filter ->
            val isSelected = selectedFilter == filter
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        when (filter) {
                            KasFilter.ACTIVE -> "Aktif"
                            KasFilter.INACTIVE -> "Nonaktif"
                            KasFilter.ALL -> "Semua"
                        }
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = OrangeBrand,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = MutedDark
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color(0xFFE8E8E8),
                    selectedBorderColor = OrangeBrand,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.dp
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
