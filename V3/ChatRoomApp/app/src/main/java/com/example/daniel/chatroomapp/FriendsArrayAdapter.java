package com.example.daniel.chatroomapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
/**
 * Created by Daniel on 02/12/2016.
 */

public class FriendsArrayAdapter  extends ArrayAdapter{

    //region GLOBAL VARIABLES
    private Activity context;
    private ArrayList<Friend> arFriends;
    private ImageLoader imageLoader = VolleyQueueSingleton.getmInstance(context).getImageLoader();
    //endregion

    //ADAPTER CONSTRUCTOR RECEIVING THE CALLING CONTEXT PLUS ARRAY LISTS CONTAINING THE RECIPE NAMES AND RECIPE IMAGES
    public FriendsArrayAdapter(Activity context, ArrayList<Friend> arFriends) {
        super(context, R.layout.custom_friends_array_row, arFriends);

        //region SETTING GLOBAL VARIABLES VALUES FROM PASSED DATA
        this.context = context;
        this.arFriends = arFriends;
        //endregion
    }

    public View getView(int position, View view, ViewGroup parent){

        LayoutInflater inflater = context.getLayoutInflater();
        View viewRow = inflater.inflate(R.layout.custom_friends_array_row, null, true);       //INFLATES THE LIST PLACING THE COMPONENTS OF THE STATED XML FILE IN EACH ROW

        //CASTS ROW COMPONENTS
        TextView txtFriendName = (TextView) viewRow.findViewById(R.id.rowText);
        NetworkImageView imFriendProfileImage = (NetworkImageView) viewRow.findViewById(R.id.rowImage);

        Friend newFriend = arFriends.get(position);
        //SETS THE VALUES OF THE COMPONENTS TO THE VALUES OF THE ARRAY DATA PASSED IN AT THE POSITION OF THE CURRENT ROW
        imFriendProfileImage.setImageUrl(newFriend.getFriendImageURL(), imageLoader);
        txtFriendName.setText(newFriend.getFriendName());

        return viewRow;
    }
}
