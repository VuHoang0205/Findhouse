package com.example.mua_mua_thu.maptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.define.SearchHouseInterface;

import java.text.NumberFormat;
import java.util.Locale;

public class DialogSearchHouse extends Dialog implements View.OnClickListener {
    private Context context;
    private SeekBar sbDistanceMin, sbDistanceMax, sbValueMin, sbValueMax;
    private EditText edtArena;
    private Button btnSreach;
    private int progressDistance = 0;
    private int progressPrice = 0;
    private TextView txtDistanceMin, txtDistanceMax, txtValueMin, txtValueMax;
    SearchHouseInterface searchHouseInterface;

    public DialogSearchHouse(@NonNull Context context, SearchHouseInterface searchHouseInterface) {
        super(context);
        this.context = context;
        this.searchHouseInterface = searchHouseInterface;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_sreach);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initializeComponents();
        registerlisner();
    }


    private void initializeComponents() {
        sbDistanceMin = findViewById(R.id.seek_bar_distance_min);
        sbDistanceMax = findViewById(R.id.seek_bar_distance_max);
        sbValueMax = findViewById(R.id.seek_bar_value_max);
        sbValueMin = findViewById(R.id.seek_bar_value_min);
        btnSreach = findViewById(R.id.btn_sreach_house);
        txtDistanceMin = findViewById(R.id.txt_distance_min);
        txtDistanceMax = findViewById(R.id.txt_distance_max);
        txtValueMax = findViewById(R.id.txt_value_max);
        txtValueMin = findViewById(R.id.txt_value_min);
        edtArena = findViewById(R.id.edt_khu_vuc);
    }

    private void registerlisner() {
        setupProgressDistance();
        setupValueMoney();
        btnSreach.setOnClickListener(this);
    }

    private void setupValueMoney() {
        String moneyMin = "Giá phòng nhỏ nhất: " + formatNumber(sbValueMin.getProgress()) + " VND";
        txtValueMin.setText(moneyMin);
        sbValueMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressPrice = progress * 100000;
                txtValueMin.setText("Giá phòng nhỏ nhất: " + formatNumber(progressPrice) + " VND");
                Log.d("", "onProgressChanged: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtValueMin.setText("Giá phòng nhỏ nhất: " + formatNumber(progressPrice) + " VND");
            }
        });
        String moneyMax = "Giá phòng lớn nhất: " + formatNumber(sbValueMax.getProgress()) + " VND";
        txtValueMax.setText(moneyMax);
        sbValueMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressPrice = progress * 100000;
                txtValueMax.setText("Giá phòng lớn nhất: " + formatNumber(progressPrice) + " VND");
                Log.d("", "onProgressChanged: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtValueMax.setText("Giá phòng lớn nhất: " + formatNumber(progressPrice) + " VND");
            }
        });
    }

    private void setupProgressDistance() {
        String distanceMin = "Khoảng cách nhỏ nhất: " + formatNumber(sbDistanceMin.getProgress()) + " m";
        txtDistanceMin.setText(distanceMin);
        sbDistanceMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressDistance = progress * 200;
                txtDistanceMin.setText("Khoảng cách nhỏ nhất: " + formatNumber(progressDistance) + " m");
                Log.d("", "onProgressChanged: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtDistanceMin.setText("Khoảng cách nhỏ nhất: " + formatNumber(progressDistance) + " m");
            }
        });
        String distanceMax = "Khoảng cách lớn nhất: " + formatNumber(sbDistanceMax.getProgress()) + " m";
        txtDistanceMax.setText(distanceMax);
        sbDistanceMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressDistance = progress * 200;
                txtDistanceMax.setText("Khoảng cách lớn nhất: " + formatNumber(progressDistance) + " m");
                Log.d("", "onProgressChanged: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtDistanceMax.setText("Khoảng cách lớn nhất: " + formatNumber(progressDistance) + " m");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sreach_house:
                sreachHouse();
                break;
            default:
                break;
        }
    }

    private void sreachHouse() {
        String arena = edtArena.getText().toString();
        long distaceMin = sbDistanceMin.getProgress() * 200;
        long distaceMax = sbDistanceMax.getProgress() * 200;
        long moneyMin = sbValueMin.getProgress() * 100000;
        long moneyMax = sbValueMax.getProgress() * 100000;
        searchHouseInterface.searchHouse(distaceMin, distaceMax, moneyMin, moneyMax, arena);
        dismiss();

    }

    private String formatNumber(long value) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.format(value);
    }
}
