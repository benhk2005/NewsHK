package com.benleungcreative.newshk.NewsDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.benleungcreative.newshk.Classes.NewsItem;
import com.benleungcreative.newshk.R;

public class NewsDetailActivity extends AppCompatActivity {

    private static final String EXTRA_NEWS_ITEM = "EXTRA_NEWS_ITEM";

    private NewsItem newsItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        if(savedInstanceState == null){
            Intent intent = getIntent();
            newsItem = (NewsItem) intent.getSerializableExtra(EXTRA_NEWS_ITEM);
        } else {
            newsItem = (NewsItem) savedInstanceState.getSerializable(EXTRA_NEWS_ITEM);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_NEWS_ITEM, newsItem);
    }
}
