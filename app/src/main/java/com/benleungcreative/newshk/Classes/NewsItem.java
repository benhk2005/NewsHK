package com.benleungcreative.newshk.Classes;

import android.support.annotation.Nullable;

import com.benleungcreative.newshk.Helpers.FormatHelper;
import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by BenLeung on 17/3/2018.
 */

public class NewsItem implements Serializable {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

    public String sourceName;
    public String title;
    public String content;
    public String url;
    public String imageUrl;
    public Date publishedAt;

    @Nullable
    public static NewsItem fromJSONObject(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        String title = jsonObject.optString("title", null);
        String content = jsonObject.optString("description", null);
        if (title == null && content == null) {
            return null;
        }
        NewsItem newsItem = new NewsItem();
        if (!jsonObject.isNull("title")) {
            newsItem.title = jsonObject.optString("title");
        }
        if (!jsonObject.isNull("description")) {
            newsItem.content = jsonObject.optString("description");
        }
        if (!jsonObject.isNull("url")) {
            newsItem.url = jsonObject.optString("url", null);
        }
        if (!jsonObject.isNull("urlToImage")) {
            newsItem.imageUrl = jsonObject.optString("urlToImage", null);
            if (newsItem.imageUrl.startsWith("//")) {
                newsItem.imageUrl = "http:" + newsItem.imageUrl;
            }
        }
        try {
            String publishedAt = jsonObject.optString("publishedAt", null);
            if (publishedAt != null) {
                //Workaround for android parse yyyy-MM-ddTHH:mm:ssZ format with incorrect timezone
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(publishedAt));
                calendar.add(Calendar.HOUR_OF_DAY, 8);
                newsItem.publishedAt = calendar.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject srcJSONObj = jsonObject.optJSONObject("source");
        if (srcJSONObj != null) {
            newsItem.sourceName = srcJSONObj.optString("name", null);
        }
        return newsItem;
    }

    public String toSHA1Hash(){
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            StringBuilder stringBuilder = new StringBuilder();
            if(title != null && !title.isEmpty()) {
                stringBuilder.append(title);
            }
            if(content != null && content.isEmpty()){
                stringBuilder.append(content);
            }
            if(sourceName != null && !sourceName.isEmpty()){
                stringBuilder.append(sourceName);
            }
            if(url != null && !url.isEmpty()){
                stringBuilder.append(url);
            }
            byte[] dataForHash = stringBuilder.toString().getBytes();
            String hashHex = FormatHelper.bytesToHex(sha1.digest(dataForHash));
            return hashHex;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return "";
    }

    public JSONObject toJSONObjectForSaveInSharedPref(){
        JSONObject jsonObject = new JSONObject();
        if(sourceName != null && !sourceName.isEmpty()) {
            try {
                jsonObject.put("sourceName", sourceName);
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
        if(title != null && !title.isEmpty()){
            try {
                jsonObject.put("title", title);
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
        if(content != null && !content.isEmpty()){
            try {
                jsonObject.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
        if(url != null && !url.isEmpty()){
            try {
                jsonObject.put("url", url);
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
        if(imageUrl != null && !imageUrl.isEmpty()){
            try {
                jsonObject.put("imageUrl", imageUrl);
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
        if(publishedAt != null){
            try {
                jsonObject.put("publishedAt", publishedAt.getTime());
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
        return jsonObject;
    }

}
