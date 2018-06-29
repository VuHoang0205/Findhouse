package com.example.mua_mua_thu.maptest.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.dialog.BaseProgess;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.example.mua_mua_thu.maptest.model.ThanhVien;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by S-O-S on 3/6/2018.
 */

public class RegisterActivity extends Activity implements FirebaseAuth.AuthStateListener, View.OnClickListener {
    private FirebaseAuth auth;
    private Button btnRegister, btnBack, btnResetPassword;
    private EditText editEmail, edtPassword, edtPassword2;
    private boolean checkEmail;
    private BaseProgess progess;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        initialize();
        registerlisner();

    }

    private void registerlisner() {
        btnRegister.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
    }

    private void createEmailPasword() {
        final String email = editEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        String password2 = edtPassword2.getText().toString();
        if (!password.equals(password2)) {
            edtPassword2.requestFocus();
            edtPassword2.setError("Mật khẩu không trùng nhau !");
            return;
        }
        if (edtPassword.getText().length() > 6) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final FirebaseUser user = auth.getCurrentUser();
                        user.sendEmailVerification();
                        final String name = user.getDisplayName();
                        final String idUser = user.getUid();
                        progess.showProgressDialog("Đang đăng ký");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (user != null) {
                                    NhaTro nhaTro = new NhaTro();
                                    nhaTro.setIdUser(idUser);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    ThanhVien thanhVien = new ThanhVien(name, email, Contants.AVATAR_USER);
                                    databaseReference.child("thanhviens").child(idUser).setValue(thanhVien);
                                }
                                progess.hideProgressDialog();
                                showAlertDialog();

//                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
//                                Account account = new Account();
//                                account.setEmail(email);
//                                account.setPassword(password);
//                                intent.putExtra(Key.ACCOUNT, account);
//                                startActivity(intent);
                            }
                        }, 2000);

                    } else {
                        editEmail.requestFocus();
                        editEmail.setError("Email đã tồn tại !");
                    }
                }
            });

        } else {
            editEmail.requestFocus();
            editEmail.setError(" Email không được để trống  !");
            edtPassword.setError("Mật khẩu không hợp lệ !");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(this);


    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);


    }

    private void initialize() {
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        FirebaseUser user = auth.getCurrentUser();
        btnBack = findViewById(R.id.btn_back_login);
        editEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnRegister = findViewById(R.id.btn_register_email);
        edtPassword2 = findViewById(R.id.edt_password_2);
        progess = new BaseProgess(this);
        btnResetPassword = findViewById(R.id.btn_reset_password);
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.d("kiem tra", "dang nhap thanh cong: ");
        } else {
            Log.d("kiem tra", "that bai: ");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back_login:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.btn_register_email:
                createEmailPasword();
                break;
            case R.id.btn_reset_password:
                resetPassword();
                break;
            default:
                break;
        }
    }

    private void resetPassword() {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Vui lòng xác minh Email trước khi đăng nhập");
        builder.setCancelable(false);
        builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}