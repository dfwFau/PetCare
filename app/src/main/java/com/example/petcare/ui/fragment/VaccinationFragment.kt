package com.example.petcare.ui.fragment

import android.app.DatePickerDialog
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
import com.example.petcare.data.entity.Vaccination
import com.example.petcare.database.AppDatabase
import com.example.petcare.databinding.DialogAddVaccinationBinding
import com.example.petcare.databinding.FragmentVaccinationBinding
import com.example.petcare.repository.PetRepository
import com.example.petcare.repository.RecordRepository
import com.example.petcare.ui.adapter.VaccinationAdapter
import com.example.petcare.viewmodel.PetViewModel
import com.example.petcare.viewmodel.RecordViewModel
import com.example.petcare.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class VaccinationFragment : Fragment() {
    private var _binding: FragmentVaccinationBinding? = null
    private val binding get() = _binding!!

    private lateinit var recordViewModel: RecordViewModel
    private lateinit var petViewModel: PetViewModel
    private lateinit var adapter: VaccinationAdapter
    private var dateGivenCalendar = Calendar.getInstance()
    private var nextDateCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVaccinationBinding.inflate(inflater, container, false)
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

        petViewModel.allPets.observe(viewLifecycleOwner) { pets ->
            if (pets.isNotEmpty()) {
                // For demo/simplicity, showing records for the first pet
                recordViewModel.getVaccinations(pets[0].id).observe(viewLifecycleOwner) { vaccinations ->
                    adapter.submitList(vaccinations)
                }
            }
        }

        binding.fabAddVaccination.setOnClickListener {
            showAddVaccinationDialog()
        }

        binding.btnDashboard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DashboardFragment())
                .commit()
        }
    }

    private fun setupRecyclerView() {
        adapter = VaccinationAdapter()
        binding.rvVaccinations.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVaccinations.adapter = adapter
    }

    private fun showAddVaccinationDialog() {
        val dialogBinding = DialogAddVaccinationBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create()

        setupPetSpinner(dialogBinding)
        setupDatePicker(dialogBinding)

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                saveVaccination(dialogBinding, dialog)
            }
        }

        dialog.show()
    }

    private fun setupPetSpinner(dialogBinding: DialogAddVaccinationBinding) {
        petViewModel.allPets.observe(viewLifecycleOwner) { pets ->
            val petAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, pets.map { it.name })
            petAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogBinding.spinnerPetVaccination.adapter = petAdapter
        }
    }

    private fun setupDatePicker(dialogBinding: DialogAddVaccinationBinding) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        dialogBinding.etDateGiven.setOnClickListener {
            DatePickerDialog(requireContext(), { _, y, m, d ->
                dateGivenCalendar.set(y, m, d)
                dialogBinding.etDateGiven.setText(sdf.format(dateGivenCalendar.time))
            }, dateGivenCalendar.get(Calendar.YEAR), dateGivenCalendar.get(Calendar.MONTH), dateGivenCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        dialogBinding.etNextDueDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, y, m, d ->
                nextDateCalendar.set(y, m, d)
                dialogBinding.etNextDueDate.setText(sdf.format(nextDateCalendar.time))
            }, nextDateCalendar.get(Calendar.YEAR), nextDateCalendar.get(Calendar.MONTH), nextDateCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun saveVaccination(dialogBinding: DialogAddVaccinationBinding, dialog: AlertDialog) {
        val name = dialogBinding.etVaccineName.text.toString().trim()
        val notes = dialogBinding.etVaccineNotes.text.toString().trim()
        val dateGiven = dialogBinding.etDateGiven.text.toString().trim()
        val selectedPetName = dialogBinding.spinnerPetVaccination.selectedItem as? String

        if (name.isEmpty() || dateGiven.isEmpty() || selectedPetName == null) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val pets = petViewModel.allPets.value
        if (pets.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please add a pet first", Toast.LENGTH_SHORT).show()
            return
        }

        val pet = pets.find { it.name == selectedPetName }
        if (pet != null) {
            val vaccination = Vaccination(
                petId = pet.id,
                vaccineName = name,
                dateGiven = dateGivenCalendar.timeInMillis,
                nextDate = nextDateCalendar.timeInMillis,
                notes = notes
            )
            recordViewModel.insertVaccination(vaccination)
            Toast.makeText(requireContext(), "Vaccination record added", Toast.LENGTH_SHORT).show()
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
