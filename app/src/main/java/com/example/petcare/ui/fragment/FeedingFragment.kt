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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.R
import com.example.petcare.data.entity.FeedingSchedule
import com.example.petcare.database.AppDatabase
import com.example.petcare.databinding.DialogAddFeedingBinding
import com.example.petcare.databinding.FragmentFeedingBinding
import com.example.petcare.receiver.AlarmReceiver
import com.example.petcare.repository.PetRepository
import com.example.petcare.repository.RecordRepository
import com.example.petcare.ui.adapter.FeedingAdapter
import com.example.petcare.viewmodel.PetViewModel
import com.example.petcare.viewmodel.RecordViewModel
import com.example.petcare.viewmodel.ViewModelFactory
import java.util.*

class FeedingFragment : Fragment() {

    private var _binding: FragmentFeedingBinding? = null
    private val binding get() = _binding!!

    private lateinit var recordViewModel: RecordViewModel
    private lateinit var petViewModel: PetViewModel
    private lateinit var adapter: FeedingAdapter
    private var selectedHour = 8
    private var selectedMinute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val recordRepo = RecordRepository(db.vaccinationDao(), db.feedingDao(), db.appointmentDao(), db.medicalRecordDao())
        val petRepo = PetRepository(db.petDao())

        recordViewModel = ViewModelProvider(this, ViewModelFactory(recordRepo))[RecordViewModel::class.java]
        petViewModel = ViewModelProvider(this, ViewModelFactory(petRepo))[PetViewModel::class.java]

        setupRecyclerView()

        // For simplicity, we get all feeding schedules or we could filter by pet
        // Here we just observe all to show them in the fragment
        petViewModel.allPets.observe(viewLifecycleOwner) { pets ->
            if (pets.isNotEmpty()) {
                recordViewModel.getFeedingSchedules(pets[0].id).observe(viewLifecycleOwner) { schedules ->
                    adapter.submitList(schedules)
                }
            }
        }

        binding.fabAddFeeding.setOnClickListener {
            showAddFeedingDialog()
        }

        binding.btnDashboard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DashboardFragment())
                .commit()
        }
    }

    private fun setupRecyclerView() {
        adapter = FeedingAdapter(
            onDoneClick = { schedule ->
                schedule.isDone = true
                recordViewModel.updateFeedingSchedule(schedule)
            },
            onDelete = { schedule ->
                showDeleteConfirmation(schedule)
            }
        )
        binding.rvFeedingSchedules.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFeedingSchedules.adapter = adapter
    }

    private fun showAddFeedingDialog() {
        val dialogBinding = DialogAddFeedingBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        setupPetSpinner(dialogBinding)
        setupTypeSpinner(dialogBinding)
        
        dialogBinding.etFeedingTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, h, m ->
                selectedHour = h
                selectedMinute = m
                dialogBinding.etFeedingTime.setText(String.format("%02d:%02d", h, m))
            }, selectedHour, selectedMinute, false).show()
        }

        dialogBinding.btnSave.setOnClickListener {
            saveFeedingSchedule(dialogBinding, dialog)
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupPetSpinner(dialogBinding: DialogAddFeedingBinding) {
        petViewModel.allPets.observe(viewLifecycleOwner) { pets ->
            val petAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, pets.map { it.name })
            petAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogBinding.spinnerPetFeeding.adapter = petAdapter
        }
    }

    private fun setupTypeSpinner(dialogBinding: DialogAddFeedingBinding) {
        val types = arrayOf("Morning", "Afternoon", "Evening", "Night")
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerFeedingType.adapter = typeAdapter
    }

    private fun saveFeedingSchedule(dialogBinding: DialogAddFeedingBinding, dialog: AlertDialog) {
        val type = dialogBinding.spinnerFeedingType.selectedItem.toString()
        val time = dialogBinding.etFeedingTime.text.toString().trim()
        val selectedPetName = dialogBinding.spinnerPetFeeding.selectedItem as? String

        if (time.isEmpty() || selectedPetName == null) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val pets = petViewModel.allPets.value
        if (pets.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please add a pet first", Toast.LENGTH_SHORT).show()
            return
        }

        val pet = pets.find { it.name == selectedPetName }
        if (pet != null) {
            val schedule = FeedingSchedule(
                petId = pet.id,
                scheduleType = type,
                feedingTime = time
            )
            recordViewModel.insertFeedingSchedule(schedule)
            scheduleAlarm(selectedHour, selectedMinute, type)
            Toast.makeText(requireContext(), "Feeding reminder saved", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        } else {
            Toast.makeText(requireContext(), "Selected pet not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmation(schedule: FeedingSchedule) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Reminder")
            .setMessage("Delete this feeding reminder?")
            .setPositiveButton("Delete") { _, _ ->
                recordViewModel.deleteFeedingSchedule(schedule)
                cancelAlarm(schedule)
            }
            .setNegativeButton("Cancel", null)
            .show()
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

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun cancelAlarm(schedule: FeedingSchedule) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            schedule.scheduleType.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
