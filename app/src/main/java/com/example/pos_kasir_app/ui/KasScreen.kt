package com.example.pos_kasir_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pos_kasir_app.model.Kas
import com.example.pos_kasir_app.viewmodel.KasUiState
import com.example.pos_kasir_app.viewmodel.KasViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasScreen(
    onNavigateBack: () -> Unit,
    viewModel: KasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedKasToEdit by remember { mutableStateOf<Kas?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Kas?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Kas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedKasToEdit = null
                    showAddEditDialog = true
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Kas")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is KasUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is KasUiState.Error -> {
                    val message = (uiState as KasUiState.Error).message
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error: $message", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchKas() }) {
                            Text("Retry")
                        }
                    }
                }
                is KasUiState.Success -> {
                    val kasList = (uiState as KasUiState.Success).kasList
                    if (kasList.isEmpty()) {
                        Text(
                            text = "Belum ada data kas.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(kasList) { kas ->
                                KasItemCard(
                                    kas = kas,
                                    onEditClick = {
                                        selectedKasToEdit = kas
                                        showAddEditDialog = true
                                    },
                                    onDeleteClick = {
                                        showDeleteConfirmDialog = kas
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showAddEditDialog) {
            KasAddEditDialog(
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

        showDeleteConfirmDialog?.let { kasToDelete ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = null },
                title = { Text("Hapus Kas") },
                text = { Text("Apakah Anda yakin ingin menghapus Kas '${kasToDelete.namaKas}'?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            kasToDelete.kasId?.let { viewModel.deleteKas(it) }
                            showDeleteConfirmDialog = null
                        }
                    ) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun KasItemCard(
    kas: Kas,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = kas.namaKas,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (kas.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatter.format(kas.saldoKas),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                kas.keteranganKas?.let {
                    if (it.isNotBlank()) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (!kas.isActive) {
                    Text(
                        text = "Tidak Aktif",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Kas", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Filled.Delete, contentDescription = "Hapus Kas", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun KasAddEditDialog(
    kas: Kas?,
    onDismiss: () -> Unit,
    onSave: (namaKas: String, saldoKas: Double, keteranganKas: String, isActive: Boolean) -> Unit
) {
    var namaKas by remember { mutableStateOf(kas?.namaKas ?: "") }
    var saldoKasStr by remember { mutableStateOf(kas?.saldoKas?.toString() ?: "") }
    var keteranganKas by remember { mutableStateOf(kas?.keteranganKas ?: "") }
    var isActive by remember { mutableStateOf(kas?.isActive ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (kas == null) "Tambah Kas Baru" else "Edit Kas") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = namaKas,
                    onValueChange = { namaKas = it },
                    label = { Text("Nama Kas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = saldoKasStr,
                    onValueChange = { saldoKasStr = it },
                    label = { Text("Saldo Awal (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = keteranganKas,
                    onValueChange = { keteranganKas = it },
                    label = { Text("Keterangan") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                    Text("Aktif")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val saldo = saldoKasStr.toDoubleOrNull() ?: 0.0
                    if (namaKas.isNotBlank()) {
                        onSave(namaKas, saldo, keteranganKas, isActive)
                    }
                },
                enabled = namaKas.isNotBlank() && saldoKasStr.isNotBlank()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
