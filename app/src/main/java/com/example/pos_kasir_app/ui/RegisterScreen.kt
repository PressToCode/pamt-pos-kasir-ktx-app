package com.example.pos_kasir_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pos_kasir_app.viewmodel.AuthUiState

@Composable
fun RegisterScreen(
    fullName: String,
    email: String,
    phone: String,
    password: String,
    uiState: AuthUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF3F4F6) // LightGrayBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A1B23),
                                Color(0xFF13141A) // DarkBackground
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Buat Akun",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Daftar untuk mulai menggunakan aplikasi",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                }
            }

            // Form Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-40).dp), // Overlapping effect
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Register",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Full Name Field
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = onFullNameChange,
                        label = { Text("Nama Lengkap") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Person Icon", tint = Color(0xFFFF5722)) // OrangeBrand
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF5722),
                            unfocusedBorderColor = Color(0xFFE8E8E8),
                            focusedContainerColor = Color(0xFFF8F9FB),
                            unfocusedContainerColor = Color(0xFFF8F9FB),
                            focusedLabelColor = Color(0xFFFF5722),
                            cursorColor = Color(0xFFFF5722)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon", tint = Color(0xFFFF5722))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF5722),
                            unfocusedBorderColor = Color(0xFFE8E8E8),
                            focusedContainerColor = Color(0xFFF8F9FB),
                            unfocusedContainerColor = Color(0xFFF8F9FB),
                            focusedLabelColor = Color(0xFFFF5722),
                            cursorColor = Color(0xFFFF5722)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone Field
                    OutlinedTextField(
                        value = phone,
                        onValueChange = onPhoneChange,
                        label = { Text("Nomor Telepon") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone Icon", tint = Color(0xFFFF5722))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF5722),
                            unfocusedBorderColor = Color(0xFFE8E8E8),
                            focusedContainerColor = Color(0xFFF8F9FB),
                            unfocusedContainerColor = Color(0xFFF8F9FB),
                            focusedLabelColor = Color(0xFFFF5722),
                            cursorColor = Color(0xFFFF5722)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon", tint = Color(0xFFFF5722))
                        },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = "Toggle Password Visibility", tint = Color.Gray)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF5722),
                            unfocusedBorderColor = Color(0xFFE8E8E8),
                            focusedContainerColor = Color(0xFFF8F9FB),
                            unfocusedContainerColor = Color(0xFFF8F9FB),
                            focusedLabelColor = Color(0xFFFF5722),
                            cursorColor = Color(0xFFFF5722)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Register Button
                    Button(
                        onClick = onRegisterClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = uiState !is AuthUiState.Loading && fullName.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && password.isNotBlank(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5722), // OrangeBrand
                            disabledContainerColor = Color(0xFFBDBDBD)
                        )
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Register",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                    
                    if (uiState is AuthUiState.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.message,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah punya akun?",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Login Disini",
                        color = Color(0xFFFF5722), // OrangeBrand
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}