package com.example.mua_mua_thu.maptest.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.dialog.BaseProgess;
import com.example.mua_mua_thu.maptest.model.DiaChi;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.dialog.BaseProgess;
import com.example.mua_mua_thu.maptest.fragments.PlaceFragment;
import com.example.mua_mua_thu.maptest.model.DiaChi;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class RegisterHouseActivity extends AppCompatActivity
        implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private EditText edtTenNhaTro, edtSDT, edtDiaChi, edtMoTa, edtGiaTro, edtDienTich;
    private String idUser, email, name;
    private Button btnCamera, btnGallery, btnRegister;
    private ImageView imgAnhNhaTro1, imgAnhNhaTro2, imgAnhNhaTro3, imgAnhNhaTro4, ivBack, ivLocation;
    private CheckBox checkWifi, checkDieuHoa, checkBNongLanh;
    private List<String> idHouseList, nameImageList;
    private SharedPreferences sharedPreferences;
    public static final int REQUEST_CODE_CAMERA = 10;
    public static final int REQUEST_CODE_PERMISSION = 101;
    public static final int REQUEST_CODE_GALLERY = 100;
    private GoogleApiClient googleApiClient;
    private BaseProgess baseProgess;
    private List<String> tienIchList;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private int dem = 0;
    private List<Bitmap> bitmapList;
    private GoogleMap googleMap;
    private double latitude = 0;
    private double longitude = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_house);
        initializeComponents();
        registerlisner();
    }


    private boolean checkPermissionGranted() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (isPermissionGranted(Manifest.permission.CAMERA)) {
                return true;
            }
            return false;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isPermissionGranted(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                    //TODO
                } else {
                    Toast.makeText(this, "Bạn cần phải cấp quyền cho ứng dụng", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initializeComponents() {
        idHouseList = new ArrayList<>();
        nameImageList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
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
        ivBack = findViewById(R.id.iv_back_register_house);
        baseProgess = new BaseProgess(this);
        tienIchList = new ArrayList<>();
        bitmapList = new ArrayList<>();
        imgAnhNhaTro1 = findViewById(R.id.iv_anh_nha_tro_1);
        imgAnhNhaTro2 = findViewById(R.id.iv_anh_nha_tro_2);
        imgAnhNhaTro3 = findViewById(R.id.iv_anh_nha_tro_3);
        imgAnhNhaTro4 = findViewById(R.id.iv_anh_nha_tro_4);
        ivLocation = findViewById(R.id.iv_location);
        MapFragment mapFragment = (MapFragment) getFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        sharedPreferences = getSharedPreferences(Contants.USERS, MODE_PRIVATE);
        idUser = sharedPreferences.getString(Contants.ID_USER, "0");
        email = sharedPreferences.getString(Contants.EMAIL, "0");
        name = sharedPreferences.getString(Contants.NAME, "0");
        sharedPreferences = this.getSharedPreferences("register", Context.MODE_PRIVATE);
        edtGiaTro.addTextChangedListener(onTextChangedListener());
    }


    private void registerlisner() {
        Log.d("", "registerlisner: " + bitmapList);
        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMyLocation();
            }
        });
    }

    private void setMyLocation() {
        SharedPreferences sharedPreferences = getSharedPreferences("toado", MODE_PRIVATE);
        latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        longitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
        LatLng myLocation = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions();
        marker.position(myLocation);
        marker.title("Vị trí của bạn");
        googleMap.clear();
        googleMap.addMarker(marker);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 15);
        googleMap.moveCamera(cameraUpdate);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initPermissions();
        } else {
            openCamera();
        }

    }

    private void openCamera() {
        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(iCamera, REQUEST_CODE_CAMERA);
    }

    private void initPermissions() {
        boolean granted = checkPermissionGranted();
        if (granted) {
            openCamera();
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
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
//
//                    Bundle extras = data.getExtras();
//                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Bitmap bitmapIMG = resizeBitmap(bitmap);
                    bitmapList.add(bitmapIMG);
                    setImage(dem, bitmapIMG);
                }
                break;
            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    try {
                        dem++;
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        //Todo bitmap
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

    @SuppressLint("MissingPermission")
    private void registerHouse() {
        Log.d("", "registerHouse: " + bitmapList);
        String nameImage = randomNameImg();
        String keyImage = randomNameImg();
        googleApiClient.connect();
        //todo
        getDrawbleImage();
        final Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm 'at' dd.MM.yyyy");
        final String timeDate = dateFormat.format(date);
        Log.d("date", "registerHouse: " + timeDate);
        if (checkWifi.isChecked()) {
            String wifi = (String) checkWifi.getText();
            Log.d("checkbox", "registerHouse: " + wifi);
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
        final String idHouse = UUID.randomUUID().toString();
        idHouseList.add(idHouse);
        Log.d("vu", "registerHouse: " + idHouse);
        final String tenNhaTro = edtTenNhaTro.getText().toString();
        final String sdt = edtSDT.getText().toString();
        final String diaChi = edtDiaChi.getText().toString();
        final String fullAddress = getAddress(latitude, longitude);
        final String giaPhong = edtGiaTro.getText().toString().replace(".", "");
        ;
        final String dienTich = edtDienTich.getText().toString();
        final String moTa = edtMoTa.getText().toString();
        final List<String> luotThichList = new ArrayList<>();
        if (longitude == 0 || longitude == 0) {
            showDialog();
            return;
        }
        checkFormRegister(tenNhaTro, sdt, diaChi, giaPhong, dienTich, moTa);
        if (isVailRegister(tenNhaTro, sdt, diaChi, giaPhong, dienTich, moTa)) {
            baseProgess.showProgressDialog("Đang đăng ký vui lòng đợi");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    latitude = (Double.parseDouble(sharedPreferences.getString("latitude", "0")));
//                    longitude = (Double.parseDouble(sharedPreferences.getString("longitude", "0")));
                    Log.d("share", "registerHouse: " + latitude);
                    NhaTro nhaTro = new NhaTro(idUser, idHouse, tenNhaTro, sdt, dienTich, tienIchList, Long.parseLong(giaPhong), true, moTa, timeDate);
                    DiaChi diaChiNhaTro = new DiaChi(diaChi, fullAddress, latitude, longitude);
                    //todo
                    for (int i = 0; i < nameImageList.size(); i++) {
                        String name = nameImageList.get(i);
                        databaseReference.child("hinhanhs").child(nhaTro.getMaNhaTro()).child(name).setValue(name + ".jpg");
                    }
                    databaseReference.child("nhatros").child(idHouse).setValue(nhaTro);
                    databaseReference.child("diachinhatro").child(idHouse).setValue(diaChiNhaTro);
                    baseProgess.hideProgressDialog();
                    Toast.makeText(RegisterHouseActivity.this, "Bạn đã đăng ký nhà trọ thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterHouseActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }, 1500);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        @SuppressLint("MissingPermission")
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (currentLocation != null) {
            sharedPreferences = getSharedPreferences("register", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("latitude", currentLocation.getLatitude() + "");
            editor.putString("longitude", currentLocation.getLongitude() + "");
            editor.commit();
            Log.d("muamuathu28", "onConnected: " + currentLocation.getLatitude());
            Log.d("muamuathu28", "onConnected: " + currentLocation.getLongitude());
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    private void setImage(int dem, Bitmap bitmap) {
        switch (dem) {
            case 1:
                imgAnhNhaTro1.setImageBitmap(bitmap);
                break;
            case 2:
                imgAnhNhaTro2.setImageBitmap(bitmap);
                break;
            case 3:
                imgAnhNhaTro3.setImageBitmap(bitmap);
                break;
            case 4:
                imgAnhNhaTro4.setImageBitmap(bitmap);
                break;
            default:
                break;
        }
    }

    private String randomNameImg() {
        Random random = new Random();
        String name = random.nextInt(100) + "" + random.nextInt(100) + ""
                + random.nextInt(100) + "" + random.nextInt(100) + "" + random.nextInt(100);
        return name;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterHouseActivity.this, HomeActivity.class));
        finish();
    }

    public Bitmap resizeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //Chỉ đọc thông tin ảnh, không đọc dữ liwwuj
        options.inSampleSize = 4; //Scale bitmap xuống 7 lần
        options.inJustDecodeBounds = false; //Cho phép đọc dữ liệu ảnh ảnh
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                googleMap.moveCamera(cameraUpdate);
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Thông báo");
        dialog.setCancelable(false);
        dialog.setMessage("Vui lòng chọn vị trí trước khi đăng ký");
        dialog.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.create();
        dialog.show();
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edtGiaTro.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    Locale locale = new Locale("vi", "VN");
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(locale);
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    edtGiaTro.setText(formattedString);
                    edtGiaTro.setSelection(edtGiaTro.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                edtGiaTro.addTextChangedListener(this);
            }
        };
    }

    public String getAddress(double latitude, double longitude) {
        String address = " ";
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList.size() > 0) {
                Address address1 = addressList.get(0);
                address = address1.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}
