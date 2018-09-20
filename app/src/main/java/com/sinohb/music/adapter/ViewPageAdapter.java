package com.sinohb.music.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sinohb.music.MusicApplication;
import com.sinohb.music.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private List<String> tabTitles;

    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPageAdapter(FragmentManager fm, List<Fragment> fragments) {
        this(fm);
        this.fragments = fragments;
    }

    public void addFragment(Fragment fragment, String title) {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        if (!fragments.contains(fragment)) {
            fragments.add(fragment);
        }
        if (tabTitles == null) {
            tabTitles = new ArrayList<>();
        }
        if (!tabTitles.contains(title)) {
            tabTitles.add(title);
        }

    }

    @Override
    public Fragment getItem(int position) {

        return fragments == null ? null : fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        return tabTitles == null ? "TAB" : tabTitles.get(position);
    }
}
