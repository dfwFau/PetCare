package com.example.petcare.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.databinding.FragmentDashboardBinding
import com.example.petcare.ui.adapter.CareTask
import com.example.petcare.ui.adapter.TaskAdapter

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
        setupTasksRecyclerView()
    }

    private fun setupTasksRecyclerView() {
        val adapter = TaskAdapter { task ->
            // Handle task completion if needed
        }
        binding.rvTodayTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTodayTasks.adapter = adapter

        val todayTasks = listOf(
            CareTask(1, "Morning Feeding", "Provide 1 cup of dry food", "08:00 AM"),
            CareTask(2, "Water Refill", "Ensure bowl is clean and full", "08:30 AM"),
            CareTask(3, "Daily Walk", "30 minutes walk in the park", "10:00 AM"),
            CareTask(4, "Grooming", "Brush the coat for 10 minutes", "04:00 PM"),
            CareTask(5, "Evening Feeding", "Provide 1 cup of wet food", "07:00 PM"),
            CareTask(6, "Medicine", "Apply flea treatment", "08:00 PM")
        )
        adapter.submitList(todayTasks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
