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
    private ArrayList<String> arChatName;
    private ArrayList<String> arChatMessage;
    private ArrayList<Bitmap> arChatImage;
    //endregion

    //ADAPTER CONSTRUCTOR RECEIVING THE CALLING CONTEXT PLUS ARRAY LISTS CONTAINING THE RECIPE NAMES AND RECIPE IMAGES
    public PrivateMessageAdapter(Activity context, ArrayList<String> arChatName, ArrayList<Bitmap> arChatImage, ArrayList<String> arChatMessage) {
        super(context, R.layout.custom_privatemessage_array_row, arChatName);

        //region SETTING GLOBAL VARIABLES VALUES FROM PASSED DATA
        this.context = context;
        this.arChatName = arChatName;
        this.arChatMessage = arChatMessage;
        this.arChatImage = arChatImage;
        //endregion
    }

    public View getView(int position, View view, ViewGroup parent){

        LayoutInflater inflater = context.getLayoutInflater();
        View viewRow = inflater.inflate(R.layout.custom_privatemessage_array_row, null, true);       //INFLATES THE LIST PLACING THE COMPONENTS OF THE STATED XML FILE IN EACH ROW

        //CASTS ROW COMPONENTS
        TextView txtChatName = (TextView) viewRow.findViewById(R.id.rowText);
        TextView txtChatMessage = (TextView) viewRow.findViewById(R.id.rowMessage);
        ImageView imChatImage = (ImageView) viewRow.findViewById(R.id.rowImage);

        //SETS THE VALUES OF THE COMPONENTS TO THE VALUES OF THE ARRAY DATA PASSED IN AT THE POSITION OF THE CURRENT ROW
        imChatImage.setImageBitmap(arChatImage.get(position));
        txtChatName.setText(arChatName.get(position));
        txtChatMessage.setText(arChatMessage.get(position));

        return viewRow;
    }
}
