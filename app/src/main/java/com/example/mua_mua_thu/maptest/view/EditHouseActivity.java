package com.example.mua_mua_thu.maptest.view;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.define.DeleteIMGInterface;
import com.example.mua_mua_thu.maptest.define.DeleteInterface;
import com.example.mua_mua_thu.maptest.dialog.BaseProgess;
import com.example.mua_mua_thu.maptest.dialog.DialogDeleteImg;
import com.example.mua_mua_thu.maptest.model.DiaChi;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class EditHouseActivity extends AppCompatActivity implements View.OnLongClickListener, DeleteIMGInterface, View.OnClickListener {
    private EditText edtTenNhaTro, edtSDT, edtDiaChi, edtMoTa, edtGiaTro, edtDienTich;
    private String idUser, email, name;
    private Button btnCamera, btnGallery, btnRegister;
    private ImageView imgAnhNhaTro1, imgAnhNhaTro2, imgAnhNhaTro3, imgAnhNhaTro4, ivBack;
    private CheckBox checkWifi, checkDieuHoa, checkBNongLanh;
    private RadioButton checkConPhong, checkHetPhong;
    private StorageReference storageReference;
    private DialogDeleteImg deleteImg;
    private DatabaseReference databaseReference;
    private NhaTro nhaTro;
    private BaseProgess baseProgess;
    int dem = 0;
    List<Bitmap> bitmapList;
    private List<String> nameAnh, nameImageList;
    public static final int REQUEST_CODE_PERMISSION = 100;
    public static final int REQUEST_CODE_CAMERA = 101;
    public static final int REQUEST_CODE_GALLERY = 102;
    boolean checkData1, checkData2, checkData3, checkData4;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_house);
        initializeComponents();
        registerlisner();
    }

    private void registerlisner() {
        imgAnhNhaTro1.setOnLongClickListener(this);
        imgAnhNhaTro2.setOnLongClickListener(this);
        imgAnhNhaTro3.setOnLongClickListener(this);
        imgAnhNhaTro4.setOnLongClickListener(this);
        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeComponents() {
        nameAnh = new ArrayList<>();
        nameImageList = new ArrayList<>();
        baseProgess = new BaseProgess(this);
        edtTenNhaTro = findViewById(R.id.edt_username);
        edtDiaChi = findViewById(R.id.edt_address);
        edtMoTa = findViewById(R.id.edt_description);
        edtSDT = findViewById(R.id.edt_phone);
        edtGiaTro = findViewById(R.id.edt_room_price);
        btnCamera = findViewById(R.id.btn_camera);
        btnGallery = findViewById(R.id.btn_gallery);
        btnRegister = findViewById(R.id.btn_register_house);
        edtDienTich = findViewById(R.id.edt_acreage);
        checkWifi = findViewById(R.id.check_wifi);
        checkDieuHoa = findViewById(R.id.check_dieu_hoa);
        checkBNongLanh = findViewById(R.id.check_binh_nong_lanh);
        imgAnhNhaTro1 = findViewById(R.id.iv_anh_nha_tro_1);
        imgAnhNhaTro2 = findViewById(R.id.iv_anh_nha_tro_2);
        imgAnhNhaTro3 = findViewById(R.id.iv_anh_nha_tro_3);
        imgAnhNhaTro4 = findViewById(R.id.iv_anh_nha_tro_4);
        checkConPhong = findViewById(R.id.check_dat_phong);
        checkHetPhong = findViewById(R.id.check_khong_dat_phong);
        ivBack = findViewById(R.id.iv_back_edit_house);
        nhaTro = new NhaTro();
        storageReference = FirebaseStorage.getInstance().getReference();
        bitmapList = new ArrayList<>();
        setDataHouse();
    }

    private void setDataHouse() {
        Intent intent = getIntent();
        if (intent != null) {
            nhaTro = intent.getParcelableExtra("house");
            edtTenNhaTro.setText(nhaTro.getTenChuTro().toString());
            edtSDT.setText(nhaTro.getSoDT());
            edtDiaChi.setText(nhaTro.getDiaChi().getTenDiaChi());
            String giaPhong = formatNumber(nhaTro.getGiaPhong());
            edtGiaTro.setText(giaPhong);
            edtDienTich.setText(nhaTro.getDienTich());
            edtMoTa.setText(nhaTro.getMoTa());
            if (nhaTro.isDatPhong()) {
                checkConPhong.setChecked(true);
            } else {
                checkHetPhong.setChecked(true);
            }

            if (nhaTro.getTienIchList() == null) {
                return;
            } else {
                for (int i = 0; i < nhaTro.getTienIchList().size(); i++) {
                    if (nhaTro.getTienIchList().get(i).equals("Wifi")) {
                        checkWifi.setChecked(true);
                    }
                    if (nhaTro.getTienIchList().get(i).equals("Điều hòa")) {
                        checkDieuHoa.setChecked(true);
                    }
                    if (nhaTro.getTienIchList().get(i).equals("Bình nóng lạnh")) {
                        checkBNongLanh.setChecked(true);
                    }
                }
            }
            for (int i = 0; i < nhaTro.getHinhAnhList().size(); i++) {
                nameAnh.add(nhaTro.getHinhAnhList().get(i));
                StorageReference reference = storageReference.child("hinhanhs/" + nhaTro.getHinhAnhList().get(i));
                Log.d("", "setDataHouse: " + reference);
                long MEGA_BYTE = 1024 * 1024;
                reference.getBytes(MEGA_BYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        dem++;
                        bitmapList.add(bitmap);
                        setImgHouse(bitmapList);
                    }
                });
            }
        }
    }

    private void setImgHouse(List<Bitmap> bitmapList) {
        if (dem == 1) {
            checkData1 = true;
            imgAnhNhaTro1.setImageBitmap(bitmapList.get(0));
        }
        if (dem == 2) {
            checkData1 = true;
            checkData2 = true;
            imgAnhNhaTro1.setImageBitmap(bitmapList.get(0));
            imgAnhNhaTro2.setImageBitmap(bitmapList.get(1));
        }
        if (dem == 3) {
            checkData1 = true;
            checkData2 = true;
            checkData3 = true;
            imgAnhNhaTro1.setImageBitmap(bitmapList.get(0));
            imgAnhNhaTro2.setImageBitmap(bitmapList.get(1));
            imgAnhNhaTro3.setImageBitmap(bitmapList.get(2));
        }
        if (dem == 4) {
            checkData1 = true;
            checkData2 = true;
            checkData3 = true;
            checkData4 = true;
            imgAnhNhaTro1.setImageBitmap(bitmapList.get(0));
            imgAnhNhaTro2.setImageBitmap(bitmapList.get(1));
            imgAnhNhaTro3.setImageBitmap(bitmapList.get(2));
            imgAnhNhaTro4.setImageBitmap(bitmapList.get(3));
        }
        if (dem == 4) {
            dem = 0;
        }
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.iv_anh_nha_tro_1:
                if (checkData1) {
                    deleteDataImg1(imgAnhNhaTro1, 1);
                }

                break;
            case R.id.iv_anh_nha_tro_2:
                if (checkData2) {
                    deleteDataImg1(imgAnhNhaTro2, 2);
                }

                break;
            case R.id.iv_anh_nha_tro_3:
                if (checkData3) {
                    deleteDataImg1(imgAnhNhaTro3, 3);
                }
                break;
            case R.id.iv_anh_nha_tro_4:
                if (checkData4) {
                    deleteDataImg1(imgAnhNhaTro4, 4);
                }
                break;
            default:
                break;

        }
        return false;
    }

    private void deleteDataImg1(ImageView imageView, int position) {
        deleteImg = new DialogDeleteImg(this, imageView, position);
        deleteImg.show();
        dem = position - 1;

    }

    @Override
    public void deleteIMGHouse(Boolean isDelete, ImageView imageView, int position) {
        if (isDelete) {
            imageView.setImageResource(R.drawable.camera);
            deleteImg.dismiss();
            if (position == 1) {
                deleteImg(position);
                checkData1 = false;
            }
            if (position == 2) {
                deleteImg(position);
                checkData2 = false;

            }
            if (position == 3) {
                deleteImg(position);
                checkData3 = false;

            }
            if (position == 4) {
                deleteImg(position);
                checkData4 = false;

            }
        }
    }

    private void deleteImg(int position) {
        bitmapList.remove(position - 1);
        String nameImg = nhaTro.getHinhAnhList().get(position - 1);
        int s = nameImg.indexOf(".");
        String name = nameImg.substring(0, s);
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("hinhanhs").child(nhaTro.getMaNhaTro()).child(name);
        databaseReference.removeValue();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("hinhanhs").child(name + ".jpg");
        storageReference.delete();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                showCamera();
                break;
            case R.id.btn_gallery:
                showGlalery();
                break;
            case R.id.btn_register_house:
                registerHouse();
                break;
        }
    }

    private void showCamera() {
        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(iCamera, REQUEST_CODE_CAMERA);
    }

    private void showGlalery() {
        Intent iGallery = new Intent(Intent.ACTION_PICK);
        iGallery.setType("image/*");
        startActivityForResult(iGallery, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK && data != null) {
                    dem++;
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    bitmapList.add(bitmap);
                    setImage(dem, bitmap);
                }
                break;
            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    try {
                        dem++;
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        bitmapList.add(bitmap);
                        setImage(dem, bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage(int dem, Bitmap bitmap) {
        switch (dem) {
            case 1:
                if (!checkData1) {
                    imgAnhNhaTro1.setImageBitmap(bitmap);
                }
                checkData1 = true;
                break;
            case 2:
                if (!checkData2) {
                    imgAnhNhaTro2.setImageBitmap(bitmap);
                }
                checkData2 = true;
                break;
            case 3:
                if (!checkData3) {
                    imgAnhNhaTro3.setImageBitmap(bitmap);

                }
                checkData3 = true;
                break;
            case 4:
                if (!checkData4) {
                    imgAnhNhaTro4.setImageBitmap(bitmap);

                }
                checkData4 = true;
                break;
            default:
                break;
        }
    }

    private void registerHouse() {
        deleteDataOld();
        getDrawbleImage();
        final List<String> tienIchList = new ArrayList<>();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm 'at' dd.MM.yyyy");
        final String timeDate = dateFormat.format(date);
        if (checkWifi.isChecked()) {
            String wifi = (String) checkWifi.getText();
            tienIchList.add(wifi);
        }
        if (checkDieuHoa.isChecked()) {
            String dieuHoa = (String) checkDieuHoa.getText();
            tienIchList.add(dieuHoa);
        }
        if (checkBNongLanh.isChecked()) {
            String binhNongLanh = (String) checkBNongLanh.getText();
            tienIchList.add(binhNongLanh);
        }

        final boolean isDatPhong;
        if (checkConPhong.isChecked()) {
            isDatPhong = true;
        } else {
            isDatPhong = false;
        }
        final String tenNhaTro = edtTenNhaTro.getText().toString();
        final String sdt = edtSDT.getText().toString();
        final String diaChi = edtDiaChi.getText().toString();
        final String giaPhong = edtGiaTro.getText().toString().replace(".","");
        final String dienTich = edtDienTich.getText().toString();
        final String moTa = edtMoTa.getText().toString();
        final String idHouse = nhaTro.getMaNhaTro();

        checkFormRegister(tenNhaTro, sdt, diaChi, giaPhong, dienTich, moTa);
        if (isVailRegister(tenNhaTro, sdt, diaChi, giaPhong, dienTich, moTa)) {
            baseProgess.showProgressDialog("Đang Cập nhật Vui Lòng Đợi");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences sharedPreferences = getSharedPreferences(Contants.USERS, MODE_PRIVATE);
                    idUser = sharedPreferences.getString(Contants.ID_USER, "0");
                    NhaTro nhaTro = new NhaTro(idUser, idHouse, tenNhaTro, sdt, dienTich, tienIchList, Long.parseLong(giaPhong), isDatPhong, moTa, timeDate);
                    //todo
                    for (int i = 0; i < nameImageList.size(); i++) {
                        String name = nameImageList.get(i);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("hinhanhs").child(nhaTro.getMaNhaTro()).child(name).setValue(name + ".jpg");
                    }
                    DatabaseReference dataNhaTro = FirebaseDatabase.getInstance().getReference();
                    dataNhaTro.child("nhatros").child(idHouse).setValue(nhaTro);
//                    dataNhaTro.child("nhatros").child(idHouse).child("tienIchList").setValue(tienIchList);
                    baseProgess.hideProgressDialog();
                    Toast.makeText(EditHouseActivity.this, "Bạn đã cập nhật nhà trọ thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditHouseActivity.this, HouseHistoryActivity.class));
                    finish();
                }
            }, 1500);
        }
    }

    private void deleteDataOld() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("hinhanhs").child(nhaTro.getMaNhaTro());
        databaseReference.removeValue();
        for (int i = 0; i < nhaTro.getHinhAnhList().size(); i++) {
            String nameImg = nhaTro.getHinhAnhList().get(i);
            int s = nameImg.indexOf(".");
            String name = nameImg.substring(0, s);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("hinhanhs").child(name + ".jpg");
            storageReference.delete();
        }
    }


    private void checkFormRegister(String tenNhaTro, String sdt, String diaChi, String giaPhong, String dienTich, String moTa) {
        if (tenNhaTro.isEmpty()) {
            edtTenNhaTro.setError("Vui lòng điền tên nhà trọ");
        }
        if (sdt.isEmpty()) {
            edtSDT.setError("Vui lòng điền số điện thoại");
        }
        if (diaChi.isEmpty()) {
            edtDiaChi.setError("Vui lòng điền thông tin địa chỉ");


        }
        if (giaPhong.isEmpty()) {
            edtGiaTro.setError("Vui lòng điền thông tin giá phòng");


        }
        if (dienTich.isEmpty()) {
            edtDienTich.setError("Vui lòng điền thông tin diện tích");


        }
        if (moTa.isEmpty()) {
            edtMoTa.setError("Vui lòng điền thông tin mô tả");

        }
    }

    private boolean isVailRegister(String tenNhaTro, String sdt, String diaChi, String giaPhong, String dienTich, String moTa) {
        if (tenNhaTro.isEmpty() || sdt.isEmpty() || diaChi.isEmpty() || giaPhong.isEmpty() || dienTich.isEmpty() || moTa.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public void getDrawbleImage() {
        imgAnhNhaTro1.setDrawingCacheEnabled(true);
        imgAnhNhaTro1.buildDrawingCache();
        imgAnhNhaTro2.setDrawingCacheEnabled(true);
        imgAnhNhaTro2.buildDrawingCache();
        imgAnhNhaTro3.setDrawingCacheEnabled(true);
        imgAnhNhaTro3.buildDrawingCache();
        imgAnhNhaTro4.setDrawingCacheEnabled(true);
        imgAnhNhaTro4.buildDrawingCache();
        for (int i = 0; i < bitmapList.size(); i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapList.get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            String name = randomNameImg();
            nameImageList.add(name);
            StorageReference mountainsRef = storageReference.child("hinhanhs").child(name + ".jpg");
            UploadTask uploadTask = mountainsRef.putBytes(data);
        }
    }

    private String randomNameImg() {
        Random random = new Random();
        String name = random.nextInt(100) + "" + random.nextInt(100) + ""
                + random.nextInt(100) + "" + random.nextInt(100) + "" + random.nextInt(100);
        return name;
    }

    public Bitmap resizeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //Chỉ đọc thông tin ảnh, không đọc dữ liwwuj
        options.inSampleSize = 2; //Scale bitmap xuống 7 lần
        options.inJustDecodeBounds = false; //Cho phép đọc dữ liệu ảnh ảnh
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }
    private String formatNumber(long value) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.format(value);
    }
}