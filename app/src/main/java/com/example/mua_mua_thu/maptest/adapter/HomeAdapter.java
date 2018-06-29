package com.example.mua_mua_thu.maptest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.mua_mua_thu.maptest.fragments.HouseFragment;
import com.example.mua_mua_thu.maptest.fragments.PlaceFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends FragmentStatePagerAdapter {
    private PlaceFragment placeFragment;
    private HouseFragment houseFragment;
    private List<Fragment> fragmentList;

    public HomeAdapter(FragmentManager fm) {
        super(fm);
        fragmentList = new ArrayList<>();
        placeFragment = new PlaceFragment();
        houseFragment = new HouseFragment();
        fragmentList.add(placeFragment);
        fragmentList.add(houseFragment);

    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
