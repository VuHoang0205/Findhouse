package com.example.mua_mua_thu.maptest.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.contants.Key;
import com.example.mua_mua_thu.maptest.define.LogoutInterface;
import com.example.mua_mua_thu.maptest.dialog.BaseProgess;
import com.example.mua_mua_thu.maptest.model.Account;
import com.example.mua_mua_thu.maptest.model.Account;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.example.mua_mua_thu.maptest.model.ThanhVien;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        FirebaseAuth.AuthStateListener, GoogleApiClient.OnConnectionFailedListener {
    public static String TAG = "kiem tra";
    private EditText editEmail, edtPassword;
    private Button btnLogin;
    private LoginButton btnLoginFB;
    private Button sinInGG;
    private TextView txtRegister, txtLostpassword;
    private FirebaseAuth mAuth;
    public static final int REQUEST_CODE_GOOGLE = 1000;
    private CallbackManager callbackManager;
    public static int LOGIN_PROVIDER = 0;
    private boolean temp;
    BaseProgess baseProgess;
    private int keyLogin;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private GoogleApiClient mApiClient;
    private int keyLogout = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.login_activity);
        initialize();
        registerlisner();

    }


    private void registerlisner() {
        btnLogin.setOnClickListener(this);
        sinInGG.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
        txtLostpassword.setOnClickListener(this);


    }

    private void initialize() {
        SharedPreferences sharedPreferences = getSharedPreferences(Contants.LOGIN, MODE_PRIVATE);
        keyLogin = sharedPreferences.getInt(Contants.KEY_LOGIN, 0);
        baseProgess = new BaseProgess(this);
        mAuth = FirebaseAuth.getInstance();
        editEmail = findViewById(R.id.tv_username);
        edtPassword = findViewById(R.id.tv_password);
        btnLogin = findViewById(R.id.btn_login);
        btnLoginFB = findViewById(R.id.btn_login_facebook);
        sinInGG = findViewById(R.id.btn_google_login);
        txtRegister = findViewById(R.id.tv_register);
        txtLostpassword = findViewById(R.id.tv_lost_pass);


        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        Intent intent = getIntent();
        if (intent != null) {
            keyLogout = intent.getIntExtra("logout", 0);
        }
        if (keyLogout == 100) {
            mApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            logout();
        }

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //Todo login Facebook
        callbackManager = CallbackManager.Factory.create();
        btnLoginFB.setReadPermissions("email", "public_profile");
        btnLoginFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LOGIN_PROVIDER = 2;
                String tokenId = loginResult.getAccessToken().getToken();
                checkLoginProvider(tokenId);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
//Todo chỗ này mình đã sửa có thể gây lỗi
//        Intent intent = getIntent();
//        Account account = (Account) intent.getSerializableExtra(Key.ACCOUNT);
//        if (account != null) {
//            String email = account.getEmail();
//            String password = account.getPassword();
//            editEmail.setText(email);
//            edtPassword.setText(password);
//            Log.d("kiem tra", "loginAccount: " + email);
//            Log.d("kiem tra", "loginAccount: " + password);
//
//        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                loginAccount();
                break;
            case R.id.btn_google_login:
                loginGoogle();
                break;
            case R.id.tv_register:
                showScreenRegister();
                break;
            case R.id.tv_lost_pass:
                resetPassword();
                break;
            default:
                break;

        }

    }

    private void loginGoogle() {
        LOGIN_PROVIDER = 1;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE);
    }

    private void resetPassword() {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void showScreenRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    private void loginAccount() {
        keyLogin = 1;
        baseProgess.showProgressDialog("Đang đăng nhập");
        final String email = editEmail.getText().toString().trim();
        final String passWord = edtPassword.getText().toString().trim();
        if (email.isEmpty() && passWord.isEmpty()) {
            editEmail.requestFocus();
            baseProgess.hideProgressDialog();
            editEmail.setError("Vui lòng điền đủ thông tin !");
        } else {
            checkEmalIsVaild(email, passWord);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth != null) {
            mAuth.addAuthStateListener(this);
        }
    }

    @Override
    protected void onStop() {
        if (mAuth != null) {
            mAuth.removeAuthStateListener(this);
        }
        super.onStop();
    }

    private void checkEmalIsVaild(final String email, final String passWord) {
        mAuth.signInWithEmailAndPassword(email, passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    baseProgess.hideProgressDialog();
                    Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không đúng ", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    baseProgess.hideProgressDialog();
                    //Todo
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GOOGLE) {
            baseProgess.showProgressDialog("Đang đăng nhập");
            if (resultCode == Activity.RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount account = result.getSignInAccount();
                String tokenID = account.getIdToken();
                checkLoginProvider(tokenID);

            }
        } else {
            baseProgess.showProgressDialog("Đang đăng nhập");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkLoginProvider(String tokenId) {
        AuthCredential credential = null;
        switch (LOGIN_PROVIDER) {
            case 1:
                credential = GoogleAuthProvider.getCredential(tokenId, null);
                keyLogin = 2;

                //mAuth.signInWithCredential(credential);
                break;
            case 2:
                temp = true;
                credential = FacebookAuthProvider.getCredential(tokenId);

                break;

        }
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {


            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    showAlertDialog();
                    baseProgess.hideProgressDialog();
                }
            }
        });
    }

    private void changeSreenHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(LoginActivity.this, "Đăng nhập thành công ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            baseProgess.hideProgressDialog();
            Log.d(TAG, "onAuthStateChanged: " + user);
            if (user.isEmailVerified() || temp == true) {
                if (keyLogin == 2) {
                    final String name = user.getDisplayName();
                    final String idUser = user.getUid();
                    String email = user.getEmail();
                    NhaTro nhaTro = new NhaTro();
                    nhaTro.setIdUser(idUser);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    ThanhVien thanhVien = new ThanhVien(name, email, Contants.AVATAR_USER);
                    databaseReference.child("thanhviens").child(idUser).setValue(thanhVien);
                }
                getInformationUser(user);
                changeSreenHome();
            } else {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                baseProgess.hideProgressDialog();
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Thông báo");
                builder.setMessage(getResources().getString(R.string.verifiation));
                builder.setIcon(R.drawable.my_logo);
                builder.create().show();
            }
        }
    }

    private void getInformationUser(FirebaseUser user) {
        SharedPreferences sharedPreferences = getSharedPreferences(Contants.USERS, MODE_PRIVATE);
        String idUser = user.getUid();
        String email = user.getEmail();
        String name = user.getDisplayName();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Contants.ID_USER, idUser);
        editor.putString(Contants.NAME, name);
        editor.putString(Contants.EMAIL, email);
        editor.commit();
        SharedPreferences sharedPreferencesKey = getSharedPreferences(Contants.LOGIN, MODE_PRIVATE);
        SharedPreferences.Editor editorKey = sharedPreferencesKey.edit();
        editorKey.putInt(Contants.KEY_LOGIN, keyLogin);
        Log.d(TAG, "getInformationUser: " + keyLogin);
        editorKey.commit();
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Email bạn đã dùng để đăng ký vui lòng thử đăng nhập Gmail khác");
        builder.setCancelable(false);
        builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void logout() {
        mApiClient.connect();
        mApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                mAuth.signOut();
                if (mApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {

                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d(TAG, "Google API Client Connection Suspended");
            }
        });
    }

}