package com.example.whoos_aplication.ui.theme.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.whoos_aplication.model.Passenger
import com.example.whoos_aplication.viewmodel.BookingViewModel
import java.util.Calendar

//import androidx.navigation.NavController
//import com.example.whoos_aplication.navigation.Screen
//import com.example.whoos_aplication.viewmodel.BookingViewModel
//import com.example.whoos_aplication.utils.WhooshUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenumpangFormScreen(nav: NavController, vm: BookingViewModel) {
    val context   = LocalContext.current
    val cal       = Calendar.getInstance()

    var gender    by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var idNumber  by remember { mutableStateOf("") }
    var name      by remember { mutableStateOf("") }
    var phone     by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var error     by remember { mutableStateOf("") }

    val sectionHeader: @Composable (String) -> Unit = { title ->
        Surface(color = Color(0xFFF0F0F0), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Surface(color = Color(0xFFCC0000), modifier = Modifier.size(4.dp, 18.dp), shape = RoundedCornerShape(2.dp)) {}
                Spacer(Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Penumpang", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {

            sectionHeader("Informasi pengguna")

            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Gender
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row { Surface(color = Color(0xFFCC0000), modifier = Modifier.size(6.dp, 6.dp)) {}; Text("  Jenis kelamin", fontSize = 14.sp) }
                    Row {
                        listOf("Pria", "Wanita").forEach { g ->
                            Row {
                                RadioButton(selected = gender == g, onClick = { gender = g },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFCC0000)))
                                Text(g, modifier = Modifier.padding(top = 12.dp), fontSize = 14.sp)
                            }
                        }
                    }
                }
                HorizontalDivider()

                // Tanggal lahir
                TextButton(
                    onClick = {
                        DatePickerDialog(context, { _, y, m, d ->
                            birthDate = "%02d/%02d/%04d".format(d, m + 1, y)
                        }, cal.get(Calendar.YEAR) - 25, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tanggal lahir", color = Color.Black, fontSize = 14.sp)
                        Text(if (birthDate.isEmpty()) "Pilih tanggal lahir  ›" else "$birthDate  ›", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                HorizontalDivider()
            }

            sectionHeader("Informasi identitas")

            Surface(color = Color(0xFFFFF0F0), modifier = Modifier.fillMaxWidth()) {
                Text("Informasi yang dimasukkan haruslah konsisten dengan identitas",
                    color = Color(0xFFCC0000), modifier = Modifier.padding(12.dp), fontSize = 12.sp)
            }

            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = idNumber, onValueChange = { idNumber = it },
                    label = { Text("Nomor identitas") },
                    placeholder = { Text("Silakan masukkan kartu ID Indonesia Anda") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true
                )
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nama") },
                    placeholder = { Text("Masukkan nama sesuai kartu identitas") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true
                )
            }

            sectionHeader("Informasi Kontak")

            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it },
                    label = { Text("WhatsApp (+62)") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true
                )
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("E-mail") },
                    placeholder = { Text("Alamat E-Mail Anda") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true
                )
                if (error.isNotEmpty()) Text(error, color = Color(0xFFCC0000), fontSize = 12.sp)

                Button(
                    onClick = {
                        when {
                            name.isBlank()     -> error = "Nama tidak boleh kosong"
                            idNumber.isBlank() -> error = "Nomor identitas tidak boleh kosong"
                            else -> {
                                vm.addPassenger(Passenger(
                                    name = name.trim(), idNumber = idNumber.trim(),
                                    gender = gender, birthDate = birthDate,
                                    phone = phone.trim(), email = email.trim()
                                ))
                                nav.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000))
                ) { Text("SIMPAN", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
        }
    }
}