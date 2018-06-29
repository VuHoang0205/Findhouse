package com.example.mua_mua_thu.maptest.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;

import com.example.mua_mua_thu.maptest.define.PlaceInterface;
import com.example.mua_mua_thu.maptest.define.PlaceInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class NhaTro implements Parcelable {

    private String idUser, maNhaTro, tenChuTro, soDT, moTa, dienTich, ngayDang;
    private List<String> tienIchList, hinhAnhList, luotThichList;
    private long giaPhong;
    private DiaChi diaChi;
    private boolean datPhong;
    private Date date;
    private List<BinhLuan> binhLuanList;
    private DatabaseReference nodeRoot;
    public static final String TAG = "kiem_tra";
    private List<Bitmap> bitmapList;
    private DataSnapshot dataRoot;

    public NhaTro() {
        nodeRoot = FirebaseDatabase.getInstance().getReference();

    }

    public NhaTro(String idUser, String maNhaTro, String tenChuTro, String soDT, String dienTich, List<String> tienIchList, List<String> luotThichList, long giaPhong, boolean datPhong, String moTa, String ngayDang) {
        this.idUser = idUser;
        this.maNhaTro = maNhaTro;
        this.tenChuTro = tenChuTro;
        this.soDT = soDT;
        this.dienTich = dienTich;
        this.tienIchList = tienIchList;
        this.luotThichList = luotThichList;
        this.giaPhong = giaPhong;
        this.datPhong = datPhong;
        this.moTa = moTa;
        this.ngayDang = ngayDang;
    }

    public NhaTro(String idUser, String maNhaTro, String tenChuTro, String soDT, String dienTich, List<String> tienIchList, long giaPhong, boolean datPhong, String moTa, String ngayDang) {
        this.idUser = idUser;
        this.maNhaTro = maNhaTro;
        this.tenChuTro = tenChuTro;
        this.soDT = soDT;
        this.dienTich = dienTich;
        this.tienIchList = tienIchList;
        this.giaPhong = giaPhong;
        this.datPhong = datPhong;
        this.moTa = moTa;
        this.ngayDang = ngayDang;
    }
    


    public NhaTro(String idUser, String maNhaTro, String tenChuTro, String soDT, String moTa, String dienTich, String ngayDang) {
        this.idUser = idUser;
        this.maNhaTro = maNhaTro;
        this.tenChuTro = tenChuTro;
        this.soDT = soDT;
        this.moTa = moTa;
        this.dienTich = dienTich;
        this.ngayDang = ngayDang;
    }

    protected NhaTro(Parcel in) {
        idUser = in.readString();
        maNhaTro = in.readString();
        tenChuTro = in.readString();
        soDT = in.readString();
        moTa = in.readString();
        dienTich = in.readString();
        ngayDang = in.readString();
        tienIchList = in.createStringArrayList();
        luotThichList = in.createStringArrayList();
        hinhAnhList = in.createStringArrayList();

        giaPhong = in.readLong();
        diaChi = in.readParcelable(DiaChi.class.getClassLoader());
        datPhong = in.readByte() != 0;
        binhLuanList = in.createTypedArrayList(BinhLuan.CREATOR);
        bitmapList = in.createTypedArrayList(Bitmap.CREATOR);
    }

    public static final Creator<NhaTro> CREATOR = new Creator<NhaTro>() {
        @Override
        public NhaTro createFromParcel(Parcel in) {
            return new NhaTro(in);
        }

        @Override
        public NhaTro[] newArray(int size) {
            return new NhaTro[size];
        }
    };


    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNgayDang() {
        return ngayDang;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setNgayDang(String ngayDang) {
        this.ngayDang = ngayDang;
    }

    public List<Bitmap> getBitmapList() {
        return bitmapList;
    }

    public void setBitmapList(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    public List<String> getLuotThichList() {
        return luotThichList;
    }

    public void setLuotThichList(List<String> luotThichList) {
        this.luotThichList = luotThichList;
    }

    public String getMaNhaTro() {
        return maNhaTro;
    }

    public void setMaNhaTro(String maNhaTro) {
        this.maNhaTro = maNhaTro;
    }

    public String getTenChuTro() {
        return tenChuTro;
    }

    public void setTenChuTro(String tenChuTro) {
        this.tenChuTro = tenChuTro;
    }

    public String getSoDT() {
        return soDT;
    }

    public void setSoDT(String soDT) {
        this.soDT = soDT;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public List<String> getTienIchList() {
        return tienIchList;
    }

    public void setTienIchList(List<String> tienIchList) {
        this.tienIchList = tienIchList;
    }

    public boolean isDatPhong() {
        return datPhong;
    }

    public void setDatPhong(boolean datPhong) {
        this.datPhong = datPhong;
    }

    public List<String> getHinhAnhList() {
        return hinhAnhList;
    }

    public void setHinhAnhList(List<String> hinhAnhList) {
        this.hinhAnhList = hinhAnhList;
    }

    public List<BinhLuan> getBinhLuanList() {
        return binhLuanList;
    }

    public void setBinhLuanList(List<BinhLuan> binhLuanList) {
        this.binhLuanList = binhLuanList;
    }

    public String getDienTich() {
        return dienTich;
    }

    public void setDienTich(String dienTich) {
        this.dienTich = dienTich;
    }

    public long getGiaPhong() {
        return giaPhong;
    }

    public void setGiaPhong(long giaPhong) {
        this.giaPhong = giaPhong;
    }

    public DiaChi getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(DiaChi diaChi) {
        this.diaChi = diaChi;
    }

    public void getListNhaTro(final PlaceInterface placeInterface,
                              final Location currentLocation, final int itemTiepTheo, final int itemDaCo) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataRoot = dataSnapshot;
                getDanhSachNhaTro(dataSnapshot, placeInterface, currentLocation, itemTiepTheo, itemDaCo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (dataRoot != null) {
            getDanhSachNhaTro(dataRoot, placeInterface, currentLocation, itemTiepTheo, itemDaCo);
        } else {
            nodeRoot.addListenerForSingleValueEvent(listener);
        }
    }

    private void getDanhSachNhaTro(DataSnapshot dataSnapshot,
                                   PlaceInterface placeInterface, Location currentLocation, int itemTiepTheo, int itemDaco) {
        int dem = 0;
        DataSnapshot dataNhaTro = dataSnapshot.child("nhatros");
        for (DataSnapshot valueNhaTro : dataNhaTro.getChildren()) {
            if (dem == itemTiepTheo) {
                break;
            }
            if (dem < itemDaco) {
                dem++;
                continue;
            }
            dem++;
            NhaTro nhaTro = valueNhaTro.getValue(NhaTro.class);
            nhaTro.setMaNhaTro(valueNhaTro.getKey());
            // Lay danh sach hinh anh quan an
            DataSnapshot dataHinhAnh = dataSnapshot.child("hinhanhs").child(valueNhaTro.getKey());
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
            Log.d(TAG, "onDataChange: " + viTri.getLongitude());
            double khoangCach = currentLocation.distanceTo(viTri) / 1000;
            diaChi.setKhoangCach(khoangCach);
            nhaTro.setDiaChi(diaChi);
            Log.d(TAG, "onDataChange: " + khoangCach);
            placeInterface.getListNhaTro(nhaTro);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idUser);
        dest.writeString(maNhaTro);
        dest.writeString(tenChuTro);
        dest.writeString(soDT);
        dest.writeString(moTa);
        dest.writeString(dienTich);
        dest.writeString(ngayDang);
        dest.writeStringList(tienIchList);
        dest.writeStringList(luotThichList);
        dest.writeStringList(hinhAnhList);
        dest.writeLong(giaPhong);
        dest.writeParcelable(diaChi, flags);
        dest.writeByte((byte) (datPhong ? 1 : 0));
        dest.writeTypedList(binhLuanList);
        dest.writeTypedList(bitmapList);
    }
}
