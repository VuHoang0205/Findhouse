package com.example.mua_mua_thu.maptest.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.adapter.PlaceAdapter2;
import com.example.mua_mua_thu.maptest.model.BinhLuan;
import com.example.mua_mua_thu.maptest.model.DiaChi;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.example.mua_mua_thu.maptest.model.ThanhVien;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Test extends AppCompatActivity {
    private List<NhaTro> nhaTroList= new ArrayList<>();
    private PlaceAdapter2 placeAdapter2;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);
        init();
    }

    private void init() {

        recyclerView = findViewById(R.id.rcv_nha_tro);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeAdapter2 = new PlaceAdapter2(this,nhaTroList,recyclerView);
        getListNhaTro();
    }

    private void getListNhaTro() {
        SharedPreferences sharedPreferences = getSharedPreferences("toado", Context.MODE_PRIVATE);
        final Location currentLocation = new Location("");
        double latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        double longitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);
        DatabaseReference databaseRoot = FirebaseDatabase.getInstance().getReference();
        databaseRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dataNhaTro = dataSnapshot.child("nhatros");
                for (DataSnapshot valueNhatro : dataNhaTro.getChildren()) {
                    NhaTro nhaTro = valueNhatro.getValue(NhaTro.class);
                    nhaTro.setMaNhaTro(valueNhatro.getKey());
                    // Lay danh sach hinh anh quan an
                    DataSnapshot dataHinhAnh = dataSnapshot.child("hinhanhs").child(nhaTro.getMaNhaTro());
                    List<String> hinhAnhList = new ArrayList<>();
                    for (DataSnapshot dataHinh : dataHinhAnh.getChildren()) {
                        hinhAnhList.add(dataHinh.getValue(String.class));
                    }
                    nhaTro.setHinhAnhList(hinhAnhList);
                    // Lay danh sach binh luan cua quan an
                    DataSnapshot dataBinhLuan = dataSnapshot.child("binhluans").child(nhaTro.getMaNhaTro());
                    List<BinhLuan> binhLuanList = new ArrayList<>();


                    for (DataSnapshot valueBL : dataBinhLuan.getChildren()) {
                        BinhLuan binhLuan = valueBL.getValue(BinhLuan.class);
                        binhLuan.setMaBinhLuan(valueBL.getKey());
                        ThanhVien thanhVien = dataSnapshot.child("thanhviens").
                                child(binhLuan.getMauser()).getValue(ThanhVien.class);
                        binhLuan.setThanhVien(thanhVien);

                        List<String> hinhanhBLs = new ArrayList<>();
                        DataSnapshot dataHinhAnhBL = dataSnapshot.child("hinhanhbinhluans").child(binhLuan.getMaBinhLuan());
                        for (DataSnapshot valueHinhBL : dataHinhAnhBL.getChildren()) {
                            hinhanhBLs.add(valueHinhBL.getValue(String.class));
                        }
                        binhLuan.setImgBinhLuans(hinhanhBLs);
                        binhLuanList.add(binhLuan);
                    }
                    nhaTro.setBinhLuanList(binhLuanList);
                    // Lay dia chi nha tro
                    DataSnapshot dataDiaChi = dataSnapshot.child("diachinhatro").child(nhaTro.getMaNhaTro());
                    DiaChi diaChi = dataDiaChi.getValue(DiaChi.class);
                    Location viTri = new Location("");
                    viTri.setLatitude(diaChi.getLatitude());
                    viTri.setLongitude(diaChi.getLongitude());
                    Log.d("", "onDataChange: " + viTri.getLongitude());
                    double khoangCach = currentLocation.distanceTo(viTri) / 1000;
                    diaChi.setKhoangCach(khoangCach);
                    nhaTro.setDiaChi(diaChi);
                    Log.d("", "onDataChange: " + khoangCach);
                    // add list Nha tro
                    nhaTroList.add(nhaTro);
                    String name = nhaTro.getTenChuTro();
                    Log.d("", "onDataChange: " + name);
                    Log.d("", "initializeComponents: " + nhaTroList.get(0).getTenChuTro());


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
