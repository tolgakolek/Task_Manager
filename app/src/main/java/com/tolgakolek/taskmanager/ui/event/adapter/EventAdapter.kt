package com.tolgakolek.taskmanager.ui.event.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tolgakolek.taskmanager.R
import com.tolgakolek.taskmanager.data.model.Event
import com.tolgakolek.taskmanager.databinding.ItemEventBinding
import java.text.SimpleDateFormat

class EventAdapter(private val listener: EventItemListener) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    interface EventItemListener {
        fun onClickDeleteEvent(eventId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding: ItemEventBinding =
            ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, listener)
    }

    private val items = ArrayList<Event>()

    fun setItems(items: List<Event>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    inner class EventViewHolder(
        private val itemBinding: ItemEventBinding,
        private val listener: EventItemListener
    ) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        lateinit var event: Event

        init {
            itemBinding.root.setOnClickListener(this)
            itemBinding.btnDelete.setOnClickListener {
                listener.onClickDeleteEvent(event.id)
            }
        }

        fun bind(item: Event) {
            event = item
            itemBinding.tvTitle.text = item.title
            itemBinding.tvDescription.text = item.description
            itemBinding.tvEventDate.text = SimpleDateFormat("dd.MM.yyyy").format(item.date.time)
        }

        override fun onClick(p0: View?) {
            if (itemBinding.tvDescription.isVisible || itemBinding.btnDelete.isVisible) {
                itemBinding.tvDescription.visibility = View.GONE
                itemBinding.btnDelete.visibility = View.GONE
                itemBinding.imgShow.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
            } else {
                itemBinding.tvDescription.visibility = View.VISIBLE
                itemBinding.btnDelete.visibility = View.VISIBLE
                itemBinding.imgShow.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            }
        }
    }
}