package com.sinohb.base.tools;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class FragmentTools {

    private List<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;
    private int currentIndex;


    private void restoreFragment(FragmentManager fragmentManager) {
        int size = fragments.size();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (int i = 0; i < size; i++) {
            Fragment fragment = fragments.get(i);
            if (i == currentIndex) {
                transaction.show(fragment);
                currentFragment = fragment;
            } else {
                transaction.hide(fragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    public void restoreFragments(FragmentManager fragmentManager, int size) {
        for (int i = 0; i < size; i++) {
            Fragment fragment = fragmentManager.findFragmentByTag("TAG" + i);
            fragments.add(fragment);
        }
        restoreFragment(fragmentManager);
    }

    public void addFragment(Fragment fragment) {
        if (!fragments.contains(fragment)) {
            fragments.add(fragment);
        }
    }

    public void showFragment(int index, FragmentManager fragmentManager, int contentId) {
        if (index < 0) {
            return;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = fragments.get(index);
        if (fragment == null || fragment == currentFragment) {
            return;
        }
        if (fragment.isAdded()) {
            transaction.hide(currentFragment).show(fragment);
        } else {
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(contentId, fragment, "TAG" + index);
        }

        transaction.commitAllowingStateLoss();
        currentIndex = index;
        currentFragment = fragment;
    }
}
