package com.benleungcreative.newshk.NewsList;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benleungcreative.newshk.Classes.MyRequest;
import com.benleungcreative.newshk.Classes.NewsCategory;
import com.benleungcreative.newshk.Classes.NewsItem;
import com.benleungcreative.newshk.Helpers.APIHelper;
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

//    private static final String API_CATEGORY_TOP_CATEGORY = "";
//    private static final String API_CATEGORY_BUSINESS = "business";
//    private static final String API_CATEGORY_ENTERTAINMENT = "entertainment";
//    private static final String API_CATEGORY_HEALTH = "health";
//    private static final String API_CATEGORY_SCIENCE = "science";
//    private static final String API_CATEGORY_SPORTS = "sports";
//    private static final String API_CATEGORY_TECHNOLOGY = "technology";

    private RecyclerView newsListRecyclerView;
    private NewsCategory newsCategory;
    private ArrayList<NewsItem> newsItemArrayList;
    private SwipeRefreshLayout newsListPullToRefreshLayout;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newsListPullToRefreshLayout = view.findViewById(R.id.newsListPullToRefreshLayout);
        newsListPullToRefreshLayout.setColorSchemeColors(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        newsListRecyclerView = view.findViewById(R.id.newsListRecyclerView);
        newsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        newsListRecyclerView.setAdapter(new NewsListAdapter());
        if (savedInstanceState == null) {
            showLoadingUI();
            getNewsFromAPI();
        }
        newsListPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewsFromAPI();
            }
        });
    }

    private void getNewsFromAPI() {
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
            }
        });
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

    private class NewsItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView newsItemImageView;
        private TextView newsTitleTextView;
        private TextView newsContentTextView;

        public NewsItemViewHolder(View itemView) {
            super(itemView);
            newsItemImageView = itemView.findViewById(R.id.newsItemImageView);
            newsTitleTextView = itemView.findViewById(R.id.newsTitleTextView);
            newsContentTextView = itemView.findViewById(R.id.newsContentTextView);
        }

        public void applyData(NewsItem newsItem) {
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
        }

    }

}
