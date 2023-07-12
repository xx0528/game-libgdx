package com.audit.pass.app

import android.app.Application

class App : Application() {
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
}
