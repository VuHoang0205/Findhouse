package com.example.mua_mua_thu.maptest.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.view.ChiTietNhaTroActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyMarker implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater;
    private Context context;

    public MyMarker(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = inflater.inflate(R.layout.item_house_map, null);
        TextView txtTenChuTro, txtNgayDang, txtDienTich, txtGiaPhong, txtDiaChi;
        LinearLayout linearLayout;
        final ImageView ivAvatarMap;
        txtTenChuTro = view.findViewById(R.id.txt_ten_chu_tro_map);
        txtDiaChi = view.findViewById(R.id.txt_dia_chi_map);
        txtDienTich = view.findViewById(R.id.txt_dien_tich_map);
        txtNgayDang = view.findViewById(R.id.txt_gio_dang_map);
        txtGiaPhong = view.findViewById(R.id.txt_gia_phong_map);
        ivAvatarMap = view.findViewById(R.id.img_avatar_map);
        linearLayout = view.findViewById(R.id.llm_map);
        // DATA
        final NhaTro nhaTro = (NhaTro) marker.getTag();
        if (nhaTro == null) {
            linearLayout.removeAllViews();
        } else {
            txtTenChuTro.setText(nhaTro.getTenChuTro());
            txtDiaChi.setText(nhaTro.getDiaChi().getTenDiaChi());
            txtGiaPhong.setText(nhaTro.getGiaPhong() + "đ");
            txtNgayDang.setText(nhaTro.getNgayDang());
            txtDienTich.setText(nhaTro.getDienTich());
            if (nhaTro.getHinhAnhList().size() > 0) {
                StorageReference hinhAnh = FirebaseStorage.getInstance()
                        .getReference().child("hinhanhs").child(nhaTro.getHinhAnhList().get(0));
                Glide.with(context).using(new FirebaseImageLoader())
                        .load(hinhAnh).into(ivAvatarMap);
                String s = String.valueOf(hinhAnh);
            }
        }
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
