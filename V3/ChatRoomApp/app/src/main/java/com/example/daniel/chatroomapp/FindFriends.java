package com.example.daniel.chatroomapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindFriends extends AppCompatActivity {

    //region GLOBAL VARIABLES

    //region CONSTANTS
    //region URL'S
    private String strSearchFriendsURL;
    //endregion
    //region VARIABLES
    private ActiveUser auCurrentUser;
    private ArrayList<Friend> arFriends;
    //endregion
    //endregion

    //region VIEWS
    private EditText etSearchFriends;
    private ListView lvResults;
    private TextView tvNoFriends;
    private ProgressBar progLoading;
    //endregion

    //region VARIABLES
    private String strSearchString;
    //endregion

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        strSearchFriendsURL = getString(R.string.SearchFriendsURL);

        //region CONSTANT INSTANTIATION
        auCurrentUser = ActiveUser.getInstance();
        arFriends = new ArrayList<Friend>();
        //endregion

        //region VARIABLE INSTANTIATION
        strSearchString = "";
        //endregion

        //region VIEW CASTING
        etSearchFriends = (EditText)findViewById(R.id.etSearchFriends);
        lvResults = (ListView)findViewById(R.id.lvResults);
        tvNoFriends = (TextView)findViewById(R.id.tvNoFriends);
        progLoading = (ProgressBar)findViewById(R.id.progLoading);
        //endregion

        //region EDIT TEXT TEXT WATCHER
        etSearchFriends.addTextChangedListener(new TextWatcher() {

            Handler handler = new Handler(){

                @Override
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == 1) {
                        strSearchString = etSearchFriends.getText().toString().trim();

                        arFriends.clear();
                        lvResults.setAdapter(null);
                        SearchFriends(strSearchString);
                    }

                    if (msg.what == 2) {
                        arFriends.clear();
                        lvResults.setAdapter(null);
                        SearchFriends("");
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

//                    strSearchString = etSearchFriends.getText().toString().trim();
//
//                    arFriends.clear();
//                    SearchFriends(strSearchString);

                }else if (s.toString().trim().length() == 0) {

                    handler.removeMessages(1);
                    handler.removeMessages(2);
                    handler.sendEmptyMessageDelayed(2, 500);

//                    arFriends.clear();
//                    SearchFriends("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //endregion

        //region LIST VIEW CLICK OBSERVER
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.rowImage);

                Friend selectedFriend = arFriends.get(position);

                String strSelectedUserID = selectedFriend.getFriendUsersId();
                String strSelectedName = selectedFriend.getFriendName();
                String strSelectedEmail = selectedFriend.getFriendEmail();
                String strSelectedUserBio = selectedFriend.getFriendBio();
                Bitmap bitSelectedProfileImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

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

    private void SearchFriends(final String strSearchValue) {

        progLoading.setVisibility(View.VISIBLE);

        StringRequest searchFriendsRequest = new StringRequest(Request.Method.POST, strSearchFriendsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("status");

                    if (result.equals("1")){

                        progLoading.setVisibility(View.INVISIBLE);
                        tvNoFriends.setVisibility(View.INVISIBLE);

                        JSONArray JsonArray = jsonObject.getJSONArray("result");

                        for(int i = 0; i < JsonArray.length(); i++){

                            JSONObject jsonMessage = JsonArray.getJSONObject(i);

                            if (jsonMessage != null){

                                String strFriendID = jsonMessage.getString("user_id");
                                String strFriendName = jsonMessage.getString("Username");
                                String strFriendEmail = jsonMessage.getString("email");
                                String strFriendBio = jsonMessage.getString("user_bio");
                                String strFriendImageURL = jsonMessage.getString("profile_image_url");

                                Friend newFriend = new Friend(strFriendID, strFriendName, strFriendEmail, strFriendBio, strFriendImageURL);
                                arFriends.add(newFriend);

                            }
                        }

                        FriendsArrayAdapter custom_listView_adapter = new FriendsArrayAdapter(FindFriends.this, arFriends);
                        lvResults.setAdapter(custom_listView_adapter);


                    }else {
                        progLoading.setVisibility(View.INVISIBLE);
                        tvNoFriends.setVisibility(View.VISIBLE);
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
                params.put("search_credential", strSearchValue);
                params.put("user_id", auCurrentUser.getUserID());

                return params;
            }
        };

        VolleyQueueSingleton.getmInstance(FindFriends.this).addToRequestQueue(searchFriendsRequest);
    }

    public void goHome(View view){
        startActivity(new Intent(this, ChatRoomGallery.class));
    }
}
