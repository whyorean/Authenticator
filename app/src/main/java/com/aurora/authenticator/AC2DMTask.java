package com.aurora.authenticator;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AC2DMTask {

    private static final String TOKEN_AUTH_URL = "https://android.clients.google.com/auth";
    private static final int BUILD_VERSION_SDK = 28;
    private static final int PLAY_SERVICES_VERSION_CODE = 19629032;


    public Map<String, String> getAC2DMResponse(String Email, String Token) throws Exception {
        Map<String, String> body = new TreeMap<>();
        body.put("lang", Locale.getDefault().toString().replace("_", "-"));
        body.put("google_play_services_version", String.valueOf(PLAY_SERVICES_VERSION_CODE));
        body.put("sdk_version", String.valueOf(BUILD_VERSION_SDK));
        body.put("device_country", Locale.getDefault().getCountry().toLowerCase(Locale.US));
        body.put("Email", Email);
        body.put("service", "ac2dm");
        body.put("get_accountid", "1");
        body.put("ACCESS_TOKEN", "1");
        body.put("callerPkg", "com.google.android.gms");
        body.put("add_account", "1");
        body.put("Token", Token);
        body.put("callerSig", "38918a453d07199354f8b19af05ec6562ced5788");

        OkHttpClient client = new OkHttpClient();

        StringBuilder bodyString = new StringBuilder();
        for (String s : body.keySet()) {
            bodyString.append(s).append("=").append(body.get(s)).append("&");
        }

        MediaType mediaType = MediaType.get("application/x-www-form-urlencoded");
        Request request = new Request.Builder()
                .url(TOKEN_AUTH_URL)
                .post(RequestBody.create(bodyString.toString(), mediaType))
                .header("app", "com.google.android.gms")
                .header("User-Agent", "")
                .build();

        Response response = client.newCall(request).execute();
        byte[] content = response.body().bytes();
        return Util.parseResponse(new String(content));
    }
}
