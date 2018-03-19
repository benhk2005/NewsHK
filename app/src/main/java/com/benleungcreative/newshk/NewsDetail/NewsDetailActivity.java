package com.benleungcreative.newshk.NewsDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.benleungcreative.newshk.Classes.NewsItem;
import com.benleungcreative.newshk.R;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NEWS_ITEM = "EXTRA_NEWS_ITEM";

    private NewsItem newsItem;
    private Toolbar newsDetailToolbar;
    private TextView newsDetailTitle;
    private TextView newsDateTime;
    private TextView newsDetailContent;
    private ImageView newsDetailImageView;

    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simpleDateFormat = new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyy/MM/dd hh:mm aa"), Locale.getDefault());
        setContentView(R.layout.activity_news_detail);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            newsItem = (NewsItem) intent.getSerializableExtra(EXTRA_NEWS_ITEM);
        } else {
            newsItem = (NewsItem) savedInstanceState.getSerializable(EXTRA_NEWS_ITEM);
        }
        if (newsItem == null) {
            finish();
            return;
        }
        newsDetailToolbar = findViewById(R.id.newsDetailToolbar);
        newsDetailTitle = findViewById(R.id.newsDetailTitle);
        newsDateTime = findViewById(R.id.newsDateTime);
        newsDetailContent = findViewById(R.id.newsDetailContent);
        newsDetailImageView = findViewById(R.id.newsDetailImageView);

        setSupportActionBar(newsDetailToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        newsDetailTitle.setText(newsItem.title);
        newsDateTime.setText(simpleDateFormat.format(newsItem.publishedAt));
        newsDetailContent.setText(newsItem.content);
        if (newsItem.imageUrl != null && !newsItem.imageUrl.isEmpty()) {
            newsDetailImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(newsItem.imageUrl).into(newsDetailImageView);
        } else {
            newsDetailImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.shareActionButton:
                shareNewsItem();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareNewsItem() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (newsItem.title != null && !newsItem.title.isEmpty()) {
            intent.putExtra(Intent.EXTRA_TITLE, newsItem.title);
        }
        intent.putExtra(Intent.EXTRA_TEXT, prepareShareText());
        startActivity(Intent.createChooser(intent, null));
    }

    private String prepareShareText() {
        StringBuilder stringBuilder = new StringBuilder();
        if (newsItem.title != null && !newsItem.title.isEmpty()) {
            stringBuilder.append(newsItem.title + "\n");
        }
        if (newsItem.content != null && !newsItem.content.isEmpty()) {
            stringBuilder.append(newsItem.content + "\n");
        }
        if (newsItem.url != null && !newsItem.url.isEmpty()) {
            stringBuilder.append(newsItem.url);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_NEWS_ITEM, newsItem);
    }
}
