package com.example.mua_mua_thu.maptest.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.model.ThanhVien;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {
    private static final String TAG = "UpdateProfile";
    private EditText edtEmail, edtTenDangNhap, edtDiaChi, edtSdt, edtNgaySinh;
    private RadioButton radioNam, radioNu;
    private String gioiTinh;
    private Button btnCapNhat;
    private CircleImageView cicAvatar;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private ImageView ivBack;
    private TextView txtEmail;
    private String nameHinh;
    private String email;
    private TextView txtChangePassword;
    public static final int REQUEST_CODE_GALLERY = 1000;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        initializeComponents();
        registerlisner();
    }


    private void initializeComponents() {
        edtEmail = findViewById(R.id.edt_email_profile);
        edtTenDangNhap = findViewById(R.id.edt_ten_dang_nha_profile);
        edtDiaChi = findViewById(R.id.edt_dia_chi_profile);
        edtSdt = findViewById(R.id.edt_sdt_profile);
        edtNgaySinh = findViewById(R.id.edt_ngay_sinh);
        radioNam = findViewById(R.id.radio_name);
        radioNu = findViewById(R.id.radio_nu);
        txtEmail = findViewById(R.id.txt_email_profile);
        sharedPreferences = getSharedPreferences(Contants.USERS, MODE_PRIVATE);
        btnCapNhat = findViewById(R.id.btn_update_house);
        cicAvatar = findViewById(R.id.cic_avatar_profile);
        ivBack = findViewById(R.id.iv_back_profile);
        auth = FirebaseAuth.getInstance();
        txtChangePassword = findViewById(R.id.txt_change_password);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getDataBase();

    }


    private void registerlisner() {
        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataUser();
            }
        });
        cicAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setType("image/*");
                startActivityForResult(iGallery, REQUEST_CODE_GALLERY);
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfile.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    cicAvatar.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void getDataBase() {
        final List<ThanhVien> thanhVienList = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dataThanhVien = dataSnapshot.child("thanhviens");
                for (DataSnapshot valueThanhVien : dataThanhVien.getChildren()) {
                    ThanhVien thanhVien = valueThanhVien.getValue(ThanhVien.class);
                    thanhVienList.add(thanhVien);
                    Log.d(TAG, "onDataChange: " + thanhVienList.size());
                    setData(thanhVienList);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setData(List<ThanhVien> thanhVienList) {
        String email = sharedPreferences.getString(Contants.EMAIL, "0");
        for (int i = 0; i < thanhVienList.size(); i++) {
            ThanhVien thanhVien = thanhVienList.get(i);
            if (email.equals(thanhVien.geteMail())) {
                setDataThanhVien(thanhVien);
                break;
            }
        }
    }

    private void setDataThanhVien(ThanhVien thanhVien) {
        email = thanhVien.geteMail();
        edtEmail.setText(thanhVien.geteMail());
        txtEmail.setText(thanhVien.geteMail());
        edtTenDangNhap.setText(thanhVien.getHoTen());
        edtSdt.setText(thanhVien.getSoDT());
        edtDiaChi.setText(thanhVien.getDiaChi());
        edtNgaySinh.setText(thanhVien.getNgaySinh());
        if (thanhVien.getGioiTinh().equals("Nam")) {
            radioNam.setChecked(true);
        }
        if (thanhVien.getGioiTinh().equals("Nữ")) {
            radioNu.setChecked(true);
        }
        if (thanhVien.getHinhAnh().equals("user.png")) {
            cicAvatar.setImageResource(R.drawable.my_logo);
        } else {
            String url = thanhVien.getHinhAnh();
            int s = url.indexOf(".");
            nameHinh = url.substring(0, s);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("thanhviens").child(nameHinh);
            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    cicAvatar.setImageBitmap(bitmap);
                }
            });
        }
    }

    private void sendDataUser() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        SharedPreferences sharedPreferences = getSharedPreferences(Contants.USERS, MODE_PRIVATE);
        String id = sharedPreferences.getString(Contants.ID_USER, "0");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String email = edtEmail.getText().toString();
        String name = edtTenDangNhap.getText().toString();
        String ngaySinh = edtNgaySinh.getText().toString();
        String soDT = edtSdt.getText().toString();
        String diaChi = edtDiaChi.getText().toString();

        if (radioNam.isChecked()) {
            gioiTinh = "Nam";
        } else {
            gioiTinh = "Nữ";
        }
        cicAvatar.getDrawingCache();
        cicAvatar.setDrawingCacheEnabled(true);
        cicAvatar.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) cicAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String nameIMG = randomNameImg();
        StorageReference mountainsRef = storageReference.child("thanhviens").child(nameIMG);
        UploadTask uploadTask = mountainsRef.putBytes(data);
        ThanhVien
                thanhVien = new ThanhVien(email, name, diaChi, soDT, ngaySinh, gioiTinh, nameIMG + ".jpg");
        reference.child("thanhviens").child(id).setValue(thanhVien);
        Toast.makeText(this, "Bạn đã cập nhật thành công ", Toast.LENGTH_SHORT).show();
    }

    private String randomNameImg() {
        Random random = new Random();
        String name = random.nextInt(100) + "" + random.nextInt(100) + ""
                + random.nextInt(100) + "" + random.nextInt(100) + "" + random.nextInt(100);
        return name;
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn muốn đổi mật khẩu ?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.my_logo);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showAlertDialogReset();
                        }
                    }
                });

            }
        });
        builder.setNegativeButton("Bỏ qua", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showAlertDialogReset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn vui lòng vào Gmail để thay nhận thông tin thay đổi mật khẩu");
        builder.setCancelable(false);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
