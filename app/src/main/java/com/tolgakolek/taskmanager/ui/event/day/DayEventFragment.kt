package com.tolgakolek.taskmanager.ui.event.day

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tolgakolek.taskmanager.R
import com.tolgakolek.taskmanager.databinding.FragmentDayEventBinding
import com.tolgakolek.taskmanager.ui.event.adapter.EventAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DayEventFragment : Fragment(), EventAdapter.EventItemListener, FragmentResultListener {

    private val viewModel: DayEventViewModel by viewModels()
    private val binding: FragmentDayEventBinding by viewBinding()
    private var eventAdapter: EventAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragmentManager.setFragmentResultListener("dayEvent", this, this)
        eventAdapter = EventAdapter(this)
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.viewState.collect {
                    val events = it.events
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

    override fun onClickDeleteEvent(eventId: Int) {
        viewModel.deleteEventDao(eventId)
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        val date = result.getString("event").toString()
        viewModel.getAllEventDao(date)
    }
}
