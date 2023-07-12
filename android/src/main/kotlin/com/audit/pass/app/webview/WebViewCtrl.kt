package com.audit.pass.app.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import com.google.gson.Gson


class WebViewCtrl(
    private val mView: FrameLayout,
    private var content: String,
    private var linkUrl: String,
    private val onWebCall: (isFinish: Boolean) -> Unit,
    private val onShowSSLError: (message: String, sslErrorHandler: SslErrorHandler) -> Unit
) {

    private val webView by lazy { mView.getChildAt(0) as WebView }

    @SuppressLint("JavascriptInterface")
    fun initSettings() {
        onWebCall(false)
        setWebSettings()
        setupWebClient()
        webView.addJavascriptInterface(OnJsInterface(content), "androidJs")
    }

    fun onDestroy() {
        mView.removeAllViews()
        webView.destroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebSettings() {
        val webSettings = webView.settings
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true
        //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件

        //其他细节操作
//        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK //关闭webview中缓存
        webSettings.domStorageEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.databaseEnabled = true
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "UTF-8"//设置编码格式
    }


    private fun setupWebClient() {
        webView.webViewClient = NewWebViewClient()
        webView.webChromeClient = ProgressWebViewChromeClient()
        refresh()
    }

    fun refresh() {
        webView.loadUrl(linkUrl)
    }


    inner class ProgressWebViewChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
        }
    }


    inner class NewWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            linkUrl = request?.url.toString()
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            linkUrl = url ?: "NullUrlString"
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            onWebCall(true)
            super.onPageFinished(view, url)
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
            onShowSSLError.invoke(message, sslErrorHandler)
        }
    }
}


//js
class OnJsInterface(private val content: String) {
    @JavascriptInterface
    fun getRechargeInfo(): String {
        val map = HashMap<String, Any>()
        map["code"] = content
        return Gson().toJson(map)
    }
}
