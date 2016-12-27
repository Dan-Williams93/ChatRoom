package com.example.daniel.chatroomapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * Created by Daniel on 02/12/2016.
 */

public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getData().get("title"); //CHAT NAME
        String messgae = remoteMessage.getData().get("message"); //CHAT MESSAGE
        String id = remoteMessage.getData().get("id"); //CHAT ID

        //ADDED
        String user_id = remoteMessage.getData().get("user_id"); //USER_ID

        byte[] bytes;
        //ADDED

        //GET AND DISPLAY SYSTEM NOTIFICATIONS
        if (title == null || title.equals(null)){

            //region FIREBASE CONSOLE NOTIFICATION HANDLER
            title = remoteMessage.getNotification().getTitle();
            messgae = remoteMessage.getNotification().getBody();

            Intent intent = new Intent(this, ChatRoomGallery.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent , PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

            notificationBuilder.setContentTitle(title);
            notificationBuilder.setContentText(messgae);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setLights(Color.CYAN, 500, 500);
            notificationBuilder.setVibrate(new long[]{0,500,500,500,500});
            notificationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            notificationBuilder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());

            sendNotification(title, messgae, id);
            //endregion
        }else{


                Intent intent = new Intent(this, ChatRoom.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                intent.putExtra("Chat_ID", id);
                intent.putExtra("Chat_Name", title);
                intent.putExtra("recipient_id", user_id);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

                notificationBuilder.setContentTitle(title);
                notificationBuilder.setContentText(messgae);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setLights(Color.CYAN, 500, 500);
                notificationBuilder.setVibrate(new long[]{0, 500, 500, 500, 500});
                notificationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                notificationBuilder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());

                sendNotification(title, messgae, id);

            //endregion
        }


        //Intent intent = new Intent(this, Splash.class);


    }

    private void sendNotification(String title, String message, String id){

        //Creating a broadcast intent
        Intent pushNotification = new Intent("pushnotification");
        //Adding notification data to the intent
        pushNotification.putExtra("message", message);
        pushNotification.putExtra("name", title);
        pushNotification.putExtra("id", id);

        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }
}
