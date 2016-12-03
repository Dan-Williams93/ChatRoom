package com.example.daniel.chatroomapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    private String strUserName, strUserID, strEmail, strUserBio;
    private Bitmap bitProfilePicture;

    private ImageView imgProfileImage;
    private TextView tvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgProfileImage = (ImageView)findViewById(R.id.imgProfileImage);
        tvTest = (TextView)findViewById(R.id.tvTest);

        strUserName = getIntent().getStringExtra("username");
        strUserID = getIntent().getStringExtra("user_id");
        strEmail = getIntent().getStringExtra("email");
        strUserBio = getIntent().getStringExtra("bio");
        byte[] byteArray = getIntent().getByteArrayExtra("profileImage");
        Bitmap bitProfileImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imgProfileImage.setImageBitmap(bitProfileImage);

        String strstring = strUserName + " " + strUserID + " " + strEmail + " " + strUserBio;

        tvTest.setText(strstring);


    }

    public void sendMessage(View view){

        //QUERY DATABASE TO SEE IF CHAT BETWEEN TWO PARTIES EXISTS
        //SELECT * FROM private_chat WHERE (member_1_id='user1_id' OR member_1_id='user2_id') AND (member_2_id='user1_id' or member_2_id='user2_id');


        //IF THERE IS A RESULT GET CHAT ID, SEND IT TO CHATROOM ACTIVITY TO GET MESSAGES

        //IF NO MESSAGES CREATE NEW CHAT INSERT INTO DATABASE AND THEN OPEN NE CHAT ACTIVITY

        //IN PRIVATE MESSAGES ACTIVITY QUERY DATABASASE TO GET CHAT DETAILS
    }
}
