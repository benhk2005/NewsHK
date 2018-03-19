package com.benleungcreative.newshk.NewsList;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.benleungcreative.newshk.Classes.MyRequest;
import com.benleungcreative.newshk.Classes.NewsCategory;
import com.benleungcreative.newshk.Classes.NewsItem;
import com.benleungcreative.newshk.Helpers.APIHelper;
import com.benleungcreative.newshk.Helpers.ConnectivityHelper;
import com.benleungcreative.newshk.NewsDetail.NewsDetailActivity;
import com.benleungcreative.newshk.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * NewsListFragment for showing the news list and fetch news from API
 */
public class NewsListFragment extends Fragment {

    private static final String EXTRA_NEWS_CATEGORY = "EXTRA_NEWS_CATEGORY";
    private static final String EXTRA_NEWS_ITEM_ARRAY_LIST = "EXTRA_NEWS_ITEM_ARRAY_LIST";
    private static final String EXTRA_ERROR_UI_SHOWING = "EXTRA_ERROR_UI_SHOWING";

    private RecyclerView newsListRecyclerView;
    private View unableToFetchNewsContainer;
    private NewsCategory newsCategory;
    private ArrayList<NewsItem> newsItemArrayList;
    private SwipeRefreshLayout newsListPullToRefreshLayout;
    private Button newsListRetryButton;

    public NewsListFragment() {
    }

