package com.audit.pass.app.login

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.audit.pass.app.login.AuthorizationInfo
import com.audit.pass.app.login.ErrorInfo
import com.audit.pass.app.login.LoginType.TwitterLogin

/**
 * Twitter 登录授权处理
 * @author xx
 * 2023/5/18 11:12
 */
class TwitterLoginClientFactory : ThirdPartyLoginClientFactory {
    lateinit var twitterObserver: TwitterLifecycleObserver
    private var _loginCallback: LoginCallback? = null

    override fun create(activity: ComponentActivity): ThirdPartyLoginClient {
        twitterObserver = TwitterLifecycleObserver(activity.activityResultRegistry)
        activity.lifecycle.addObserver(twitterObserver)
        return object : ThirdPartyLoginClient {

            override fun login(loginCallback: LoginCallback) {
                _loginCallback = loginCallback
                val intent = Intent(activity, TwitterAuthorizationActivity::class.java)
                twitterObserver.twitterRequest.launch(intent)
            }

        }
    }

    inner class TwitterLifecycleObserver(private val registry: ActivityResultRegistry) : DefaultLifecycleObserver {
        //twitter配置
        lateinit var twitterRequest: ActivityResultLauncher<Intent>

        override fun onCreate(owner: LifecycleOwner) {
            twitterRequest = registry.register("activity_rq#" + "twitter_login", owner, StartActivityForResult()) { result ->
                result.data?.let {
                    val authorizationInfo = it.getParcelableExtra<AuthorizationInfo>("authorizationInfo")
                    if (authorizationInfo != null) {
                        _loginCallback?.onSuccess(authorizationInfo, TwitterLogin)
                        return@let
                    }
                    val errorInfo = it.getParcelableExtra<ErrorInfo>("authorizationInfo")
                    if (errorInfo != null) {
                        _loginCallback?.onError(errorInfo)
                    }

                }
            }
        }


    }
}
