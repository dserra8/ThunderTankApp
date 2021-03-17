package com.example.thundertank.setup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.thundertank.R
import com.example.thundertank.databinding.FinalHomeScreenBinding
import com.example.thundertank.repository.Repository


class FinalHomeScreen : Fragment() {

    private lateinit var binding: FinalHomeScreenBinding
    private lateinit var viewModelFactory: SharedSetupViewModelFactory
    private val viewModel: SharedSetupViewModel by activityViewModels {viewModelFactory}


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.final_home_screen,
            container,
            false
        )

        val repository = Repository()
        viewModelFactory = SharedSetupViewModelFactory(repository)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.eventConfirm.observe(viewLifecycleOwner, Observer { confirm ->
            if(confirm == true) {
                var configurationChange = false
                viewModel.eventConfirmComplete()
               // viewModel.postRanges()
                if(viewModel.postResponse.value?.success == "1") {
                    configurationChange = true
                    saveData()
                }
                findNavController().navigate(FinalHomeScreenDirections.actionFinalHomeScreenToHomeScreenFragment(configurationChange))
            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun saveData() {
        val sharedPreferences =
            this.activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putInt("numFish", viewModel.fishNum.value!!)
        editor?.apply()
    }
}