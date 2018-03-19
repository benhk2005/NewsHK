package com.benleungcreative.newshk.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.benleungcreative.newshk.Classes.NewsItem;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by BenLeung on 20/3/2018.
 */

public class OfflineNewsHelper {

    private static final String SHARE_PREF_OFFLINE_NEWS_HELPER = "SHARE_PREF_OFFLINE_NEWS_HELPER";
    private static final String KEY_SAVED_NEWS_SEQ = "KEY_SAVED_NEWS_SEQ";

    public static void saveNewsForOffline(Context context, NewsItem newsItem) {
        //Can migrate to database for future development
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_PREF_OFFLINE_NEWS_HELPER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String newsHash = newsItem.toSHA1Hash();
        editor.putString(newsHash, newsItem.toJSONObjectForSaveInSharedPref().toString());
        String newsSeqJSONArray = sharedPreferences.getString(KEY_SAVED_NEWS_SEQ, "[]");
        try {
            JSONArray seqJSONArray = new JSONArray(newsSeqJSONArray);
            Log.d("newJA", "[" + newsHash + "," + seqJSONArray.join(",") + "]");
            editor.putString(KEY_SAVED_NEWS_SEQ, "[" + newsHash + "," + seqJSONArray.join(",") + "]");
        } catch (JSONException e) {
            e.printStackTrace();
            JSONArray seqJSONArray = new JSONArray();
            seqJSONArray.put(newsHash);
            editor.putString(KEY_SAVED_NEWS_SEQ, seqJSONArray.toString());
        }
        editor.apply();
    }

    public static boolean isNewsSaved(Context context, String newsHash) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_PREF_OFFLINE_NEWS_HELPER, Context.MODE_PRIVATE);
        String seqString = sharedPreferences.getString(KEY_SAVED_NEWS_SEQ, "[]");
        try {
            JSONArray seqJSONArray = new JSONArray(seqString);
            for (int i = 0; i < seqJSONArray.length(); i++) {
                if (seqJSONArray.optString(i, "").equals(newsHash)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return false;
    }

    public static ArrayList<NewsItem> readOfflineNews(Context context) {
        //Can migrate to database for future development
        ArrayList<NewsItem> newsItems = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_PREF_OFFLINE_NEWS_HELPER, Context.MODE_PRIVATE);
        String seqString = sharedPreferences.getString(KEY_SAVED_NEWS_SEQ, null);
        if (seqString == null || seqString.isEmpty()) {
            return newsItems;
        }
        try {
            JSONArray jsonArray = new JSONArray(seqString);
            for (int i = 0; i < jsonArray.length(); i++) {
                String key = jsonArray.optString(i, null);
                if (key == null) {
                    continue;
                }
                String newsJSONString = sharedPreferences.getString(key, null);
                if (newsJSONString == null || newsJSONString.isEmpty()) {
                    continue;
                }
                JSONObject newsJSONObject = new JSONObject(newsJSONString);
                NewsItem possibleNewsItem = NewsItem.fromSharedPrefJSONObject(newsJSONObject);
                if (possibleNewsItem != null) {
                    newsItems.add(possibleNewsItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return newsItems;
        }
        return newsItems;
    }


}
