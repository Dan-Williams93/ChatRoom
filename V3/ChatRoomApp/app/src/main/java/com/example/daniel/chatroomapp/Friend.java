package com.example.daniel.chatroomapp;

/**
 * Created by Daniel on 17/12/2016.
 */

public class Friend {

    private String strUserID;
    private String strName;
    private String strEmail;
    private String strBio;
    private String strImageURL;

    public Friend(String strUserID, String strName, String strEmail, String strBio, String strImageURL) {
        this.strUserID = strUserID;
        this.strName = strName;
        this.strEmail = strEmail;
        this.strBio = strBio;
        this.strImageURL = strImageURL;
    }

    public String getFriendUsersId() {
        return strUserID;
    }

    public String getFriendName() {
        return strName;
    }

    public String getFriendEmail() {
        return strEmail;
    }

    public String getFriendBio() { return strBio; }

    public String getFriendImageURL() { return strImageURL; }
}
