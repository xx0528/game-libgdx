package com.audit.pass.app

import android.app.Application
import com.audit.pass.app.utils.MJBCfg

class App : Application() {
    private var data: MJBCfg = MJBCfg(isOpen = false)

    companion object {
        private lateinit var instance: App

        fun getInstance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun setData(data: MJBCfg) {
        this.data = data
    }

    fun getData(): MJBCfg {
        return this.data
    }
}
