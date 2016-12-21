package com.example.daniel.chatroomapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ChatRoomGallery extends AppCompatActivity {

    private TextView tvTest;
    private ActiveUser auCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_gallery);

        tvTest = (TextView)findViewById(R.id.tvTest);

        auCurrentUser = ActiveUser.getInstance();

        tvTest.setText("isLogged: " + auCurrentUser.getIsLoggedIn().toString() + "\n" +
                        "User ID: " + auCurrentUser.getUserID().toString() + "\n" +
                        "Name: " + auCurrentUser.getName().toString() + "\n" +
                        "Email: " + auCurrentUser.getEmail().toString() + "\n" +
                        "Token: " + auCurrentUser.getUsersFCMToken().toString());

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
}
