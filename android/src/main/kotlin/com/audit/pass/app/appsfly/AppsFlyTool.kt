package com.audit.pass.app.appsfly

import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.audit.pass.app.App
import com.audit.pass.app.utils.Const

object AppsFlyTool {
    fun init(afKey: String) {
        Log.i(Const.TAG, "init AppsFlyer------------")
        try {
            //AppsFlyerLib.getInstance().init("4NpqzgqtUaqs8eSyU8HJ57", null, this);
            AppsFlyerLib.getInstance().setDebugLog(true);
            if (afKey != null) {
                AppsFlyerLib.getInstance().init(afKey, null, App.getInstance())
            };
            AppsFlyerLib.getInstance().start(App.getInstance());
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
