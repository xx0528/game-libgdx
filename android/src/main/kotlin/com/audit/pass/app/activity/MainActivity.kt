package com.audit.pass.app.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val gameScreen = Intent(this, AndroidLauncher::class.java)
//        startActivity(gameScreen)

        setContent {
            MainContent()
        }

    }
}

@Composable
fun MainContent() {
    Box(modifier = Modifier.fillMaxSize().background(Color.Blue)) {}

}

