package com.benleungcreative.newshk;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.benleungcreative.newshk.Classes.NewsCategory;
import com.benleungcreative.newshk.Helpers.ConnectivityHelper;
import com.benleungcreative.newshk.NewsList.NewsListFragment;

public class MainActivity extends AppCompatActivity {

    private static final int BACK_BUTTON_LOCK_DURATION = 3000;

    private Toolbar mainToolbar;
    private TabLayout mainTabLayout;
    private ViewPager mainViewPager;
    private Long lastBackButtonTimestamp = null;
    private Toast lastExitToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainTabLayout = findViewById(R.id.mainTabLayout);
        mainViewPager = findViewById(R.id.mainViewPager);
        mainViewPager.setAdapter(new NewsListFragmentAdapter(getSupportFragmentManager()));
        mainTabLayout.setupWithViewPager(mainViewPager, false);
        if (savedInstanceState == null) {
            if (ConnectivityHelper.hasConnection(this)) {
                mainViewPager.setCurrentItem(1);
            } else {
                mainViewPager.setCurrentItem(0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (lastBackButtonTimestamp != null && System.currentTimeMillis() - lastBackButtonTimestamp < BACK_BUTTON_LOCK_DURATION) {
            if (lastExitToast != null) {
                lastExitToast.cancel();
            }
            super.onBackPressed();
        } else {
            lastBackButtonTimestamp = System.currentTimeMillis();
            lastExitToast = Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_LONG);
            lastExitToast.show();
        }
    }

    private class NewsListFragmentAdapter extends FragmentStatePagerAdapter {

        private NewsListFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return NewsListFragment.newInstance(NewsCategory.OFFLINE_NEWS);
                case 1:
                    return NewsListFragment.newInstance(NewsCategory.TOP_CATEGORY);
                case 2:
                    return NewsListFragment.newInstance(NewsCategory.BUSINESS);
                case 3:
                    return NewsListFragment.newInstance(NewsCategory.ENTERTAINMENT);
                case 4:
                    return NewsListFragment.newInstance(NewsCategory.HEALTH);
                case 5:
                    return NewsListFragment.newInstance(NewsCategory.SCIENCE);
                case 6:
                    return NewsListFragment.newInstance(NewsCategory.SPORTS);
                case 7:
                    return NewsListFragment.newInstance(NewsCategory.TECHNOLOGY);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.downloaded_news);
                case 1:
                    return getString(R.string.tab_top_headlines);
                case 2:
                    return getString(R.string.tab_business);
                case 3:
                    return getString(R.string.tab_entertainment);
                case 4:
                    return getString(R.string.tab_health);
                case 5:
                    return getString(R.string.tab_science);
                case 6:
                    return getString(R.string.tab_sports);
                case 7:
                    return getString(R.string.tab_technology);
                default:
                    return "";
            }
        }
    }

}
