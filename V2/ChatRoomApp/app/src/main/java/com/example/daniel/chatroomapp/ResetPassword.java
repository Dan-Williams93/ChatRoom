package com.example.daniel.chatroomapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {

    private EditText etEmail, etAnswer;
    private TextView tvQuestion;
    private Button btnEnter;
    private LinearLayout linSecurityQuestions;
    private ProgressBar progSecurityLoading, progPasswordResetLoading;

    private String strSecurityRequestURL, strEmailRequestURL;
    private String strEmail, strAnswer, strRetrievedEmail, strRetrievedName, strRetrievedQuestion, strRetrievedAnswer, strNewPassword;
    private Boolean isEmailReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etAnswer = (EditText) findViewById(R.id.etAnswer);
        tvQuestion = (TextView) findViewById(R.id.tvQuestion);
        btnEnter = (Button) findViewById(R.id.btnEnter);
        linSecurityQuestions = (LinearLayout) findViewById(R.id.linSecurityQuestions);
        progSecurityLoading = (ProgressBar)findViewById(R.id.progSecurityLoading);
        progPasswordResetLoading = (ProgressBar)findViewById(R.id.progResetLoading);

        strSecurityRequestURL = getString(R.string.SecurityRequestURL);
        strEmailRequestURL = getString(R.string.EmailRequestURL);
    }

    public void resetProcess(View view) {

        btnEnter.setEnabled(false);

        if (!isEmailReceived) {

            progSecurityLoading.setVisibility(View.VISIBLE);

            //region GET SECURITY QUESTION
            strEmail = etEmail.getText().toString().trim();

            if (!strEmail.equals("")) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {

                    //GET SECURITY CREDENTIALS FOR ACCOUNT
                    StringRequest securityRequest = new StringRequest(Request.Method.POST, strSecurityRequestURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    //region PARSE JSON RESPONSE

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String strResult = jsonObject.getString("result");

                                        if (strResult.equals("1")) {

                                            isEmailReceived = true;
                                            btnEnter.setEnabled(true);
                                            progSecurityLoading.setVisibility(View.INVISIBLE);

                                            JSONArray jsonArray = jsonObject.getJSONArray("response");

                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonMessage = jsonArray.getJSONObject(i);

                                                strRetrievedEmail = jsonMessage.getString("email");
                                                strRetrievedName = jsonMessage.getString("Username");
                                                strRetrievedQuestion = jsonMessage.getString("security_question");
                                                strRetrievedAnswer = jsonMessage.getString("security_answer");
                                            }

                                            tvQuestion.setText(strRetrievedQuestion);
                                            linSecurityQuestions.setVisibility(View.VISIBLE);

                                        } else {
                                            //region NO EMAIL FOUND ALERT DIALOG
                                            ContextThemeWrapper ctw = new ContextThemeWrapper(ResetPassword.this, R.style.Theme_AppCompat_Dialog_Alert);

                                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                                            alertBuilder.setTitle("Invalid Email")
                                                    .setMessage("No account found that matches the entered email address")
                                                    .setIcon(R.drawable.ic_action_warning)
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });

                                            AlertDialog noEmailAlert = alertBuilder.create();
                                            noEmailAlert.show();
                                            //endregion
                                            btnEnter.setEnabled(true);
                                            progSecurityLoading.setVisibility(View.INVISIBLE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        //region ERROR ALERT DIALOG
                                        ContextThemeWrapper ctw = new ContextThemeWrapper(ResetPassword.this, R.style.Theme_AppCompat_Dialog_Alert);

                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                                        alertBuilder.setTitle("Error!")
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
                                        btnEnter.setEnabled(true);
                                        progSecurityLoading.setVisibility(View.INVISIBLE);
                                    }
                                    //endregion

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //region ERROR ALERT DIALOG
                            ContextThemeWrapper ctw = new ContextThemeWrapper(ResetPassword.this, R.style.Theme_AppCompat_Dialog_Alert);

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                            alertBuilder.setTitle("Error!")
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
                            btnEnter.setEnabled(true);
                            progSecurityLoading.setVisibility(View.INVISIBLE);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("user_email", strEmail);
                            return params;
                        }
                    };

                    VolleyQueueSingleton.getmInstance(ResetPassword.this).addToRequestQueue(securityRequest);

                } else {
                    //region INVALID EMAIL ALERT DIALOG
                    ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                    alertBuilder.setTitle("Invalid Email")
                            .setMessage("Please enter a valid email address")
                            .setIcon(R.drawable.ic_action_warning)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    AlertDialog invalidEmailAlert = alertBuilder.create();
                    invalidEmailAlert.show();
                    //endregion
                    btnEnter.setEnabled(true);
                    progSecurityLoading.setVisibility(View.INVISIBLE);
                }
            } else {
                //region NO EMAIL ALERT DIALOG
                ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                alertBuilder.setTitle("No Email")
                        .setMessage("Please enter a valid email address")
                        .setIcon(R.drawable.ic_action_warning)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog noEmailAlert = alertBuilder.create();
                noEmailAlert.show();
                //endregion
                btnEnter.setEnabled(true);
                progSecurityLoading.setVisibility(View.INVISIBLE);
            }
            //endregion

        } else {

            progPasswordResetLoading.setVisibility(View.VISIBLE);

            //region CHECK SECURITY ANSWER AND SEND EMAIL IF VALIDATED

            strAnswer = etAnswer.getText().toString().trim();

            if (!strAnswer.equals("")) {

                if (strAnswer.equals(strRetrievedAnswer)) {
                    StringRequest emailRequest = new StringRequest(Request.Method.POST, strEmailRequestURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    btnEnter.setEnabled(true);
                                    progPasswordResetLoading.setVisibility(View.INVISIBLE);

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String strResult = jsonObject.getString("result");

                                        if (strResult.equals("1")) {

                                            //region PASSWORD RESET SUCCESS ALERT DIALOG
                                            ContextThemeWrapper ctw = new ContextThemeWrapper(ResetPassword.this, R.style.Theme_AppCompat_Dialog_Alert);

                                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                                            alertBuilder.setTitle("Success!")
                                                    .setMessage("Your password has been reset.\n\nConfirmation has been sent to the email address you provided")
                                                    .setIcon(R.drawable.ic_action_warning)
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            startActivity(new Intent(ResetPassword.this, Login.class));
                                                            finish();
                                                        }
                                                    });

                                            AlertDialog successAlert = alertBuilder.create();
                                            successAlert.show();
                                            //endregion

                                        } else {
                                            //region EMAIL FAILED ALERT DIALOG
                                            ContextThemeWrapper ctw = new ContextThemeWrapper(ResetPassword.this, R.style.Theme_AppCompat_Dialog_Alert);

                                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                                            alertBuilder.setTitle("Failed!")
                                                    .setMessage("Failed to send email.\n\n Please try again, if the problem persists please contact the RoomChat team")
                                                    .setIcon(R.drawable.ic_action_warning)
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            etAnswer.setText("");
                                                        }
                                                    });

                                            AlertDialog emailFailedALert = alertBuilder.create();
                                            emailFailedALert.show();
                                            //endregion
                                            btnEnter.setEnabled(true);
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //region EMAIL FAILED ALERT DIALOG
                            ContextThemeWrapper ctw = new ContextThemeWrapper(ResetPassword.this, R.style.Theme_AppCompat_Dialog_Alert);

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                            alertBuilder.setTitle("Failed!")
                                    .setMessage("Failed to send email.\n\n Please try again, if the problem persists please contact the RoomChat team")
                                    .setIcon(R.drawable.ic_action_warning)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            etAnswer.setText("");
                                        }
                                    });

                            AlertDialog emailFailedALert = alertBuilder.create();
                            emailFailedALert.show();
                            //endregion
                            btnEnter.setEnabled(true);
                            progPasswordResetLoading.setVisibility(View.INVISIBLE);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("email", strRetrievedEmail);
                            params.put("name", strRetrievedName);
                            return params;
                        }
                    };

                    VolleyQueueSingleton.getmInstance(ResetPassword.this).addToRequestQueue(emailRequest);
                } else {
                    //region INVALID ANSWER ALERT DIALOG
                    ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                    alertBuilder.setTitle("Invalid Answer")
                            .setMessage("The answer provided did not match our records.\n\nPlease try again")
                            .setIcon(R.drawable.ic_action_warning)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    etAnswer.setText("");
                                }
                            });

                    AlertDialog invalidAnswerAlert = alertBuilder.create();
                    invalidAnswerAlert.show();
                    //endregion
                    btnEnter.setEnabled(true);
                    progPasswordResetLoading.setVisibility(View.INVISIBLE);
                }

            } else {
                //region INVALID ANSWER ALERT DIALOG
                ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                alertBuilder.setTitle("Invalid Answer")
                        .setMessage("Please enter an answer to your security question")
                        .setIcon(R.drawable.ic_action_warning)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog noAnswerAlert = alertBuilder.create();
                noAnswerAlert.show();
                //endregion
                btnEnter.setEnabled(true);
                progPasswordResetLoading.setVisibility(View.INVISIBLE);
            }
            //endregion
        }

    }

}