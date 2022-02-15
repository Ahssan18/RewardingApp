package com.ncodelab.rewardingapp.services;

import android.app.Service;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        getFirebaseNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }
    public void getFirebaseNotification(String title,String message){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"NOTIFICATIONS")
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(15,builder.build());

    }
}
