package com.sinohb.music.ui.collect;

import com.sinohb.music.R;
import com.sinohb.music.ui.collect.setting.SettingsFragment;
import com.sinohb.music.ui.collect.collectsong.CollectListsFragment;
import com.sinohb.music.ui.music.FolderMainFragment;

public class CollectMainFragment extends FolderMainFragment {

    public static CollectMainFragment newInstance() {
        return new CollectMainFragment();
    }

    @Override
    protected void buildAdapter() {
        adapter.addFragment(CollectListsFragment.newInstance(), getString(R.string.favorite));
        adapter.addFragment(SettingsFragment.newInstance(), getString(R.string.settings));
    }
}
