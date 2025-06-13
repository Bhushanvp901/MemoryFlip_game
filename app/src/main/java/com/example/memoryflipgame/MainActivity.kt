package com.example.memoryflipgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.memoryflipgame.presentation.Home.HomeScreen
import com.example.memoryflipgame.presentation.MemoryFlip.MemoryFlipGameScreen
import com.example.memoryflipgame.presentation.Navigation
import com.example.memoryflipgame.ui.theme.MemoryFlipGameTheme

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoryFlipGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Navigation()
                }
            }
        }
    }
}

