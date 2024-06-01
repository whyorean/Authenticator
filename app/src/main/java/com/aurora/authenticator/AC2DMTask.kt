package com.aurora.authenticator

import com.github.kittinunf.fuel.Fuel
import java.util.*

class AC2DMTask {
    @Throws(Exception::class)
    fun getAC2DMResponse(email: String?, oAuthToken: String?): Map<String, String> {
        if (email == null || oAuthToken == null)
            return mapOf()

        val params: MutableMap<String, Any> = hashMapOf()
        params["lang"] = Locale.getDefault().toString().replace("_", "-")
        params["google_play_services_version"] = PLAY_SERVICES_VERSION_CODE
        params["sdk_version"] = BUILD_VERSION_SDK
        params["device_country"] = Locale.getDefault().country.lowercase(Locale.US)
        params["Email"] = email
        params["service"] = "ac2dm"
        params["get_accountid"] = 1
        params["ACCESS_TOKEN"] = 1
        params["callerPkg"] = "com.google.android.gms"
        params["add_account"] = 1
        params["Token"] = oAuthToken
        params["callerSig"] = "38918a453d07199354f8b19af05ec6562ced5788"

        val body = params.map { "${it.key}=${it.value}" }.joinToString(separator = "&")

        val response = Fuel.post(TOKEN_AUTH_URL)
                .body(body)
                .header("app" to "com.google.android.gms")
                .header("User-Agent" to "")
                .header("Content-Type" to "application/x-www-form-urlencoded")
                .response()

        return response.third.fold(success = {
            Util.parseResponse(String(it))
        }, failure = {
            mapOf()
        })
    }

    companion object {
        private const val TOKEN_AUTH_URL = "https://android.clients.google.com/auth"
        private const val BUILD_VERSION_SDK = 28
        private const val PLAY_SERVICES_VERSION_CODE = 19629032
    }
}
