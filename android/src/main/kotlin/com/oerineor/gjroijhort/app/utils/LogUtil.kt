package com.oerineor.gjroijhort.app.utils

import android.util.Log

object LogUtil {
    private var isPrintLog = true
    private var TAG: String = Const.TAG


    fun d(tag: String, msg: String) {
        if (isPrintLog) {
            Log.d(tag, msg)
        }
    }


    fun d(msg: String) {
        if (isPrintLog) {
            Log.d(TAG, msg)
        }
    }

    fun i(msg: String) {
        if (isPrintLog) {
            Log.i(TAG, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (isPrintLog) {
            Log.e(tag, msg)
        }
    }

    fun e(msg: String) {
        if (isPrintLog) {
            Log.e(TAG, msg)
        }
    }

    fun v(tag: String, msg: String) {
        if (isPrintLog) {
            Log.v(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (isPrintLog) {
            Log.w(tag, msg)
        }
    }

    fun w(msg: String) {
        if (isPrintLog) {
            Log.w(TAG, msg)
        }
    }


}
