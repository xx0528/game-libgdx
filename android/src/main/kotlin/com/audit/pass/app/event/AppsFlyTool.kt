package com.audit.pass.app.event

import android.text.TextUtils
import android.util.Log
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.audit.pass.app.App
import com.audit.pass.app.utils.Const
import com.audit.pass.app.utils.jsonToMap
import com.audit.pass.app.utils.log
import org.json.JSONObject


object AppsFlyTool {
    fun init(afKey: String) {
        log("init AppsFlyer------------$afKey")
        try {
            AppsFlyerLib.getInstance().init(afKey, null, App.getInstance())
            AppsFlyerLib.getInstance().start(App.getInstance(), afKey, object :
                AppsFlyerRequestListener {
                override fun onSuccess() {
                    log("Launch sent successfully")
                }

                override fun onError(errorCode: Int, errorDesc: String) {
                    log(
                        "Launch failed to be sent:\n" +
                            "Error code: " + errorCode + "\n"
                            + "Error description: " + errorDesc
                    )
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onEvent(jSONObj : JSONObject) : String {
        try {
            val eventName = jSONObj.getString("eventName")
            val map = jsonToMap(jSONObj.getString("param"))
            val revenue = jSONObj.getDouble("amount")
            val currency = jSONObj.getString("currency")

            if (!TextUtils.isEmpty(currency)) {
                map[AFInAppEventParameterName.CURRENCY] = currency
                map[AFInAppEventParameterName.PURCHASE_CURRENCY] = currency
            }
            if (revenue > 0.0) {
                map[AFInAppEventParameterName.REVENUE] = revenue
            }

            log("event = $eventName  map = $jSONObj")

            AppsFlyerLib.getInstance().logEvent(App.getInstance(), eventName, map, object :
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
        return ""
    }
}
