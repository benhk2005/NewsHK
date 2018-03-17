package com.benleungcreative.newshk;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.benleungcreative.newshk.NewsList.NewsListFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private TabLayout mainTabLayout;
    private ViewPager mainViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainTabLayout = findViewById(R.id.mainTabLayout);
        mainViewPager = findViewById(R.id.mainViewPager);
        mainViewPager.setAdapter(new NewsListFragmentAdapter(getSupportFragmentManager()));
        mainTabLayout.setupWithViewPager(mainViewPager, false);
    }

    private class NewsListFragmentAdapter extends FragmentStatePagerAdapter {

        private NewsListFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new NewsListFragment();
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_top_headlines);
                case 1:
                    return getString(R.string.tab_business);
                case 2:
                    return getString(R.string.tab_entertainment);
                case 3:
                    return getString(R.string.tab_health);
                case 4:
                    return getString(R.string.tab_science);
                case 5:
                    return getString(R.string.tab_sports);
                case 6:
                    return getString(R.string.tab_technology);
                default:
                    return "";
            }
        }
    }

}
