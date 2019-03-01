package com.example.cloudmusic.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OKhttpUtil {

    public static OkHttpClient client;

    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

    public static String IP = "http://musicapi.leanapp.cn";

    public static void loginRequest(String phone,String psw,
                                    okhttp3.Callback callback){

        String url = IP + "/login/cellphone" + "?phone=" + phone + "&password=" + psw;

        client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {

                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getUserDetail(long uid,okhttp3.Callback callback){

        String url = IP + "/user/detail?uid=" + uid;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);

    }

}
