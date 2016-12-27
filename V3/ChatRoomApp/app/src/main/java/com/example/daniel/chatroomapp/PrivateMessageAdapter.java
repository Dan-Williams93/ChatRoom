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
 * Created by Daniel on 13/12/2016.
 */

public class PrivateMessageAdapter extends ArrayAdapter{

    //region GLOBAL VARIABLES
    private Activity context;   //GETS CONTEXT OF CALLING ACTIVITY
    private ArrayList<PrivateChat> arChat;
    private ImageLoader imageLoader = VolleyQueueSingleton.getmInstance(context).getImageLoader();
    //endregion

    //ADAPTER CONSTRUCTOR RECEIVING THE CALLING CONTEXT PLUS ARRAY LISTS CONTAINING THE RECIPE NAMES AND RECIPE IMAGES
    public PrivateMessageAdapter(Activity context, ArrayList<PrivateChat> arChat) {
        super(context, R.layout.custom_privatemessage_array_row, arChat);

        //region SETTING GLOBAL VARIABLES VALUES FROM PASSED DATA
        this.context = context;
        this.arChat = arChat;
        //endregion
    }

    public View getView(int position, View view, ViewGroup parent){

        LayoutInflater inflater = context.getLayoutInflater();
        View viewRow = inflater.inflate(R.layout.custom_privatemessage_array_row, null, true);       //INFLATES THE LIST PLACING THE COMPONENTS OF THE STATED XML FILE IN EACH ROW

        //CASTS ROW COMPONENTS
        TextView txtChatName = (TextView) viewRow.findViewById(R.id.rowText);
        TextView txtChatMessage = (TextView) viewRow.findViewById(R.id.rowMessage);
        NetworkImageView imChatImage = (NetworkImageView) viewRow.findViewById(R.id.rowImage);

        PrivateChat newChat = arChat.get(position);

        //SETS THE VALUES OF THE COMPONENTS TO THE VALUES OF THE ARRAY DATA PASSED IN AT THE POSITION OF THE CURRENT ROW
        imChatImage.setImageUrl(newChat.getChatImageURL(), imageLoader);
        txtChatName.setText(newChat.getChatName());
        txtChatMessage.setText(newChat.getChatMessage());

        return viewRow;
    }
}
