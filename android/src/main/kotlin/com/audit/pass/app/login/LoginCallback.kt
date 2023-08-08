package com.audit.pass.app.login

import com.audit.pass.app.login.AuthorizationInfo
import com.audit.pass.app.login.ErrorInfo
import com.audit.pass.app.login.LoginType

/**
 *
 * @author xx
 * 2023/5/19 13:47
 */
interface LoginCallback {

    fun onSuccess(result: AuthorizationInfo, type: LoginType)

    fun onError(error: ErrorInfo)
}
