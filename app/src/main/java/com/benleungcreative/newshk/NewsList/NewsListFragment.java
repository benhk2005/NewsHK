package com.benleungcreative.newshk.NewsList;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benleungcreative.newshk.R;

/**
 * NewsListFragment for showing the news list and fetch news from API
 */
public class NewsListFragment extends Fragment {

    private RecyclerView newsListRecyclerView;

    public NewsListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newsListRecyclerView = view.findViewById(R.id.newsListRecyclerView);
    }

}
