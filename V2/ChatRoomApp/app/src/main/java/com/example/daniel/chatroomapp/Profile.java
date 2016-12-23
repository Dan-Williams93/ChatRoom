package com.example.daniel.chatroomapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private String strUserName, strUserID, strEmail, strUserBio, strActiveUserID, strChatId, strChatName, strNewChatID;
    private String strChatCheckURL, strCreateChatURL, strSendMessageURL;
    private Bitmap bitProfileImage;

    private ImageView imgProfileImage;
    private TextView tvName, tvBio;

    private ActiveUser auActiveUser = ActiveUser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        strActiveUserID = auActiveUser.getUserID();
        strChatCheckURL = getString(R.string.ChatCheckURL);
        strCreateChatURL = getString(R.string.CreateChatURL);

        imgProfileImage = (ImageView)findViewById(R.id.imgProfileImage);
        tvName = (TextView)findViewById(R.id.tvName);
        tvBio = (TextView)findViewById(R.id.tvBio);

        strUserName = getIntent().getStringExtra("username");
        strUserID = getIntent().getStringExtra("user_id");
        strEmail = getIntent().getStringExtra("email");
        strUserBio = getIntent().getStringExtra("bio");
        byte[] byteArray = getIntent().getByteArrayExtra("profileImage");
        bitProfileImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imgProfileImage.setImageBitmap(bitProfileImage);


        tvName.setText(strUserName);
        tvBio.setText("Bio:\n" + strUserBio);

    }

    public void sendMessage(View view){

        StringRequest chatCheckRequest = new StringRequest(Request.Method.POST, strChatCheckURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String strResult = jsonObject.getString("result");

                            if (strResult.equals("1")){

                                startChat(jsonObject);


                            }else{
                                createNewChat();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                Map<String, String> params = new HashMap<>();
                params.put("active_id", strActiveUserID);
                params.put("recipient_id", strUserID );
                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(Profile.this).addToRequestQueue(chatCheckRequest);
    }

    public void startChat(JSONObject jsonResponse){

        try {
            JSONArray jsonArray = jsonResponse.getJSONArray("response");

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonMessage = jsonArray.getJSONObject(i);
                strChatId = jsonMessage.getString("chat_id");
            }

            Intent chatIntent = new Intent(Profile.this, ChatRoom.class);
            chatIntent.putExtra("Chat_ID", strChatId);
            chatIntent.putExtra("Chat_Name", strUserName);
            chatIntent.putExtra("recipient_id", strUserID);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitProfileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            chatIntent.putExtra("Chat_Image", byteArray);

            startActivity(chatIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createNewChat(){

        strChatName = auActiveUser.getName()+","+strUserName;

        StringRequest newChatRequest = new StringRequest(Request.Method.POST, strCreateChatURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String strResult = jsonObject.getString("result");

                            if (strResult.equals("1")){
                                strNewChatID = jsonObject.getString("chat_id");
                                //startNewChat();
                                insertMessage();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                Map<String, String> params = new HashMap<>();
                params.put("active_id", strActiveUserID);
                params.put("recipient_id", strUserID);
                params.put("chat_name", strChatName);
                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(Profile.this).addToRequestQueue(newChatRequest);
    }

    public void insertMessage(){

        strSendMessageURL = getString(R.string.SendMessageURL);

        StringRequest insertRequest = new StringRequest(Request.Method.POST, strSendMessageURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String strResult = jsonObject.getString("result");

                            if (strResult.equals("1")){
                                startNewChat();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                Map<String, String> params = new HashMap<>();

                params.put("chat_id", strNewChatID);
                params.put("user_id", strActiveUserID);
                params.put("username", auActiveUser.getName());
                params.put("message", " ");
                params.put("timestamp", " ");

                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(Profile.this).addToRequestQueue(insertRequest);
    }

    public void startNewChat(){
        Intent chatIntent = new Intent(Profile.this, ChatRoom.class);
        chatIntent.putExtra("Chat_ID", strNewChatID);
        chatIntent.putExtra("Chat_Name", strUserName);
        chatIntent.putExtra("recipient_id", strUserID);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitProfileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        chatIntent.putExtra("Chat_Image", byteArray);

        startActivity(chatIntent);
    }
}
