package com.tolgakolek.taskmanager.ui.mainpage

import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.android.material.tabs.TabLayoutMediator
import com.tolgakolek.taskmanager.R
import com.tolgakolek.taskmanager.data.model.Event
import com.tolgakolek.taskmanager.databinding.FragmentMainPageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MainPageFragment : Fragment(R.layout.fragment_main_page), View.OnTouchListener {

    private val binding: FragmentMainPageBinding by viewBinding()
    private val viewModel: MainPageViewModel by viewModels()
    private val eventDays: ArrayList<EventDay> = ArrayList()
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vpAdapter = activity?.let { MainPageAdapter(it) }
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.viewState.collect {
                    addEvents(it.events)
                }
            }
        }
        binding.viewPager.adapter = vpAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "BUGÜN"
                }
                1 -> {
                    tab.text = "BU HAFTA"
                }
                2 -> {
                    tab.text = "BU YIL"
                }
            }
        }.attach()

        binding.tabLayout.setOnTouchListener(this)

        binding.floatingActionButton.setOnClickListener {
            val date = simpleDateFormat.format(binding.calendarView.selectedDate.time)
            findNavController().navigate(
                R.id.action_mainPageFragment_to_eventFragment,
                bundleOf("date" to date)
            )
        }

        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay?) {
                val date = eventDay?.calendar
                val resultDayAndWeekEvent = Bundle()
                val resultYearEvent = Bundle()
                resultDayAndWeekEvent.putString("event", simpleDateFormat.format(date?.time))
                parentFragment?.setFragmentResult("dayEvent", resultDayAndWeekEvent)
                parentFragment?.setFragmentResult("weekEvent", resultDayAndWeekEvent)
                resultYearEvent.putString("event", date?.get(Calendar.YEAR).toString())
                parentFragment?.setFragmentResult("yearEvent", resultYearEvent)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun onTouch(p0: View?, motionEvent: MotionEvent?): Boolean {
        if (motionEvent?.actionMasked == MotionEvent.ACTION_UP) {
            if (binding.tabLayout.y > motionEvent.rawY) {
                binding.calendarView.slideUp(600)
                binding.tabLayout.slideUp(600)
                binding.viewPager.slideUp(600)
            } else if (binding.tabLayout.y < motionEvent.rawY) {
                binding.tabLayout.slideDown(600)
                binding.viewPager.slideDown(600)
                binding.calendarView.slideDown(600)
            }
        }
        return false
    }

    fun addEvents(events: List<Event>) {
        eventDays.clear()
        events.forEach {
            val calendar = Calendar.getInstance()
            calendar.time = it.date
            eventDays.add(EventDay(calendar, R.drawable.sample_circile))
        }
        binding.calendarView.setEvents(eventDays)
    }

    fun CalendarView.slideDown(duration: Int = 500) {
        val animate = ScaleAnimation(
            1f, 1f,
            0f, 1f, Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        animate.duration = duration.toLong()
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                visibility = View.VISIBLE
            }
            override fun onAnimationRepeat(p0: Animation?) {}
        })
        this.startAnimation(animate)
    }

    fun CalendarView.slideUp(duration: Int = 500) {
        visibility = View.GONE
        val animate = ScaleAnimation(
            1f, 1f,
            1f, 0f, Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        animate.duration = duration.toLong()
        this.startAnimation(animate)
    }

    fun View.slideUp(duration: Int = 500) {
        val animate = TranslateAnimation(0f, 0f, binding.calendarView.height.toFloat(), 0f)
        animate.duration = duration.toLong()
        this.startAnimation(animate)
    }

    fun View.slideDown(duration: Int = 500) {
        val animate = TranslateAnimation(0f, 0f, 0f, binding.calendarView.height.toFloat())
        animate.duration = duration.toLong()
        this.startAnimation(animate)
    }
}
