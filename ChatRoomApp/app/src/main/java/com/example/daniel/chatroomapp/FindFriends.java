package com.example.daniel.chatroomapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindFriends extends AppCompatActivity {

    private EditText etSearchFriends;
    private ListView lvResults;
    //private static final String strSearchFriendsURL = "http://80.0.165.187/chatroomapp/search_friend.php";
    private String strSearchFriendsURL;

    private ArrayList<String> arFriendsNames = new ArrayList<String>();
    private ArrayList<String> arFriendsEmail = new ArrayList<String>();
    private ArrayList<String> arFriendsUserID = new ArrayList<String>();
    private ArrayList<String> arFriendsBio = new ArrayList<String>();
    private ArrayList<Bitmap> arFriendProfileImage = new ArrayList<Bitmap>();

    private Bitmap bitDefaultProfileImage;
    private Bitmap bitProfileImage;

    private String strProfileImageUrL;
    private String strSearchCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        strSearchFriendsURL = getString(R.string.SearchFriendsURL);

        bitDefaultProfileImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.anonymous);

        etSearchFriends = (EditText)findViewById(R.id.etSearchFriends);
        lvResults = (ListView)findViewById(R.id.lvResults);

        etSearchFriends.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0){
                    arFriendsNames.clear();
                    arFriendsUserID.clear();;
                    arFriendsEmail.clear();
                    strSearchCredential = etSearchFriends.getText().toString();
                    SearchFriends(strSearchCredential);
                }else if (s.toString().trim().length() == 0) {
                    arFriendsNames.clear();
                    arFriendsUserID.clear();
                    arFriendsEmail.clear();
                    lvResults.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //region LIST VIEW CLICK OBSERVER
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String strSelectedName = arFriendsNames.get(position);
                String strSelectedUserID = arFriendsUserID.get(position);
                String strSelectedEmail = arFriendsEmail.get(position);
                String strSelectedUserBio = arFriendsBio.get(position);
                Bitmap bitSelectedProfileImage = arFriendProfileImage.get(position);

                Intent viewProfileIntent = new Intent(FindFriends.this, Profile.class);
                viewProfileIntent.putExtra("username", strSelectedName);
                viewProfileIntent.putExtra("user_id", strSelectedUserID);
                viewProfileIntent.putExtra("email", strSelectedEmail);
                viewProfileIntent.putExtra("bio", strSelectedUserBio);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitSelectedProfileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                viewProfileIntent.putExtra("profileImage",byteArray );
                startActivity(viewProfileIntent);

            }
        });
        //endregion

        SearchFriends("");
    }

    private void SearchFriends(final String strSearchCredential) {

        StringRequest searchFriendsRequest = new StringRequest(Request.Method.POST, strSearchFriendsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("status");

                    if (result.equals("1")){
                        JSONArray JsonArray = jsonObject.getJSONArray("result");

                        for(int i = 0; i < JsonArray.length(); i++){

                            JSONObject jsonMessage = JsonArray.getJSONObject(i);

                            if (jsonMessage != null){
                                arFriendsNames.add(jsonMessage.getString("Username"));
                                arFriendsEmail.add(jsonMessage.getString("email"));
                                arFriendsUserID.add(jsonMessage.getString("user_id"));
                                arFriendsBio.add(jsonMessage.getString("user_bio"));

                                strProfileImageUrL = jsonMessage.getString("profile_image_url");

                                if (strProfileImageUrL.equals("not specified")){
                                    arFriendProfileImage.add(bitDefaultProfileImage);
                                }else{

                                    //DOWNLOAD IMAGE FROM URL AND STORE IN ACTIVE USER
                                }

                            }

                        }

                        //CHANGE FOR CUSTOM ADAPTER
                        //ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(FindFriends.this, android.R.layout.simple_list_item_1, arFriendsNames);
                        //lvResults.setAdapter(itemsAdapter);

                        FriendsArrayAdapter custom_listView_adapter = new FriendsArrayAdapter(FindFriends.this, arFriendsNames, arFriendProfileImage);
                        lvResults.setAdapter(custom_listView_adapter);
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
                params.put("search_credential", strSearchCredential);

                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(FindFriends.this).addToRequestQueue(searchFriendsRequest);
    }
}
