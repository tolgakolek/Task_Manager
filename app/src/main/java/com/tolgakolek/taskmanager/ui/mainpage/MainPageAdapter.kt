package com.tolgakolek.taskmanager.ui.mainpage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tolgakolek.taskmanager.ui.event.day.DayEventFragment
import com.tolgakolek.taskmanager.ui.event.week.WeekEventFragment
import com.tolgakolek.taskmanager.ui.event.year.YearEventFragment

class MainPageAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                DayEventFragment()
            }
            1 -> {
                WeekEventFragment()
            }
            2 -> {
                YearEventFragment()
            }
            else -> {
                DayEventFragment()
            }
        }
    }
}