package com.example.daniel.chatroomapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.bottom;
import static android.R.attr.left;
import static android.R.attr.right;
import static android.R.attr.top;
import static java.security.AccessController.getContext;

public class Login extends AppCompatActivity {

    //region GLOBAL VARIABLES
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar proLogin;

    private String strEmail, strPassword, strResult, strUpdateUserID, strUpdateToken, strRegisteredEmail, strRegisteredPass;
    private ActiveUser auCurrentUser;

    private Bitmap bitDefaultProfileImage;
    private Bitmap bitProfileImage;

    private String strAuthenticationURL;
    private String strUpdateTokenURL;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //CLOSE SOFTKEYBOARD
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //region URL'S
        strAuthenticationURL = getString(R.string.AuthenticationURL);
        strUpdateTokenURL = getString(R.string.UpdateTokenURL);
        //endregion

        //region VIEW CASTING
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        proLogin = (ProgressBar)findViewById(R.id.proLogin);
        //endregion

        bitDefaultProfileImage = BitmapFactory.decodeResource(this.getResources(),R.drawable.anonymous);

        auCurrentUser = ActiveUser.getInstance();

        //region GET DETAILS OF NEWLY REGISTERED USER FROM REGISTER ACTIVITY
        Bundle intentBundle = getIntent().getExtras();

