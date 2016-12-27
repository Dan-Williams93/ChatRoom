package com.example.daniel.chatroomapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterUser extends AppCompatActivity {

    private EditText etFirstName, etSecondName, etPassword, etConfirmPassword, etEmail, etSecurityAnswer, etConfirmSecurityAnswer;
    private Spinner spiSecurityQuestions;
    private Button btnRegister;

    private String strFirstName, strSecondName, strName, strPassword, strConfirmPassword, strEmail, strFCM_Reg_Token, strSecurityQuestionSelected, strSecretAnswer,
        strConfirmSecretAnswer;

    //private static final String strRegisterUserURL = "http://80.0.165.187/chatroomapp/register_user.php";
    private String strRegisterUserURL, strValidateEmailURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        strRegisterUserURL = getString(R.string.RegisterUserURL);
        strValidateEmailURL = getString(R.string.ValidateEmailURL);

        etFirstName = (EditText)findViewById(R.id.etFirstName);
        etSecondName = (EditText)findViewById(R.id.etSecondName);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etConfirmPassword = (EditText)findViewById(R.id.etConfirmPassword);
        etSecurityAnswer = (EditText)findViewById(R.id.etSecurityAnswer);
        etConfirmSecurityAnswer = (EditText)findViewById(R.id.etConfirmSecurityAnswer);
        spiSecurityQuestions = (Spinner)findViewById(R.id.spSecurityQuestions);

        //region CREATE QUESTION SPINNER
        String[] strQuestions = getResources().getStringArray(R.array.security_questions);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item){

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() -1;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(strQuestions);

        spiSecurityQuestions.setAdapter(adapter);
        spiSecurityQuestions.setSelection(adapter.getCount()); //display hint
        //endregion

        //region SPINNER CLICK LISTENER
        spiSecurityQuestions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSecurityQuestionSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //endregion

    }

    public void registerUser(View view) {

        strFirstName = etFirstName.getText().toString().trim();
        strSecondName = etSecondName.getText().toString().trim();
        strEmail = etEmail.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();
        strConfirmPassword = etConfirmPassword.getText().toString().trim();
        strSecretAnswer = etSecurityAnswer.getText().toString().trim();
        strConfirmSecretAnswer = etConfirmSecurityAnswer.getText().toString().trim();

        if (!strFirstName.equals("") && !strSecondName.equals("") && !strEmail.equals("") && !strPassword.equals("") && !strConfirmPassword.equals("")
                && !strSecurityQuestionSelected.equals("Select A Question...") && !strSecretAnswer.equals("")
                    && !strConfirmSecretAnswer.equals("")) {

            strName = strFirstName + " " + strSecondName;

            if (android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                if (strPassword.equals(strConfirmPassword)) {

                    if (strSecretAnswer.equals(strConfirmSecretAnswer)) {

                        //region VALIDATE EMAIL
                        StringRequest validateEmailRequest = new StringRequest(Request.Method.POST, strValidateEmailURL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String strResult = jsonObject.getString("result");

                                            if (strResult.equals("0")) {
                                                Register();
                                            } else {
                                                //region INVALID EMAIL ALERT DIALOG
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

                                                AlertDialog invalidEmailAlert = alertBuilder.create();
                                                invalidEmailAlert.show();
                                                //endregion
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //region ERROR ALERT
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

                                AlertDialog errorAlert = alertBuilder.create();
                                errorAlert.show();
                                //endregion

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("email", strEmail);
                                return params;
                            }
                        };

                        VolleyQueueSingleton.getmInstance(this).addToRequestQueue(validateEmailRequest);
                        //endregion

                    }else{
                        //region SECRET ANSWER ALERT DIALOG
                        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                        alertBuilder.setTitle("Not Matching")
                                .setMessage("Please re-enter your secret security answers")
                                .setIcon(R.drawable.ic_action_warning)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        etSecurityAnswer.setText("");
                                        etConfirmSecurityAnswer.setText("");
                                    }
                                });

                        AlertDialog secretAnswerAlert = alertBuilder.create();
                        secretAnswerAlert.show();
                        //endregion
                    }

                } else {

                    //region PASSWORD ALERT DIALOG
                    ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                    alertBuilder.setTitle("Passwords do not match!")
                            .setMessage("Please re-enter your passwords")
                            .setIcon(R.drawable.ic_action_warning)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    etPassword.setText("");
                                    etConfirmPassword.setText("");
                                }
                            });

                    AlertDialog passwordAlert = alertBuilder.create();
                    passwordAlert.show();
                    //endregion

                }
            }else{
                //region INVALID EMAIL ALERT DIALOG
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

                AlertDialog invalidEmailAlert = alertBuilder.create();
                invalidEmailAlert.show();
                //endregion
            }
        }else{

            //region INVALID EMAIL ALERT DIALOG
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

            AlertDialog invalidCredentials = alertBuilder.create();
            invalidCredentials.show();
            //endregion
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

                            //region REGISTRATION SUCCESS ALERT DIALOG
                            ContextThemeWrapper ctw = new ContextThemeWrapper(RegisterUser.this, R.style.Theme_AppCompat_Dialog_Alert);

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                            alertBuilder.setTitle("Success!")
                                    .setMessage("Your account has been created")
                                    .setIcon(R.drawable.ic_action_tick)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent loginIntent = new Intent(RegisterUser.this, Login.class);
                                            loginIntent.putExtra("username", strEmail);
                                            loginIntent.putExtra("password", strPassword);
                                            startActivity(loginIntent);
                                            finish();
                                        }
                                    });

                            AlertDialog successAlert = alertBuilder.create();
                            successAlert.show();
                            //endregion

                        }else{

                            //region FAILED REGISTRATION ALERT DIALOG
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

                            AlertDialog registrationFailedAlert = alertBuilder.create();
                            registrationFailedAlert.show();
                            //endregion
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //region FAILED REGISTRATION ALERT DIALOG
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

                AlertDialog registrationFailedAlert = alertBuilder.create();
                registrationFailedAlert.show();
                //endregion
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("username", strName);
                params.put("email", strEmail);
                params.put("password", strPassword);
                params.put("registration_token", strFCM_Reg_Token);

                params.put("security_question", strSecurityQuestionSelected);
                params.put("secret_answer", strSecretAnswer);

                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(RegisterUser.this).addToRequestQueue(stringRequest);

    }
}

