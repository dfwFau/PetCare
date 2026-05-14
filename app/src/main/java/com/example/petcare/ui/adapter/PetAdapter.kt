package com.example.petcare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petcare.data.entity.Pet
import com.example.petcare.databinding.ItemPetBinding

class PetAdapter(private val onClick: (Pet) -> Unit) :
    ListAdapter<Pet, PetAdapter.PetViewHolder>(PetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val binding = ItemPetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PetViewHolder(private val binding: ItemPetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pet: Pet) {
            binding.tvName.text = pet.name
            binding.tvBreed.text = pet.breed
            binding.tvType.text = pet.type
            
            if (pet.imagePath != null) {
                Glide.with(binding.ivPet.context).load(pet.imagePath).into(binding.ivPet)
            } else {
                binding.ivPet.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            binding.root.setOnClickListener { onClick(pet) }
        }
    }

    class PetDiffCallback : DiffUtil.ItemCallback<Pet>() {
        override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean = oldItem == newItem
    }
}
