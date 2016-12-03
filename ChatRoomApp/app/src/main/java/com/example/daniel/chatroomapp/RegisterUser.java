package com.example.daniel.chatroomapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity {

    private EditText etName, etPassword, etConfirmPassword, etEmail, etConfirmEmail;
    private Button btnRegister;

    private String strName, strPassword, strConfirmPassword, strEmail, strConfirmEmail, strFCM_Reg_Token;

    private static final String strRegisterUserURL = "http://80.0.165.187/chatroomapp/register_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        etName = (EditText)findViewById(R.id.etName);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etConfirmEmail = (EditText)findViewById(R.id.etConfirmEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etConfirmPassword = (EditText)findViewById(R.id.etConfirmPassword);
    }

    public void registerUser(View view) {

        strName = etName.getText().toString();
        strEmail = etEmail.getText().toString();
        strConfirmEmail = etConfirmEmail.getText().toString();
        strPassword = etPassword.getText().toString();
        strConfirmPassword = etConfirmPassword.getText().toString();

        if (!strName.equals("") && !strEmail.equals("") && !strConfirmEmail.equals("") && !strPassword.equals("") && !strConfirmPassword.equals("")){

            //start registration

            SharedPreferences ChatRoomPrefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
            strFCM_Reg_Token = ChatRoomPrefs.getString(getString(R.string.FCM_TOKEN_PREF), "");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, strRegisterUserURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

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
}
