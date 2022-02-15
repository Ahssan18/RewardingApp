package com.ncodelab.rewardingapp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class WithdrawRequest {
    private String name;
    private String userId;
    private String paypalId;
    private long points;
    private String withdrawId;

    private String status = "Pending";


    public WithdrawRequest() {
    }

    public WithdrawRequest(String name, String userId, String paypalId, long points,String withdrawId) {
        this.name = name;
        this.userId = userId;
        this.paypalId = paypalId;
        this.points = points;
        this.withdrawId = withdrawId;
    }

    public WithdrawRequest(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaypalId() {
        return paypalId;
    }

    public void setPaypalId(String paypalId) {
        this.paypalId = paypalId;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public String getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(String withdrawId) {
        this.withdrawId = withdrawId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ServerTimestamp
    private Date requestedDate;

    public Date getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Date requestedDate) {
        this.requestedDate = requestedDate;
    }
}
