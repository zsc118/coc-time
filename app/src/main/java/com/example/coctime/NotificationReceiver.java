package com.example.coctime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

@RequiresApi(api = Build.VERSION_CODES.O)
public class NotificationReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "CocTimeChannel";
    public static final int REQUEST_CODE = 3;
    //private static final String NOTIFICATION_TAG = "CocTimeNotification";
    public static int notificationId;
    static final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_notification).setContentTitle("项目时间提醒").setContentText(intent.getStringExtra("content")).setPriority(NotificationCompat.PRIORITY_DEFAULT).setSound(soundUri).setDefaults(NotificationCompat.DEFAULT_ALL).setContentIntent(PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE)).setAutoCancel(true);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId++, builder.build());
    }

    public static void createNotificationChannel(Context context) {
        CharSequence name = "CocTime Channel";
        String description = "CocTime 通知频道";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        /*channel.enableVibration(true); // 启用震动
        channel.setVibrationPattern(new long[]{0, 250, 250, 250}); // 设置震动模式
        channel.enableLights(true); // 启用指示灯*/
        channel.setSound(soundUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}