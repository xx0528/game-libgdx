package com.audit.pass.app.utils

import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder


/**
 * HttpURLConnection网络请求工具类
 */
object HttpUtil {
    /**
     * Get请求
     */
    fun sendGetRequest(
        urlString: String?, listener: HttpCallbackListener?
    ) {
        // 因为网络请求是耗时操作，所以需要另外开启一个线程来执行该任务。
        Thread {
            val url: URL
            var urlConnect: HttpURLConnection? = null
            try {
                // 根据URL地址创建URL对象
                url = URL(urlString)
                // 获取HttpURLConnection对象
                urlConnect = url.openConnection() as HttpURLConnection
                // 设置请求方式，默认为GET
                urlConnect.requestMethod = "GET"
                // 设置连接超时
                urlConnect.connectTimeout = 5000
                // 设置读取超时
                urlConnect.readTimeout = 8000
                // 响应码为200表示成功，否则失败。
                if (urlConnect.responseCode != 200) {
                    Log.i("HttpUtil", "请求失败")
                }
                // 获取网络的输入流
                // 读取输入流中的数据
                val bis = BufferedInputStream(urlConnect.inputStream)
                val baos = ByteArrayOutputStream()
                val bytes = ByteArray(1024)
                var len = -1
                while (bis.read(bytes).also { len = it } != -1) {
                    baos.write(bytes, 0, len)
                }
                bis.close()
                urlConnect.inputStream.close()
                // 响应的数据
                val response = baos.toByteArray()
                listener?.onFinish(response)
            } catch (e: MalformedURLException) {
                listener?.onError(e)
            } catch (e: IOException) {
                listener?.onError(e)
            } finally {
                urlConnect?.disconnect()
            }
        }.start()
    }

    /**
     * Post请求
     */
    fun sendPostRequest(
        urlString: String?, listener: HttpCallbackListener?
    ) {

        // 因为网络请求是耗时操作，所以需要另外开启一个线程来执行该任务。
        Thread {
            val url: URL
            var urlConnect: HttpURLConnection? = null
            try {
                url = URL(urlString)
                urlConnect = url.openConnection() as HttpURLConnection
                urlConnect.requestMethod = "POST"
                urlConnect.connectTimeout = 5000
                urlConnect.readTimeout = 8000

                // 设置运行输入
                urlConnect.doInput = true
                // 设置运行输出
                urlConnect.doOutput = true

                // 请求的数据
                val data = "num=" + URLEncoder.encode("10", "UTF-8") +
                        "&page=" + URLEncoder.encode("1", "UTF-8")

                // 将请求的数据写入输出流中
                val os = urlConnect.outputStream
                val bos = BufferedOutputStream(os)
                bos.write(data.toByteArray())
                bos.flush()
                bos.close()
                os.close()
                if (urlConnect.responseCode == 200) {
                    val bis = BufferedInputStream(urlConnect.inputStream)
                    val baos = ByteArrayOutputStream()
                    val bytes = ByteArray(1024)
                    var len = -1
                    while (bis.read(bytes).also { len = it } != -1) {
                        baos.write(bytes, 0, len)
                    }
                    urlConnect.inputStream.close()
                    bis.close()
                    // 响应的数据
                    val response = baos.toByteArray()
                    listener?.onFinish(response)
                } else {
                    Log.e("HttpUtil", "请求失败")
                }
            } catch (e: MalformedURLException) {
                listener?.onError(e)
            } catch (e: IOException) {
                listener?.onError(e)
            } finally {
                urlConnect?.disconnect()
            }
        }.start()
    }
}

interface HttpCallbackListener {
    // 网络请求成功
    fun onFinish(response: ByteArray?)

    // 网络请求失败
    fun onError(e: Exception?)
}
