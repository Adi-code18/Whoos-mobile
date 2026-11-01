package com.example.whoos_aplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.whoos_aplication.ui.theme.WhooshAppTheme
import com.example.whoos_aplication.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhooshAppTheme {
                AppNavGraph()
            }
        }
    }
}
