package com.example.petcare.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import com.example.petcare.R
import com.example.petcare.databinding.ItemRecordBinding

data class CareTask(
    val id: Int,
    val title: String,
    val description: String,
    val time: String,
    var isCompleted: Boolean = false
)

class TaskAdapter(
    private val onTaskCompleted: (CareTask) -> Unit
) : ListAdapter<CareTask, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: CareTask) {
            binding.tvTitle.text = task.title
            binding.tvSubtitle.text = task.time
            binding.tvExtra.text = task.description

            if (task.isCompleted) {
                binding.btnDone.visibility = View.GONE
                binding.root.alpha = 0.8f
                binding.cardRecord.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.gray_100))
            } else {
                binding.btnDone.visibility = View.VISIBLE
                binding.root.alpha = 1.0f
                binding.cardRecord.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            }

            binding.btnDone.setOnClickListener {
                task.isCompleted = true
                onTaskCompleted(task)
                notifyItemChanged(adapterPosition)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<CareTask>() {
        override fun areItemsTheSame(oldItem: CareTask, newItem: CareTask): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CareTask, newItem: CareTask): Boolean = oldItem == newItem
    }
}
