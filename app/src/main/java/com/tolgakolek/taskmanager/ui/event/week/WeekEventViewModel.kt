package com.tolgakolek.taskmanager.ui.event.week

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tolgakolek.taskmanager.data.local.EventDao
import com.tolgakolek.taskmanager.data.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WeekEventViewModel @Inject constructor(private val eventDao: EventDao) : ViewModel() {
    private val _viewState = MutableStateFlow(initialCreateViewState())
    val viewState = _viewState.asStateFlow()
    val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")

    init {
        val date = simpleDateFormat.format(Calendar.getInstance().time)
        getAllWeekEventDao(date)
    }

    fun getAllWeekEventDao(nowDate: String) {
        val date = simpleDateFormat.parse(nowDate)
        val startCalendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()
        startCalendar.time = date
        endCalendar.time = date
        var selectDayOfWeek = startCalendar.get(Calendar.DAY_OF_WEEK)
        if (selectDayOfWeek == 1) selectDayOfWeek = 8
        startCalendar.add(Calendar.DAY_OF_MONTH, 2 - selectDayOfWeek)
        endCalendar.add(Calendar.DAY_OF_MONTH, (8 - selectDayOfWeek))
        eventDao.getAllWeekEvent(startCalendar.time, endCalendar.time).observeForever(Observer {
            _viewState.value = _viewState.value.copy(events = it.sortedBy { it.date.time })
        })
    }

    fun deleteEventDao(eventId: Int) {
        viewModelScope.launch {
            eventDao.deleteEvent(eventId)
        }
    }

    private fun initialCreateViewState() = WeekEventState(
        events = emptyList()
    )
}

data class WeekEventState(
    val events: List<Event> = emptyList()
)