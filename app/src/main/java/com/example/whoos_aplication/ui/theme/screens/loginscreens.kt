package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

//import androidx.navigation.NavController
//import com.example.whoos_aplication.navigation.Screen
//import com.example.whoos_aplication.viewmodel.BookingViewModel
//import com.example.whoos_aplication.utils.WhooshUtils

@Composable
fun LoginScreen(nav: NavController, vm: BookingViewModel) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error    by remember { mutableStateOf("") }
    var loading  by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.whooslogo),
            contentDescription = null,
            modifier = Modifier.width(200.dp).height(50.dp),
            contentScale = ContentScale.Fit
        )
        Text("Selamat Datang Di Aplikasi Booking Tiket Whoos", color = Color.Gray, fontSize = 13.sp)
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it; error = "" },
            label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it; error = "" },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
        )
        if (error.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(error, color = Color(0xFFCC0000), fontSize = 12.sp)
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                when {
                    email.isBlank() -> error = "Email tidak boleh kosong"
                    password.isBlank() -> error = "Password tidak boleh kosong"
                    else -> {
                        loading = true
                        vm.login(
                            email.trim(),
                            password,
                            onSuccess = {
                                loading = false
                                nav.navigate(Screen.Dashboard.route) {
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
                Text("LOGIN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Belum punya akun? ", fontSize = 14.sp, color = Color.Gray)
            Text(
                "Daftar", 
                fontSize = 14.sp, 
                color = Color(0xFFCC0000), 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { nav.navigate(Screen.Register.route) }
            )
        }
    }
}