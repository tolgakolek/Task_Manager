package com.tolgakolek.taskmanager.ui.event.day

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tolgakolek.taskmanager.R
import com.tolgakolek.taskmanager.databinding.FragmentDayEventBinding
import com.tolgakolek.taskmanager.service.alarm.AlarmService
import com.tolgakolek.taskmanager.ui.event.adapter.EventAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DayEventFragment : Fragment(), EventAdapter.EventItemListener, FragmentResultListener {

    private val viewModel: DayEventViewModel by viewModels()
    private val binding: FragmentDayEventBinding by viewBinding()
    private var eventAdapter: EventAdapter? = null
    private lateinit var alarmService: AlarmService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragmentManager.setFragmentResultListener("dayEvent", this, this)
        eventAdapter = EventAdapter(this)
        alarmService = AlarmService(requireContext())
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.viewState.collect {
                    val events = it.events
                    events.map {
                        if(Calendar.getInstance().time.after(it.date) && it.alarmActive){
                            onChangeSwitchAlarm(it.alarmActive.not(),it.id)
                            it.alarmActive = false
                        }
                    }
                    eventAdapter.let {
                        it?.setItems(events)
                    }
                }
            }
        }
        binding.dayRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dayRecyclerView.adapter = eventAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_day_event, container, false)
    }

    private fun setAlarm(eventId: Int){
        viewModel.getEventDateById(eventId).observeForever(Observer {
            it?.let {
                alarmService.setEventAlarm(it.date.time,it.id,it.title)
            }
        })
    }

    private fun cancelAlarm(eventId: Int){
        viewModel.getEventDateById(eventId).observeForever(Observer {
            it?.let {
                alarmService.cancelEventAlarm(it.id)
            }
        })
    }

    override fun onClickDeleteEvent(eventId: Int) {
        viewModel.deleteEventDao(eventId)
    }

    override fun onChangeSwitchAlarm(isChecked: Boolean, eventId: Int) {
        if (isChecked){
            setAlarm(eventId)
        } else {
            cancelAlarm(eventId)
        }
        viewModel.setAlarmStatus(isChecked,eventId)
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        val date = result.getString("event").toString()
        viewModel.getAllEventDao(date)
    }
}
