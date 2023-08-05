package com.audit.pass.app

import android.app.Application
import com.audit.pass.app.utils.MJBCfg
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import java.io.IOException


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

    fun getGoogleAdId(): String? {
        try {
            val info: AdvertisingIdClient.Info = AdvertisingIdClient.getAdvertisingIdInfo(this)
            return info.id
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        }
        return ""
    }
}
