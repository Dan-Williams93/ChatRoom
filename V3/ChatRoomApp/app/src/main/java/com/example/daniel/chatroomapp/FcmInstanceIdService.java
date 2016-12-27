package com.example.daniel.chatroomapp;

import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 02/12/2016.
 */

public class FcmInstanceIdService extends FirebaseInstanceIdService {



    @Override
    public void onTokenRefresh() {
        String strRecentToken = FirebaseInstanceId.getInstance().getToken();

        SharedPreferences ChatPrefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
        SharedPreferences.Editor editor = ChatPrefs.edit();
        editor.putString(getString(R.string.FCM_TOKEN_PREF), strRecentToken);
        editor.commit();

        sendTokenToServer(strRecentToken);

    }

    public void sendTokenToServer (final String strToken){

        //check if there is an active user if not do nothing if there is...
        //retrieve active user from either user class or shared preferences
        //update database
        //will need a new PHP script to update refference in database
        //String strUpdateTokenURL = "http://80.0.165.187/chatroomapp/update_token.php";
        String strUpdateTokenURL = getString(R.string.UpdateTokenURL);

        SharedPreferences ChatRoomPrefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
        Boolean isLogged = ChatRoomPrefs.getBoolean(getString(R.string.isLogged), false);

        if (isLogged){
            final String strUserID = ChatRoomPrefs.getString(getString(R.string.UserID), "");
            if (!strUserID.equals("")){

                StringRequest updateTokenRequest = new StringRequest(Request.Method.POST, strUpdateTokenURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
                        params.put("user_id", strUserID);
                        params.put("new_token", strToken);

                        return params;
                    }
                };

                VolleyQueueSingleton.getmInstance(this).addToRequestQueue(updateTokenRequest);
            }
        }
    }
}
