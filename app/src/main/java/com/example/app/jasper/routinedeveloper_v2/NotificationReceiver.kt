package com.example.app.jasper.routinedeveloper_v2

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.app.jasper.routinedeveloper_v2.OverviewActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val returnIntent = Intent(context, OverviewActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                context,
                CALL_NOTIFICATION_ALERT_TIME,
                returnIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val notifBuilder = NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentText("Checkmark reminder.\nHave you done all tasks already?")
                .setTicker("Don't forget to your tasks") // text, der in der taskbar angezeigt wird!
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.alert_symbol)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationManager.notify(CALL_NOTIFICATION_ALERT_TIME, notifBuilder.build())
    }

    companion object {
        const val CALL_NOTIFICATION_ALERT_TIME = 100
    }
}