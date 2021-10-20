package com.tolgakolek.taskmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.tolgakolek.taskmanager.data.util.DateConverter
import java.util.*


@Entity(tableName = "event")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @TypeConverters(DateConverter::class)
    var date: Date,
    var title: String,
    var description: String
)