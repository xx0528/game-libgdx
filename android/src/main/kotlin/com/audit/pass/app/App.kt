package com.audit.pass.app

import android.app.Application
import com.audit.pass.app.utils.getInstallerPackageName
import com.audit.pass.app.utils.getSimCountryIso
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

        val jsonArray = JSONArray()
        jsonArray.put(getSimCountryIso())
        jsonArray.put(getInstallerPackageName())
        jsonArray.put(applicationContext.packageName)

        val jsonString = jsonArray.toString()
        data = JSONArray(JniLibrary.getData(jsonString))
    }
}