    public static NewsListFragment newInstance(NewsCategory newsCategory) {
        NewsListFragment newsListFragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_NEWS_CATEGORY, newsCategory);
        newsListFragment.setArguments(args);
        return newsListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getArguments();
            newsItemArrayList = new ArrayList<>();
        } else {
            newsItemArrayList = (ArrayList<NewsItem>) bundle.getSerializable(EXTRA_NEWS_ITEM_ARRAY_LIST);
        }
        newsCategory = (NewsCategory) bundle.getSerializable(EXTRA_NEWS_CATEGORY);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_NEWS_CATEGORY, newsCategory);
        outState.putSerializable(EXTRA_NEWS_ITEM_ARRAY_LIST, newsItemArrayList);
        outState.putBoolean(EXTRA_ERROR_UI_SHOWING, unableToFetchNewsContainer.getVisibility() == View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unableToFetchNewsContainer = view.findViewById(R.id.unableToFetchNewsContainer);
        newsListPullToRefreshLayout = view.findViewById(R.id.newsListPullToRefreshLayout);
        newsListRetryButton = view.findViewById(R.id.newsListRetryButton);
        newsListPullToRefreshLayout.setColorSchemeColors(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        newsListRecyclerView = view.findViewById(R.id.newsListRecyclerView);
        newsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        newsListRecyclerView.setAdapter(new NewsListAdapter());
        if (savedInstanceState == null) {
            showLoadingUI();
            getNewsFromAPI();
        } else {
            unableToFetchNewsContainer.setVisibility(savedInstanceState.getBoolean(EXTRA_ERROR_UI_SHOWING, false)?View.VISIBLE:View.GONE);
        }
        newsListPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewsFromAPI();
            }
        });
        newsListRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewsFromAPI();
            }
        });
    }

    private void getNewsFromAPI() {
        if(ConnectivityHelper.hasConnection(getContext())) {
            hideNetworkErrorUI();
            showLoadingUI();
            APIHelper.getInstance().getNewsList(this, newsCategory.toAPIKey(), new MyRequest.MyRequestCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) throws IOException {
                    JSONArray articlesJSONArray = jsonObject.optJSONArray("articles");
                    if (articlesJSONArray == null) {
                        throw new IOException("articles not found");
                    } else {
                        for (int i = 0; i < articlesJSONArray.length(); i++) {
                            JSONObject articleJSONObj = articlesJSONArray.optJSONObject(i);
                            NewsItem tmpNewsItem = com.benleungcreative.newshk.Classes.NewsItem.fromJSONObject(articleJSONObj);
                            if (tmpNewsItem != null) {
                                newsItemArrayList.add(tmpNewsItem);
                            }
                        }
                        updateRecyclerView();
                        hideLoadingUI();
                    }
                    newsListPullToRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFail(Exception e) {
                    e.printStackTrace();
                    newsListPullToRefreshLayout.setRefreshing(false);
                    showNetworkErrorUI();
                }
            });
        } else {
            showNetworkErrorUI();
        }
    }

    private void updateRecyclerView() {
        if (newsListRecyclerView != null) {
            RecyclerView.Adapter adapter = newsListRecyclerView.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showLoadingUI() {
        newsListPullToRefreshLayout.setRefreshing(true);
    }

    private void hideLoadingUI() {
        newsListPullToRefreshLayout.setRefreshing(false);
    }

    private void showNetworkErrorUI(){
        unableToFetchNewsContainer.setVisibility(View.VISIBLE);
    }

    private void hideNetworkErrorUI(){
        unableToFetchNewsContainer.setVisibility(View.GONE);
    }

    private class NewsListAdapter extends RecyclerView.Adapter<NewsItemViewHolder> {

        @Override
        public NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NewsItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_news_items_viewholder, parent, false));
        }

        @Override
        public void onBindViewHolder(NewsItemViewHolder holder, int position) {
            holder.applyData(newsItemArrayList.get(position));
        }

        @Override
        public int getItemCount() {
            return newsItemArrayList == null ? 0 : newsItemArrayList.size();
        }
    }

    private class NewsItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private FrameLayout newsItemContainer;
        private ImageView newsItemImageView;
        private TextView newsTitleTextView;
        private TextView newsRelativeTimeTextView;
        private TextView newsContentTextView;

        public NewsItemViewHolder(View itemView) {
            super(itemView);
            newsItemContainer = itemView.findViewById(R.id.newsItemContainer);
            newsItemContainer.setOnClickListener(this);
            newsItemImageView = itemView.findViewById(R.id.newsItemImageView);
            newsTitleTextView = itemView.findViewById(R.id.newsTitleTextView);
            newsRelativeTimeTextView = itemView.findViewById(R.id.newsRelativeTimeTextView);
            newsContentTextView = itemView.findViewById(R.id.newsContentTextView);
        }

        public void applyData(NewsItem newsItem) {
            newsItemContainer.setTag(R.id.newsItem, newsItem);
            if (newsItem.imageUrl != null) {
                Glide.with(newsItemImageView)
                        .load(newsItem.imageUrl)
                        .apply(new RequestOptions().placeholder(R.drawable.splash_screen_background_drawable))
                        .into(new SimpleTarget<Drawable>() {

                            @Override
                            public void onLoadStarted(@Nullable Drawable placeholder) {
                                super.onLoadStarted(placeholder);
                                newsItemImageView.setImageDrawable(placeholder);
                                newsItemImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            }

                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                newsItemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                newsItemImageView.setImageDrawable(resource);
                            }


                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                newsItemImageView.setImageDrawable(errorDrawable);
                                newsItemImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            }
                        });
            } else {
                newsItemImageView.setImageResource(R.drawable.splash_screen_background_drawable);
                newsItemImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            if (newsItem.title == null) {
                newsTitleTextView.setVisibility(View.GONE);
            } else {
                newsTitleTextView.setVisibility(View.VISIBLE);
                newsTitleTextView.setText(newsItem.title);
            }
            if (newsItem.content == null) {
                newsContentTextView.setVisibility(View.GONE);
            } else {
                newsTitleTextView.setVisibility(View.VISIBLE);
                newsContentTextView.setText(newsItem.content);
            }
            if (newsItem.publishedAt != null) {
                newsRelativeTimeTextView.setVisibility(View.VISIBLE);
                newsRelativeTimeTextView.setText(DateUtils.getRelativeTimeSpanString(newsItem.publishedAt.getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_NUMERIC_DATE));
            } else {
                newsRelativeTimeTextView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            NewsItem newsItem = (NewsItem) v.getTag(R.id.newsItem);
            if (newsItem == null) {
                return;
            }
            Intent intent = new Intent(getContext(), NewsDetailActivity.class);
            intent.putExtra(NewsDetailActivity.EXTRA_NEWS_ITEM, newsItem);
            startActivity(intent);
        }
    }

}
