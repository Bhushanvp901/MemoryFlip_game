package com.example.memoryflipgame.presentation.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import network.chaintech.sdpcomposemultiplatform.sdp


@Composable
fun HomeScreen(
    onNavigateTo:(String)->Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            modifier = Modifier.fillMaxWidth()
                .padding(10.sdp),
            onClick = {onNavigateTo("game")}
        ) {
            Text("Play MindFlip")
        }


        Button(
            modifier = Modifier.fillMaxWidth()
                .padding(10.sdp),
            onClick = {}
        ) {
            Text("Online Multiplayer")
        }

        Button(
            modifier = Modifier.fillMaxWidth()
                .padding(10.sdp),
            onClick = {}
        ) {
            Text("Offline Multiplayer")
        }
    }
}





@Preview
@Composable
fun HomeScreenPrview() {
    HomeScreen(){}
}