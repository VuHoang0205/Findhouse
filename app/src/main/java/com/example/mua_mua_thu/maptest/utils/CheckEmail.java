package com.example.mua_mua_thu.maptest.utils;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by S-O-S on 3/6/2018.
 */

public class CheckEmail {
    public boolean isEmpty(EditText editText) {
        if (editText.getText().toString().trim().length() > 0) {
            return true;
        } else {
            editText.requestFocus();
            editText.setError("Vui lòng nhập đủ thông tin!");
            return false;
        }
    }

    public boolean isEmailVail(String email) {
        boolean isVail = false;
        String expression = "[a-zA-Z0-9._-]+@[a-z]+(\\.+[a-z]+)+";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isVail = true;
        }
        return isVail;

    }
}
