package com.example.thundertank.settings

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.thundertank.R
import com.example.thundertank.databinding.SettingsScreenFragmentBinding
import com.example.thundertank.repository.Repository
import com.example.thundertank.setup.SetupScreen1FragmentDirections


class SettingsScreenFragment : Fragment() {
    private lateinit var binding: SettingsScreenFragmentBinding

    private lateinit var viewModelFactory: SettingsViewModelFactory
    private val viewModel: SettingsViewModel by viewModels({ this }, { viewModelFactory })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.settings_screen_fragment,
            container,
            false
        )

        val repository = Repository()
        viewModelFactory = SettingsViewModelFactory(repository)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.eventRESET.observe(viewLifecycleOwner, Observer { Reset ->
            if (Reset) {
                viewModel.postReset()
            }
        })

        viewModel.eventChangeName.observe(viewLifecycleOwner, Observer { event ->
            if (event) {
                viewModel.eventChangeNameComplete()
                val dialog = LayoutInflater.from(context).inflate(R.layout.change_name, null)
                val mBuilder = AlertDialog.Builder(context).setView(dialog)
                val mAlertDialog = mBuilder.show()
                val button = dialog.findViewById<Button>(R.id.enter_button)
                button.setOnClickListener {
                    val newName = dialog.findViewById<EditText>(R.id.new_name).text.toString()
                    saveName(newName)
                    mAlertDialog.dismiss()
                }
            }
        })

        viewModel.postResponse.observe(viewLifecycleOwner, Observer { response ->
            if (viewModel.eventRESET.value == true) {
                if (viewModel.postResponse.value?.success == "1") {
                    saveData()
                    Toast.makeText(context, "Full Reset Successful ", Toast.LENGTH_LONG).show()
                } else {Toast.makeText(
                    context,
                    "Success: ${response.success} Message: ${response.message}",
                    Toast.LENGTH_LONG
                ).show()}
                viewModel.eventRESETcomplete()
            }
        })

        return binding.root
    }

    private fun saveData() {
        val sharedPreferences =
            this.activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putInt("numFish", 0)
        editor?.putFloat("ph", 0f)
        editor?.putFloat("temp", 0f)
        editor?.putFloat("clarity", 0f)
        editor?.putFloat("phLow", 0f)
        editor?.putFloat("phHigh", 14f)
        editor?.putFloat("tempLow", 0f)
        editor?.putFloat("tempHigh", 150f)
        editor?.putFloat("clarityLow", 0f)
        editor?.putFloat("clarityHigh", 40f)
        editor?.putFloat("feedingRate", 0f)
        editor?.apply()
    }
    private fun saveName(name: String) {
        val sharedPreferences =
            this.activity?.getSharedPreferences("other", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString("tankName", name)
        editor?.apply()
    }
}