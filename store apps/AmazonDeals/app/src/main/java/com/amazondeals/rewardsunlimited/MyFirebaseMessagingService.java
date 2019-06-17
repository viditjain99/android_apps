package com.amazondeals.rewardsunlimited;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String TAG="MyFirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        if(remoteMessage.getNotification()!=null)
        {
            Log.e(TAG,"From: "+remoteMessage.getFrom());
            Log.d(TAG,"Message Notification Body: "+remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String token)
    {
        Log.i(TAG,"Refreshed token: "+token);
        sendRegistrationToServer(token);
    }

    private void handleNow()
    {
        Log.d(TAG,"Short lived task is done.");
    }

    private void sendRegistrationToServer(String token)
    {

    }

    private void sendNotification(String title,String messageBody)
    {
        String channelId=getString(R.string.default_notification_channel_id);
        Intent intent=new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,channelId).setSmallIcon(R.mipmap.startup_icon);
        builder.setContentTitle(title);
        builder.setContentText(messageBody);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setAutoCancel(true);
        Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=new NotificationChannel(channelId,"FirebaseChannel",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0,builder.build());
    }
}
