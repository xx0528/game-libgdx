package com.audit.pass.app.appsfly

import android.text.TextUtils
import android.util.Log
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.audit.pass.app.App
import com.audit.pass.app.utils.Const
import com.badlogic.gdx.math.MathUtils.map


object AppsFlyTool {
    fun init(afKey: String) {
        Log.i(Const.TAG, "init AppsFlyer------------$afKey")
        try {
            AppsFlyerLib.getInstance().init(afKey, null, App.getInstance())
            AppsFlyerLib.getInstance().start(App.getInstance(), afKey, object :
                AppsFlyerRequestListener {
                override fun onSuccess() {
                    Log.i(Const.TAGAF, "Launch sent successfully")
                }

                override fun onError(errorCode: Int, errorDesc: String) {
                    Log.i(
                        Const.TAGAF, "Launch failed to be sent:\n" +
                            "Error code: " + errorCode + "\n"
                            + "Error description: " + errorDesc
                    )
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun trackEvent(str: String,
                   d: Double,
                   currencyStr: String,
                   map: MutableMap<String, Any>) {
        var currency = currencyStr
        try {
            if (TextUtils.isEmpty(currency)) {
                currency = App.getInstance().getData().currency
            }
            if (!TextUtils.isEmpty(currency)) {
                map[AFInAppEventParameterName.CURRENCY] = currency
                map[AFInAppEventParameterName.PURCHASE_CURRENCY] = currency
            }
            if (d > 0.0) {
                map[AFInAppEventParameterName.REVENUE] = d
            }
            AppsFlyerLib.getInstance().logEvent(App.getInstance(), str, map, object :
                AppsFlyerRequestListener {
                override fun onSuccess() {
                    Log.i(Const.TAGAF, "Event sent successfully")
                }

                override fun onError(errorCode: Int, errorDesc: String) {
                    Log.i(
                        Const.TAGAF, "Event failed to be sent:\n" +
                            "Error code: " + errorCode + "\n"
                            + "Error description: " + errorDesc
                    )
                }
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
}
