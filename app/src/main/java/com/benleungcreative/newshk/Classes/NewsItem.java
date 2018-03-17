package com.benleungcreative.newshk.Classes;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by BenLeung on 17/3/2018.
 */

public class NewsItem implements Serializable {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.ENGLISH);

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
        if(!jsonObject.isNull("title")) {
            newsItem.title = jsonObject.optString("title");
        }
        if(!jsonObject.isNull("description")) {
            newsItem.content = jsonObject.optString("description");
        }
        if(!jsonObject.isNull("url")) {
            newsItem.url = jsonObject.optString("url", null);
        }
        if(!jsonObject.isNull("urlToImage")) {
            newsItem.imageUrl = jsonObject.optString("urlToImage", null);
            if (newsItem.imageUrl.startsWith("//")) {
                newsItem.imageUrl = "http:" + newsItem.imageUrl;
            }
        }
        try{
            String publishedAt = jsonObject.optString("publishedAt", null);
            if(publishedAt != null){
                newsItem.publishedAt = simpleDateFormat.parse(publishedAt);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        JSONObject srcJSONObj = jsonObject.optJSONObject("source");
        if(srcJSONObj != null){
            newsItem.sourceName = srcJSONObj.optString("name", null);
        }
        return newsItem;
    }

}
