package com.audit.pass.app.login

/**
 *
 * @author xx
 * 2023/5/17 11:32
 */
interface ThirdPartyLoginClient {
    fun login(loginCallback: LoginCallback)
}
