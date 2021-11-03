package com.tolgakolek.taskmanager.ui.event.year

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tolgakolek.taskmanager.data.local.EventDao
import com.tolgakolek.taskmanager.data.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class YearEventViewModel @Inject constructor(private val eventDao: EventDao) : ViewModel() {
    private val _viewState = MutableStateFlow(initialCreateViewState())
    val viewState = _viewState.asStateFlow()

    init {
        val calendar = Calendar.getInstance()
        getAllYearEventDao(calendar.get(Calendar.YEAR).toString())
    }

    fun getAllYearEventDao(year: String) {
        eventDao.getAllYearEvent(year).observeForever(Observer {
            _viewState.value = _viewState.value.copy(events = it.sortedBy { it.date.time })
        })
    }

    fun deleteEventDao(eventId: Int) {
        viewModelScope.launch {
            eventDao.deleteEvent(eventId)
        }
    }

    fun setAlarmStatus(isActive: Boolean, eventId: Int) {
        viewModelScope.launch {
            eventDao.updateEventAlarm(isActive, eventId)
        }
    }

    fun getEventDateById(eventId: Int) =
        eventDao.getEventById(eventId)

    private fun initialCreateViewState() = DayEventState(
        events = emptyList()
    )
}

data class DayEventState(
    val events: List<Event> = emptyList()
)