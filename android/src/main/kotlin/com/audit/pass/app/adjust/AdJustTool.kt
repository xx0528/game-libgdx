package com.audit.pass.app.adjust

import android.util.Log
import com.audit.pass.app.utils.Const


object AdJustTool {
    fun init(ajToken: String) {
        Log.i(Const.TAG, "init AdJust------------$ajToken")
        try {

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
//        val adjustEvent = AdjustEvent(str)
//        if (d > 0.0) {
//            adjustEvent.setRevenue(d, str2)
//        }
//        if (!map.isEmpty()) {
//            for (str3 in ArrayList<Any?>(map.keys)) {
//                adjustEvent.addCallbackParameter(str3, map[str3].toString())
//            }
//        }
//        Adjust.trackEvent(adjustEvent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
}
