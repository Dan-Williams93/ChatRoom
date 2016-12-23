package com.example.daniel.chatroomapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class ChatRoomGallery extends AppCompatActivity {

    private TextView tvTest;
    private ActiveUser auCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_gallery);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        tvTest = (TextView)findViewById(R.id.tvTest);

        auCurrentUser = ActiveUser.getInstance();

//        tvTest.setText("isLogged: " + auCurrentUser.getIsLoggedIn().toString() + "\n" +
//                        "User ID: " + auCurrentUser.getUserID().toString() + "\n" +
//                        "Name: " + auCurrentUser.getName().toString() + "\n" +
//                        "Email: " + auCurrentUser.getEmail().toString() + "\n" +
//                        "Token: " + auCurrentUser.getUsersFCMToken().toString());
        tvTest.setText("Coming Soon!");

    }

    public void searchFriends(View view) {
        startActivity(new Intent(this, FindFriends.class));
    }

    public void viewMyProfile(View view) {

        startActivity(new Intent(this, MyProfile.class));
    }

    public void goToMessages(View view){
        startActivity(new Intent(this, PrivateMessages.class));
    }

    @Override
    public void onBackPressed() {

    }
}
