package com.example.mua_mua_thu.maptest.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.define.AddressInterface;
import com.example.mua_mua_thu.maptest.model.BinhLuan;
import com.example.mua_mua_thu.maptest.model.DiaChi;
import com.example.mua_mua_thu.maptest.model.MyMarker;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.example.mua_mua_thu.maptest.model.ThanhVien;
import com.example.mua_mua_thu.maptest.view.ChiTietNhaTroActivity;
import com.example.mua_mua_thu.maptest.view.RegisterActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HouseFragment extends Fragment implements OnMapReadyCallback {
    private View rootView;
    private GoogleMap googleMap;
    private List<DiaChi> diaChiList;
    private DiaChi diaChi;
    private SharedPreferences sharedPreferences;
    private ImageView imgMyLocation;
    private LatLng myLocation;
    private Location currentLocation;
    private double latitude, longitude;
    private List<NhaTro> nhaTroList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_house, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeComponents();
        registerliners();
    }


    private void initializeComponents() {
        imgMyLocation = rootView.findViewById(R.id.my_location);
        MapFragment mapFragment = (MapFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sharedPreferences = getContext().getSharedPreferences("toado", Context.MODE_PRIVATE);
        currentLocation = new Location("");
        latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        longitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);
        nhaTroList = new ArrayList<>();
        getListNhaTro();

    }

    private void showHouse(List<NhaTro> nhaTroList) {
        sharedPreferences = getActivity().getSharedPreferences("toado", Context.MODE_PRIVATE);
        double latitudeM = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        double longitudeM = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
        myLocation = new LatLng(latitudeM, longitudeM);
        for (int i = 0; i < nhaTroList.size(); i++) {
            NhaTro nhaTro = nhaTroList.get(i);
            double latitude = nhaTro.getDiaChi().getLatitude();
            double longitude = nhaTro.getDiaChi().getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_home));
            Marker marker = googleMap.addMarker(markerOptions);
            marker.setTag(nhaTro);
        }
        MarkerOptions marker = new MarkerOptions();
        marker.position(myLocation);
        marker.title("MyLocation");
        googleMap.addMarker(marker);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 15);
        googleMap.moveCamera(cameraUpdate);
    }

    private void getListNhaTro() {
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

                    showHouse(nhaTroList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void registerliners() {
        imgMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 15);
                googleMap.moveCamera(cameraUpdate);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setInfoWindowAdapter(new MyMarker(getContext()));
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getContext(), ChiTietNhaTroActivity.class);
                NhaTro nhaTro = (NhaTro) marker.getTag();
                if (nhaTro == null) {
                    return;
                }
                intent.putExtra(Contants.NHA_TRO, nhaTro);
                intent.putExtra(Contants.KEY_MAP, 1);
                startActivity(intent);
                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
