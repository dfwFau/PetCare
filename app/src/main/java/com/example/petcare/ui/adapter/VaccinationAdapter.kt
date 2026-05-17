package com.example.petcare.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.data.entity.Vaccination
import com.example.petcare.databinding.ItemRecordBinding
import java.text.SimpleDateFormat
import java.util.*

class VaccinationAdapter(private val onDoneClick: (Vaccination) -> Unit) : ListAdapter<Vaccination, VaccinationAdapter.VaccinationViewHolder>(VaccinationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccinationViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VaccinationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VaccinationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VaccinationViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(vaccination: Vaccination) {
            binding.tvTitle.text = vaccination.vaccineName
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dateGiven = sdf.format(Date(vaccination.dateGiven))
            val nextDate = sdf.format(Date(vaccination.nextDate))
            
            binding.tvSubtitle.text = "Given: $dateGiven | Next: $nextDate"
            binding.tvExtra.text = "Notes: ${vaccination.notes}"

            if (vaccination.isDone) {
                binding.btnDone.visibility = View.GONE
                binding.root.alpha = 0.5f
            } else {
                binding.btnDone.visibility = View.VISIBLE
                binding.root.alpha = 1.0f
            }

            binding.btnDone.setOnClickListener {
                onDoneClick(vaccination)
            }
        }
    }

    class VaccinationDiffCallback : DiffUtil.ItemCallback<Vaccination>() {
        override fun areItemsTheSame(oldItem: Vaccination, newItem: Vaccination): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Vaccination, newItem: Vaccination): Boolean = oldItem == newItem
    }
}
