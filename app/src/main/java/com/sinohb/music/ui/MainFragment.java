package com.sinohb.music.ui;

import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sinohb.base.tools.FragmentTools;
import com.sinohb.music.R;
import com.sinohb.music.ui.collect.CollectMainFragment;
import com.sinohb.music.ui.music.FolderMainFragment;
import com.sinohb.music.ui.player.PlayerFragment;
import com.sinohb.music.utils.RxViewUtils;


public class MainFragment extends Fragment implements RxViewUtils.Action1<View> {
    private View root;
    private FragmentTools fragmentTools = new FragmentTools();
    private ImageView folderView;
    private ImageView playerView;
    private ImageView collectView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            fragmentTools.restoreFragments(getChildFragmentManager(), 3);
        } else {
            fragmentTools.addFragment(FolderMainFragment.newInstance());
            fragmentTools.addFragment(PlayerFragment.newInstance());
            fragmentTools.addFragment(CollectMainFragment.newInstance());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_main, container, false);
            folderView = root.findViewById(R.id.folder_iv);
            playerView = root.findViewById(R.id.playing_iv);
            collectView = root.findViewById(R.id.collect_iv);
            RxViewUtils.setOnClickListeners(this, folderView, playerView, collectView);
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerView.performClick();
    }

//    private <T extends View> T inflateStub(View root, int viewId) {
//        View view = root.findViewById(viewId);
//        view.setOnClickListener(this);
//        if (view_stub.getParent() != null) {
//            View stubView = view_stub.inflate();
//            ImageView imageView = stubView.findViewById(R.id.nav_icon_iv);
//            imageView.setImageResource(imageResId);
//            TextView textView = stubView.findViewById(R.id.nav_title_tv);
//            textView.setText(textResId);
//            stubView.setOnClickListener(this);
//            return stubView;
//        }
//        return (T) view;
//    }

    @Override
    public void onClick(View view) {
        if (view == folderView) {
            displayFragmentView(0);
            playerView.setSelected(false);
            collectView.setSelected(false);
        } else if (view == playerView) {
            displayFragmentView(1);
            folderView.setSelected(false);
            collectView.setSelected(false);
        } else if (view == collectView) {
            displayFragmentView(2);
            folderView.setSelected(false);
            playerView.setSelected(false);
        }
        view.setSelected(true);
    }

    private void displayFragmentView(int index) {
        fragmentTools.showFragment(index, getChildFragmentManager(), R.id.fragment_content);
    }

    @Override
    public void onResume() {
        super.onResume();
        Debug.stopMethodTracing();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (root != null && root.getParent() != null) {
            ((ViewGroup) root.getParent()).removeView(root);
        }
    }
}
