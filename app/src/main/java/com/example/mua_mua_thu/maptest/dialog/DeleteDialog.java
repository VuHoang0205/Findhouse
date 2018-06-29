package com.example.mua_mua_thu.maptest.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.example.mua_mua_thu.maptest.view.HouseHistoryActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteDialog extends Dialog {
    private Context context;
    private TextView txtBoQua, txtDongY;
    private NhaTro nhaTro;

    public DeleteDialog(@NonNull Context context, NhaTro nhaTro) {
        super(context);
        this.context = context;
        this.nhaTro = nhaTro;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_delete);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        init();
    }

    private void init() {
        txtBoQua = findViewById(R.id.btn_bo_qua);
        txtDongY = findViewById(R.id.btn_dong_y);
        txtBoQua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        txtDongY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHouse(nhaTro);
            }
        });
    }

    private void deleteHouse(NhaTro nhaTro) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataNhaTro = reference.
                child("nhatros").child(nhaTro.getMaNhaTro());
        dataNhaTro.removeValue();
        DatabaseReference dataDiaChi = reference.child("diachinhatro").child(nhaTro.getMaNhaTro());
        dataDiaChi.removeValue();
        DatabaseReference dataHinhAnh = reference.child("hinhanhs").child(nhaTro.getMaNhaTro());
        dataHinhAnh.removeValue();
        if (nhaTro.getHinhAnhList() == null) {
            return;
        } else {
            for (int i = 0; i < nhaTro.getHinhAnhList().size(); i++) {
                String nameImg = nhaTro.getHinhAnhList().get(i);
                int s = nameImg.indexOf(".");
                String name = nameImg.substring(0, s);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                        .child("hinhanhs").child(name + ".jpg");
                storageReference.delete();
            }
        }
        context.startActivity(new Intent(context, HouseHistoryActivity.class));
        ((Activity) context).finish();
    }
}
