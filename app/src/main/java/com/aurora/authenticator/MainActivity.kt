package com.aurora.authenticator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.aurora.authenticator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var B: ActivityMainBinding

    private val cookieManager = CookieManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        B = ActivityMainBinding.inflate(layoutInflater)
        setContentView(B.root)
        setup()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null)
            cookieManager.acceptThirdPartyCookies(B.webview)
            cookieManager.setAcceptThirdPartyCookies(B.webview, true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            B.webview.settings.safeBrowsingEnabled = false
        }

        B.webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val cookies = CookieManager.getInstance().getCookie(url)
                val cookieMap = Util.parseCookieString(cookies)
                if (cookieMap.isNotEmpty() && cookieMap[AUTH_TOKEN] != null) {
                    val oauthToken = cookieMap[AUTH_TOKEN]
                    B.webview.evaluateJavascript("(function() { return document.getElementById('profileIdentifier').innerHTML; })();") {
                        val email = it.replace("\"".toRegex(), "")
                        startResultsActivity(email, oauthToken)
                    }
                }
            }
        }

        B.webview.apply {
            settings.apply {
                allowContentAccess = true
                databaseEnabled = true
                domStorageEnabled = true
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
            }
            loadUrl(EMBEDDED_SETUP_URL)
        }
    }

    private fun startResultsActivity(email: String, oauthToken: String?) {
        val intent = Intent(this@MainActivity, ResultActivity::class.java).apply {
            putExtra(AUTH_EMAIL, email)
            putExtra(AUTH_TOKEN, oauthToken)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(
            intent, ActivityOptionsCompat.makeCustomAnimation(
                this@MainActivity,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()
        )
    }

    companion object {
        const val EMBEDDED_SETUP_URL = "https://accounts.google.com/EmbeddedSetup"
        const val AUTH_TOKEN = "oauth_token"
        const val AUTH_EMAIL = "AUTH_EMAIL"
    }
}
