package com.example.daniel.chatroomapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity {

    private EditText etName, etPassword, etConfirmPassword, etEmail;
    private Button btnRegister;

    private String strName, strPassword, strConfirmPassword, strEmail, strFCM_Reg_Token;

    //private static final String strRegisterUserURL = "http://80.0.165.187/chatroomapp/register_user.php";
    private String strRegisterUserURL, strValidateEmailURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        strRegisterUserURL = getString(R.string.RegisterUserURL);
        strValidateEmailURL = getString(R.string.ValidateEmailURL);

        etName = (EditText)findViewById(R.id.etName);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etConfirmPassword = (EditText)findViewById(R.id.etConfirmPassword);
    }

    public void registerUser(View view) {

        strName = etName.getText().toString().trim();
        strEmail = etEmail.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();
        strConfirmPassword = etConfirmPassword.getText().toString().trim();

        if (!strName.equals("") && !strEmail.equals("") && !strPassword.equals("") && !strConfirmPassword.equals("")) {

            if (android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                if (strPassword.equals(strConfirmPassword)) {

                    //VALIDATE EMAIL
                    StringRequest validateEmailRequest = new StringRequest(Request.Method.POST, strValidateEmailURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String strResult = jsonObject.getString("result");

                                        if (strResult.equals("0")){
                                            Register();
                                        }
                                        else{
                                            ContextThemeWrapper ctw = new ContextThemeWrapper(RegisterUser.this, R.style.Theme_AppCompat_Dialog_Alert);

                                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                                            alertBuilder.setTitle("Invalid Email")
                                                    .setMessage("This email is already in use.\nIf you have forgotten your password please see the forgotten password option")
                                                    .setIcon(R.drawable.ic_action_warning)
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });

                                            AlertDialog passwordAlert = alertBuilder.create();
                                            passwordAlert.show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper(RegisterUser.this, R.style.Theme_AppCompat_Dialog_Alert);

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                            alertBuilder.setTitle("Registration Failed!")
                                    .setMessage("Please try again.\n If the problem persists please contact the RoomChat team")
                                    .setIcon(R.drawable.ic_action_warning)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                            AlertDialog passwordAlert = alertBuilder.create();
                            passwordAlert.show();

                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("email", strEmail);
                            return params;
                        }
                    };

                    VolleyQueueSingleton.getmInstance(this).addToRequestQueue(validateEmailRequest);

                } else {

                    ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                    alertBuilder.setTitle("Passwords do not match!")
                            .setMessage("Please re-enter your passwords")
                            .setIcon(R.drawable.ic_action_warning)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    AlertDialog passwordAlert = alertBuilder.create();
                    passwordAlert.show();

                }
            }else{
                ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                alertBuilder.setTitle("Invalid Email Credentials")
                        .setMessage("Please make sure you have entered a valid email address")
                        .setIcon(R.drawable.ic_action_warning)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog passwordAlert = alertBuilder.create();
                passwordAlert.show();
            }
        }else{

            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
            alertBuilder.setTitle("Invalid Credentials")
                    .setMessage("Please make sure all fields are filled correctly")
                    .setIcon(R.drawable.ic_action_warning)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            AlertDialog passwordAlert = alertBuilder.create();
            passwordAlert.show();
        }
    }

    public void Register(){

        //start registration
        SharedPreferences ChatRoomPrefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
        strFCM_Reg_Token = ChatRoomPrefs.getString(getString(R.string.FCM_TOKEN_PREF), "");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, strRegisterUserURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        response = response.trim();

                        if(response.equals("1")){

                            Intent loginIntent = new Intent(RegisterUser.this, Login.class);
                            loginIntent.putExtra("username", strEmail);
                            loginIntent.putExtra("password", strPassword);
                            startActivity(loginIntent);
                            finish();


                        }else{
                            ContextThemeWrapper ctw = new ContextThemeWrapper(RegisterUser.this, R.style.Theme_AppCompat_Dialog_Alert);

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                            alertBuilder.setTitle("Registration Failed!")
                                    .setMessage("Please try again.\n If the problem persists please contact the RoomChat team")
                                    .setIcon(R.drawable.ic_action_warning)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                            AlertDialog passwordAlert = alertBuilder.create();
                            passwordAlert.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ContextThemeWrapper ctw = new ContextThemeWrapper(RegisterUser.this, R.style.Theme_AppCompat_Dialog_Alert);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                alertBuilder.setTitle("Registration Failed!")
                        .setMessage("Please try again.\n If the problem persists please contact the RoomChat team")
                        .setIcon(R.drawable.ic_action_warning)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog passwordAlert = alertBuilder.create();
                passwordAlert.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("username", strName);
                params.put("email", strEmail);
                params.put("password", strPassword);
                params.put("registration_token", strFCM_Reg_Token);

                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(RegisterUser.this).addToRequestQueue(stringRequest);

    }
}

