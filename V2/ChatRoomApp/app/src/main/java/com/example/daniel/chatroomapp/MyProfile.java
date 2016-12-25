package com.example.daniel.chatroomapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.R.drawable.ic_menu_edit;
import static android.R.drawable.ic_menu_save;

public class MyProfile extends AppCompatActivity {

    //region CONSTANTS
    //region URL'S
    private String strUpdatePasswordURL, strUpdateBioURL, strUploadImageURL;
    //endregion
    //region VARIABLES
    private String strUserID;
    //endregion
    //region VIEWS
    private ScrollView svWrapper;
    private Button btnSignOut;
    //endregion
    //endregion

    //region PROFILE IMAGE VIEWS
    private ImageView imProfileImage;
    private ImageButton imbtnImageChange;
    //endregion
    //region NAME VIEWS
    private TextView tvName;
    //endregion
    //region CHANGE BIO VIEWS
    private TextView tvChangeBio;
    private ImageButton imbtnBioDrop;
    private EditText etPersonalBio;
    private Button btnChangeBio;
    //endregion
    //region CHANGE PASSWORD VIEWS
    private TextView tvChangePass;
    private ImageButton imbtnPasswordDrop;
    private EditText etOldPassword, etNewPassword, etNewPasswordConfirm;
    private Button btnChangePass;
    private RelativeLayout relPassword;
    //endregion

    //region PROFILE IMAGE VARIABLES
    private Bitmap bitProfileImage, bitSelectedImage;
    //endregion
    //region NAME VARIABLES
    private String strName;
    //endregion
    //region CHANGE BIO VARIABLES
    private String strBio, strNewBio;
    //endregion
    //region CHANGE PASSWORD VARIABLES
    private String strOldPassword, strNewPassword, strNewPasswordConfirm;
    //endregion

    private ActiveUser auCurrentUsser;
    private String strImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        auCurrentUsser = ActiveUser.getInstance();

        //region INSTANTIATE CONSTANTS
        //region INSTANTIATE URL'S
        strUpdatePasswordURL = getString(R.string.UpdatePasswordURL);
        strUpdateBioURL = getString(R.string.UpdateBioURL);
        strUploadImageURL = getString(R.string.UploadImageURL);
        //endregion
        //region INSTANTIATE VARIABLES
        strUserID = auCurrentUsser.getUserID();
        //endregion
        //region VIEW CASTING
        svWrapper = (ScrollView)findViewById(R.id.svWrapper);
        btnSignOut = (Button)findViewById(R.id.btnSignOut);
        //endregion
        //endregion

        //region PROFILE IMAGE VIEW CASTING
        imProfileImage = (ImageView)findViewById(R.id.imProfileImage);
        imbtnImageChange = (ImageButton)findViewById(R.id.imbtnImageChange);
        //endregion

        //region NAME VIEW CASTING
        tvName = (TextView)findViewById(R.id.tvName);
        //endregion

        //region CHANGE BIO VIEW CASTING
        tvChangeBio = (TextView)findViewById(R.id.tvChangeBio);
        imbtnBioDrop = (ImageButton)findViewById(R.id.imbtnBioDrop);
        etPersonalBio = (EditText)findViewById(R.id.etPersonalBio);
        btnChangeBio = (Button)findViewById(R.id.btnChangeBio);
        //endregion

        //region CHANGE PASSWORD VIEW CASTING
        tvChangePass = (TextView)findViewById(R.id.tvChangePass);
        imbtnPasswordDrop = (ImageButton)findViewById(R.id.imbtnPasswordDrop);
        etOldPassword = (EditText)findViewById(R.id.etOldPassword);
        etNewPassword = (EditText)findViewById(R.id.etNewPassword);
        etNewPasswordConfirm = (EditText)findViewById(R.id.etNewPasswordConfirm);
        btnChangePass = (Button)findViewById(R.id.btnChangePass);
        relPassword = (RelativeLayout)findViewById(R.id.relPassword);
        //endregion


