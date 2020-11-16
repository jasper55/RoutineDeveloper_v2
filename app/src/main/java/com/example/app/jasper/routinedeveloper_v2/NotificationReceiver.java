package com.example.app.jasper.routinedeveloper_v2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final int CALL_NOTIFICATION_ALERT_TIME = 100;
    private static final int CHECKBOX_ON_BACKGROUND = 17301520;

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent returnIntent = new Intent(context,OverviewActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                CALL_NOTIFICATION_ALERT_TIME,
                returnIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentText("Checkmark reminder.\nHave you done all tasks already?")
                .setTicker("Don't forget to your tasks") // text, der in der taskbar angezeigt wird!
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.alert_symbol)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        notificationManager.notify(CALL_NOTIFICATION_ALERT_TIME,notifBuilder.build());
    }
}
