package com.example.petcare.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.petcare.databinding.FragmentDashboardBinding

import com.example.petcare.R

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardPets.setOnClickListener { navigateTo(PetListFragment()) }
        binding.cardVaccines.setOnClickListener { navigateTo(VaccinationFragment()) }
        binding.cardReminders.setOnClickListener { navigateTo(FeedingFragment()) }
        binding.cardAppointments.setOnClickListener { navigateTo(AppointmentFragment()) }
        binding.cardMedicalRecords.setOnClickListener { navigateTo(MedicalHistoryFragment()) }
    }

    private fun navigateTo(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
