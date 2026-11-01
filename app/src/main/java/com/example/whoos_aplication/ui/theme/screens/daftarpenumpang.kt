package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.whoos_aplication.navigation.Screen
import com.example.whoos_aplication.viewmodel.BookingViewModel

//import androidx.navigation.NavController
//import com.example.whoos_aplication.navigation.Screen
//import com.example.whoos_aplication.viewmodel.BookingViewModel
//import com.example.whoos_aplication.utils.WhooshUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarPenumpangScreen(nav: NavController, vm: BookingViewModel) {
    val passengers = vm.passengers

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Penumpang", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    TextButton(onClick = { nav.popBackStack() }) {
                        Text("Selesai", color = Color(0xFFCC0000), fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {

            // Info maksimum
            Surface(color = Color(0xFFFFF0F0), modifier = Modifier.fillMaxWidth()) {
                Text(
                    "${passengers.size}/15 ( Maksimum 15 penumpang dapat ditambahkan)",
                    modifier = Modifier.padding(12.dp), fontSize = 13.sp, color = Color(0xFFCC0000)
                )
            }

            LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)) {

                // Tombol tambah
                item {
                    OutlinedButton(
                        onClick = { nav.navigate(Screen.PenumpangForm.route) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape  = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFCC0000))
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("⊕", color = Color(0xFFCC0000), fontSize = 18.sp)
                            Text("Tambah penumpang", color = Color(0xFFCC0000), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                items(passengers) { p ->
                    val isSelected = vm.booking.passengers.any { it.id == p.id }
                    
                    Column {
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { vm.togglePassengerSelection(p) } // Klik baris untuk pilih
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp), 
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null, // Biarkan Row yang menangani klik
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFCC0000)),
                                    modifier = Modifier.padding(8.dp)
                                )
                                Column {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(p.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp), 
                                            border = BorderStroke(0.5.dp, Color.LightGray)
                                        ) {
                                            Text(
                                                p.passengerType, 
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), 
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    Text(
                                        "ID Card   ${p.idNumber.take(4)}****${p.idNumber.takeLast(2)}",
                                        color = Color.Gray, 
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                            
                            // Tombol Edit sekarang bisa diklik (navigasi ke form)
                            IconButton(onClick = { 
                                // Navigasi ke form dengan membawa ID untuk edit (jika didukung rutenya)
                                nav.navigate(Screen.PenumpangForm.route) 
                            }) {
                                Icon(
                                    Icons.Default.Edit, 
                                    contentDescription = "Edit", 
                                    tint = Color.Gray, 
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}