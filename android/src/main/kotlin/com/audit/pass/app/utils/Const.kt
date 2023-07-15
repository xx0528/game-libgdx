package com.audit.pass.app.utils

import android.text.TextUtils
import org.json.JSONObject


object Const {
    const val LINK_URL = "http://game-config-aa.oss-us-west-1.aliyuncs.com/"
    const val AF_KEY = "YcQCMtATkHdwH4nHUq3igV"
    const val ADJUST_TOKEN = "xvssr72ru0ow"
    const val SP_KEY_REFERRER = "referrer"
    const val SP_KEY_AID = "aid"
    const val APP_KEY_ID = "mjb0712"
    const val AdjustToken = "AdjustToken"
    const val AFKey = "AFKey"
    const val Orientation = "Orientation"
    const val JSInterfaceName = "JSInterfaceName"
    const val URL = "url"
    const val isOpen = "isOpen"
    const val TAG = "MJB-------"
    const val TAGAF = "MJB--AppsFly-----"
}

data class ActivityResultData(
    var resultCode: Int = -1,
    var requestCode: Int = 1,
    var jsCode: String = "",
)

data class MJBCfg(
    var url: String = "",
    var afKey: String = "",
    var ajToken: String = "",
    var jsInterface: ArrayList<String> = arrayListOf(),
    var jsCode: ArrayList<String> = arrayListOf(),
    var onActivityResultCode: ActivityResultData = ActivityResultData(),
    var orientation: String = "",
    var currency: String = "USD",
    var isOpen: Boolean,
)

fun jsonToMap(str: String): MutableMap<String, Any> {
    val hashMap: HashMap<String, Any> = HashMap()
    try {
        if (!TextUtils.isEmpty(str)) {
            val jSONObject = JSONObject(str)
            val keys = jSONObject.keys()
            while (keys.hasNext()) {
                val next = keys.next()
                hashMap[next] = jSONObject[next]
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return hashMap
}
