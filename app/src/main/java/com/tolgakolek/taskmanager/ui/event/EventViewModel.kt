package com.tolgakolek.taskmanager.ui.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tolgakolek.taskmanager.data.local.EventDao
import com.tolgakolek.taskmanager.data.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(private val eventDao: EventDao) : ViewModel() {

    fun addEventsDao(events:List<Event>){
        viewModelScope.launch {
            println("addEventsDao " + events[0].date)
            eventDao.insertAllEvent(events)
        }
    }
}