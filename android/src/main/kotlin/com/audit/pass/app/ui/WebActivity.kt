package com.audit.pass.app.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AppsFlyerLib
import com.audit.pass.app.App
import com.audit.pass.app.appsfly.AppsFlyTool
import com.audit.pass.app.utils.*
import com.libgdx.game.R
import org.json.JSONArray
import java.lang.*


class WebActivity : AppCompatActivity() {
    lateinit var mWebView: WebView
    lateinit var content: FrameLayout
    private var popWebView: WebView? = null

    private var uploadMessage: ValueCallback<Uri>? = null
    var uploadMessageAboveL: ValueCallback<Array<Uri?>?>? = null
    private val FILE_CHOOSER_RESULT_CODE = 10000

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInit()
        setContentView(R.layout.activity_web)

        setDirection()

        val url = intent.getStringExtra(Intent.ACTION_ATTACH_DATA)
        if (url.isNullOrEmpty())
            return

        content = findViewById(R.id.content)
        mWebView = findViewById(R.id.webView)

        initSetting(mWebView)
        initJsInterface()
        mWebView.loadUrl(url)

        var webClient = NewWebViewClient()
        webClient.setHandle(this)
        mWebView.webViewClient = webClient
        mWebView.webChromeClient = WebViewChromeClient()
    }

    private fun setInit() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val lp = window.attributes
        lp.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) lp.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        window.attributes = lp
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initSetting(webView: WebView) {
        val webSettings = webView.settings
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true
        //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true) // 支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件

        //其他细节操作
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT //缓存相关
        webSettings.domStorageEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.databaseEnabled = true
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = false //支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8"//设置编码格式
        webSettings.setSupportMultipleWindows(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webSettings.setUserAgentString(webSettings.userAgentString.replace("; wv".toRegex(), ""))
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    private fun initJsInterface() {
        var interfaceArrayJson: String = SpUtil[Const.JSInterfaceName, ""] as String
        if (interfaceArrayJson.isEmpty()) {
            return
        }

        val nameList = App.getInstance().getData().jsInterface
        if (nameList.isEmpty()) {
            return
        }

        nameList.forEach {
            if (it.isNotEmpty()) {
                LogUtil.i("加入接口---$it")
                mWebView.addJavascriptInterface(this, it)
            }
        }
    }

    @Keep
    @JavascriptInterface
    fun setActivityOrientation(orientation: String) {
        if (orientation == "sensorLandscape") {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            return
        } else if (orientation == "portrait") {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            return
        }
        LogUtil.i("传入的方向字符串不对=--$orientation")
    }

    private fun setDirection() {
        LogUtil.i("director = ${resources.configuration.orientation}")
        val cfg: MJBCfg = App.getInstance().getData()
        if (cfg.orientation == "portrait") {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else if (cfg.orientation == "sensorLandscape") {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    private fun onAFEvent(
        str: String,
        d: Double,
        currencyStr: String,
        map: MutableMap<String, Any>
    ) {
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
            AppsFlyerLib.getInstance().logEvent(this, str, map)
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        try {
//            if (TextUtils.isEmpty(str)) {
//                return
//            }
//            if (TextUtils.isEmpty(currency)) {
//                currency = App.getInstance().getData().currency
//            }
//            val intValue = (AppUtils.getConfig(this, 6, -1) as Int).toInt()
//            if (TextUtils.isEmpty(AppUtils.getConfig(this, 7, null) as String)) {
//                return
//            }
//            if (intValue == 0) {
//                if (!TextUtils.isEmpty(str2)) {
//                    map[AFInAppEventParameterName.CURRENCY] = str2
//                    map[AFInAppEventParameterName.PURCHASE_CURRENCY] = str2
//                }
//                if (d > 0.0) {
//                    map[AFInAppEventParameterName.REVENUE] = java.lang.Double.valueOf(d)
//                }
//                AppsFlyerLib.getInstance().logEvent(this, str, map)
//            } else if (intValue == 1) {
//                val adjustEvent = AdjustEvent(str)
//                if (d > 0.0) {
//                    adjustEvent.setRevenue(d, str2)
//                }
//                if (!map.isEmpty()) {
//                    for (str3 in ArrayList<Any?>(map.keys)) {
//                        adjustEvent.addCallbackParameter(str3, map[str3].toString())
//                    }
//                }
//                Adjust.trackEvent(adjustEvent)
//            } else if (intValue == 2) {
//                val buildWithEventName: EventApi = Event.buildWithEventName(str)
//                if (d > 0.0) {
//                    buildWithEventName.setPrice(d).setCurrency(str2)
//                }
//                if (!map.isEmpty()) {
//                    for (str4 in ArrayList<Any?>(map.keys)) {
//                        buildWithEventName.setCustomStringValue(str4, map[str4].toString())
//                    }
//                }
//                buildWithEventName.send()
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
    }

    private fun onAJEvent(str: String,
                          d: Double,
                          currencyStr: String,
                          map: MutableMap<String, Any>) {
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
    }

    //给js增加接口或逻辑
    fun addJs() {
        val cfg = App.getInstance().getData()
        if (cfg.jsCode.isNullOrEmpty())
            return

        cfg.jsCode.forEach {
            mWebView.evaluateJavascript(it, null)
        }
    }

    //文件选择回调
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_RESULT_CODE) { //处理返回的图片，并进行上传
            if (null == uploadMessage && null == uploadMessageAboveL) return
            val result = if (data == null || resultCode != RESULT_OK) null else data.data
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data)
            } else if (uploadMessage != null) {
                uploadMessage!!.onReceiveValue(result)
                uploadMessage = null
            }
            return
        }

        val resultData = App.getInstance().getData().onActivityResultCode
        if (resultData.jsCode.isNotEmpty()) {
            if (resultCode == resultData.resultCode) {
                if (requestCode == resultData.requestCode) {
                    mWebView.evaluateJavascript(resultData.jsCode) { }
                }
            }
        }
    }

    @JavascriptInterface
    fun loadUrlOpen(url: String) {
        mWebView.post { mWebView.loadUrl("javascript:window.open('$url','_blank');") }
    }

    private fun loadUrl(url: String) {
        mWebView.loadUrl(url)
    }
    fun getWebSetting(): WebSettings {
        return mWebView.settings
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (popWebView == null) {
                    if (mWebView.canGoBack()) {
                        Log.e("back", "webview back")
                        mWebView.goBack()
                    } else {
                        Log.e("back", "webview close")
                        super.onBackPressed()
                    }
                } else {
                    if (popWebView!!.canGoBack()) {
                        Log.e("back", "popWebView back")
                        popWebView!!.goBack()
                        //这里不要删，有的网页返回两次才行
                        if (!popWebView!!.canGoBack()) {
                            Log.e("back", "popWebView close")
                            mWebView.loadUrl("javascript:window.closeGame();")
                            (popWebView!!.parent as ViewGroup).removeView(popWebView)
                            popWebView!!.stopLoading()
                            popWebView = null
                        }
                    } else {
                        Log.e("back", "popWebView close")
                        mWebView.loadUrl("javascript:window.closeGame();")
                        (popWebView!!.parent as ViewGroup).removeView(popWebView)
                        popWebView!!.stopLoading()
                        popWebView = null
                    }
                }
                true
            }

            else -> super.onKeyUp(keyCode, event)
        }
    }

    @JavascriptInterface
    fun onCall(params: String): String? {
        LogUtil.i("调用了 onCall -- $params")
        return try {
            if (TextUtils.isEmpty(params)) {
                return null
            }
            val jSONArray = JSONArray(params)
            jSONArray.optInt(0, -1)
            when (jSONArray.optString(0, "")) {
                "event" -> {
                    val eventType = jSONArray.optString(1)
                    val eventName = jSONArray.optString(2)
                    when (eventType) {
                        "af" -> {
                            AppsFlyTool.trackEvent(
                                eventName,
                                jSONArray.optDouble(3, -1.0),
                                jSONArray.optString(4, null),
                                jsonToMap(jSONArray.optString(5))
                            )
                        }

                        "aj" -> {
                            onAJEvent(
                                eventName,
                                jSONArray.optDouble(2, -1.0),
                                jSONArray.optString(3, null),
                                jsonToMap(jSONArray.optString(4))
                            )
                        }

                    }
                    ""
                }

                "openUrlWebview" -> {
                    val url = jSONArray.optString(1)
                    openUrlWebView(url)
                    ""
                }

                "openUrlBrowser" -> {
                    val url = jSONArray.optString(1)
                    openUrlBrowser(url)
                    ""
                }
                "openWindow" -> {
                        val url = jSONArray.optString(1)
                        mWebView.post { mWebView.loadUrl("javascript:window.open('$url','_blank');") }
                    ""
                }
                "getAppsFlyerUID" -> AppsFlyerLib.getInstance().getAppsFlyerUID(this)
                "getSPAID" -> {
                    App.getInstance().getGoogleAdId()
                    ""
                }

                "getSPREFERRER" -> {
//                    AppUtils.getSP(this, IConstant.SP_KEY_REFERRER)
                    ""
                }

                "fbLogin" -> {
//                    val basicLogin: BasicLogin = this.basicLogin
//                    if (basicLogin != null) {
//                        basicLogin.fbLogin(webView, optString)
//                        return ""
//                    }
                    ""
                }

                "googleLogin" -> {
//                    val basicLogin2: BasicLogin = this.basicLogin
//                    if (basicLogin2 != null) {
//                        basicLogin2.googleLogin(webView, optString)
//                        return ""
//                    }
                    ""
                }

                else -> ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE) return
        var results: Array<Uri?>? = null
        if (resultCode == RESULT_OK) {
            if (intent != null) {
                val dataString = intent.dataString
                val clipData = intent.clipData
                if (clipData != null) {
                    results = arrayOfNulls(clipData.itemCount)
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        results[i] = item.uri
                    }
                }
                if (dataString != null) results = arrayOf(Uri.parse(dataString))
            }
        }
        uploadMessageAboveL?.onReceiveValue(results)
        uploadMessageAboveL = null
    }

    private fun openUrlBrowser(str: String?) {
        try {
            if (TextUtils.isEmpty(str)) {
                return
            }
            startActivity(
                Intent.parseUri(str, Intent.URI_INTENT_SCHEME)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun openUrlWebView(str: String?) {
        try {
            if (TextUtils.isEmpty(str)) {
                return
            }
            startActivity(
                Intent(this, WebActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Intent.ACTION_ATTACH_DATA, str)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    // webView setting
    fun closeActivity(result: Int) {
        mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
        if (result > 0) { // 异常关闭清空所有缓存
            mWebView.clearCache(true)
            mWebView.clearHistory()
            mWebView.clearFormData()
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 退出时先清理webview，防止内存泄漏
        destoryWebView(mWebView)
        System.gc()
    }
    fun destoryWebView(webView: WebView) {
        try {
            if (webView.parent != null) {
                (webView.parent as FrameLayout).removeView(webView)
            }
            webView.removeAllViews()
            webView.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class WebViewChromeClient : WebChromeClient() {
        override fun onCloseWindow(window: WebView?) {
            if (popWebView != null) {
                mWebView.loadUrl("javascript:window.closeGame();")
                (popWebView!!.parent as ViewGroup).removeView(popWebView)
                popWebView!!.stopLoading()
                popWebView = null
            }
            super.onCloseWindow(window)
        }
        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            Log.e(
                "onConsoleMessage",
                String.format(
                    "%s:%d\t%s",
                    consoleMessage.sourceId(),
                    consoleMessage.lineNumber(),
                    consoleMessage.message()
                )
            )
            return super.onConsoleMessage(consoleMessage)
        }
        @SuppressLint("SetJavaScriptEnabled")
        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?
        ): Boolean {
            Log.e("window create", "window create!!")

            popWebView = WebView(view!!.context)
            val webSettings = popWebView!!.settings
            webSettings.javaScriptEnabled = true
            webSettings.useWideViewPort = true //将图片调整到适合webview的大小

            webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小

            webSettings.setSupportZoom(true) // 支持缩放，默认为true。是下面那个的前提。

            webSettings.builtInZoomControls = true // 设置内置的缩放控件

            webSettings.displayZoomControls = false // 隐藏原生的缩放控件

            webSettings.cacheMode = WebSettings.LOAD_DEFAULT // 缓存相关

            webSettings.databaseEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.loadsImagesAutomatically = true // 支持自动加载图片

            webSettings.defaultTextEncodingName = "utf-8" // 设置编码格式

            webSettings.setSupportMultipleWindows(false)
            CookieManager.getInstance().setAcceptThirdPartyCookies(popWebView, true)
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            //通知 & 请求事件
            //通知 & 请求事件
            val webClient = NewWebViewClient()
            popWebView!!.webViewClient = webClient
            popWebView!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            val transport = resultMsg!!.obj as WebViewTransport
            transport.webView = popWebView
            resultMsg.sendToTarget()
            content.addView(popWebView)
            Log.e("createWindow", "createWindow")
            return true
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
        }

        // 文件选择
        override fun onShowFileChooser(
            webView: WebView?,
            valueCallback: ValueCallback<Array<Uri?>?>,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            uploadMessageAboveL = valueCallback;

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(
                Intent.createChooser(intent, "File Chooser"),
                FILE_CHOOSER_RESULT_CODE
            );
            return true
        }
    }

    inner class NewWebViewClient : WebViewClient() {
        private var mHandle: WebActivity? = null

        fun setHandle(handle: WebActivity?) {
            mHandle = handle
        }
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return false
//            request?.url?.toString().let { url ->
//                if (!url!!.startsWith("http://") && !url.startsWith("https://")) {
//                    try {
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                        startActivity(intent)
//                        return true
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        return true
//                    }
//                } else {
//                    mWebView.loadUrl(url)
//                    return true
//                }
//            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            addJs()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d("webLog_PageFinished ", " $url");
            if (mHandle != null && !mHandle!!.getWebSetting().loadsImagesAutomatically)
                mHandle!!.getWebSetting().loadsImagesAutomatically = true;
            addJs()
        }

        // WEB请求收到服务器的错误消息时回调
        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
            Log.d(
                "webLog_error: Http ",
                " code:" + errorResponse.statusCode + " request:" + request
            )
            if (request.isForMainFrame) {
                mHandle?.closeActivity(1)
            }
        }

        override fun onReceivedSslError(
            webView: WebView?,
            sslErrorHandler: SslErrorHandler,
            sslError: SslError
        ) {
            var message = "SSL Certificate error."
            when (sslError.primaryError) {
                SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                SslError.SSL_EXPIRED -> message = "The certificate has expired."
                SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
            }
            message += " Do you want to continue anyway?"
//            onShowSSLError.invoke(message, sslErrorHandler)
        }
    }

}
