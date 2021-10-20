package com.tolgakolek.taskmanager.ui.mainpage

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.tolgakolek.taskmanager.data.local.EventDao
import com.tolgakolek.taskmanager.data.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainPageViewModel @Inject constructor(private val eventDao: EventDao) : ViewModel() {
    private val _viewState = MutableStateFlow(initialCreateViewState())
    val viewState = _viewState.asStateFlow()

    init {
        getAllEventDao()
    }

    private fun getAllEventDao() {
        eventDao.getAllEvent().observeForever(Observer {
            _viewState.value = _viewState.value.copy(events = it)
        })
    }

    private fun initialCreateViewState() = MainPageState(
        events = emptyList()
    )
}

data class MainPageState(
    val events: List<Event> = emptyList()
)