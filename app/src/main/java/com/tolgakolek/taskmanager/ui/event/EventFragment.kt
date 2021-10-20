package com.tolgakolek.taskmanager.ui.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tolgakolek.taskmanager.R
import com.tolgakolek.taskmanager.data.model.Event
import com.tolgakolek.taskmanager.databinding.FragmentEventBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class EventFragment : Fragment(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {

    private val viewModel: EventViewModel by viewModels()
    private val binding: FragmentEventBinding by viewBinding()
    private val days =
        listOf("Pazar","Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi")
    private val events = ArrayList<Event>()
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")

    private var year = 0
    private var month = 0
    private var day = 0
    private var hours = 23
    private var minutes = 59
    private var startDateClick = false
    private var calendarStart = Calendar.getInstance()
    private var calendarEnd = Calendar.getInstance()

    private lateinit var eventDate: String
    private lateinit var calender: Calendar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCalanderInstance()
        arguments?.getString("date").let {
            val date = simpleDateFormat.parse(it)
            calendarStart.time = date
            calendarStart.set(Calendar.HOUR,hours)
            calendarEnd.set(Calendar.MINUTE,minutes)
            calendarEnd.time = calendarStart.time
            eventDate = it + " ${days[calender.get(Calendar.DAY_OF_WEEK)-1]} " + String.format(" %02d:%02d", hours, minutes)
            binding.tvStartDate.text = eventDate
            binding.tvEndDate.text = eventDate
        }
        binding.tvStartDate.setOnClickListener {
            startDateClick = true
            showDatePickerDialog()
        }
        binding.tvEndDate.setOnClickListener {
            startDateClick = false
            showDatePickerDialog()
        }
        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_eventFragment_to_mainPageFragment)
        }
        binding.btnOkey.setOnClickListener {
            if (binding.etEventName.text.isNotEmpty() && binding.etDescription.text.isNotEmpty()) {
                events.clear()
                if(calendarStart.before(calendarEnd) || calendarStart.time == calendarEnd.time) {
                    while (!calendarStart.after(calendarEnd)) {
                        events.add(
                            Event(
                                0,
                                calendarStart.time,
                                binding.etEventName.text.toString(),
                                binding.etDescription.text.toString()
                            )
                        )
                        calendarStart.add(Calendar.DATE, 1)
                    }
                    viewModel.addEventsDao(events)
                    findNavController().navigate(R.id.action_eventFragment_to_mainPageFragment)
                } else {
                    showToast("Başlangıç tarihi bitiş tarihinden büyük olamaz!")
                }
            } else {
                showToast("Lütfen tüm alanları doldurun!")
            }
        }
    }

    private fun showToast(message:String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
            .show()
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            this,
            year, month, day
        )
        datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        datePickerDialog.show()
    }

    private fun getCalanderInstance() {
        calender = Calendar.getInstance()
        year = calender.get(Calendar.YEAR)
        month = calender.get(Calendar.MONTH)
        day = calender.get(Calendar.DAY_OF_MONTH)
        hours = calender.get(Calendar.HOUR_OF_DAY)
        minutes = calender.get(Calendar.MINUTE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        eventDate += String.format(" %02d:%02d", hour, minute)
        if (startDateClick) {
            binding.tvStartDate.text = eventDate
            calendarStart.set(Calendar.HOUR, hour)
        } else {
            binding.tvEndDate.text = eventDate
            calendarEnd.set(Calendar.MINUTE, minute)
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        calender.set(year, month, day)
        if (startDateClick) {
            calendarStart = Calendar.getInstance()
            calendarStart.set(year, month, day)
        } else {
            calendarEnd = Calendar.getInstance()
            calendarEnd.set(year, month, day)
        }
        eventDate = "$day.${month + 1}.$year ${days[calender.get(Calendar.DAY_OF_WEEK)-1]}"
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            this,
            hours,
            minutes,
            true
        )
        timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.show()
    }
}
