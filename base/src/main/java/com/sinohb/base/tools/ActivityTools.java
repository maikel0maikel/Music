
package com.sinohb.base.tools;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.sinohb.base.R;


public class ActivityTools {
    private ActivityTools() {
    }

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commitAllowingStateLoss();
    }



    public static void addFragmentToFragment(@NonNull Context context,
                                             @NonNull Fragment fragment, int frameId) {
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.hide(fragmentManager.findFragmentById(R.id.content));
//        transaction.add(frameId, fragment);
//        transaction.addToBackStack(null).commitAllowingStateLoss();

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.content));
        transaction.add(frameId, fragment);
        transaction.addToBackStack(null).commit();
    }
}
