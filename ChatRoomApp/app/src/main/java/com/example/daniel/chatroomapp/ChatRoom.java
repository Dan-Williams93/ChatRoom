package com.example.daniel.chatroomapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom extends AppCompatActivity {

    //region GLOBAL VARIABLES
    private ImageView imChatImage;
    private TextView tvChatName;
    private EditText etMessage;
    private Button btnSend;
    private RecyclerView recMessages;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;


    private ActiveUser auActiveUser;

    private String strActiveUserID, strActiveUserName, strChatID, strRecipientID, strChatName, strNewMessage, strDateTime;
    private String strFetchMessageURL, strSendNotificationURL, strSendMessageURL;
    private Bitmap bitChatImage;
    private ArrayList<Message> arMessages = new ArrayList<Message>();

    private BroadcastReceiver mNotificationBroadcastReciever;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        strFetchMessageURL = getString(R.string.FetchMessagesURL);
        strSendNotificationURL = getString(R.string.SendNotificationURL);
        strSendMessageURL = getString(R.string.SendMessageURL);

        imChatImage = (ImageView)findViewById(R.id.imChatImage);
        tvChatName = (TextView)findViewById(R.id.tvChatName);
        etMessage = (EditText)findViewById(R.id.etMessage);
        btnSend = (Button)findViewById(R.id.btnSend);

        recMessages = (RecyclerView)findViewById(R.id.recMessages);
        recMessages.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recMessages.setLayoutManager(layoutManager);


        auActiveUser = ActiveUser.getInstance();
        strActiveUserID = auActiveUser.getUserID();

        strChatID = getIntent().getExtras().getString("Chat_ID");
        strChatName = getIntent().getExtras().getString("Chat_Name");
        strRecipientID = getIntent().getExtras().getString("recipient_id");

        byte[] byteArray = getIntent().getByteArrayExtra("Chat_Image");
        bitChatImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        tvChatName.setText(strChatID + " " + strChatName);
        imChatImage.setImageBitmap(bitChatImage);

        fetchMessages();

        mNotificationBroadcastReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals("pushnotification")){

                    String strID = intent.getStringExtra("id");

                    if (strID.equals(strChatID)){
                        //arMessages.clear();
                        //fetchMessages();
                        String message = intent.getStringExtra("message");
                        String name = intent.getStringExtra("name");
                        processNewMesage(message, name);
                    }
                }
            }
        };
    }

    public void processNewMesage(String message, String name){
        Message m = new Message("",message,getTimeStamp().toString(),name);
        arMessages.add(m);
        adapter.notifyDataSetChanged();
        scrollToBottom();
    }

    public void fetchMessages(){
        //fetch messages from database using

        StringRequest stringRequest = new StringRequest(Request.Method.POST, strFetchMessageURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String strResult = jsonObject.getString("result");

                            if (strResult.equals("1")){
                                JSONArray jsonArray = jsonObject.getJSONArray("response");

                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jsonMessage = jsonArray.getJSONObject(i);

                                    String strUserID = jsonMessage.getString("user_id");
                                    String strUsername = jsonMessage.getString("username");
                                    String strMessage = jsonMessage.getString("message");
                                    String strTimeStamp = jsonMessage.getString("time_stamp");

                                    Message newMessage = new Message(strUserID, strMessage, strTimeStamp, strUsername);

                                    arMessages.add(newMessage);
                                }
                            }

                            adapter = new ChatThreadAdapter(ChatRoom.this, arMessages, strActiveUserID);
                            recMessages.setAdapter(adapter);
                            scrollToBottom();

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
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("id", strChatID);
                        return params;
                    }
                };

        VolleyQueueSingleton.getmInstance(ChatRoom.this).addToRequestQueue(stringRequest);

    }

    private void scrollToBottom() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1)
            recMessages.getLayoutManager().smoothScrollToPosition(recMessages, null, adapter.getItemCount() - 1);
    }

    public void sendMessage(View view){
        strNewMessage = etMessage.getText().toString().trim();
        strActiveUserName = auActiveUser.getName();
        strDateTime = getTimeStamp();

        if (!strNewMessage.equals("")){

            Message m = new Message(strActiveUserID, strNewMessage, strDateTime, strActiveUserName);

            //send the message to database
            uploadMessage(m);

        }
    }

    public void uploadMessage(final Message msgNewMessage){

        StringRequest uploadMessageRequest = new StringRequest(Request.Method.POST, strSendMessageURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String strResult = jsonObject.getString("result");

                            if (strResult.equals("1")){

                                sendNotification();

                                //CHECK IF CHAT IS EMPTY
                                if (arMessages.size() > 0) {
                                    arMessages.add(msgNewMessage);
                                    adapter.notifyDataSetChanged();
                                    scrollToBottom();
                                }else{
                                    arMessages.add(msgNewMessage);
                                    adapter = new ChatThreadAdapter(ChatRoom.this, arMessages, strActiveUserID);
                                    recMessages.setAdapter(adapter);
                                }
                                
                                etMessage.setText("");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return;
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
                params.put("chat_id", strChatID);
                params.put("user_id", msgNewMessage.getUsersId());
                params.put("username", msgNewMessage.getName());
                params.put("message", msgNewMessage.getMessage());
                params.put("timestamp", msgNewMessage.getSentAt());
                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(ChatRoom.this).addToRequestQueue(uploadMessageRequest);
    }

    public void sendNotification(){

        StringRequest notificationRequest = new StringRequest(Request.Method.POST, strSendNotificationURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String str = response;
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
                params.put("message", strNewMessage);
                params.put("chat_name", auActiveUser.getName()); ///changed from strChatName
                params.put("user_ids", strRecipientID);
                params.put("chat_id", strChatID);
                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(ChatRoom.this).addToRequestQueue(notificationRequest);
    }

    private String getTimeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    //Registering broadcast receivers
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationBroadcastReciever,
                new IntentFilter("pushnotification"));
    }


    //Unregistering receivers
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationBroadcastReciever);
    }
}
