package com.example.daniel.chatroomapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//##################################################################################################
//##
//##    SPLASH CLASS RESPONSIBLE FOR CHECKING IF A USER HAS ALREADY LOGGED INTO THE APPLICATION
//##    IF FOUND THE USERS DATA IS REPOPULATED AND THEY ARE LOGGED BACK IN
//##    THE USERS TOKEN IS ALSO UPDATED TO MAKE SURE THE LATEST TOKEN IS IN USE
//##
//##################################################################################################

public class Splash extends AppCompatActivity {


    //region GLOBAL VARIABLES
    private Boolean isLogged = false;
    private ActiveUser auCurrentUser;
    private String strUserID, strToken;
    //private static final String strUpdateTokenURL = "http://80.0.165.187/chatroomapp/update_token.php";
    private String strUpdateTokenURL;
    private TextView tvMessage;
    private ProgressBar progLoading;

    private Bitmap bitDefaultProfileImage;
    private Bitmap bitProfileImage;
    private String strProfileImageURL;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        strUpdateTokenURL = getString(R.string.UpdateTokenURL);

        bitDefaultProfileImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.anonymous);
        tvMessage = (TextView)findViewById(R.id.tvMessage);
        progLoading = (ProgressBar)findViewById(R.id.progLoading);

        //SHOW SPINNER AND TEXT VIEW SETTING UP CHAT ROOMS
        progLoading.setVisibility(View.VISIBLE);

        auCurrentUser = ActiveUser.getInstance();

        SharedPreferences ChatRoomPrefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
        isLogged = ChatRoomPrefs.getBoolean(getString(R.string.isLogged), false);

        //CHECK IF USER IS LOGGED IN
        if (isLogged){

            //GET USER DATA AND SET IN CLASS
            auCurrentUser.setIsLoggedIn(ChatRoomPrefs.getBoolean(getString(R.string.isLogged), false));
            auCurrentUser.setName(ChatRoomPrefs.getString(getString(R.string.UserName), ""));
            auCurrentUser.setEmail(ChatRoomPrefs.getString(getString(R.string.UserEmail), ""));
            auCurrentUser.setBio(ChatRoomPrefs.getString(getString(R.string.UserBio),"not specified"));
            strProfileImageURL = ChatRoomPrefs.getString(getString(R.string.profileImageURL), "not specified");


            //getimage or set efault
            if (strProfileImageURL.equals("not specified")){
                auCurrentUser.setUserProfileImage(bitDefaultProfileImage);
            }else{

                ImageRequest request = new ImageRequest(strProfileImageURL,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                auCurrentUser.setUserProfileImage(bitmap);
                                UpdateToken();
                            }
                        }, 0, 0, null,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                auCurrentUser.setUserProfileImage(bitDefaultProfileImage);
                            }
                        });
                // Access the RequestQueue through your singleton class.
                VolleyQueueSingleton.getmInstance(Splash.this).addToRequestQueue(request);
            }

            auCurrentUser.setStrProfileImageURL(strProfileImageURL);

            strUserID = ChatRoomPrefs.getString(getString(R.string.UserID), "");
            strToken = ChatRoomPrefs.getString(getString(R.string.ActiveUserToken), "");

            auCurrentUser.setUserID(strUserID);
            auCurrentUser.setUsersFCMToken(strToken);

//            //region UPDATE USERS FCM TOKEN
//            StringRequest updateRequest = new StringRequest(Request.Method.POST, strUpdateTokenURL,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            response = response.trim();
//
//                            if (response.equals("1")){
//                                //set to finalising settings
//                                tvMessage.setText("Finalising Settings");
//                                Handler delayHandler = new Handler();
//                                delayHandler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        startActivity(new Intent(Splash.this, ChatRoomGallery.class));
//                                        finish();
//                                    }
//                                },2000);
//                            }else{
//                                //set to internal error may have occured
//                                tvMessage.setText("Internal Error May Have Occured");
//                                Handler delayHandler = new Handler();
//                                delayHandler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        startActivity(new Intent(Splash.this, ChatRoomGallery.class));
//                                        finish();
//                                    }
//                                },2000);
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                    //CHECK FOR ERROR AND DISPLAY MESSAGE
//                    progLoading.setVisibility(View.INVISIBLE);
//                    //CHANGE TO DIALOG
//                    Toast.makeText(Splash.this, "THERE HAS BEEN AN ERROR" + error, Toast.LENGTH_LONG).show();
//                }
//            })
//            {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("user_id", strUserID);
//                    params.put("new_token", strToken);
//
//                    return params;
//                }
//            };
//
//            VolleyQueueSingleton.getmInstance(Splash.this).addToRequestQueue(updateRequest);
//            //endregion

        }else{
            //LOG IN NEW USER
            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this, Login.class));
                    finish();
                }
            },2000);

        }
    }

    private void UpdateToken(){
        //region UPDATE USERS FCM TOKEN
        StringRequest updateRequest = new StringRequest(Request.Method.POST, strUpdateTokenURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        response = response.trim();

                        if (response.equals("1")){
                            //set to finalising settings
                            tvMessage.setText("Finalising Settings");
                            Handler delayHandler = new Handler();
                            delayHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(Splash.this, ChatRoomGallery.class));
                                    finish();
                                }
                            },500);
                        }else{
                            //set to internal error may have occured
                            tvMessage.setText("Internal Error May Have Occured");
                            Handler delayHandler = new Handler();
                            delayHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(Splash.this, ChatRoomGallery.class));
                                    finish();
                                }
                            },2000);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //CHECK FOR ERROR AND DISPLAY MESSAGE
                progLoading.setVisibility(View.INVISIBLE);
                //CHANGE TO DIALOG
                Toast.makeText(Splash.this, "THERE HAS BEEN AN ERROR" + error, Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", strUserID);
                params.put("new_token", strToken);

                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(Splash.this).addToRequestQueue(updateRequest);
        //endregion
    }
}