package com.audit.pass.app.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.audit.pass.app.App
import com.audit.pass.app.appsfly.AppsFlyTool
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
        setInit()
        setContentView(R.layout.activity_main)

        checkOpen(Const.LINK_URL + Const.APP_KEY_ID + ".json")
    }

    private fun setInit() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val lp = window.attributes
        lp.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) lp.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        window.attributes = lp
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
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

                    App.getInstance().setData(mjbData)
                    openWeb(mjbData)
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

    fun openWeb(mjbData: MJBCfg) {

        if (mjbData.afKey.isNotEmpty()) {
            AppsFlyTool.init(mjbData.afKey)
        }

        if (mjbData.ajToken.isNotEmpty()) {

        }

        val intent = Intent(this, WebActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.ACTION_ATTACH_DATA, mjbData.url)
        startActivity(intent)
    }
}
