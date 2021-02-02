package com.iti41g1.tripreminder.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class ViewPagerAdaptor extends FragmentStatePagerAdapter {

    private List<Fragment> mfragments;
    private List<String> mfragmentTitles;

    public ViewPagerAdaptor(@NonNull FragmentManager fm, int behavior, List<Fragment> fragments, List<String> fragmentTitles) {
        super(fm, behavior);
        mfragments = fragments;
        mfragmentTitles = fragmentTitles;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mfragments.get(position);
    }

    @Override
    public int getCount() {
        return mfragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mfragmentTitles.get(position);
    }
}
