package com.tolgakolek.taskmanager.service.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.tolgakolek.taskmanager.util.Constants

class AlarmService(private val context: Context) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    fun setEventAlarm(timeInMillis: Long, eventId: Int, eventTitle: String) {
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    putExtra(Constants.ALARM_TITLE, eventTitle)
                    putExtra(Constants.ALARM_TIME, timeInMillis)
                },
                eventId
            )
        )
    }

    fun cancelEventAlarm(eventId: Int) {
        cancelAlarm(
            getPendingIntent(
                getIntent(),
                eventId
            )
        )
    }

    private fun cancelAlarm(pendingIntent: PendingIntent) {
        alarmManager?.let {
            alarmManager.cancel(pendingIntent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    private fun getPendingIntent(intent: Intent, eventId: Int) =
        PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    fun getIntent() = Intent(context, AlarmReceiver::class.java)
}