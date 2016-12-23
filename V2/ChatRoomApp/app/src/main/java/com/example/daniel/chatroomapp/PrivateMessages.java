package com.example.daniel.chatroomapp;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private TextView tvNoChats;
    private ListView lvChatResults;
    private EditText etFindChat;
    private ProgressBar progLoading;

    private ArrayList<String> arChatIDs = new ArrayList<String>();
    private ArrayList<String> arChatName = new ArrayList<String>();
    private ArrayList<String> arRecipientIDs = new ArrayList<String>();
    private ArrayList<String> arChatMessage = new ArrayList<String>();
    private ArrayList<Bitmap> arChatBitMap = new ArrayList<Bitmap>();
    private ArrayList<String> arImageURLS = new ArrayList<String>();

    private Bitmap bitDefault;

    private ActiveUser auCurrentUser;

    //private static final String strGetDetailsURL = "http://80.0.165.187/chatroomapp/getpcdetails.php";
    //private static final String strSearchDetailsURL = "http://80.0.165.187/chatroomapp/search_pc.php";
    private String strGetDetailsURL;
    private String strSearchDetailsURL;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_messages);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        strGetDetailsURL = getString(R.string.GetPrivateChatDetailsURL);
        strSearchDetailsURL = getString(R.string.SearchPrivateChatDetailsURL);

        tvNoChats = (TextView)findViewById(R.id.tvNoChats);
        etFindChat = (EditText)findViewById(R.id.etFindChat);
        lvChatResults = (ListView)findViewById(R.id.lvChatResults);
        progLoading = (ProgressBar)findViewById(R.id.progLoading);

        auCurrentUser = ActiveUser.getInstance();

        bitDefault = BitmapFactory.decodeResource(this.getResources(), R.drawable.anonymous);

        //chatDetailsRequest();
        GetChatDetails();

        etFindChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0){
                    arChatIDs.clear();
                    arChatName.clear();
                    arChatMessage.clear();
                    arChatBitMap.clear();
                    lvChatResults.setAdapter(null);
                    SearchChatDetails();
                }else if (s.toString().trim().length() == 0) {
                  //clear all and run getchat
                    arChatIDs.clear();
                    arChatName.clear();
                    arChatMessage.clear();
                    arChatBitMap.clear();
                    tvNoChats.setVisibility(View.INVISIBLE);
                    GetChatDetails();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lvChatResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //OPEN CHAT WITH DETAILS OF SELECTED ROW

                String strSelectedChatID = arChatIDs.get(position);
                String strSelectedChatName = arChatName.get(position);
                String strSellectedRecipientID = arRecipientIDs.get(position);
                Bitmap bitSelectedChatImage = arChatBitMap.get(position);
                String strSelectedImageURL = arImageURLS.get(position);

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
                                    //SHOW NO CHATS FOUND OR ERROR PARSING DATA
                                    tvNoChats.setText("Error parsing data!");
                                    tvNoChats.setVisibility(View.VISIBLE);
                                }

                                if (jsonIDSArray != null) {

                                    for (int i = 0; i < jsonIDSArray.length(); i++) {
                                        arChatIDs.add(jsonIDSArray.get(i).toString());
                                    }
                                }

                                if (jsonNamesArray != null) {

                                    for (int i = 0; i < jsonNamesArray.length(); i++) {
                                        arChatName.add(jsonNamesArray.get(i).toString());
                                    }
                                }

                                //ADDED
                                if (jsonRecipientIDArray != null){

                                    for (int i = 0; i < jsonRecipientIDArray.length(); i++) {
                                        arRecipientIDs.add(jsonRecipientIDArray.get(i).toString());
                                    }
                                }
                                //ADDED

                                if (jsonMessagesArray != null) {

                                    //if (jsonMessagesArray.length() == jsonIDSArray.length()) {

//                                        for (int i = 0; i < jsonMessagesArray.length(); i++) {
//                                            arChatMessage.add(jsonMessagesArray.get(i).toString());
//                                        }

                                    for (int i = 0; i < jsonIDSArray.length(); i++) {

                                        String mMessage;

                                        try {
                                            mMessage = jsonMessagesArray.get(i).toString();
                                            if (mMessage.equals(null)){
                                                mMessage = " ";
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            mMessage = " ";
                                        }

                                        arChatMessage.add(mMessage);

                                    }
                                    //}
                                }

                                if (jsonURLArray != null) {

                                    for (int i = 0; i < jsonURLArray.length(); i++) {
                                        String strURL = jsonURLArray.get(i).toString();

                                        arImageURLS.add(strURL);

                                        if (strURL.equals("not specified")){
                                            arChatBitMap.add(bitDefault);
                                        }else{
                                            ImageRequest request = new ImageRequest(strURL,
                                                    new Response.Listener<Bitmap>() {
                                                        @Override
                                                        public void onResponse(Bitmap bitmap) {
                                                            arChatBitMap.add(bitmap);
                                                        }
                                                    }, 0, 0, null,
                                                    new Response.ErrorListener() {
                                                        public void onErrorResponse(VolleyError error) {
                                                            arChatBitMap.add(bitDefault);
                                                        }
                                                    });
                                            // Access the RequestQueue through your singleton class.
                                            VolleyQueueSingleton.getmInstance(PrivateMessages.this).addToRequestQueue(request);
                                        }
                                    }
                                }

                                PrivateMessageAdapter privateMessageAdapter = new PrivateMessageAdapter(PrivateMessages.this, arChatName, arChatBitMap, arChatMessage);
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
                                JSONArray jsonMessagesArray = null;
                                JSONArray jsonURLArray = null;

                                try {
                                    jsonIDSArray = jsonObject.getJSONArray("chat_ids");
                                    jsonNamesArray = jsonObject.getJSONArray("chat_names");
                                    jsonMessagesArray = jsonObject.getJSONArray("chat_messages");
                                    jsonURLArray = jsonObject.getJSONArray("chat_images");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //SHOW NO CHATS FOUND OR ERROR PARSING DATA
                                    tvNoChats.setText("Error parsing data!");
                                    tvNoChats.setVisibility(View.VISIBLE);
                                }

                                if (jsonIDSArray != null) {

                                    for (int i = 0; i < jsonIDSArray.length(); i++) {
                                        arChatIDs.add(jsonIDSArray.get(i).toString());
                                    }
                                }

                                if (jsonNamesArray != null) {

                                    for (int i = 0; i < jsonNamesArray.length(); i++) {
                                        arChatName.add(jsonNamesArray.get(i).toString());
                                    }
                                }

                                if (jsonMessagesArray != null) {

                                    for (int i = 0; i < jsonMessagesArray.length(); i++) {
                                        arChatMessage.add(jsonMessagesArray.get(i).toString());
                                    }
                                }

                                if (jsonURLArray != null) {

                                    for (int i = 0; i < jsonURLArray.length(); i++) {
                                        String strURL = jsonURLArray.get(i).toString();

                                        if (strURL.equals("not specified")){
                                            arChatBitMap.add(bitDefault);
                                        }else{
                                            ImageRequest request = new ImageRequest(strURL,
                                                    new Response.Listener<Bitmap>() {
                                                        @Override
                                                        public void onResponse(Bitmap bitmap) {
                                                            arChatBitMap.add(bitmap);
                                                        }
                                                    }, 0, 0, null,
                                                    new Response.ErrorListener() {
                                                        public void onErrorResponse(VolleyError error) {
                                                            arChatBitMap.add(bitDefault);
                                                        }
                                                    });
                                            // Access the RequestQueue through your singleton class.
                                            VolleyQueueSingleton.getmInstance(PrivateMessages.this).addToRequestQueue(request);
                                        }
                                    }
                                }

                                PrivateMessageAdapter privateMessageAdapter = new PrivateMessageAdapter(PrivateMessages.this, arChatName, arChatBitMap, arChatMessage);
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
