package com.tolgakolek.taskmanager.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tolgakolek.taskmanager.data.model.Event
import java.util.*

@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAllEvent(): LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE id=:eventId")
    fun getEventById(eventId: Int): LiveData<Event>

    @Query("SELECT * FROM event WHERE date LIKE :date || '%'")
    fun getOneDayEvent(date: String): LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE date BETWEEN :startDate AND :endDate")
    fun getAllWeekEvent(startDate: Date, endDate: Date): LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE date LIKE '%' || :year || '%'")
    fun getAllYearEvent(year: String): LiveData<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEvent(events: List<Event>)

    @Query("UPDATE event SET alarmActive=:alarmActive WHERE id = :eventId")
    suspend fun updateEventAlarm(alarmActive:Boolean,eventId: Int)

    @Query("DELETE FROM event WHERE id = :eventId")
    suspend fun deleteEvent(eventId: Int)
}