package com.example.petcare.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.data.entity.Appointment
import com.example.petcare.databinding.ItemRecordBinding
import java.text.SimpleDateFormat
import java.util.*

class AppointmentAdapter(private val onDoneClick: (Appointment) -> Unit) : ListAdapter<Appointment, AppointmentAdapter.AppointmentViewHolder>(AppointmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AppointmentViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(appointment: Appointment) {
            binding.tvTitle.text = appointment.clinicName
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            binding.tvSubtitle.text = "Date: ${sdf.format(Date(appointment.dateTime))}"
            binding.tvExtra.text = "Purpose: ${appointment.purpose}\nNotes: ${appointment.notes}"

            if (appointment.isDone) {
                binding.btnDone.visibility = View.GONE
                binding.root.alpha = 0.5f
            } else {
                binding.btnDone.visibility = View.VISIBLE
                binding.root.alpha = 1.0f
            }

            binding.btnDone.setOnClickListener {
                onDoneClick(appointment)
            }
        }
    }

    class AppointmentDiffCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean = oldItem == newItem
    }
}
