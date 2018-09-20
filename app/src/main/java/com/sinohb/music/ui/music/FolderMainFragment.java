package com.sinohb.music.ui.music;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sinohb.music.R;
import com.sinohb.music.adapter.ViewPageAdapter;
import com.sinohb.music.ui.music.album.AlbumsFragment;
import com.sinohb.music.ui.music.artists.ArtistsFragment;
import com.sinohb.music.ui.music.folder.FoldersFragment;
import com.sinohb.music.ui.music.songs.SongsFragment;

public class FolderMainFragment extends Fragment {
    protected ViewPageAdapter adapter;
    private View rootView;
    private ViewPager viewPager;

    public static FolderMainFragment newInstance() {
        return new FolderMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_folder_main, container, false);
            viewPager = rootView.findViewById(R.id.viewpager);
            TabLayout tabLayout = rootView.findViewById(R.id.tabs);
            adapter = new ViewPageAdapter(getChildFragmentManager());
            buildAdapter();
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(3);
        } else {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        return rootView;
    }

    protected void buildAdapter() {
        adapter.addFragment(SongsFragment.newInstance(), getString(R.string.songs));
        adapter.addFragment(ArtistsFragment.newInstance(), getString(R.string.artists));
        adapter.addFragment(AlbumsFragment.newInstance(), getString(R.string.albums));
        adapter.addFragment(FoldersFragment.newInstance(), getString(R.string.folders));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setOnTouchListener((view1, motionEvent) -> true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (viewPager != null && adapter != null) {
            Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
            if (fragment!=null)fragment.onHiddenChanged(isVisible()||isHidden());
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
}
