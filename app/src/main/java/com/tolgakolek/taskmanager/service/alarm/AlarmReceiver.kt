package com.tolgakolek.taskmanager.service.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.tolgakolek.taskmanager.util.Constants
import android.text.format.DateFormat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tolgakolek.taskmanager.MainActivity
import com.tolgakolek.taskmanager.R


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra(Constants.ALARM_TIME, 0L)
        val eventTitle = intent.getStringExtra(Constants.ALARM_TITLE)

        eventTitle?.let {
            buildNotification(context, it, convertDate(timeInMillis))
        }
    }

    @SuppressLint("LaunchActivityFromNotification")
    private fun buildNotification(
        context: Context,
        title: String,
        time: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("n", "n", NotificationManager.IMPORTANCE_DEFAULT)
            val manager: NotificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(notificationChannel)
        }
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(context, "n")
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_notification_alert)
            .setAutoCancel(true)
            .setContentText(time)
            .setContentIntent(pendingIntent)
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(999, notificationBuilder.build())
    }

    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd.MM.yyyy HH:mm", timeInMillis).toString()
}