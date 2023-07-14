//package com.audit.pass.app.webview
//
//import android.annotation.SuppressLint
//import android.view.ViewGroup
//import android.webkit.SslErrorHandler
//import android.webkit.WebView
//import android.widget.FrameLayout
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.viewinterop.AndroidView
//import com.audit.pass.app.utils.SampleAlertDialog
//import com.audit.pass.app.utils.WebData
//
//@SuppressLint("UseCompatLoadingForDrawables", "JavascriptInterface")
//@Composable
//fun WebViewPage(
//    webData: WebData,
//) {
//    var ctrl: WebViewCtrl? by remember { mutableStateOf(null) }
//    Box {
//        var isRefreshing: Boolean by remember { mutableStateOf(false) }
//        var sslErrorState by remember {
//            mutableStateOf<Pair<String, SslErrorHandler?>>(Pair("", null))
//        }
//        AndroidView(
//            modifier = Modifier
//                .fillMaxSize(),
//            factory = { context ->
//                FrameLayout(context).apply {
//                    layoutParams = FrameLayout.LayoutParams(
//                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
//                    )
//                    val webView = WebView(context).apply {
//                        layoutParams = ViewGroup.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
//                        )
//                    }
//                    addView(webView)
//                    ctrl = WebViewCtrl(this, "", webData.url, onWebCall = { isFinish ->
//                        isRefreshing = !isFinish
//                    }, onShowSSLError = { message, sslErrorHandler ->
//                        run {
//                            sslErrorState = sslErrorState.copy(message, sslErrorHandler)
//                        }
//
//                    })
//                    ctrl?.initSettings()
//                }
//
//            },
//            update = {
//
//            }
//        )
//
//        if (sslErrorState.first.isNotEmpty()) {
//            SampleAlertDialog(
//                "SSL Certificate Error",
//                sslErrorState.first,
//                "cancel",
//                "continue",
//                {
//                    sslErrorState.second?.proceed()
//                    sslErrorState = sslErrorState.copy("", null)
//                },
//                {
//                    sslErrorState.second?.cancel()
//                    sslErrorState = sslErrorState.copy("", null)
//                })
//        }
//    }
//}
//
