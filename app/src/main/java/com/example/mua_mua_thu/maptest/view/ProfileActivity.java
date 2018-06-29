package com.example.mua_mua_thu.maptest.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.define.LogoutInterface;
import com.example.mua_mua_thu.maptest.model.Account;
import com.example.mua_mua_thu.maptest.model.ThanhVien;
import com.facebook.login.LoginManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends Activity implements View.OnClickListener {
    private CircleImageView cicAvatar;
    private ImageView ivBack;
    private TextView txtUser, txtEmail, txtProfile, txtHistory, txtLogout;
    private SharedPreferences sharedPreferences;
    private int key;
    private ProgressBar progressBar;
    private LinearLayout llmProfile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);
        initializeComponents();
        registerlisner();
    }

    private void initializeComponents() {
        SharedPreferences sharedPreferences = getSharedPreferences(Contants.LOGIN, MODE_PRIVATE);
        key = sharedPreferences.getInt(Contants.KEY_LOGIN, 0);
        cicAvatar = findViewById(R.id.iv_avatar_profile);
        txtUser = findViewById(R.id.txt_prolife_login);
        txtEmail = findViewById(R.id.txt_prolife_email);
        txtProfile = findViewById(R.id.txt_prolife);
        txtHistory = findViewById(R.id.txt_prolife_history);
        txtLogout = findViewById(R.id.txt_prolife_logout);
        ivBack = findViewById(R.id.iv_back_profile);
        progressBar =findViewById(R.id.progess_profile);
        llmProfile=findViewById(R.id.llm_profile);
        llmProfile.setVisibility(View.GONE);
        if (key == 2) {
            SharedPreferences KeyLogin = getSharedPreferences(Contants.USERS, MODE_PRIVATE);
            String email = KeyLogin.getString(Contants.EMAIL, "0");
            String name = KeyLogin.getString(Contants.NAME, "0");
            String url = "https://pikmail.herokuapp.com/" + email;
            Glide.with(this).load(url).into(cicAvatar);
            txtEmail.setText(email);
            txtUser.setText(name);
            progressBar.setVisibility(View.GONE);
            llmProfile.setVisibility(View.VISIBLE);
        } else {
            setDataView();
        }


    }

    private void setDataView() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final List<ThanhVien> thanhVienList = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences sharedPreferences = getSharedPreferences(Contants.USERS, MODE_PRIVATE);
                String email = sharedPreferences.getString(Contants.EMAIL, "0");
                DataSnapshot dataThanhVien = dataSnapshot.child("thanhviens");
                for (DataSnapshot valueThanhVien : dataThanhVien.getChildren()) {
                    ThanhVien thanhVien = valueThanhVien.getValue(ThanhVien.class);
                    if (thanhVien.geteMail().equals(email)) {
                        setDataThanhVien(thanhVien);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void registerlisner() {
        txtHistory.setOnClickListener(this);
        txtLogout.setOnClickListener(this);
        txtProfile.setOnClickListener(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_prolife_history:
                showScreenHistory();
                break;
            case R.id.txt_prolife_logout:
                showAlertDialog();
                break;
            case R.id.txt_prolife:
                changeSreenUpdateProfile();
                break;
            default:
                break;
        }
    }

    private void changeSreenUpdateProfile() {
        if (key == 1) {
            startActivity(new Intent(ProfileActivity.this, UpdateProfile.class));
        } else {
            showAlertDialogHistory();
        }

    }

    private void showScreenHistory() {
        String name = txtUser.getText().toString();
        String email = txtEmail.getText().toString();
        Intent intent = new Intent(ProfileActivity.this, HouseHistoryActivity.class);
        Account account = new Account(name, email);
        intent.putExtra("acc", account);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn có muốn đăng xuất khỏi MyApp ?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.my_logo);
        builder.setNegativeButton("Bỏ qua", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.putExtra("logout", 100);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void showAlertDialogHistory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn chưa đăng ký tài khoản với MyApp vui lòng vào Google để đổi thông tin");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.my_logo);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

//    private void setData(List<ThanhVien> thanhVienList) {
//        SharedPreferences sharedPreferences = getSharedPreferences(Contants.USERS, MODE_PRIVATE);
//        String email = sharedPreferences.getString(Contants.EMAIL, "0");
//        for (int i = 0; i < thanhVienList.size(); i++) {
//            ThanhVien thanhVien = thanhVienList.get(i);
//            if (email.equals(thanhVien.geteMail())) {
//                setDataThanhVien(thanhVien);
//                break;
//            }
//        }
//    }

    private void setDataThanhVien(ThanhVien thanhVien) {
        String url = thanhVien.getHinhAnh();
        int s = url.indexOf(".");
        String nameHinh = url.substring(0, s);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("thanhviens").child(nameHinh);
        if (thanhVien.getHinhAnh().equals("user.png")) {
            cicAvatar.setImageResource(R.drawable.my_logo);
        } else {
            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    cicAvatar.setImageBitmap(bitmap);
                    progressBar.setVisibility(View.GONE);
                    llmProfile.setVisibility(View.VISIBLE);
                }
            });
        }
        txtEmail.setText(thanhVien.geteMail());
        txtUser.setText(thanhVien.getHoTen());
        SharedPreferences sharedPreferences = getSharedPreferences(Contants.USERS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Contants.NAME_LOGIN, thanhVien.getHoTen());
        editor.apply();
    }
}

