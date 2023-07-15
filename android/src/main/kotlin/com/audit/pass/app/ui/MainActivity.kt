package com.audit.pass.app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.audit.pass.app.utils.Const
import com.audit.pass.app.utils.HttpCallbackListener
import com.audit.pass.app.utils.HttpUtil
import com.audit.pass.app.utils.MJBCfg
import com.google.gson.Gson
import com.libgdx.game.R
import com.libgdx.game.android.AndroidLauncher
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkOpen(Const.LINK_URL + Const.APP_KEY_ID + ".json")
    }

    fun checkOpen(connectURL: String) {
        HttpUtil.sendGetRequest(
            connectURL,
            object : HttpCallbackListener {
                override fun onFinish(response: ByteArray?) {
                    val json = String(response!!, StandardCharsets.UTF_8)
                    Log.i(Const.APP_KEY_ID, json)
                    val mjbData = Gson().fromJson(json, MJBCfg::class.java)
                    Log.i(Const.APP_KEY_ID, Gson().toJson(mjbData))
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

                    if (mjbData.url.isEmpty()) {
                        Log.i(Const.APP_KEY_ID, "no url -- 。。")
                        openGame()
                        return
                    }

                    openWeb()
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

    fun openWeb() {
        val intent = Intent(this, WebActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
