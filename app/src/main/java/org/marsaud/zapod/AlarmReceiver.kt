package org.marsaud.zapod

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val channelId = "org.marsaud.zapod.notifier"
        val notificationContent = intent?.getStringExtra("notification_content")
        val notificationChannelDescription = intent?.getStringExtra("notification_channeldescription")
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        /* Since Oreo we must:
         * 1) build a Notification;
         * 2) build a NotificationChannel for this notification;
         * 3) build a NotificationManager and pass the two to it;
         * 4) call notify() method on the NotificationManager.
         *
         * Before Oreo we must:
         * 1) build a Notification;
         * 2) build a NotificationManager and pass the notification to it;
         * 3) call notify() method on the NotificationManager.
         */
        if (Build.VERSION.SDK_INT >= 26) { // >= Android 8.0 (Oreo)
            val notification = Notification.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Zapod")
                    .setContentText(notificationContent)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true) // auto-remove notification when tapped
                    .build()
            val channel = NotificationChannel(channelId, "APOD", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = notificationChannelDescription
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(1, notification)
        } else {
            if (context != null) {
                val notification = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Zapod")
                        .setContentText(notificationContent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true) // auto-remove notification when tapped
                        .build()
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.notify(1, notification)
            }
        }
    }
}