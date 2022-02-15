package com.ncodelab.rewardingapp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {

    private String name;
    private String email;
    private String phone;
    private String password;
    private String referCode;
    private String referredBy;
    private String signInBy;

    private long userPoints = 0;

    public User() {
    }

    public User(String name, String email, String phone, String password, String referCode, String referredBy, String signInBy) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.referCode = referCode;
        this.referredBy = referredBy;
        this.signInBy = signInBy;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    public String getSignInBy() {
        return signInBy;
    }

    public void setSignInBy(String signInBy) {
        this.signInBy = signInBy;
    }

    public long getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(long userPoints) {
        this.userPoints = userPoints;
    }

    @ServerTimestamp
    private Date lastLogin;

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}
