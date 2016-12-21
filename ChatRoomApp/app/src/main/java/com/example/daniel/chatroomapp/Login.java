package com.example.daniel.chatroomapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar proLogin;

    private String strEmail, strPassword, strResult, strUpdateUserID, strUpdateToken, strRegisteredEmail, strRegisteredPass;
    private ActiveUser auCurrentUser;

    private Bitmap bitDefaultProfileImage;
    private Bitmap bitProfileImage;

    //private static final String strAuthenticationURL = "http://80.0.165.187/chatroomapp/authenticate_user.php";
    //private static final String strUpdateTokenURL = "http://80.0.165.187/chatroomapp/update_token.php";
    private String strAuthenticationURL;
    private String strUpdateTokenURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        strAuthenticationURL = getString(R.string.AuthenticationURL);
        strUpdateTokenURL = getString(R.string.UpdateTokenURL);

        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        proLogin = (ProgressBar)findViewById(R.id.proLogin);

        bitDefaultProfileImage = BitmapFactory.decodeResource(this.getResources(),R.drawable.anonymous);

        auCurrentUser = ActiveUser.getInstance();

        Bundle intentBundle = getIntent().getExtras();

        if (intentBundle != null) {
            strRegisteredEmail = getIntent().getExtras().getString("username");
            strRegisteredPass = getIntent().getExtras().getString("password");
            etEmail.setText(strRegisteredEmail);
            etPassword.setText(strRegisteredPass);
        }

    }

    public void loginUser(View view) {

        strEmail = etEmail.getText().toString();
        strPassword = etPassword.getText().toString();

        if (strEmail.equals("") || strPassword.equals(null)){
            Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_LONG).show();
        }else{

            proLogin.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);

            //GET TOKEN FROM SHARED PREFS
            SharedPreferences ChatPrefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
            final String strNewToken = ChatPrefs.getString(getString(R.string.FCM_TOKEN_PREF), "");

            StringRequest authenticationRequest = new StringRequest(Request.Method.POST, strAuthenticationURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                strResult = jsonObject.getString("result");

                                if (strResult.equals("1")) {


                                    //region RETRIEVE LOGIN DETAILS
                                    //GET USER DETAILS FROM RETURNED JSON AND STORE IN USER SINGLETON CLASS
                                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonMessage = jsonArray.getJSONObject(i);

                                        if (jsonMessage != null) {

                                            //GET COMPONENTS
                                            String strUserID = jsonMessage.getString("user_id");
                                            String strEmail = jsonMessage.getString("email");
                                            String strName = jsonMessage.getString("Username");
                                            //String strFCMToken = jsonMessage.getString("registration_token");
                                            String strBio = jsonMessage.getString("user_bio");
                                            String strProfileImageURL = jsonMessage.getString("profile_image_url");

                                            auCurrentUser.setIsLoggedIn(true);
                                            auCurrentUser.setUserID(strUserID);
                                            auCurrentUser.setEmail(strEmail);
                                            auCurrentUser.setName(strName);
                                            //auCurrentUser.setUsersFCMToken(strFCMToken);
                                            auCurrentUser.setUsersFCMToken(strNewToken);
                                            auCurrentUser.setBio(strBio);

                                            //getimage or set efault
                                            if (strProfileImageURL.equals("not specified")){
                                                auCurrentUser.setUserProfileImage(bitDefaultProfileImage);
                                            }else{

                                                //DOWNLOAD IMAGE FROM URL AND STORE IN ACIVE USER USING VOLLEY
                                                //ImageRequest imageRequest = new ImageRequest()
                                                //Set default in respons
                                                //set default in error
                                            }

                                            auCurrentUser.setStrProfileImageURL(strProfileImageURL);

                                            //STORE USE DETAILS SO DOESN'T HAVE TO LOG IN EVERY TIME
                                            StoreForReturn(true, strUserID, strEmail, strName, strNewToken, strBio, strProfileImageURL); //ADD BIO

                                            //UPDATE USERS TOKEN IN DATABASE
                                            UpdateToken(strUserID, strNewToken);

                                        }
                                    }
                                    //endregion


                                    proLogin.setVisibility(View.INVISIBLE);
                                    btnLogin.setText("Success");
                                    btnLogin.setBackgroundResource(R.drawable.round_button_success);
                                    btnLogin.setVisibility(View.VISIBLE);

                                    Handler delayHandler = new Handler();
                                    delayHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(Login.this, ChatRoomGallery.class));
                                            finish();
                                        }
                                    },200);

                                }
                                else
                                    Toast.makeText(Login.this, "Unrecognisable User!", Toast.LENGTH_SHORT).show();
                                    proLogin.setVisibility(View.INVISIBLE);
                                    btnLogin.setVisibility(View.VISIBLE);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //HANDLE ERROR
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
        //SET BIO
        editor.commit();
    }

    public void goToRegistration(View view) {

        startActivity(new Intent(this, RegisterUser.class));
    }
}
