package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.whoos_aplication.R
import com.example.whoos_aplication.navigation.Screen
import com.example.whoos_aplication.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(nav: NavController, vm: BookingViewModel) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Akun", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.whooslogo),
                contentDescription = null,
                modifier = Modifier.width(180.dp).height(45.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(8.dp))
            Text("Buat Akun Baru", color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = fullName, onValueChange = { fullName = it; error = "" },
                label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            
            OutlinedTextField(
                value = email, onValueChange = { email = it; error = "" },
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = phone, onValueChange = { phone = it; error = "" },
                label = { Text("Nomor Telepon") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it; error = "" },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it; error = "" },
                label = { Text("Konfirmasi Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
            )

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(error, color = Color(0xFFCC0000), fontSize = 12.sp)
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    when {
                        fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() -> error = "Semua field harus diisi"
                        password != confirmPassword -> error = "Password tidak cocok"
                        else -> {
                            loading = true
                            vm.registerUser(
                                fullName = fullName.trim(),
                                email = email.trim(),
                                phone = phone.trim(),
                                pass = password,
                                onSuccess = {
                                    loading = false
                                    nav.navigate(Screen.Dashboard.route) {
                                        popUpTo(Screen.Register.route) { inclusive = true }
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onError = { 
                                    loading = false
                                    error = it 
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !loading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000))
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("DAFTAR SEKARANG", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
