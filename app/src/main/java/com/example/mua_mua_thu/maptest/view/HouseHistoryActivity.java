package com.example.mua_mua_thu.maptest.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.adapter.HouseHistoryAdapter;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.model.Account;
import com.example.mua_mua_thu.maptest.model.DiaChi;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HouseHistoryActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private TextView txtTenDangNhap, txtEmail;
    private List<NhaTro> nhaTroList;
    private String idUser;
    private LinearLayoutManager llm;
    private HouseHistoryAdapter houseHistoryAdapter;
    private RecyclerView rcvHouseHistory;
    private ImageView ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_house_history);
        initializeCompnents();
        registerlisner();
        getListNhaTro();
        setAdapter();
    }

    private void sortByTime(List<NhaTro> nhaTroList) {
        Collections.sort(nhaTroList, new Comparator<NhaTro>() {
            Date date1;
            Date date2;

            @Override
            public int compare(NhaTro o1, NhaTro o2) {
                String stringDateO1 = o1.getNgayDang();
                String stringDateO2 = o2.getNgayDang();
                try {
                    date1 = new SimpleDateFormat("HH:mm 'at' dd.MM.yyyy").parse(stringDateO1);
                    date2 = new SimpleDateFormat("HH:mm 'at' dd.MM.yyyy").parse(stringDateO2);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                o1.setDate(date1);
                o2.setDate(date2);
                return date2.compareTo(date1);
            }
        });
    }

    private void setAdapter() {
        Log.d("", "nhatrolist2: " + nhaTroList);
        houseHistoryAdapter = new HouseHistoryAdapter(this, nhaTroList);
        rcvHouseHistory.setAdapter(houseHistoryAdapter);
        houseHistoryAdapter.notifyDataSetChanged();
    }

    private void registerlisner() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeCompnents() {
        sharedPreferences = getSharedPreferences(Contants.USERS, MODE_PRIVATE);
        idUser = sharedPreferences.getString(Contants.ID_USER, "0");
        String name = sharedPreferences.getString(Contants.NAME_LOGIN, "0");
        String email = sharedPreferences.getString(Contants.EMAIL, "0");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        txtTenDangNhap = findViewById(R.id.txt_ten_dang_nhap);
        txtEmail = findViewById(R.id.txt_email);
        nhaTroList = new ArrayList<>();
        ivBack = findViewById(R.id.iv_back_history_house);
        rcvHouseHistory = findViewById(R.id.rcv_house_history);
        llm = new LinearLayoutManager(this);
        rcvHouseHistory.setLayoutManager(llm);

        txtTenDangNhap.setText("Tên đăng nhập: " + name);
        txtEmail.setText("Email: " + email);

    }

    private void getListNhaTro() {
        // TODO ADDSINGLE EVENT LISTENER
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nhaTroList.clear();
                DataSnapshot dataNhatro = dataSnapshot.child("nhatros");
                for (DataSnapshot valueNhaTro : dataNhatro.getChildren()) {
                    NhaTro nhaTro = valueNhaTro.getValue(NhaTro.class);
                    if (idUser.equals(nhaTro.getIdUser())) {
                        nhaTroList.add(nhaTro);
                        nhaTro.setMaNhaTro(valueNhaTro.getKey());
                        // Lấy địa chỉ nhà trọ
                        DataSnapshot dataDiaChi = dataSnapshot.child("diachinhatro").child(nhaTro.getMaNhaTro());
                        DiaChi diaChi = dataDiaChi.getValue(DiaChi.class);
                        nhaTro.setDiaChi(diaChi);
                        // Lấy hình ảnh nhà trọ
                        DataSnapshot dataHinhAnh = dataSnapshot.child("hinhanhs").child(nhaTro.getMaNhaTro());
                        List<String> hinhNhaTroList = new ArrayList<>();
                        for (DataSnapshot data : dataHinhAnh.getChildren()) {
                            String tenHinh = data.getValue(String.class);
                            hinhNhaTroList.add(tenHinh);
                        }
                        nhaTro.setHinhAnhList(hinhNhaTroList);
                    }
                }
                sortByTime(nhaTroList);
                houseHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
