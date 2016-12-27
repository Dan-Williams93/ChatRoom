package com.example.daniel.chatroomapp;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.*;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//##################################################################################################
//##
//##
//##
//##################################################################################################


public class PrivateMessages extends AppCompatActivity {

    //region GLOBAL VARIABLES

    //region VIEWS
    private TextView tvNoChats;
    private ListView lvChatResults;
    private EditText etFindChat;
    private ProgressBar progLoading;
    //endregion

    //region CONSTANTS
    //region URL'S
    private String strGetDetailsURL;
    private String strSearchDetailsURL;
    //endregion
    //region VARIABLES
    private ActiveUser auCurrentUser;
    //endregion
    //endregion

    //region VARIABLES
    ArrayList<PrivateChat> arChats;
    //endregion

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_messages);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //region CONSTANTS INITIALISATION
        //region URL's
        strGetDetailsURL = getString(R.string.GetPrivateChatDetailsURL);
        strSearchDetailsURL = getString(R.string.SearchPrivateChatDetailsURL);
        //endregion
        //region VARIABLES
        auCurrentUser = ActiveUser.getInstance();
        //endregion
        //endregion

        //region VIEWS
        tvNoChats = (TextView)findViewById(R.id.tvNoChats);
        etFindChat = (EditText)findViewById(R.id.etFindChat);
        lvChatResults = (ListView)findViewById(R.id.lvChatResults);
        progLoading = (ProgressBar)findViewById(R.id.progLoading);
        //endregion

        //region VARIABLE INSTANTIATION
        arChats = new ArrayList<PrivateChat>();
        //endregion

        GetChatDetails();



        //region SEARCH TEXT WATCHER
        etFindChat.addTextChangedListener(new TextWatcher() {

            Handler handler = new Handler(){

                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        arChats.clear();
                        lvChatResults.setAdapter(null);
                        SearchChatDetails();
                    }

                    if (msg.what == 2) {
                        arChats.clear();
                        lvChatResults.setAdapter(null);
                        tvNoChats.setVisibility(View.INVISIBLE);
                        GetChatDetails();
                    }
                }
            };

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0){
                    handler.removeMessages(1);
                    handler.removeMessages(2);
                    handler.sendEmptyMessageDelayed(1, 500);

//                    arChats.clear();
//                    lvChatResults.setAdapter(null);
//                    SearchChatDetails();
                }else if (s.toString().trim().length() == 0) {
                    handler.removeMessages(1);
                    handler.removeMessages(2);
                    handler.sendEmptyMessageDelayed(2, 500);

//                    arChats.clear();
//                    lvChatResults.setAdapter(null);
//                    tvNoChats.setVisibility(View.INVISIBLE);
//                    GetChatDetails();
                }
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //endregion

        //region LIST VIEW ON CLICK LISTENER
        lvChatResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //OPEN CHAT WITH DETAILS OF SELECTED ROW

                NetworkImageView imageView = (NetworkImageView)view.findViewById(R.id.rowImage);

                PrivateChat newChat = arChats.get(position);

                String strSelectedChatID = newChat.getChatId();
                String strSelectedChatName = newChat.getChatName();
                String strSellectedRecipientID = newChat.getRecipientID();
                String strSelectedImageURL = newChat.getChatImageURL();
                Bitmap bitSelectedChatImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();


                Intent chatIntent = new Intent(PrivateMessages.this, ChatRoom.class);
                chatIntent.putExtra("Chat_ID", strSelectedChatID);
                chatIntent.putExtra("Chat_Name", strSelectedChatName);
                chatIntent.putExtra("recipient_id", strSellectedRecipientID);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitSelectedChatImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                chatIntent.putExtra("Chat_Image", byteArray);

                chatIntent.putExtra("imageURL", strSelectedImageURL);

                startActivity(chatIntent);
            }
        });
        //endregion

    }


    private void GetChatDetails(){

        progLoading.setVisibility(View.VISIBLE);

        StringRequest getDetailsRequest = new StringRequest(Request.Method.POST, strGetDetailsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progLoading.setVisibility(View.INVISIBLE);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String strResult = jsonObject.getString("result");

                            if (strResult.equals("1")){

                                JSONArray jsonIDSArray = null;
                                JSONArray jsonNamesArray = null;
                                JSONArray jsonRecipientIDArray = null;
                                JSONArray jsonMessagesArray = null;
                                JSONArray jsonURLArray = null;

                                try {
                                    jsonIDSArray = jsonObject.getJSONArray("chat_ids");
                                    jsonNamesArray = jsonObject.getJSONArray("chat_names");
                                    jsonRecipientIDArray = jsonObject.getJSONArray("recipient_id");
                                    jsonMessagesArray = jsonObject.getJSONArray("chat_messages");
                                    jsonURLArray = jsonObject.getJSONArray("chat_images");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    tvNoChats.setText("Error parsing data!");
                                    tvNoChats.setVisibility(View.VISIBLE);
                                }

                                //CHECK ALL ARE NOT NULL
                                if (jsonIDSArray != null && jsonNamesArray != null && jsonRecipientIDArray != null && jsonMessagesArray != null
                                        && jsonURLArray != null){

                                    for (int i = 0; i < jsonIDSArray.length(); i++) {

                                        String strChatID = jsonIDSArray.get(i).toString().trim();
                                        String strChatName = jsonNamesArray.get(i).toString().trim();
                                        String strRecipientID = jsonRecipientIDArray.get(i).toString().trim();
                                        String strChatMessage;

                                        try {
                                            strChatMessage = jsonMessagesArray.get(i).toString();
                                            if (strChatMessage.equals(null)){
                                                strChatMessage = " ";
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            strChatMessage = " ";
                                        }

                                        String strChatImageURL = jsonURLArray.get(i).toString().trim();

                                        PrivateChat newPrivateChat = new PrivateChat(strChatID, strChatName, strRecipientID, strChatMessage, strChatImageURL);
                                        arChats.add(newPrivateChat);
                                    }
                                }

                                //REMOVED TO ALLOW CHAT IMAGES TO LOAD
                                PrivateMessageAdapter privateMessageAdapter = new PrivateMessageAdapter(PrivateMessages.this, arChats);
                                lvChatResults.setAdapter(privateMessageAdapter);

                            }else{
                                //SHOW NO CHATS FOUND
                                tvNoChats.setText("No chats found!");
                                tvNoChats.setVisibility(View.VISIBLE);
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", auCurrentUser.getUserID());
                params.put("user_name", auCurrentUser.getName());

                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(PrivateMessages.this).addToRequestQueue(getDetailsRequest);
    }

    private void SearchChatDetails(){

        progLoading.setVisibility(View.VISIBLE);

        StringRequest getDetailsRequest = new StringRequest(Request.Method.POST, strSearchDetailsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progLoading.setVisibility(View.INVISIBLE);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String strResult = jsonObject.getString("result");

                            if (strResult.equals("1")){

                                tvNoChats.setVisibility(View.INVISIBLE);

                                JSONArray jsonIDSArray = null;
                                JSONArray jsonNamesArray = null;
                                JSONArray jsonRecipientIDArray = null;
                                JSONArray jsonMessagesArray = null;
                                JSONArray jsonURLArray = null;

                                try {
                                    jsonIDSArray = jsonObject.getJSONArray("chat_ids");
                                    jsonNamesArray = jsonObject.getJSONArray("chat_names");
                                    jsonRecipientIDArray = jsonObject.getJSONArray("recipient_id");
                                    jsonMessagesArray = jsonObject.getJSONArray("chat_messages");
                                    jsonURLArray = jsonObject.getJSONArray("chat_images");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    tvNoChats.setText("Error parsing data!");
                                    tvNoChats.setVisibility(View.VISIBLE);
                                }

                                //CHECK ALL ARE NOT NULL
                                if (jsonIDSArray != null && jsonNamesArray != null && jsonRecipientIDArray != null && jsonMessagesArray != null
                                        && jsonURLArray != null){

                                    for (int i = 0; i < jsonIDSArray.length(); i++) {

                                        String strChatID = jsonIDSArray.get(i).toString().trim();
                                        String strChatName = jsonNamesArray.get(i).toString().trim();
                                        String strRecipientID = jsonRecipientIDArray.get(i).toString().trim();
                                        String strChatMessage;

                                        try {
                                            strChatMessage = jsonMessagesArray.get(i).toString();
                                            if (strChatMessage.equals(null)){
                                                strChatMessage = " ";
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            strChatMessage = " ";
                                        }

                                        String strChatImageURL = jsonURLArray.get(i).toString().trim();

                                        PrivateChat newPrivateChat = new PrivateChat(strChatID, strChatName, strRecipientID, strChatMessage, strChatImageURL);
                                        arChats.add(newPrivateChat);
                                    }


                                }

                                PrivateMessageAdapter privateMessageAdapter = new PrivateMessageAdapter(PrivateMessages.this, arChats);
                                lvChatResults.setAdapter(privateMessageAdapter);

                            }else{
                                //SHOW NO CHATS FOUND
                                tvNoChats.setText("No chats found!");
                                tvNoChats.setVisibility(View.VISIBLE);
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", auCurrentUser.getUserID());
                params.put("user_name", auCurrentUser.getName());
                params.put("search_credential", etFindChat.getText().toString());

                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(PrivateMessages.this).addToRequestQueue(getDetailsRequest);
    }

    public void goHome(View view){
        startActivity(new Intent(this, ChatRoomGallery.class));
    }

}
