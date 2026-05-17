package com.example.petcare.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.data.entity.FeedingSchedule
import com.example.petcare.databinding.ItemRecordBinding

class FeedingAdapter(
    private val onDoneClick: (FeedingSchedule) -> Unit,
    private val onDelete: (FeedingSchedule) -> Unit
) : ListAdapter<FeedingSchedule, FeedingAdapter.FeedingViewHolder>(FeedingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedingViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FeedingViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: FeedingSchedule) {
            binding.tvTitle.text = "${schedule.scheduleType} Feeding"
            binding.tvSubtitle.text = "Time: ${schedule.feedingTime}"
            binding.tvExtra.text = if (schedule.isEnabled) "Status: Enabled" else "Status: Disabled"

            if (schedule.isDone) {
                binding.btnDone.visibility = View.GONE
                binding.root.alpha = 0.5f
            } else {
                binding.btnDone.visibility = View.VISIBLE
                binding.root.alpha = 1.0f
            }

            binding.btnDone.setOnClickListener {
                onDoneClick(schedule)
            }

            binding.root.setOnLongClickListener {
                onDelete(schedule)
                true
            }
        }
    }

    class FeedingDiffCallback : DiffUtil.ItemCallback<FeedingSchedule>() {
        override fun areItemsTheSame(oldItem: FeedingSchedule, newItem: FeedingSchedule): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FeedingSchedule, newItem: FeedingSchedule): Boolean = oldItem == newItem
    }
}
