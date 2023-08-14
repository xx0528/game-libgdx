package com.audit.pass.app.utils

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.audit.pass.app.App


fun log(log: Any?) {
    Log.e("game-libgdx-" + Thread.currentThread().name, log.toString())
}

fun showToast(msg: String) {
    Toast.makeText(App.getInstance(), msg, Toast.LENGTH_SHORT).show()
}

fun getSimCountryIso(): String {
    val telephonyManager =
        App.getInstance().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

//    log("国家代码--- SIM Country ISO: $simCountryIso")
    return telephonyManager.simCountryIso
}

fun getInstallerPackageName(): String? {
    val packageManager: PackageManager = App.getInstance().packageManager
    val installerPackageName = packageManager.getInstallerPackageName(App.getInstance().packageName)

    if (installerPackageName != null) {
        // 安装源名称
        Log.d("Installer", "Installer package name: $installerPackageName")

        // 安装源的应用程序信息
        try {
            val applicationInfo = packageManager.getApplicationInfo(installerPackageName, 0)
            return applicationInfo.packageName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }
    } else {
        Log.d("Installer", "Installer package name is null")
        return null
    }
}
fun setDirection(activity: AppCompatActivity, orientation: String) {
    if (orientation == "portrait") {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    } else if (orientation == "sensorLandscape") {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}

fun setFullWindow(activity: AppCompatActivity) {
    activity.window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    val lp = activity.window.attributes
    lp.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) lp.layoutInDisplayCutoutMode =
        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

    activity.window.attributes = lp
    activity.window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
}

fun String.getFirstAndLastName(): Pair<String, String> {
    val nameArray = this.trim().split(" ")
    return if (nameArray.size > 1) {
        Pair(nameArray[0], nameArray[1])
    } else {
        Pair("", this)
    }
}
