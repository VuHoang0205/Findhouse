package com.example.mua_mua_thu.maptest.model;

import java.io.Serializable;

/**
 * Created by S-O-S on 3/6/2018.
 */

public class Account implements Serializable {
    private String email;
    private String password;
    private String name;
    public Account(){

    }

    public Account(String name,String email) {
        this.name=name;
        this.email=email;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
