package com.example.mua_mua_thu.maptest.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.adapter.BinhLuanAdapter;
import com.example.mua_mua_thu.maptest.adapter.PhotoAdapter;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.define.CommentInterface;
import com.example.mua_mua_thu.maptest.define.DeleteBinhluanInterface;
import com.example.mua_mua_thu.maptest.define.HouseFragmentInterface;
import com.example.mua_mua_thu.maptest.fragments.HouseFragment;
import com.example.mua_mua_thu.maptest.fragments.PlaceFragment;
import com.example.mua_mua_thu.maptest.model.BinhLuan;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.example.mua_mua_thu.maptest.model.ThanhVien;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChiTietNhaTroActivity extends AppCompatActivity implements
        OnMapReadyCallback, View.OnClickListener, DeleteBinhluanInterface {
    private TextView txtTenNhaTroTollBar, txtTenNhaTro, txtDiaChi, txtTongHinhAnh, txtTongBinhLuan,
            txtTongLượtThich, txtNgayDang, txtDienTich, txtTienPhong, txtTienIch, txtMota, txtKhoangCach;
    private EditText edtBinhLuan;
    private ImageView btnSend;
    private ImageView imgAnhNhaTro, ivLike;
    private RecyclerView recyclerBinhLuan;
    private ImageView ivBack;
    private int key;
    private int dem = 0;
    //
    private List<BinhLuan> binhLuanList;
    private BinhLuanAdapter binhLuanAdapter;
    private NhaTro nhaTro;
    private GoogleMap googleMap;
    private BinhLuan binhLuan;
    private ViewPager viewPager;
    private PhotoAdapter photoAdapter;
    private RatingBar ratingBar;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private boolean isLike;
    private String idUserRoot;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_chi_tiet);
        initializeComponents();
        setCommentAdapter();
        registerlisner();
    }

    private void registerlisner() {
        btnSend.setOnClickListener(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key == 0) {
                   finish();
                } else {
                    finish();
                }

            }
        });
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nhaTro.getLuotThichList() != null) {
                    if (nhaTro.getLuotThichList().size() > 0) {
                        List<String> luotThichList = nhaTro.getLuotThichList();
                        // code xoa
                        if (isLike) {
                            ivLike.setImageResource(R.drawable.icon_like_default);
                            isLike = false;
                            for (int i = 0; i < luotThichList.size(); i++) {
                                if (idUserRoot.equals(luotThichList.get(i))) {
                                    luotThichList.remove(i);
                                    databaseReference.child("nhatros").child(nhaTro.getMaNhaTro())
                                            .child("luotThichList").setValue(luotThichList);
                                    break;
                                }
                            }
                            txtTongLượtThich.setText(nhaTro.getLuotThichList().size() + "");
                            // code them
                        } else {
                            ivLike.setImageResource(R.drawable.icon_like_active);
                            isLike = true;
                            for (int i = 0; i < luotThichList.size(); i++) {
                                if (!idUserRoot.equals(luotThichList.get(i))) {
                                    luotThichList.add(idUserRoot);
                                    databaseReference.child("nhatros").child(nhaTro.getMaNhaTro())
                                            .child("luotThichList").setValue(luotThichList);
                                    break;

                                }
                            }
                            txtTongLượtThich.setText(nhaTro.getLuotThichList().size() + "");
                        }
                    } else {
                        List<String> luotThichList = new ArrayList<>();
                        luotThichList.add(idUserRoot);
                        databaseReference.child("nhatros").child(nhaTro.getMaNhaTro())
                                .child("luotThichList").setValue(luotThichList);
                        ivLike.setImageResource(R.drawable.icon_like_active);
                        isLike = true;
                        txtTongLượtThich.setText("1");
                        nhaTro.setLuotThichList(luotThichList);
                    }

                } else {
                    List<String> luotThichList = new ArrayList<>();
                    luotThichList.add(idUserRoot);
                    databaseReference.child("nhatros").child(nhaTro.getMaNhaTro())
                            .child("luotThichList").setValue(luotThichList);
                    ivLike.setImageResource(R.drawable.icon_like_active);
                    isLike = true;
                    txtTongLượtThich.setText("1");
                    nhaTro.setLuotThichList(luotThichList);
                }
            }
        });

    }


    private void initializeComponents() {
        binhLuanList = new ArrayList<>();
        sharedPreferences = getSharedPreferences(Contants.USERS, MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        txtTenNhaTro = findViewById(R.id.txt_ten_nha_tro_ct);
        txtNgayDang = findViewById(R.id.txt_ngay_dang_ct);
        txtDienTich = findViewById(R.id.txt_dien_tich_ct);
        txtTienPhong = findViewById(R.id.txt_tien_phong_ct);
        txtTienIch = findViewById(R.id.txt_tien_ich_ct);
        txtMota = findViewById(R.id.txt_mot_ta_ct);
        txtKhoangCach = findViewById(R.id.txt_khoang_cach_ct);
        txtDiaChi = findViewById(R.id.txt_dia_chi_ct);
        txtTongHinhAnh = findViewById(R.id.txt_tong_anh_chi_tiet);
        txtTongBinhLuan = findViewById(R.id.txt_tong_binh_luan_ct);
        txtTongLượtThich = findViewById(R.id.tong_luot_thich);
        recyclerBinhLuan = findViewById(R.id.rcv_chitiet_nha_tro);
        ivBack = findViewById(R.id.iv_back_chi_tiet);
        edtBinhLuan = findViewById(R.id.edt_binh_luan);
        btnSend = findViewById(R.id.btn_send);
        txtTenNhaTroTollBar = findViewById(R.id.txt_ten_nha_tro_toolbar);
        ratingBar = findViewById(R.id.showRatingBar);
        viewPager = findViewById(R.id.view_pager_avatar);
        ivLike = findViewById(R.id.iv_like);
        binhLuan = new BinhLuan();

        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(0, true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerBinhLuan.setLayoutManager(llm);
        binhLuanAdapter = new BinhLuanAdapter(this, binhLuanList, this);
        recyclerBinhLuan.setAdapter(binhLuanAdapter);
//        fragmentInterface = (HouseFragmentInterface) this;
//        llm = new LinearLayoutManager(this);
//        recyclerBinhLuan.setLayoutManager(llm);

//        Animation animationRotale = AnimationUtils.loadAnimation(this, R.anim.logo_set);
//        imgLogo.startAnimation(animationRotale);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        nhaTro = intent.getParcelableExtra(Contants.NHA_TRO);
        if (intent != null) {
            key = intent.getIntExtra(Contants.KEY_MAP, 5);
            key = intent.getIntExtra(Contants.KEY_LIST, 5);
            photoAdapter = new PhotoAdapter(this, nhaTro);
            viewPager.setAdapter(photoAdapter);
            Log.d("test", "initializeComponents: " + nhaTro.getHinhAnhList().size());
            txtTenNhaTro.setText("Tên chủ trọ: " + nhaTro.getTenChuTro());
            int temp = nhaTro.getHinhAnhList().size();
            Log.d("", "txtTongHinhAnh: " + temp);
            txtTongHinhAnh.setText(nhaTro.getHinhAnhList().size() + "");
            txtTongBinhLuan.setText(nhaTro.getBinhLuanList().size() + "");
            txtTenNhaTroTollBar.setText("Nhà Trọ: " + nhaTro.getTenChuTro());
            txtNgayDang.setText(nhaTro.getNgayDang());
            txtDienTich.setText("Diện tích: " + nhaTro.getDienTich());
            txtDiaChi.setText("Địa chỉ: " + nhaTro.getDiaChi().getTenDiaChi());
            txtTienPhong.setText("Tiền phòng: " + nhaTro.getGiaPhong() + "đ");
            txtKhoangCach.setText(String.format("%.1f", nhaTro.getDiaChi().getKhoangCach()) + " km");
            if (nhaTro.getLuotThichList() != null) {
                txtTongLượtThich.setText(nhaTro.getLuotThichList().size() + "");
            } else {
                txtTongLượtThich.setText("0");
            }
            String tienIch = "";
            if (nhaTro.getTienIchList() == null) {
                txtTienIch.setVisibility(View.GONE);
            } else {
                for (int i = 0; i < nhaTro.getTienIchList().size(); i++) {
                    tienIch += nhaTro.getTienIchList().get(i) + ", ";
                }
                txtTienIch.setText("Tiện ích: " + tienIch);
            }


            txtMota.setText("Giới thiệu nhà trọ: " + nhaTro.getMoTa());

        }
        checkIsLike();
    }

    private void checkIsLike() {
        idUserRoot = sharedPreferences.getString(Contants.ID_USER, "0");
        if (nhaTro.getLuotThichList() != null) {
            for (int i = 0; i < nhaTro.getLuotThichList().size(); i++) {
                String idUserDB = nhaTro.getLuotThichList().get(i);
                if (idUserRoot.equals(idUserDB)) {
                    isLike = true;
                }
            }
            if (isLike) {
                ivLike.setImageResource(R.drawable.icon_like_active);
            } else {
                ivLike.setImageResource(R.drawable.icon_like_default);
            }
        } else {
            isLike = false;
            txtTongLượtThich.setText("0");
        }
    }

    public void setHinhBinhLuan(ImageView circleImageView) {
        StorageReference hinhAnhBL = FirebaseStorage.getInstance().getReference().child("hinhanhs")
                .child(nhaTro.getHinhAnhList().get(0));
        Glide.with(this).using(new FirebaseImageLoader())
                .load(hinhAnhBL)
                .into(circleImageView);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        double latitude = nhaTro.getDiaChi().getLatitude();
        double longitude = nhaTro.getDiaChi().getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(nhaTro.getTenChuTro());
        googleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap.moveCamera(cameraUpdate);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                sendBinhLuan();
                break;
            default:
                break;
        }
    }

    private void sendBinhLuan() {
        long chamDiem = (long) ratingBar.getRating() * 2;
        if (chamDiem < 1) {
            Toast.makeText(ChiTietNhaTroActivity.this, "Vui lòng chấm điểm nhà trọ để tránh spam", Toast.LENGTH_SHORT).show();
            return;
        }
        String binhLuan = edtBinhLuan.getText().toString();
        if (!binhLuan.isEmpty()) {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss 'at' dd.MM.yyyy");
            String updateDate = dateFormat.format(date);
            String tieuDe = sharedPreferences.getString(Contants.NAME, "0");
            String idUser = sharedPreferences.getString(Contants.ID_USER, "0");
            String email = sharedPreferences.getString(Contants.EMAIL, "0");
            Log.d("", "sendBinhLuan: " + idUser);
            String moTa = binhLuan;
            String idBinhLuan = UUID.randomUUID().toString();
            BinhLuan binhLuan1 = new BinhLuan(idBinhLuan, chamDiem, tieuDe, moTa, idUser, updateDate, email);
            databaseReference.child("binhluans").child(nhaTro.getMaNhaTro()).child(idBinhLuan).setValue(binhLuan1);
            edtBinhLuan.setText("");
            ratingBar.setRating(0);
            dem++;
            txtTongBinhLuan.setText(nhaTro.getBinhLuanList().size() + dem + "");
            Toast.makeText(ChiTietNhaTroActivity.this, "Cảm ơn bạn đã đóng góp ý kiến", Toast.LENGTH_SHORT).show();
            setCommentAdapter();
        }
    }

    private void setCommentAdapter() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                binhLuanList.clear();
                DataSnapshot dataComments = dataSnapshot.child(Contants.COMMENTS).child(nhaTro.getMaNhaTro());
                for (DataSnapshot valueComment : dataComments.getChildren()) {
                    BinhLuan binhLuan = valueComment.getValue(BinhLuan.class);
                    binhLuanList.add(binhLuan);
                    sortCommentByDate();
                    binhLuan.setCommentId(valueComment.getKey());
                    ThanhVien thanhVien = dataSnapshot.child(Contants.THANH_VIEN).child(binhLuan.getMauser()).getValue(ThanhVien.class);
                    binhLuan.setThanhVien(thanhVien);
                }
                binhLuanAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sortCommentByDate() {
        Collections.sort(binhLuanList, new Comparator<BinhLuan>() {
            Date date1;
            Date date2;

            @Override
            public int compare(BinhLuan o1, BinhLuan o2) {
                String updateDate1 = o1.getDate();
                String updateDate2 = o2.getDate();
                try {
                    date1 = new SimpleDateFormat("HH:mm:ss 'at' dd.MM.yyyy").parse(updateDate1);
                    date2 = new SimpleDateFormat("HH:mm:ss 'at' dd.MM.yyyy").parse(updateDate2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return date2.compareTo(date1);
            }
        });
    }

    @Override
    public void deleteInterface() {
        int dem = Integer.parseInt(txtTongBinhLuan.getText().toString());
        txtTongBinhLuan.setText(dem - 1 + "");
    }

    @Override
    public void onBackPressed() {
        if (key == 0) {
            startActivity(new Intent(ChiTietNhaTroActivity.this, HomeActivity.class));
            finish();
        } else {
            finish();
        }
    }
}