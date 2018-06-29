package com.example.mua_mua_thu.maptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.define.DeleteIMGInterface;
import com.example.mua_mua_thu.maptest.model.NhaTro;

public class DialogDeleteImg extends Dialog {
    private Context context;
    private TextView txtBoQua, txtDongY;
    private NhaTro nhaTro;
    private ImageView imageView;
    private int position;
    private DeleteIMGInterface deleteIMGInterface;

    public DialogDeleteImg(@NonNull Context context, ImageView imageView, int position) {
        super(context);
        this.context = context;
        this.imageView = imageView;
        this.position = position;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_delete_img);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteIMGInterface = (DeleteIMGInterface) context;
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
                deleteIMGInterface.deleteIMGHouse(true, imageView, position);
            }
        });

    }
}
