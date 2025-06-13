package com.example.memoryflipgame.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.memoryflipgame.data.ClientSocketManager
import com.example.memoryflipgame.data.ServerSocketManager
import com.example.memoryflipgame.presentation.Home.HomeScreen
import com.example.memoryflipgame.presentation.MemoryFlip.MemoryFlipGameScreen
import com.example.memoryflipgame.presentation.Multiplayer.ConnectSocketScreen

@Composable
fun Navigation(){

    fun handleMessage(message: String) {
        println("Received: $message")
    }
    val navController = rememberNavController()
    val context = LocalContext.current
    val serverManager = ServerSocketManager(::handleMessage)
    val clientManager = ClientSocketManager(::handleMessage)



    NavHost(navController = navController, startDestination = "socket"){

        composable("home") {
            HomeScreen(){
                navController.navigate(it)
            }
        }

        composable("game") {
            MemoryFlipGameScreen()
        }


        composable("socket") {
            ConnectSocketScreen(serverManager,clientManager)
        }

    }

}