package com.tolgakolek.taskmanager.data.local

import android.content.Context
import androidx.room.*
import com.tolgakolek.taskmanager.data.model.Event
import com.tolgakolek.taskmanager.data.util.DateConverter

@TypeConverters(DateConverter::class)
@Database(entities = [Event::class],version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "event")
                .fallbackToDestructiveMigration()
                .build()
    }
}