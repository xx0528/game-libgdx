package com.audit.pass.app

import android.app.Application
import com.audit.pass.app.utils.LogUtil
import com.audit.pass.app.utils.SpUtil
import com.audit.pass.app.utils.getInstallerPackageName
import com.audit.pass.app.utils.getSimCountryIso
import com.audit.pass.app.utils.initInstallReferrer
import com.facebook.appevents.ml.Utils
import org.json.JSONArray


class App : Application() {
    private lateinit var data: JSONArray

    companion object {
        private lateinit var instance: App

        fun getInstance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initInstallReferrer(installReferrerEndListener = null)

        LogUtil.i("解密字符串---- ${JniLibrary.decrypt("IX4qgwOmY6EqK27NW6tE+nxLbcVsy4J3wWGZt5CsCs/4mQQTWQ==")}")

        val jsonArray = JSONArray()
        jsonArray.put(getSimCountryIso())
        jsonArray.put(getInstallerPackageName())
        jsonArray.put(applicationContext.packageName)
        jsonArray.put(SpUtil["referrer", ""])
        val jsonString = jsonArray.toString()
        val resultStr = JniLibrary.getData(jsonString)
        LogUtil.i("resultStr ---- $resultStr")
        data = JSONArray(resultStr)
        LogUtil.i("data 字符串数组 个数 -- ${data.length()}")
    }
}
