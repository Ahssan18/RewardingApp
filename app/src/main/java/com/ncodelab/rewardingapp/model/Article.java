package com.ncodelab.rewardingapp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Article {

    String articleTitle, articleImageUrl,articleDescription,url;


    public Article() {
    }

    public Article(String articleTitle, String articleImage, String articleDescription, String url) {
        this.articleTitle = articleTitle;
        this.articleImageUrl = articleImage;
        this.articleDescription = articleDescription;
        this.url = url;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleImageUrl() {
        return articleImageUrl;
    }

    public String getArticleDescription() {
        return articleDescription;
    }

    public String getUrl() {
        return url;
    }

    @ServerTimestamp
    private Date uploadTime;

    public Date getUploadTime() {
        return uploadTime;
    }

}
