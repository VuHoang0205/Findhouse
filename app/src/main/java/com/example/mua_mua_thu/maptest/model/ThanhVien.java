package com.example.mua_mua_thu.maptest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ThanhVien implements Parcelable {
    private String maThanhVien, hoTen, eMail, hinhAnh, ngaySinh, diaChi, soDT;
    private String gioiTinh;


    public ThanhVien() {

    }

    public ThanhVien(String eMail, String hoTen, String diaChi, String soDT, String ngaySinh, String gioiTinh, String hinhAnh) {
        this.eMail = eMail;
        this.hoTen = hoTen;
        this.diaChi = diaChi;
        this.soDT = soDT;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.hinhAnh = hinhAnh;
    }

    public ThanhVien(String hoTen, String eMail, String hinhAnh) {
        this.hoTen = hoTen;
        this.eMail = eMail;
        this.hinhAnh = hinhAnh;

    }


    protected ThanhVien(Parcel in) {
        hoTen = in.readString();
        hinhAnh = in.readString();
        maThanhVien = in.readString();
        eMail = in.readString();
        ngaySinh = in.readString();
        diaChi = in.readString();
        soDT = in.readString();
        gioiTinh = in.readString();

    }

    public static final Creator<ThanhVien> CREATOR = new Creator<ThanhVien>() {
        @Override
        public ThanhVien createFromParcel(Parcel in) {
            return new ThanhVien(in);
        }

        @Override
        public ThanhVien[] newArray(int size) {
            return new ThanhVien[size];
        }
    };

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getMaThanhVien() {
        return maThanhVien;
    }

    public void setMaThanhVien(String maThanhVien) {
        this.maThanhVien = maThanhVien;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDT() {
        return soDT;
    }

    public void setSoDT(String soDT) {
        this.soDT = soDT;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hoTen);
        dest.writeString(hinhAnh);
        dest.writeString(maThanhVien);
        dest.writeString(eMail);
        dest.writeString(ngaySinh);
        dest.writeString(diaChi);
        dest.writeString(soDT);
        dest.writeString(gioiTinh);
    }
}
