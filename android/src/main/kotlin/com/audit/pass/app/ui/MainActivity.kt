package com.audit.pass.app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.audit.pass.app.ui.theme.MyApplicationTheme
import com.audit.pass.app.utils.Const
import com.audit.pass.app.utils.HttpCallbackListener
import com.audit.pass.app.utils.HttpUtil.sendGetRequest
import com.audit.pass.app.utils.MJBCfg
import com.audit.pass.app.utils.SpUtil
import com.libgdx.game.R
import com.google.gson.Gson
import com.libgdx.game.android.AndroidLauncher
import java.nio.charset.StandardCharsets


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkOpen(Const.LINK_URL + Const.APP_KEY_ID)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashScreen()
                }

            }
        }
    }

    fun checkOpen(connectURL: String?) {
        sendGetRequest(
            connectURL,
            object : HttpCallbackListener {
                override fun onFinish(response: ByteArray?) {
                    val json = String(response!!, StandardCharsets.UTF_8)
                    Log.i(Const.APP_KEY_ID, json)
                    val mjbData = Gson().fromJson(json, MJBCfg::class.java)

                    if (mjbData == null) {
                        Log.i(Const.APP_KEY_ID, "no data 。。")
                        openGame()
                        return
                    }

                    if (!mjbData.isOpen) {
                        Log.i(Const.APP_KEY_ID, "not open 。。")
                        openGame()
                        return
                    }
                    SpUtil.put(Const.URL, mjbData.url)
                    SpUtil.put(Const.AFKey, mjbData.afKey)
                    SpUtil.put(Const.ADJUST_TOKEN, mjbData.ajToken)
                    SpUtil.put(Const.Orientation, mjbData.orientation)
                    SpUtil.put(Const.JSInterfaceName, mjbData.jsInterface)

                    return
                }

                override fun onError(e: Exception?) {
                    Log.e(Const.TAG, e!!.message!!)
                    openGame()
                }
            })
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
