package com.benleungcreative.newshk.Helpers;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.benleungcreative.newshk.Classes.MyRequest;

import okhttp3.OkHttpClient;

/**
 * Created by BenLeung on 17/3/2018.
 */

public class APIHelper {

    private static APIHelper instance;
    private OkHttpClient okHttpClient;

    private APIHelper() {
        okHttpClient = new OkHttpClient();
    }

    public synchronized static APIHelper getInstance() {
        if (instance == null) {
            instance = new APIHelper();
        }
        return instance;
    }

    public void getNewsList(Fragment fragment, String type, @Nullable MyRequest.MyRequestCallback callback) {
        new MyRequest(fragment, "https://newsapi.org/v2/top-headlines?country=hk" + (type != null ? "&category=" + type : "") + "&apiKey=550f553703344b4b9ffa2f9ac639e874").enqueue(okHttpClient, callback);
    }

}
