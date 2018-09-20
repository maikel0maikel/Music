package com.sinohb.music.base;

import com.sinohb.base.tools.ActivityTools;
import com.sinohb.music.ui.music.detail.DetailFragment;

public abstract class BaseShowDetailFragment<T> extends BaseFragment<T> implements BaseDetailView{
    @Override
    public void showDetial(int type,String title, Object value) {
        DetailFragment  detailFragment = DetailFragment.newInstance(type,title,value);
            ActivityTools.addFragmentToFragment(
                   mContext, detailFragment, com.sinohb.base.R.id.content);
    }

}
