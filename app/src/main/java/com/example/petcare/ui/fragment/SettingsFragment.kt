package com.example.petcare.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.petcare.databinding.FragmentDashboardBinding // Reusing for placeholder or create a new one

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Just return a simple view or reuse dashboard binding for now
        return FragmentDashboardBinding.inflate(inflater, container, false).root
    }
}
