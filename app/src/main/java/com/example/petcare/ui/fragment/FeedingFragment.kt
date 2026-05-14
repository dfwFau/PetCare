package com.example.petcare.ui.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.petcare.databinding.FragmentFeedingBinding
import com.example.petcare.receiver.AlarmReceiver
import java.util.*

class FeedingFragment : Fragment() {

    private var _binding: FragmentFeedingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSetMorning.setOnClickListener { showTimePicker("Morning") }
        binding.btnSetAfternoon.setOnClickListener { showTimePicker("Afternoon") }
        binding.btnSetEvening.setOnClickListener { showTimePicker("Evening") }
    }

    private fun showTimePicker(type: String) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, h, m ->
            scheduleAlarm(h, m, type)
        }, hour, minute, false).show()
    }

    private fun scheduleAlarm(hour: Int, minute: Int, type: String) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("title", "Feeding Time!")
            putExtra("message", "It's time for $type feeding.")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            type.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(requireContext(), "Reminder set for $hour:$minute", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
