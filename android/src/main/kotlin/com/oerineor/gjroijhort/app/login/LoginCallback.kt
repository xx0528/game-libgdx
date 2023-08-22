package com.oerineor.gjroijhort.app.login

import com.oerineor.gjroijhort.app.login.AuthorizationInfo
import com.oerineor.gjroijhort.app.login.ErrorInfo
import com.oerineor.gjroijhort.app.login.LoginType

/**
 *
 * @author xx
 * 2023/5/19 13:47
 */
interface LoginCallback {

    fun onSuccess(result: AuthorizationInfo, type: LoginType)

    fun onError(error: ErrorInfo)
}
