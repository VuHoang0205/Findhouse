package com.example.mua_mua_thu.maptest.dialog;

import android.content.Context;

/**
 * Created by Mua_Mua_Thu on 3/14/2018.
 */

public class BaseProgess {
    private Context context;
    public android.app.ProgressDialog mProgressDialog;

    public BaseProgess(Context context) {
        this.context = context;
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new android.app.ProgressDialog(context);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();

        }
    }
}
