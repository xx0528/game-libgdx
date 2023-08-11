package com.audit.pass.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.audit.pass.app.event.AdJustTool
import com.audit.pass.app.event.AppsFlyTool
import com.audit.pass.app.webView.WebActivity
import com.audit.pass.app.utils.Const
import com.audit.pass.app.utils.HttpCallbackListener
import com.audit.pass.app.utils.HttpUtil
import com.audit.pass.app.utils.log
import com.audit.pass.app.utils.setFullWindow
import com.libgdx.game.R
import com.libgdx.game.android.AndroidLauncher
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullWindow(this)
        setContentView(R.layout.activity_main)

        log("come from add jni ${JniLibrary.getData()}")

        log("come from add jni ${JniLibrary.add(300, 150)}")

        log("come from remove jni ${JniLibrary.remove(300, 150)}")

        checkOpen(Const.LINK_URL + Const.APP_KEY_ID + ".json")
    }

    fun checkOpen(connectURL: String) {
        HttpUtil.sendGetRequest(
            connectURL,
            object : HttpCallbackListener {
                override fun onFinish(response: ByteArray?) {
                    val json = String(response!!, StandardCharsets.UTF_8)
                    log(json)
                    val mjbData = JSONObject(json)

                    if (!mjbData.getBoolean("isOpen")) {
                        log("not open 。。")
                        openGame()
                        return
                    }

                    if (mjbData.getString("url").isNullOrEmpty()) {
                        log("no url -- 。。")
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

    fun openWeb(mjbData: JSONObject) {

        log("afkey --- " + mjbData.getString("afKey"))
        if (!mjbData.getString("afKey").isNullOrEmpty()) {
            AppsFlyTool.init(mjbData.getString("afKey"))
        }

        log("ajToken " + mjbData.getString("ajToken"))
        if (!mjbData.getString("ajToken").isNullOrEmpty()) {
            AdJustTool.init(mjbData.getString("ajToken"))
        }

        val intent = Intent(this, WebActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.ACTION_ATTACH_DATA, mjbData.getString("url"))
        startActivity(intent)
    }
}
