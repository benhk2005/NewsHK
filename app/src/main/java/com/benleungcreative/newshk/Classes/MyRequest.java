package com.benleungcreative.newshk.Classes;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by BenLeung on 17/3/2018.
 */

public class MyRequest {

    private WeakReference<Activity> activityWeakReference;
    private WeakReference<Fragment> fragmentWeakReference;
    private String url;

    MyRequest(Activity activity, String url) {
        this.activityWeakReference = new WeakReference<Activity>(activity);
        this.url = url;
    }

    MyRequest(Fragment fragment, String url) {
        this.fragmentWeakReference = new WeakReference<Fragment>(fragment);
        this.url = url;
    }

    public void enqueue(OkHttpClient okHttpClient, @Nullable final Callback callback) {
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Activity activity = activityWeakReference.get();
                Fragment fragment = fragmentWeakReference.get();
                if (activity == null && fragment == null) {
                    return;
                }
                if (activity != null && activity.isFinishing()) {
                    return;
                }
                if (fragment != null && !(fragment.isAdded() && !fragment.isDetached() && !fragment.isRemoving())) {
                    return;
                }
                if (callback != null) {
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Activity activity = activityWeakReference.get();
                Fragment fragment = fragmentWeakReference.get();
                if (activity == null && fragment == null) {
                    return;
                }
                if (activity != null && activity.isFinishing()) {
                    return;
                }
                if (fragment != null && !(fragment.isAdded() && !fragment.isDetached() && !fragment.isRemoving())) {
                    return;
                }
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }

}
