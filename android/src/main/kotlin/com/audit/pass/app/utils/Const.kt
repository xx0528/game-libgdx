package com.audit.pass.app.utils

object Const {
    const val LINK_URL = "http://47.90.250.28:8089/get?gameId="
    const val AF_KEY = "YcQCMtATkHdwH4nHUq3igV"
    const val ADJUST_TOKEN = "xvssr72ru0ow"
    const val SP_KEY_REFERRER = "referrer"
    const val SP_KEY_AID = "aid"
    const val APP_KEY_ID = "mjb0703"
    const val AdjustToken = "AdjustToken"
    const val AFKey = "AFKey"
    const val Orientation = "Orientation"
    const val JSInterfaceName = "JSInterfaceName"
    const val URL = "url"
    const val isOpen = "isOpen"
    const val TAG = "MJB-------"
}


data class MJBCfg(
    var url: String,
    var afKey: String,
    var ajToken: String,
    var jsInterface: String,
    var orientation: String,
    var isOpen: Boolean,
)
