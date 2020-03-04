package com.example.hfund;

import android.util.Log;

public class ChitUser {
    private String name,address,id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        Log.d("ChitUser","getting : "+name);
        return name;
    }

    public void setName(String name) {
        Log.d("ChitUser","setting :"+name);
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }

    private Long mobile;
}
