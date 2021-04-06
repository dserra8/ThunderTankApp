package com.example.thundertank.setup


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
import com.example.thundertank.databinding.FragmentSetupScreen1Binding
import com.example.thundertank.home.HomeScreenViewModelFactory
import com.example.thundertank.repository.Repository


class SetupScreen1Fragment : Fragment() {

    private lateinit var binding: FragmentSetupScreen1Binding

    private lateinit var viewModelFactory: SharedSetupViewModelFactory
    private val viewModel: SharedSetupViewModel by activityViewModels {viewModelFactory}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_setup_screen1,
            container,
            false
        )

        val repository = Repository()
        viewModelFactory = SharedSetupViewModelFactory(repository)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.eventPreset.observe(viewLifecycleOwner, Observer { choosePreset ->
            if(choosePreset) {
                viewModel.eventPresetComplete()
                findNavController().navigate(SetupScreen1FragmentDirections.actionSetupScreen1FragmentToFinalHomeScreen())
            }
        })

        viewModel.eventManualSetup.observe(viewLifecycleOwner, Observer{ event ->
            if(event){
                viewModel.eventManualSetupComplete()
                findNavController().navigate(SetupScreen1FragmentDirections.actionSetupScreen1FragmentToSetupRanges())
            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }
}


