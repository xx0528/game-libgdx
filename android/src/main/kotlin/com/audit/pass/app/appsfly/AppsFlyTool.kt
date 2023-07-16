package com.audit.pass.app.appsfly

import android.text.TextUtils
import android.util.Log
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.audit.pass.app.App
import com.audit.pass.app.utils.Const
import com.badlogic.gdx.math.MathUtils.map
import com.google.gson.Gson


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
                   revenue: Double,
                   currencyStr: String,
                   map: MutableMap<String, Any>) {
        var currency = currencyStr
        try {

            if (!TextUtils.isEmpty(currency)) {
                map[AFInAppEventParameterName.CURRENCY] = currency
                map[AFInAppEventParameterName.PURCHASE_CURRENCY] = currency
            }
            if (revenue > 0.0) {
                map[AFInAppEventParameterName.REVENUE] = revenue
            }
            Log.i(Const.TAGAF, "event = $str  map = ${Gson().toJson(map)}")
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
