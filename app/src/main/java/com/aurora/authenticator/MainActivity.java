package com.aurora.authenticator;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String EMBEDDED_SETUP_URL = "https://accounts.google.com/EmbeddedSetup";
    public static final String AUTH_TOKEN = "oauth_token";
    public static final String AUTH_EMAIL = "AUTH_EMAIL";

    @BindView(R.id.webview)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setup();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setup() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.getSettings().setSafeBrowsingEnabled(false);
        }

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                String cookies = CookieManager.getInstance().getCookie(url);
                Map<String, String> cookieMap = Util.parseCookieString(cookies);
                if (!cookieMap.isEmpty() && cookieMap.get(AUTH_TOKEN) != null) {
                    String oauthToken = cookieMap.get(AUTH_TOKEN);
                    webView.evaluateJavascript("(function() { return document.getElementById(\"profileIdentifier\").innerHTML; })();",
                            email -> startResultsActivity(email, oauthToken));
                }
            }
        });

        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        cookieManager.acceptThirdPartyCookies(webView);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        webView.loadUrl(EMBEDDED_SETUP_URL);
    }

    private void startResultsActivity(String email, String oauthToken) {
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra(AUTH_EMAIL, email);
        intent.putExtra(AUTH_TOKEN, oauthToken);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, null);
        startActivity(intent, activityOptions.toBundle());
        finish();
    }
}
