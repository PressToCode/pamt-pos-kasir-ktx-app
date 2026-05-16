package com.example.pos_kasir_app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pos_kasir_app.model.User
import com.example.pos_kasir_app.viewmodel.ProfileUiState
import com.example.pos_kasir_app.viewmodel.ProfileViewModel

// Local colors (aligned to black + orange design system)
private val SoftWhite = Color(0xFFF8F9FB)
private val SubtleGray = Color(0xFF9E9E9E)
private val OrangeSubtle = Color(0xFFFFF3E0)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreenContent(
        user = User(
            userId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            namaUser = "Wedanta Christy",
            telpUser = "081234567890",
            isActive = true,
            role = "kasir",
            createdAt = "2025-01-15T10:30:00+07:00"
        ),
        editNama = "Wedanta Christy",
        editTelp = "081234567890",
        uiState = ProfileUiState.Success,
        onNamaChange = {},
        onTelpChange = {},
        onSaveClick = {},
        onBackClick = {},
        onDismissSuccess = {}
    )
}

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val editNama by viewModel.editNamaUser.collectAsStateWithLifecycle()
    val editTelp by viewModel.editTelpUser.collectAsStateWithLifecycle()

    when (uiState) {
        is ProfileUiState.Loading, is ProfileUiState.Idle -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightGrayBg),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = OrangeBrand)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Memuat profil...",
                        color = SubtleGray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        is ProfileUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightGrayBg),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = OrangeBrand,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Gagal Memuat Profil",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = (uiState as ProfileUiState.Error).message,
                            color = SubtleGray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.fetchProfile() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangeBrand
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }

        else -> {
            userProfile?.let { user ->
                ProfileScreenContent(
                    user = user,
                    editNama = editNama,
                    editTelp = editTelp,
                    uiState = uiState,
                    onNamaChange = viewModel::onNamaUserChange,
                    onTelpChange = viewModel::onTelpUserChange,
                    onSaveClick = { viewModel.updateProfile() },
                    onBackClick = onNavigateBack,
                    onDismissSuccess = { viewModel.resetToSuccess() }
                )
            }
        }
    }
}

