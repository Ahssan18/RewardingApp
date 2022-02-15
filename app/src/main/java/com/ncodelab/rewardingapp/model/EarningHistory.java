package com.ncodelab.rewardingapp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class EarningHistory {

    private String earnedBy;
    private Long pointsEarned;


    public EarningHistory() {
    }

    public EarningHistory(String earnedBy, Long pointsEarned) {
        this.earnedBy = earnedBy;
        this.pointsEarned = pointsEarned;
    }

    public String getEarnedBy() {
        return earnedBy;
    }

    public void setEarnedBy(String earnedBy) {
        this.earnedBy = earnedBy;
    }

    public Long getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Long pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    @ServerTimestamp
    private Date earningDate;

    public Date getEarningDate() {
        return earningDate;
    }

    public void setEarningDate(Date earningDate) {
        this.earningDate = earningDate;
    }
}
