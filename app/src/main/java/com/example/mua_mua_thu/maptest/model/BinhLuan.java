package com.example.mua_mua_thu.maptest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.define.CommentInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

public class BinhLuan implements Parcelable {
    private String commentId;
    private long chamdiem;
    private String email;
    private String tieude, noidung, mauser, maBinhLuan;
    public ThanhVien thanhVien;
    private List<String> imgBinhLuans;
    private String date;
    private DatabaseReference databaseReference;

    public BinhLuan() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public BinhLuan(String commentId, long chamdiem, String tieude, String noidung, String mauser, String date, String email) {
        this.commentId = commentId;
        this.chamdiem = chamdiem;
        this.tieude = tieude;
        this.noidung = noidung;
        this.mauser = mauser;
        this.date = date;
        this.email = email;
    }

    protected BinhLuan(Parcel in) {
        commentId = in.readString();
        email = in.readString();
        chamdiem = in.readLong();
        tieude = in.readString();
        noidung = in.readString();
        mauser = in.readString();
        maBinhLuan = in.readString();
        imgBinhLuans = in.createStringArrayList();
        thanhVien = in.readParcelable(ThanhVien.class.getClassLoader());
    }

    public static final Creator<BinhLuan> CREATOR = new Creator<BinhLuan>() {
        @Override
        public BinhLuan createFromParcel(Parcel in) {
            return new BinhLuan(in);
        }

        @Override
        public BinhLuan[] newArray(int size) {
            return new BinhLuan[size];
        }
    };

    public long getChamdiem() {
        return chamdiem;
    }

    public void setChamdiem(long chamdiem) {
        this.chamdiem = chamdiem;
    }


    public String getTieude() {
        return tieude;
    }

    public void setTieude(String tieude) {
        this.tieude = tieude;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }

    public ThanhVien getThanhVien() {
        return thanhVien;
    }

    public void setThanhVien(ThanhVien thanhVien) {
        this.thanhVien = thanhVien;
    }

    public String getMauser() {
        return mauser;
    }

    public void setMauser(String mauser) {
        this.mauser = mauser;
    }

    public List<String> getImgBinhLuans() {
        return imgBinhLuans;
    }

    public void setImgBinhLuans(List<String> imgBinhLuans) {
        this.imgBinhLuans = imgBinhLuans;
    }

    public String getMaBinhLuan() {
        return maBinhLuan;
    }

    public void setMaBinhLuan(String maBinhLuan) {
        this.maBinhLuan = maBinhLuan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(commentId);
        dest.writeString(email);
        dest.writeLong(chamdiem);
        dest.writeString(tieude);
        dest.writeString(noidung);
        dest.writeString(mauser);
        dest.writeString(maBinhLuan);
        dest.writeStringList(imgBinhLuans);
        dest.writeParcelable(thanhVien, flags);
    }

    public void getCommentList(final CommentInterface commentListener, final String houseId) {


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dataComments = dataSnapshot.child(Contants.COMMENTS).child(houseId);
                for (DataSnapshot valueComment : dataComments.getChildren()) {
                    BinhLuan binhLuan = valueComment.getValue(BinhLuan.class);
                    binhLuan.setCommentId(valueComment.getKey());
                    ThanhVien thanhVien = dataSnapshot.child(Contants.THANH_VIEN).child(binhLuan.mauser).getValue(ThanhVien.class);
                    binhLuan.setThanhVien(thanhVien);
                    commentListener.getCommentList(binhLuan);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
