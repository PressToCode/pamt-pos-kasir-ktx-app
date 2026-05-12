package com.example.pos_kasir_app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pos_kasir_app.viewmodel.UserProfile

// Define Brand Colors
val DarkBackground = Color(0xFF13141A)
val OrangeBrand = Color(0xFFFF5722)
val LightGrayBg = Color(0xFFF3F4F6)
val BlueButton = Color(0xFF004D8C)
val GrayButton = Color(0xFF5B5B5B)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CashierPreview() {
    CashierScreen(
        userProfile = remember {
            UserProfile(
                fullName = "Test",
                email = "Test@Test.Test",
                phone = "1234"
            )
        },
        onLogoutClick = { }
    )
}

@Composable
fun CashierScreen(userProfile: UserProfile, onLogoutClick: () -> Unit) {
    Scaffold(
        bottomBar = { CustomBottomNavigation() },
        containerColor = LightGrayBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            TopSection(userProfile)
            Spacer(modifier = Modifier.height(16.dp))
            MyCashierSection()
            Spacer(modifier = Modifier.height(16.dp))
            TopPicksSection()
            Spacer(modifier = Modifier.height(16.dp))
            MoreIndicator()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TopSection(userProfile: UserProfile) {
    Surface(
        color = DarkBackground,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header: Logo & Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CASHIER",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row {
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = "Help",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box {
                        Icon(
                            imageVector = Icons.Outlined.MoreHoriz,
                            contentDescription = "Menu",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        // Notification dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(OrangeBrand)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Placeholder for profile picture
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Good Evening!", color = Color.LightGray, fontSize = 14.sp)
                    Text(
                        text = userProfile.fullName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueButton),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Money", color = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { /* Navigate to account or profile */ },
                    colors = ButtonDefaults.buttonColors(containerColor = GrayButton),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("My account", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MyCashierSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Cashier",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Surface(
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Edit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GridItem(icon = Icons.Outlined.Send, title = "Money\nTransfer", weight = 1f)
            GridItem(icon = Icons.Outlined.Receipt, title = "Utility\nBills", weight = 1f)
            GridItem(icon = Icons.Outlined.PhoneAndroid, title = "Mobile Load\n& Packages", weight = 1f)
            GridItem(icon = Icons.Outlined.AccountBalance, title = "Banking &\nFinance", weight = 1f)
        }
    }
}

@Composable
fun TopPicksSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Top Picks for You",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GridItem(icon = Icons.Outlined.Person, title = "Debit Card", badge = "New", weight = 1f)
            GridItem(icon = Icons.Outlined.Inventory2, title = "Mobile\nPackages", weight = 1f)
            GridItem(icon = Icons.Outlined.HealthAndSafety, title = "Insurance", weight = 1f)
            Spacer(modifier = Modifier.weight(1f)) // Empty spacer to maintain sizing
        }
    }
}

@Composable
fun GridItem(icon: ImageVector, title: String, weight: Float, badge: String? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFF2A303A)
                    )
                }
            }

            if (badge != null) {
                Surface(
                    color = OrangeBrand,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-8).dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.DarkGray,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun MoreIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE5E5E5)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "More", tint = Color.Gray)
            Text("More", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.offset(y = (-4).dp))
        }
    }
}

@Composable
fun CustomBottomNavigation() {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(icon = Icons.Filled.Home, label = "Home", isSelected = true)
            BottomNavItem(icon = Icons.Outlined.LocationOn, label = "Locator")

            // Central QR Scan Button
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scan",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Black
                    )
                }
            }

            BottomNavItem(icon = Icons.Outlined.Search, label = "Search")
            BottomNavItem(icon = Icons.Outlined.StarBorder, label = "Marked")
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean = false) {
    val color = if (isSelected) Color.Black else Color.Gray
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = color,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}