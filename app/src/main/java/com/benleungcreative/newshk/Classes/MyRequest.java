package com.benleungcreative.newshk.Classes;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

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

    public static interface MyRequestCallback {

        public void onSuccess(JSONObject jsonObject) throws IOException;

        public void onFail(Exception e);

    }

    private WeakReference<Activity> activityWeakReference;
    private WeakReference<Fragment> fragmentWeakReference;
    private String url;

    public MyRequest(Activity activity, String url) {
        this.activityWeakReference = new WeakReference<Activity>(activity);
        this.url = url;
    }

    public MyRequest(Fragment fragment, String url) {
        this.fragmentWeakReference = new WeakReference<Fragment>(fragment);
        this.url = url;
    }

    public void enqueue(OkHttpClient okHttpClient, @Nullable final MyRequestCallback callback) {
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
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
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            callback.onFail(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Activity activity = null;
                Fragment fragment = null;
                if (activityWeakReference != null) {
                    activity = activityWeakReference.get();
                }
                if (fragmentWeakReference != null) {
                    fragment = fragmentWeakReference.get();
                }
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
                    try {
                        String responseString = response.body().string();
                        final JSONObject jsonObject = new JSONObject(responseString);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    callback.onSuccess(jsonObject);
                                } catch (Exception e) {
                                    callback.onFail(e);
                                }
                            }
                        });
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
//                    callback.onResponse(call, response);
                }
            }
        });
    }

}
