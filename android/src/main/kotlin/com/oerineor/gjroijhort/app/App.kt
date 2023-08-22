package com.oerineor.gjroijhort.app

import android.app.Application
import com.oerineor.gjroijhort.app.event.AppsFlyTool
import com.oerineor.gjroijhort.app.utils.LogUtil
import com.oerineor.gjroijhort.app.utils.initInstallReferrer
import org.json.JSONArray


class App : Application() {
    lateinit var data: JSONArray

    companion object {
        private lateinit var instance: App

        fun getInstance(): App {
            return instance
        }

        @JvmStatic
        fun NBOEIIE(content: String) {
            LogUtil.i("App 接收到的数据-==============- Received data: $content")
            if (content.isEmpty())
                return
            instance.data = JSONArray(content)
            // 在这里处理接收到的数据
            if (instance.data.length() >= 2)
                AppsFlyTool.init(JniLibrary.LKVMEWQ(instance.data[1].toString()))
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initInstallReferrer(installReferrerEndListener = null)

    }
}
