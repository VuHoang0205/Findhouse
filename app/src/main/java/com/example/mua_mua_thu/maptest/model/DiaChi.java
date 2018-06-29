package com.example.mua_mua_thu.maptest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.mua_mua_thu.maptest.define.AddressInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DiaChi implements Parcelable {
    private String tenDiaChi,fullTenDiaChi;
    private double latitude;
    private double longitude;
    private double khoangCach;
    private DatabaseReference databaseReference;

    public DiaChi() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public DiaChi(String tenDiaChi, double latitude, double longitude) {
        this.tenDiaChi = tenDiaChi;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public DiaChi(String tenDiaChi, String fullTenDiaChi, double latitude, double longitude) {
        this.tenDiaChi = tenDiaChi;
        this.fullTenDiaChi = fullTenDiaChi;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    protected DiaChi(Parcel in) {
        tenDiaChi = in.readString();
        fullTenDiaChi=in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        khoangCach = in.readDouble();
    }

    public static final Creator<DiaChi> CREATOR = new Creator<DiaChi>() {
        @Override
        public DiaChi createFromParcel(Parcel in) {
            return new DiaChi(in);
        }

        @Override
        public DiaChi[] newArray(int size) {
            return new DiaChi[size];
        }
    };

    public String getFullTenDiaChi() {
        return fullTenDiaChi;
    }

    public void setFullTenDiaChi(String fullTenDiaChi) {
        this.fullTenDiaChi = fullTenDiaChi;
    }

    public String getTenDiaChi() {
        return tenDiaChi;
    }

    public void setTenDiaChi(String tenDiaChi) {
        this.tenDiaChi = tenDiaChi;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getKhoangCach() {
        return khoangCach;
    }

    public void setKhoangCach(double khoangCach) {
        this.khoangCach = khoangCach;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tenDiaChi);
        dest.writeString(fullTenDiaChi);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(khoangCach);
    }

    public void getListDiaChi(final AddressInterface addressInterface) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dataDiaChi = dataSnapshot.child("diachinhatro");
                for (DataSnapshot snapshot : dataDiaChi.getChildren()) {
                    DiaChi diaChi = snapshot.getValue(DiaChi.class);
                    addressInterface.getListDiaChi(diaChi);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
