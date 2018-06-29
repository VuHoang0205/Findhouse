package com.example.mua_mua_thu.maptest.utils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class IntanceTokenID extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String idToken = FirebaseInstanceId.getInstance().getToken();
    }
}
