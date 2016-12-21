package com.example.daniel.chatroomapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Daniel on 02/12/2016.
 */

public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getData().get("title");
        String messgae = remoteMessage.getData().get("message");
        String id = remoteMessage.getData().get("id");

        //GET AND DISPLAY SYSTEM NOTIFICATIONS
        if (title == null || title.equals(null)){
            title = remoteMessage.getNotification().getTitle();
            messgae = remoteMessage.getNotification().getBody();
        }


        Intent intent = new Intent(this, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent , PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(messgae);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        sendNotification(title, messgae, id);
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
