package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.whoos_aplication.navigation.Screen
import com.example.whoos_aplication.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(nav: NavController, vm: BookingViewModel) {
    val user = vm.booking
    val red = Color(0xFFCC0000)
    val context = LocalContext.current

    Scaffold(
        containerColor = Color(0xFFF1F4F9),
        bottomBar = { WhooshBottomBar(nav = nav, currentRoute = Screen.Account.route) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── HEADER / PROFILE ────────────────────────────────────────
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Image Circle
                    Box(
                        Modifier
                            .size(70.dp)
                            .background(Color(0xFFF1F4F9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountCircle, 
                            null, 
                            tint = if (user.username.isNotEmpty()) red else Color.LightGray, 
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    Spacer(Modifier.width(20.dp))
                    Column {
                        if (user.username.isNotEmpty()) {
                            Text(
                                text = user.username,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = user.userEmail,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        } else {
                            Text(
                                text = "Belum Login",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Row {
                                Text(
                                    "Masuk / Daftar",
                                    fontSize = 14.sp,
                                    color = red,
                                    modifier = Modifier.clickable { nav.navigate(Screen.Login.route) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── COMMON FUNCTIONS ────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(vertical = 12.dp)) {
                    Text(
                        "Common Functions",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    HorizontalDivider(color = Color(0xFFF1F4F9))
                    
                    // Grid of icons
                    val gridItems = listOf(
                        GridItemData("Passenger", Icons.Default.Badge) {
                            nav.navigate(Screen.DaftarPenumpang.route)
                        },
                        GridItemData("Language", Icons.Default.Translate) {
                            Toast.makeText(context, "English Language Selected", Toast.LENGTH_SHORT).show()
                        },
                        GridItemData("Password", Icons.Default.Lock) {
                            Toast.makeText(context, "Change Password feature coming soon", Toast.LENGTH_SHORT).show()
                        },
                    )

                    // Menggunakan FlowRow atau Chunked Column karena LazyVerticalGrid di dalam Scrollable Column bermasalah
                    gridItems.chunked(3).forEach { rowItems ->
                        Row(Modifier.fillMaxWidth()) {
                            rowItems.forEach { item ->
                                GridIconItem(item, red, Modifier.weight(1f))
                            }
                            // Filler if row is not full
                            if (rowItems.size < 3) {
                                repeat(3 - rowItems.size) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── SERVICE INFORMATION ─────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(vertical = 12.dp)) {
                    Text(
                        "Service Information",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    HorizontalDivider(color = Color(0xFFF1F4F9), modifier = Modifier.padding(horizontal = 16.dp))

                    ServiceItem("Notice", Icons.Default.VolumeUp)
                    ServiceItem("Railway regulations", Icons.Default.LibraryBooks)
                    ServiceItem("About", Icons.Default.Info, "Version V1.2.002")
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── LOG OUT ─────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp),
                onClick = {
                    vm.logout {
                        nav.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Log Out", color = red, fontWeight = FontWeight.Normal, fontSize = 18.sp)
                }
            }
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

data class GridItemData(val label: String, val icon: ImageVector, val onClick: () -> Unit = {})

@Composable
fun GridIconItem(item: GridItemData, red: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .clickable { item.onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(item.icon, null, tint = red, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(8.dp))
        Text(
            item.label,
            fontSize = 12.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun ServiceItem(title: String, icon: ImageVector, trailingText: String? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.Black, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), fontSize = 14.sp, color = Color.Black)
        if (trailingText != null) {
            Text(trailingText, color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp))
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
    }
}
