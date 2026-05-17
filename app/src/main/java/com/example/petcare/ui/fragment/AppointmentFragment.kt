package com.example.petcare.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.petcare.database.AppDatabase
import com.example.petcare.data.entity.Appointment
import com.example.petcare.databinding.DialogAddAppointmentBinding
import com.example.petcare.databinding.FragmentAppointmentBinding
import com.example.petcare.repository.PetRepository
import com.example.petcare.repository.RecordRepository
import com.example.petcare.ui.adapter.AppointmentAdapter
import com.example.petcare.viewmodel.PetViewModel
import com.example.petcare.viewmodel.RecordViewModel
import com.example.petcare.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AppointmentFragment : Fragment() {
    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var recordViewModel: RecordViewModel
    private lateinit var petViewModel: PetViewModel
    private lateinit var adapter: AppointmentAdapter
    private var selectedCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
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

        recordViewModel.getAllAppointments().observe(viewLifecycleOwner) { appointments ->
            adapter.submitList(appointments)
        }

        binding.fabAddAppointment.setOnClickListener {
            showAddAppointmentDialog()
        }

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter { appointment ->
            appointment.isDone = !appointment.isDone
            recordViewModel.updateAppointment(appointment)
        }
        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAppointments.adapter = adapter
    }

    private fun showAddAppointmentDialog() {
        val dialogBinding = DialogAddAppointmentBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        setupPetSpinner(dialogBinding)
        setupDateTimePickers(dialogBinding)

        dialogBinding.btnSave.setOnClickListener {
            saveAppointment(dialogBinding, dialog)
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupPetSpinner(dialogBinding: DialogAddAppointmentBinding) {
        petViewModel.allPets.observe(viewLifecycleOwner) { pets ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, pets.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogBinding.spinnerPet.adapter = adapter
        }
    }

    private fun setupDateTimePickers(dialogBinding: DialogAddAppointmentBinding) {
        dialogBinding.etDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                selectedCalendar.set(year, month, day)
                dialogBinding.etDate.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCalendar.time))
            }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        dialogBinding.etTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hour, minute ->
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hour)
                selectedCalendar.set(Calendar.MINUTE, minute)
                dialogBinding.etTime.setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedCalendar.time))
            }, selectedCalendar.get(Calendar.HOUR_OF_DAY), selectedCalendar.get(Calendar.MINUTE), true).show()
        }
    }

    private fun saveAppointment(dialogBinding: DialogAddAppointmentBinding, dialog: AlertDialog) {
        val clinicName = dialogBinding.etClinicName.text.toString().trim()
        val purpose = dialogBinding.etPurpose.text.toString().trim()
        val dateStr = dialogBinding.etDate.text.toString().trim()
        val timeStr = dialogBinding.etTime.text.toString().trim()
        val notes = dialogBinding.etNotes.text.toString().trim()
        val selectedPetName = dialogBinding.spinnerPet.selectedItem as? String

        if (clinicName.isEmpty() || purpose.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPetName == null) {
            Toast.makeText(requireContext(), "Please add a pet first", Toast.LENGTH_SHORT).show()
            return
        }

        val pets = petViewModel.allPets.value
        val pet = pets?.find { it.name == selectedPetName }
        if (pet != null) {
            val appointment = Appointment(
                petId = pet.id,
                clinicName = clinicName,
                dateTime = selectedCalendar.timeInMillis,
                purpose = purpose,
                notes = notes
            )
            recordViewModel.insertAppointment(appointment)
            Toast.makeText(requireContext(), "Appointment added", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        } else {
            Toast.makeText(requireContext(), "Selected pet not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