@Composable
fun ProfileScreenContent(
    user: User,
    editNama: String,
    editTelp: String,
    uiState: ProfileUiState,
    onNamaChange: (String) -> Unit,
    onTelpChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    onDismissSuccess: () -> Unit
) {
    val isUpdating = uiState is ProfileUiState.Updating
    val showSuccess = uiState is ProfileUiState.Updated

    // Auto-dismiss success banner
    if (showSuccess) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2500)
            onDismissSuccess()
        }
    }

    // Check if editable fields have changed from the DB values
    val hasChanges = editNama != user.namaUser || editTelp != (user.telpUser ?: "")

    Scaffold(
        containerColor = LightGrayBg,
        modifier = Modifier.safeDrawingPadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // ═══════════════════════════════════════
            // HEADER - Dark Section with Avatar
            // ═══════════════════════════════════════
            ProfileHeader(
                user = user,
                onBackClick = onBackClick
            )

            // ═══════════════════════════════════════
            // SUCCESS BANNER (animated)
            // ═══════════════════════════════════════
            AnimatedVisibility(
                visible = showSuccess,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            ) {
                Surface(
                    color = OrangeBrand.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = OrangeBrand,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Profil berhasil diperbarui!",
                            color = DarkBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ═══════════════════════════════════════
            // EDIT FORM SECTION
            // ═══════════════════════════════════════
            ProfileFormCard(
                editNama = editNama,
                editTelp = editTelp,
                onNamaChange = onNamaChange,
                onTelpChange = onTelpChange,
                isUpdating = isUpdating
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ═══════════════════════════════════════
            // ACCOUNT INFO SECTION
            // ═══════════════════════════════════════
            AccountInfoCard(user = user)

            Spacer(modifier = Modifier.height(24.dp))

            // ═══════════════════════════════════════
            // SAVE BUTTON
            // ═══════════════════════════════════════
            Button(
                onClick = onSaveClick,
                enabled = !isUpdating && editNama.isNotBlank() && hasChanges,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeBrand,
                    disabledContainerColor = Color(0xFFBDBDBD)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(54.dp)
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Menyimpan...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Simpan Perubahan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ═══════════════════════════════════════════════════
// HEADER SECTION
// ═══════════════════════════════════════════════════

@Composable
fun ProfileHeader(
    user: User,
    onBackClick: () -> Unit
) {
    Surface(
        color = DarkBackground,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
    ) {
        Box {
            // Subtle gradient overlay for depth
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A1B23),
                                DarkBackground
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Back button + Title row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Profil Saya",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Invisible spacer for balance
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Avatar + Info centered
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar with gradient ring
                    Box(contentAlignment = Alignment.Center) {
                        // Outer ring
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(OrangeBrand, Color(0xFFFF9800))
                                    )
                                )
                        )
                        // Inner circle with initials
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(DarkBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.namaUser
                                    .split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercase() }
                                    .joinToString(""),
                                color = OrangeBrand,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Active indicator dot on avatar
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-4).dp, y = (-4).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(DarkBackground)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    if (user.isActive) OrangeBrand else SubtleGray
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // User name
                    Text(
                        text = user.namaUser,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Role + Status badges row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Role badge
                        Surface(
                            color = OrangeBrand,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Badge,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = user.role.replaceFirstChar { it.uppercase() },
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Status badge
                        Surface(
                            color = if (user.isActive) OrangeBrand.copy(alpha = 0.15f)
                            else Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (user.isActive) OrangeBrand else SubtleGray
                                        )
                                )
                                Text(
                                    text = if (user.isActive) "Aktif" else "Nonaktif",
                                    color = if (user.isActive) OrangeBrand else Color.LightGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// EDIT FORM CARD
// ═══════════════════════════════════════════════════

@Composable
fun ProfileFormCard(
    editNama: String,
    editTelp: String,
    onNamaChange: (String) -> Unit,
    onTelpChange: (String) -> Unit,
    isUpdating: Boolean
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Section header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = OrangeBrand.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = OrangeBrand,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Edit Profil",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBackground
                    )
                    Text(
                        text = "Ubah nama dan nomor telepon Anda",
                        fontSize = 12.sp,
                        color = SubtleGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nama Field
            Text(
                text = "Nama Lengkap",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF444444),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = editNama,
                onValueChange = onNamaChange,
                placeholder = { Text("Masukkan nama lengkap", color = Color(0xFFCCCCCC)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = OrangeBrand,
                        modifier = Modifier.size(22.dp)
                    )
                },
                enabled = !isUpdating,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangeBrand,
                    unfocusedBorderColor = Color(0xFFE8E8E8),
                    focusedContainerColor = SoftWhite,
                    unfocusedContainerColor = SoftWhite,
                    focusedLabelColor = OrangeBrand,
                    cursorColor = OrangeBrand
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Telepon Field
            Text(
                text = "Nomor Telepon",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF444444),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = editTelp,
                onValueChange = onTelpChange,
                placeholder = { Text("Masukkan nomor telepon", color = Color(0xFFCCCCCC)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        contentDescription = null,
                        tint = OrangeBrand,
                        modifier = Modifier.size(22.dp)
                    )
                },
                enabled = !isUpdating,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangeBrand,
                    unfocusedBorderColor = Color(0xFFE8E8E8),
                    focusedContainerColor = SoftWhite,
                    unfocusedContainerColor = SoftWhite,
                    focusedLabelColor = OrangeBrand,
                    cursorColor = OrangeBrand
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// ACCOUNT INFO CARD
// ═══════════════════════════════════════════════════

@Composable
fun AccountInfoCard(user: User) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Section header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = DarkBackground.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Informasi Akun",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBackground
                    )
                    Text(
                        text = "Detail akun dari database",
                        fontSize = 12.sp,
                        color = SubtleGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Info rows
            InfoItem(
                icon = Icons.Outlined.Fingerprint,
                label = "User ID",
                value = user.userId.take(8) + "..." + user.userId.takeLast(4),
                iconBgColor = OrangeSubtle,
                iconTint = OrangeBrand
            )

            InfoDivider()

            InfoItem(
                icon = Icons.Outlined.Shield,
                label = "Role",
                value = user.role.replaceFirstChar { it.uppercase() },
                iconBgColor = OrangeSubtle,
                iconTint = OrangeBrand
            )

            InfoDivider()

            InfoItem(
                icon = Icons.Outlined.ToggleOn,
                label = "Status Akun",
                value = if (user.isActive) "Aktif" else "Tidak Aktif",
                valueColor = if (user.isActive) OrangeBrand else SubtleGray,
                iconBgColor = OrangeSubtle,
                iconTint = OrangeBrand
            )

            InfoDivider()

            InfoItem(
                icon = Icons.Outlined.CalendarToday,
                label = "Bergabung Sejak",
                value = formatCreatedAt(user.createdAt),
                iconBgColor = OrangeSubtle,
                iconTint = OrangeBrand
            )
        }
    }
}

@Composable
private fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = DarkBackground,
    iconBgColor: Color = LightGrayBg,
    iconTint: Color = GrayButton
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = iconBgColor,
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = SubtleGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        }
    }
}

@Composable
private fun InfoDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
        thickness = 0.5.dp,
        color = Color(0xFFEEEEEE)
    )
}

/**
 * Format the created_at timestamp to a more readable format.
 * Input example: "2025-01-15T10:30:00+07:00"
 * Output: "15 Januari 2025"
 */
private fun formatCreatedAt(createdAt: String?): String {
    if (createdAt == null) return "-"

    return try {
        val datePart = createdAt.substring(0, 10) // "2025-01-15"
        val parts = datePart.split("-")
        if (parts.size != 3) return datePart

        val year = parts[0]
        val month = parts[1].toIntOrNull() ?: return datePart
        val day = parts[2].toIntOrNull() ?: return datePart

        val monthName = when (month) {
            1 -> "Januari"
            2 -> "Februari"
            3 -> "Maret"
            4 -> "April"
            5 -> "Mei"
            6 -> "Juni"
            7 -> "Juli"
            8 -> "Agustus"
            9 -> "September"
            10 -> "Oktober"
            11 -> "November"
            12 -> "Desember"
            else -> return datePart
        }

        "$day $monthName $year"
    } catch (e: Exception) {
        createdAt.take(10)
    }
}
