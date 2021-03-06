package com.example.daniel.chatroomapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import static android.R.drawable.ic_menu_edit;
import static android.R.drawable.ic_menu_save;

public class MyProfile extends AppCompatActivity {

    private ImageView imgProfilePic;
    private ImageButton imbtEditProfilePic;
    private EditText etName, etBio;
    private Button btnEdit;

    private String strBio;
    private String strName;
    private String strUserID;

    private ActiveUser auCurrentUsser;

    //PROFILE PIC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        imgProfilePic = (ImageView)findViewById(R.id.imgProfilePic);
        imbtEditProfilePic = (ImageButton)findViewById(R.id.imbtEditProfilePic);
        etName = (EditText)findViewById(R.id.etName);
        etBio = (EditText)findViewById(R.id.etBio);

        btnEdit = (Button)findViewById(R.id.btnEdit);
        btnEdit.setTag(1);

        auCurrentUsser = ActiveUser.getInstance();
        etName.setText(auCurrentUsser.getName());

        strName = auCurrentUsser.getUserID();
        strBio = auCurrentUsser.getBio();


        //GET PROFILE PIC
        imgProfilePic.setImageBitmap(auCurrentUsser.getUserProfileImage());

        if (strBio.equals("") || strBio.equals(null) || strBio == "" || strBio == null){
            strBio = "Bio - Not Set";
            etBio.setText(strBio);
        }else{etBio.setText(strBio);}




    }

    public void revealEditOptions(View view) {

        final int status = (Integer) view.getTag();

        if(status == 1) {
            etName.setEnabled(true);
            etBio.setEnabled(true);
            imbtEditProfilePic.setVisibility(View.VISIBLE);

            view.setTag(0);
            btnEdit.setText("Save Changes");
            btnEdit.setCompoundDrawablesWithIntrinsicBounds(ic_menu_save, 0, 0, 0);
        }else {
            //SAVE CHANGES CALL
            etName.setEnabled(false);
            etBio.setEnabled(false);
            imbtEditProfilePic.setVisibility(View.INVISIBLE);

            view.setTag(1);
            btnEdit.setText("Edit Profile");
            btnEdit.setCompoundDrawablesWithIntrinsicBounds(ic_menu_edit, 0, 0, 0);
        }
    }

    public void ChangeProfilePic(View view) {

        //SET NEW PROFILE IMAGE
    }

    public void SaveChanged(){
        //WRITE PHP SCRIPT TO UPDATE USERS INFO
    }

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

//WRITE SCRIPT TO
//COUNT PRIVATE_CHAT table Rows for userID
//COUNT ChatRoom_Users table Rows for userID


//COMPLETE PROFILE ACTIVITY
//ADD CUSTOM ADAPTER TO FIND FRIENDS ACTIVITY SHOW NAME AND IMAGE