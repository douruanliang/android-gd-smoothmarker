package com.github.dourl.smoothmarker.base;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

/**
 * @author: douruanliang
 * @date: 2019-11-28
 */

public class BaseActivity extends FragmentActivity {

    protected Fragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        super.onCreate(savedInstanceState);
    }

    protected void addFragment(int container, Fragment fragment) {
        mFragment = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(container, fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }


    protected void changeFragment(int container, Fragment fragment) {
        if (isFinishing()) return;
        mFragment = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(container, fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    protected void removeFragment(Fragment fragment) {
        mFragment = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }
}