        if (intentBundle != null) {
            strRegisteredEmail = getIntent().getExtras().getString("username");
            strRegisteredPass = getIntent().getExtras().getString("password");
            etEmail.setText(strRegisteredEmail);
            etPassword.setText(strRegisteredPass);
        }
        //endregion

    }

    public void loginUser(View view) {

        strEmail = etEmail.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();

        if (strEmail.equals("") || strPassword.equals(null)){
            Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_LONG).show();
        }else{

            proLogin.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);

            //region GET TOKEN FROM SHARED PREFS
            SharedPreferences ChatPrefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
            final String strNewToken = ChatPrefs.getString(getString(R.string.FCM_TOKEN_PREF), "");
            //endregion

            StringRequest authenticationRequest = new StringRequest(Request.Method.POST, strAuthenticationURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                strResult = jsonObject.getString("result");

                                if (strResult.equals("1")) {

                                    //region PARSE RETURNED JSON
                                    //GET USER DETAILS FROM RETURNED JSON AND STORE IN USER SINGLETON CLASS
                                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonMessage = jsonArray.getJSONObject(i);

                                        if (jsonMessage != null) {

                                            //region GET USER DETAILS
                                            String strUserID = jsonMessage.getString("user_id");
                                            String strEmail = jsonMessage.getString("email");
                                            String strName = jsonMessage.getString("Username");
                                            String strBio = jsonMessage.getString("user_bio");
                                            String strProfileImageURL = jsonMessage.getString("profile_image_url");
                                            //endregion

                                            //region SET ACTIVE USER DETAILS
                                            auCurrentUser.setIsLoggedIn(true);
                                            auCurrentUser.setUserID(strUserID);
                                            auCurrentUser.setEmail(strEmail);
                                            auCurrentUser.setName(strName);
                                            auCurrentUser.setUsersFCMToken(strNewToken);
                                            auCurrentUser.setBio(strBio);
                                            auCurrentUser.setStrProfileImageURL(strProfileImageURL);
                                            //endregion

                                            //region GET USER PROFILE IMAGE
                                            if (strProfileImageURL.equals("not specified")){
                                                auCurrentUser.setUserProfileImage(bitDefaultProfileImage);
                                            }else{
                                                ImageRequest request = new ImageRequest(strProfileImageURL,
                                                        new Response.Listener<Bitmap>() {
                                                            @Override
                                                            public void onResponse(Bitmap bitmap) {
                                                                bitProfileImage = bitmap;
                                                                auCurrentUser.setUserProfileImage(bitProfileImage);
                                                            }
                                                        }, 0, 0, null,
                                                        new Response.ErrorListener() {
                                                            public void onErrorResponse(VolleyError error) {
                                                                auCurrentUser.setUserProfileImage(bitDefaultProfileImage);
                                                            }
                                                        });
                                                VolleyQueueSingleton.getmInstance(Login.this).addToRequestQueue(request);
                                            }
                                            //endregion

                                            //auCurrentUser.setStrProfileImageURL(strProfileImageURL);

                                            //STORE USE DETAILS SO DOESN'T HAVE TO LOG IN EVERY TIME
                                            StoreForReturn(true, strUserID, strEmail, strName, strNewToken, strBio, strProfileImageURL); //ADD BIO

                                            //UPDATE USERS TOKEN IN DATABASE
                                            UpdateToken(strUserID, strNewToken);

                                        }
                                    }
                                    //endregion

                                    //region SET VISUAL PROPERTIES
                                    proLogin.setVisibility(View.INVISIBLE);
                                    btnLogin.setText("Success");
                                    btnLogin.setBackgroundResource(R.drawable.round_button_success);
                                    btnLogin.setVisibility(View.VISIBLE);
                                    //endregion

                                    //region START CHAT ROOM GALLERY ACTIVITY
                                    Handler delayHandler = new Handler();
                                    delayHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(Login.this, ChatRoomGallery.class));
                                            finish();
                                        }
                                    },200);
                                    //endregion

                                }
                                else {
                                    proLogin.setVisibility(View.INVISIBLE);
                                    btnLogin.setVisibility(View.VISIBLE);

                                    //###########
                                    //region INVALID USER ALERT DIALOG
                                    ContextThemeWrapper ctw = new ContextThemeWrapper(Login.this, R.style.Theme_AppCompat_Dialog_Alert);

                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                                    alertBuilder.setTitle("Invalid Credentials")
                                            .setMessage("No users found who match the entered credentials")
                                            .setIcon(R.drawable.ic_action_warning)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });

                                    AlertDialog invalidUserAlert = alertBuilder.create();
                                    invalidUserAlert.show();
                                    //endregion
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    //region LOGIN ERROR DIALOG
                    ContextThemeWrapper ctw = new ContextThemeWrapper(Login.this, R.style.Theme_AppCompat_Dialog_Alert);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                    alertBuilder.setTitle("Error!")
                            .setMessage("There was an error whilst attempting to log in.\nIf this problem persists please contact the RoomChat team.")
                            .setIcon(R.drawable.ic_action_warning)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    AlertDialog invalidUserAlert = alertBuilder.create();
                    invalidUserAlert.show();
                    //endregion
                }
            })
            {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", strEmail);
                    params.put("password", strPassword);

                    return params;
                }
            };

            VolleyQueueSingleton.getmInstance(Login.this).addToRequestQueue(authenticationRequest);
        }
    }

    private void UpdateToken(String strUserID, String strToken) {

        strUpdateToken = strToken;
        strUpdateUserID = strUserID;

        //region UPDATE USERS FCM TOKEN
        StringRequest updateRequest = new StringRequest(Request.Method.POST, strUpdateTokenURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        response = response.trim();

                        if (response.equals("1")){

                        }else{
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", strUpdateUserID);
                params.put("new_token", strUpdateToken);

                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(Login.this).addToRequestQueue(updateRequest);
        //endregion
    }

    private void StoreForReturn(boolean isLogged, String strUserID, String strEmail, String strName, String strFCMToken, String strBio, String strProfileImageURL) {

        SharedPreferences ChatRoomPrefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
        SharedPreferences.Editor editor = ChatRoomPrefs.edit();
        editor.putBoolean(getString(R.string.isLogged), isLogged);
        editor.putString(getString(R.string.UserID), strUserID);
        editor.putString(getString(R.string.UserEmail), strEmail);
        editor.putString(getString(R.string.UserName), strName);
        editor.putString(getString(R.string.ActiveUserToken), strFCMToken);
        editor.putString(getString(R.string.UserBio), strBio);
        editor.putString(getString(R.string.profileImageURL), strProfileImageURL);
        editor.commit();
    }

    public void goToRegistration(View view) {

        startActivity(new Intent(this, RegisterUser.class));
    }

    public void forgottenPassword(View view){ startActivity(new Intent(this, ResetPassword.class));}

    public void resetPassword(){

        //region RESET PASS ALERT DIALOG
        ContextThemeWrapper ctw = new ContextThemeWrapper(Login.this, R.style.Theme_AppCompat_Dialog_Alert);

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);

        //SCALE FOR PIXEL DP CONVERSION
        final float scale = getResources().getDisplayMetrics().density;

        //region CONFIGURE EMAIL EDIT TEXT
        final EditText etAlertEmail = new EditText(this);

        int heightPixels = (int) (40 * scale + 0.5f);
        int marginPixes = (int) (15 * scale + 0.5f);
        int paddingPixes = (int) (10 * scale + 0.5f);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = marginPixes;
        params.leftMargin = marginPixes;
        params.rightMargin = marginPixes;

        etAlertEmail.setMaxLines(1);
        etAlertEmail.setSingleLine(true);
        etAlertEmail.setHeight(heightPixels);
        etAlertEmail.setHint("Email");
        etAlertEmail.setLayoutParams(params);
        etAlertEmail.setPadding(paddingPixes,0,0,0);
        etAlertEmail.setBackground(getDrawable(R.drawable.rounded_edittext));

        FrameLayout container = new FrameLayout(this);
        container.addView(etAlertEmail);
        //endregion

        //region CONFIGURE PROGRESS BAR
        final ProgressBar proLoading = new ProgressBar(this);

        int proTopMarginPixels = (int) (10 * scale + 0.5f);
        FrameLayout.LayoutParams progressParams = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = proTopMarginPixels;

        proLoading.setVisibility(View.INVISIBLE);
        proLoading.setLayoutParams(progressParams);
        //endregion

        //region CONFIGURE DIALOG LAYOUT
        FrameLayout.LayoutParams listParams = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(listParams);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(container);
        ll.addView(proLoading);
        //endregion

        alertBuilder.setTitle("Forgotten Password")
                .setMessage("Please enter your email address.")
                .setIcon(R.drawable.ic_action_warning)
                //.setView(container) //ADD EDIT TEXT
                .setView(ll)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String strEmail = etAlertEmail.getText().toString().trim();
                        if (!strEmail.equals("")){

                            //GET SECRET QUESTION AND ANSWER
                            etAlertEmail.setEnabled(false);
                            proLoading.setVisibility(View.VISIBLE);

                        }
                    }
                });

        AlertDialog invalidUserAlert = alertBuilder.create();
        invalidUserAlert.show();
        //endregion

    }
}
