package com.mli.crown.tytyhelper.bean;

/**
 * Created by mli on 2017/4/17.
 */

public class SimpleLoginInfo {

    private String mUsername;

    private String mPasswrod;

    private String mDesc;

    public SimpleLoginInfo(String username, String password, String desc) {
        this.mUsername = username == null ? "" : username;
        this.mPasswrod = password == null ? "" : password;
        this.mDesc = desc == null ? "" : desc;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getPasswrod() {
        return mPasswrod;
    }

    public void setPasswrod(String password) {
        this.mPasswrod = password;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setmDesc(String desc) {
        this.mDesc = desc;
    }
}