        //region SET BUTTON DISABLED PROPERTIES
        disableChangeBioButton();
        disableChangePasswordButton();
        //endregion


        //region INSTANTIATE PROFILE IMAGE VARIABLES AND VIEWS
        bitProfileImage = auCurrentUsser.getUserProfileImage();

        //region CHECK THERE IS A SET BITMAP AND APLY DEFAULT IF NOT
        if(bitProfileImage != null) {
            imProfileImage.setImageBitmap(bitProfileImage);
        }else{
            imProfileImage.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.anonymous));
        }
        //endregion

        //region PROFILE IMAGE TAP LISTENER
        imProfileImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (imbtnImageChange.getVisibility() != View.VISIBLE) {
                    imbtnImageChange.setVisibility(View.VISIBLE);
                }else{
                    imbtnImageChange.setVisibility(View.GONE);
                }

                return false;
            }
        });
        //endregion
        //endregion

        //region INSTANTIATE NAME VARIABLES AND VIEWS
        strName = auCurrentUsser.getName();
        tvName.setText(strName);
        //endregion

        //region INSTANTIATE BIO VARIABLES AND VIEWS
        strBio = auCurrentUsser.getBio();
        etPersonalBio.setText(strBio);

        etPersonalBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().trim().equals(strBio)){
                    enableChangeBioButton();
                }else if (s.toString().trim().equals(strBio)) {
                    disableChangeBioButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //endregion

        //region INSTANTIATE PASSWORD VARIABLES AND VIEWS
        etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0){
                    strNewPassword = etNewPassword.getText().toString().trim();
                    strNewPasswordConfirm = etNewPasswordConfirm.getText().toString().trim();

                    if(!strNewPassword.equals("") && !strNewPasswordConfirm.equals("")){
                        enableChangePasswordButton();
                    }
                }else if (s.toString().trim().length() == 0) {
                    disableChangePasswordButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0){
                    strOldPassword = etOldPassword.getText().toString().trim();
                    strNewPasswordConfirm = etNewPasswordConfirm.getText().toString().trim();

                    if(!strOldPassword.equals("") && !strNewPasswordConfirm.equals("")){
                        enableChangePasswordButton();
                    }
                }else if (s.toString().trim().length() == 0) {
                    disableChangePasswordButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etNewPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0){
                    strOldPassword = etOldPassword.getText().toString().trim();
                    strNewPassword = etNewPassword.getText().toString().trim();

                    if(!strOldPassword.equals("") && !strNewPassword.equals("")){
                        enableChangePasswordButton();
                    }
                }else if (s.toString().trim().length() == 0) {
                    disableChangePasswordButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //endregion
    }


    //region DISABLE BUTTON METHODS
    private void disableChangeBioButton(){
        btnChangeBio.setEnabled(false);
        btnChangeBio.setBackgroundTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.colorDisabled)));
        btnChangeBio.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_save_grey, 0, 0, 0);
    }

    private void disableChangePasswordButton(){
        btnChangePass.setEnabled(false);
        btnChangePass.setBackgroundTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.colorDisabled)));
        btnChangePass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_save_grey, 0, 0, 0);
    }
    //endregion

    //region ENABLE BUTTON METHODS
    private void enableChangeBioButton(){
        btnChangeBio.setEnabled(true);
        btnChangeBio.setBackgroundTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.colorStatus)));
        btnChangeBio.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_save, 0, 0, 0);
    }

    private void enableChangePasswordButton(){
        btnChangePass.setEnabled(true);
        btnChangePass.setBackgroundTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.colorStatus)));
        btnChangePass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_save, 0, 0, 0);
    }
    //endregion


    //region CHANGE IMAGE METHODS
    public void selectProfileImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image Using:"), 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (resultCode == RESULT_OK){ //EXECUTES IF RESULT OF USER ACTION ON INTENT RETURNS OK STATUS

            if (requestCode == 1){

                Uri selectedImageUri = data.getData();     //GETS IMAGE URI FROM THE RETURNED INTENT DATA
                strImageURI = selectedImageUri.toString();  //SETS IMAGE URI TO STRING
                InputStream is;

                try {
                    is = getContentResolver().openInputStream(selectedImageUri);       //OPENS INPUT STREAM
                    bitSelectedImage = BitmapFactory.decodeStream(is);                 //DECODE THE INPUT STREAM AND STORES IN A BITMAP
                    imProfileImage.setImageBitmap(bitSelectedImage);                   //SETS PROFILE IMAGE TO THE RETURNED AND DECODED BITMAP
                    is.close();                                                        //CLOSES INPUT STREAM

                    //############################################
                    uploadNewImage();
                    //############################################
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    imProfileImage.setImageBitmap(bitProfileImage);                    //SETS PROFILE IMAGE TO THE DEFAULT BITMAP
                    bitSelectedImage = bitProfileImage;
                } catch (IOException e) {
                    e.printStackTrace();
                    imProfileImage.setImageBitmap(bitProfileImage);                    //SETS PROFILE IMAGE TO THE DEFAULT BITMAP
                    bitSelectedImage = bitProfileImage;
                }
            }else Toast.makeText(MyProfile.this, "Image Not Available", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(MyProfile.this, "Unable to Access Image Selection", Toast.LENGTH_SHORT).show();

    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);         //REDUCE SIZE IF ENCOUNTER ERRORS
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadNewImage(){

        final ProgressDialog uploadProgress = new ProgressDialog(this, android.app.AlertDialog.THEME_HOLO_DARK);
        uploadProgress.setCancelable(false);
        uploadProgress.setIndeterminate(false);
        uploadProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        uploadProgress.setMessage("Uploading image...");
        uploadProgress.show();

        StringRequest uploadRequest = new StringRequest(Request.Method.POST, strUploadImageURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {

                        if (result.equals("1")) {
                            uploadProgress.dismiss();
                            //region PROFILE IMAGE SUCCESS CHANGE ALERT DIALOG
                            ContextThemeWrapper ctw = new ContextThemeWrapper(MyProfile.this, R.style.Theme_AppCompat_Dialog_Alert);

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                            alertBuilder.setTitle("Success!")
                                    .setMessage("Your profile image has successfully been updated")
                                    .setIcon(R.drawable.ic_action_tick)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            imbtnImageChange.setVisibility(View.GONE);
                                        }
                                    });

                            AlertDialog successAlert = alertBuilder.create();
                            successAlert.show();
                            //endregion
                        }else{
                            //region ERROR ALERT DIALOG
                            ContextThemeWrapper ctw = new ContextThemeWrapper(MyProfile.this, R.style.Theme_AppCompat_Dialog_Alert);

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                            alertBuilder.setTitle("Error!")
                                    .setMessage("An error has occurred.\n\nPlease try again.\n\nIf this problem persists please contact the RoomChat Team")
                                    .setIcon(R.drawable.ic_action_tick)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            imbtnImageChange.setVisibility(View.GONE);
                                        }
                                    });

                            AlertDialog errorAlert = alertBuilder.create();
                            errorAlert.show();
                            //endregion
                            uploadProgress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //region ERROR ALERT DIALOG
                        ContextThemeWrapper ctw = new ContextThemeWrapper(MyProfile.this, R.style.Theme_AppCompat_Dialog_Alert);

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                        alertBuilder.setTitle("Error!")
                                .setMessage("An error has occured.\n\nPlease try again.\n\nIf this problem persists please contact the RoomChat Team")
                                .setIcon(R.drawable.ic_action_tick)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        imbtnImageChange.setVisibility(View.GONE);
                                    }
                                });

                        AlertDialog errorAlert = alertBuilder.create();
                        errorAlert.show();
                        //endregion
                        uploadProgress.dismiss();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitSelectedImage);
                String name = strName.replaceAll(" ", "");

                Map<String, String> params = new HashMap<>();

                params.put("image", image);
                params.put("name", name);
                params.put("user_id", strUserID);

                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(this).addToRequestQueue(uploadRequest);

    }
    //endregion

    //region CHANGE BIO METHODS
    public void showBioOptions(View view){

        if (etPersonalBio.getVisibility() != view.VISIBLE) {
            etPersonalBio.setVisibility(View.VISIBLE);
            btnChangeBio.setVisibility(View.VISIBLE);
        }
        else {
            etPersonalBio.setVisibility(View.GONE);
            btnChangeBio.setVisibility(View.GONE);
        }
    }

    public void changeBio(View view){

        disableChangeBioButton();

        strNewBio = etPersonalBio.getText().toString().trim();

        StringRequest bioUpdateRequest = new StringRequest(Request.Method.POST, strUpdateBioURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("1")){
                            //region BIO SUCCESS CHANGE ALERT DIALOG
                            ContextThemeWrapper ctw = new ContextThemeWrapper(MyProfile.this, R.style.Theme_AppCompat_Dialog_Alert);

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                            alertBuilder.setTitle("Success!")
                                    .setMessage("Your bio has successfully been updated")
                                    .setIcon(R.drawable.ic_action_tick)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            strBio = strNewBio;
                                            etPersonalBio.setText(strBio);
                                            etPersonalBio.setVisibility(View.GONE);
                                            btnChangeBio.setVisibility(View.GONE);
                                        }
                                    });

                            AlertDialog successAlert = alertBuilder.create();
                            successAlert.show();
                            //endregion
                        }else{
                            //region ERROR ALERT DIALOG
                            ContextThemeWrapper ctw = new ContextThemeWrapper(MyProfile.this, R.style.Theme_AppCompat_Dialog_Alert);

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                            alertBuilder.setTitle("Error!")
                                    .setMessage("An error has occurred.\n\nPlease try again.\n\nIf this problem persists please contact the RoomChat Team")
                                    .setIcon(R.drawable.ic_action_tick)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            etPersonalBio.setText(strBio);
                                            etPersonalBio.setVisibility(View.GONE);
                                            btnChangeBio.setVisibility(View.GONE);
                                        }
                                    });

                            AlertDialog errorAlert = alertBuilder.create();
                            errorAlert.show();
                            //endregion
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //region ERROR ALERT DIALOG
                ContextThemeWrapper ctw = new ContextThemeWrapper(MyProfile.this, R.style.Theme_AppCompat_Dialog_Alert);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                alertBuilder.setTitle("Error!")
                        .setMessage("An error has occurred.\n\nPlease try again.\n\nIf this problem persists please contact the RoomChat Team")
                        .setIcon(R.drawable.ic_action_tick)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                etPersonalBio.setText(strBio);
                                etPersonalBio.setVisibility(View.GONE);
                                btnChangeBio.setVisibility(View.GONE);
                            }
                        });

                AlertDialog errorAlert = alertBuilder.create();
                errorAlert.show();
                //endregion
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", strUserID);
                params.put("new_bio", strNewBio);

                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(this).addToRequestQueue(bioUpdateRequest);
    }
    //endregion

    //region CHANGE PASSWORD METHODS
    public void showPasswordOptions(View view){

        if (etOldPassword.getVisibility() != view.VISIBLE) {

            etOldPassword.setVisibility(View.VISIBLE);
            etNewPassword.setVisibility(View.VISIBLE);
            etNewPasswordConfirm.setVisibility(View.VISIBLE);
            btnChangePass.setVisibility(View.VISIBLE);

        }
        else {
            etOldPassword.setVisibility(View.GONE);
            etNewPassword.setVisibility(View.GONE);
            etNewPasswordConfirm.setVisibility(View.GONE);
            btnChangePass.setVisibility(View.GONE);
        }

        //region SCROLL TO FOCUS ALL PASSWORD OPTIONS
        svWrapper.post(new Runnable() {
            @Override
            public void run() {
                svWrapper.smoothScrollTo(0, (int) relPassword.getBottom());
            }
        });
        //endregion
    }

    public void changePassword(View view) {

        disableChangePasswordButton();

        strOldPassword = etOldPassword.getText().toString().trim();
        strNewPassword = etNewPassword.getText().toString().trim();
        strNewPasswordConfirm = etNewPasswordConfirm.getText().toString().trim();

        if (strNewPassword.equals(strNewPasswordConfirm)) {

            //region UPDATE PASSWORD STRING REQUEST SEQUENCE
            StringRequest updatePasswordRequest = new StringRequest(Request.Method.POST, strUpdatePasswordURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("1")) {
                                //region PASSWORD CHANGE SUCCESS ALERT DIALOG
                                ContextThemeWrapper ctw = new ContextThemeWrapper(MyProfile.this, R.style.Theme_AppCompat_Dialog_Alert);

                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                                alertBuilder.setTitle("Success!")
                                        .setMessage("Your password has been updated.")
                                        .setIcon(R.drawable.ic_action_tick)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                etOldPassword.setText("");
                                                etNewPassword.setText("");
                                                etNewPasswordConfirm.setText("");
                                                etOldPassword.setVisibility(View.GONE);
                                                etNewPassword.setVisibility(View.GONE);
                                                etNewPasswordConfirm.setVisibility(View.GONE);
                                                btnChangePass.setVisibility(View.GONE);
                                            }
                                        });

                                AlertDialog passwordAlert = alertBuilder.create();
                                passwordAlert.show();
                                //endregion

                            }else{
                                //region PASSWORD UPDATE INVALID ALERT DIALOG
                                ContextThemeWrapper ctw = new ContextThemeWrapper(MyProfile.this, R.style.Theme_AppCompat_Dialog_Alert);

                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                                alertBuilder.setTitle("Invalid Request")
                                        .setMessage("The details you provided did not match our records.\n\nPlease try again.\n\nIf this problem persists please contact the RoomChat Team")
                                        .setIcon(R.drawable.ic_action_warning)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                etOldPassword.setText("");
                                                etNewPassword.setText("");
                                                etNewPasswordConfirm.setText("");
                                            }
                                        });

                                AlertDialog passwordAlert = alertBuilder.create();
                                passwordAlert.show();
                                //endregion
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //region PASSWORD ERROR INVALID ALERT DIALOG
                    ContextThemeWrapper ctw = new ContextThemeWrapper(MyProfile.this, R.style.Theme_AppCompat_Dialog_Alert);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
                    alertBuilder.setTitle("Error!")
                            .setMessage("An Error has occured.\n\nPlease try again.\n\nIf this problem persists please contact the RoomChat Team")
                            .setIcon(R.drawable.ic_action_warning)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    etOldPassword.setText("");
                                    etNewPassword.setText("");
                                    etNewPasswordConfirm.setText("");
                                }
                            });

                    AlertDialog passwordAlert = alertBuilder.create();
                    passwordAlert.show();
                    //endregion
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", strUserID);
                    params.put("old_password", strOldPassword);
                    params.put("new_password", strNewPasswordConfirm);

                    return params;
                }
            };

            VolleyQueueSingleton.getmInstance(this).addToRequestQueue(updatePasswordRequest);
            //endregion

        }else{
            //region NEW PASSWORDS NO MATCH ALERT DIALOG
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctw);
            alertBuilder.setTitle("Passwords do not match!")
                    .setMessage("Please re-enter your new passwords")
                    .setIcon(R.drawable.ic_action_warning)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            etNewPassword.setText("");
                            etNewPasswordConfirm.setText("");
                        }
                    });

            AlertDialog passwordAlert = alertBuilder.create();
            passwordAlert.show();
            //endregion
        }
    }
    //endregion


    public void SignOutUser(View view) {

        SharedPreferences ChatPrefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
        String strRecentToken = ChatPrefs.getString(getString(R.string.FCM_TOKEN_PREF), "");

        SharedPreferences.Editor editor = ChatPrefs.edit();
        editor.clear();
        editor.commit();

        editor.putString(getString(R.string.FCM_TOKEN_PREF), strRecentToken);
        editor.commit();

        auCurrentUsser.userLogOut();
        startActivity(new Intent(this, Login.class));
        finish();


    }
}