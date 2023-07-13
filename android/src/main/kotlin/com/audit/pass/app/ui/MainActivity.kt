package com.audit.pass.app.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.audit.pass.app.ui.theme.MyApplicationTheme
import com.audit.pass.app.utils.Const
import com.audit.pass.app.utils.WebData
import com.audit.pass.app.webview.WebViewPage
import com.libgdx.game.R
import com.libgdx.game.android.AndroidLauncher


class MainActivity : ComponentActivity() {

    companion object {
        private lateinit var instance: MainActivity

        fun getInstance(): MainActivity {
            return instance
        }
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) { Content() }
            }
        }
    }

    @Composable
    fun Content() {

        val viewState = viewModel.viewStates

        if (viewState.isSplash) {
            SplashScreen()
        } else {
            if (viewState.isOpen) {
                // 如果 orientation 为有效值，则设置屏幕方向
                LaunchedEffect(viewState.orientation) {
                    if (viewState.orientation == "sensorLandscape" && requestedOrientation !=  ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    } else if (viewState.orientation == "portrait" && requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    } else {
                        Log.e(Const.TAG, "传入的方向字符串不对=--" + viewState.orientation)
                    }
                }

                WebViewPage(webData = WebData(viewState.url))
            } else {
                openGame()
            }

        }
    }

    fun openGame() {
        val intent = Intent(this, AndroidLauncher::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finishAffinity()
    }
}


@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(R.drawable.bg),
            contentDescription = null,
            Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
