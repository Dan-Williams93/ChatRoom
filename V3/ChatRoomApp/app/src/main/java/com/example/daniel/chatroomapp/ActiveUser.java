package com.example.daniel.chatroomapp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Daniel on 02/12/2016.
 */

public class ActiveUser {

    private Boolean isLoggedIn = false;
    private String strID = "";
    private String strName = "";
    private String strEmail = "";
    private String strFCM_Token = "";
    private String strBio = "";
    private String strProfileImageURL = "";
    private Bitmap bitUserProfileImage;

    //ChatRoom ID's

    private static ActiveUser mActiveUser;

    private ActiveUser(){}

    public static synchronized ActiveUser getInstance(){
        if (mActiveUser == null)
            mActiveUser = new ActiveUser();
        return mActiveUser;
    }

    //region GETTERS

    public Boolean getIsLoggedIn(){return isLoggedIn;}

    public String getUserID(){return strID;}

    public String getName(){return strName;}

    public String getEmail(){return strEmail;}

    public String getBio(){return  strBio;}

    public String getStrProfileImageURL(){return strProfileImageURL;}

    public Bitmap getUserProfileImage(){return bitUserProfileImage;}

    public String getUsersFCMToken(){return strFCM_Token;}
    //endregion

    //region SETTERS
    public void setIsLoggedIn(Boolean isLoggedIn){this.isLoggedIn = isLoggedIn;}

    public void setUserID(String strID){this.strID = strID;}

    public void setName(String strName){this.strName = strName;}

    public void setEmail(String strEmail){this.strEmail = strEmail;}

    public void setBio(String strBio){this.strBio = strBio;}

    public void setStrProfileImageURL(String strProfileImageURL){this.strProfileImageURL = strProfileImageURL;}

    public void setUserProfileImage(Bitmap bitUserProfileImage){this.bitUserProfileImage = bitUserProfileImage;}

    public void setUsersFCMToken (String strFCM_Token){this.strFCM_Token = strFCM_Token;}
    //endregion

    public void userLogOut(){
        this.isLoggedIn = false;
        this.strID = "";
        this.strName = "";
        this.strEmail = "";
        this.strFCM_Token = "";
        this.strBio = "";
        this.strProfileImageURL = "";
        this.bitUserProfileImage = null;
    }

}
