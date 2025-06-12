package com.example.memoryflipgame.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.memoryflipgame.presentation.Home.HomeScreen
import com.example.memoryflipgame.presentation.MemoryFlip.MemoryFlipGameScreen

@Composable
fun Navigation(){

    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = remember { MemoryFlipGameViewModel(context) }


    NavHost(navController = navController, startDestination = "home"){

        composable("home") {
            HomeScreen(){
                navController.navigate(it)
            }
        }

        composable("game") {
            MemoryFlipGameScreen(viewModel)
        }

    }

}