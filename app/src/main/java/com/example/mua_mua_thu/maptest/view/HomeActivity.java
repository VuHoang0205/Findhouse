package com.example.mua_mua_thu.maptest.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mua_mua_thu.maptest.R;

import com.example.mua_mua_thu.maptest.adapter.HomeAdapter;
import com.example.mua_mua_thu.maptest.contants.Contants;

import com.example.mua_mua_thu.maptest.define.HouseFragmentInterface;
import com.example.mua_mua_thu.maptest.fragments.HouseFragment;
import com.example.mua_mua_thu.maptest.model.ThanhVien;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        RadioGroup.OnCheckedChangeListener, View.OnClickListener, HouseFragmentInterface {
    private ViewPager viewPagerHome;
    private RadioButton radioPlace;
    private RadioButton radioHouse;
    private RadioGroup radioGroupHome;
    private ImageView imvPlus, imgAvatar;
    private FirebaseUser firebaseUser;
    private ThanhVien thanhVien;
    private int dem = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        initialize();
        registerlisner();
    }

    private void registerlisner() {
        viewPagerHome.addOnPageChangeListener(this);
        radioGroupHome.setOnCheckedChangeListener(this);
        imvPlus.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);
    }

    private void initialize() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("muamuathu", "initialize: " + firebaseUser);
        viewPagerHome = findViewById(R.id.view_pager_home);
        HomeAdapter homeAdapter = new HomeAdapter(getSupportFragmentManager());
        viewPagerHome.setAdapter(homeAdapter);
        radioHouse = findViewById(R.id.radio_house);
        radioPlace = findViewById(R.id.radio_place);
        radioGroupHome = findViewById(R.id.radio_group_home);
        imvPlus = findViewById(R.id.iv_plus);
        imgAvatar = findViewById(R.id.img_avatar);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                radioPlace.setChecked(true);
                break;
            case 1:
                radioHouse.setChecked(true);
                break;
            default:
                break;
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_place:
                viewPagerHome.setCurrentItem(0);
                break;
            case R.id.radio_house:
                viewPagerHome.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_plus:
                addHouse();
                break;
            case R.id.img_avatar:
                changeScreenProfile();
                break;
            default:
                break;
        }
    }

    private void changeScreenProfile() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);


    }

    private void addHouse() {
        Intent intent = new Intent(HomeActivity.this, RegisterHouseActivity.class);
        startActivity(intent);

    }

    @Override
    public void showHouseFragmnetSreen() {
        viewPagerHome.setCurrentItem(1);
    }

    @Override
    public void onBackPressed() {
        dem++;
        if (dem == 2) {
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Nhấn phím Back một lần nữa để thoát ", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
