package com.tolgakolek.taskmanager.data.util

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateConverter {
    @TypeConverter
    fun toDate(dateString: String?): Date? {
        val date : Date = SimpleDateFormat("dd.MM.yyyy").parse(dateString)
        return date
    }

    @TypeConverter
    fun fromDate(date: Date?): String? {
        return if (date == null) null else SimpleDateFormat("dd.MM.yyyy").format(date)
    }
}