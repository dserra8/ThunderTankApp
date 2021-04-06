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
import com.example.thundertank.databinding.SetupOtherBinding
import com.example.thundertank.databinding.SetupRangesBinding
import com.example.thundertank.repository.Repository

class SetupOther : Fragment() {
    private lateinit var binding: SetupOtherBinding

    private lateinit var viewModelFactory: SharedSetupViewModelFactory
    private val viewModel: SharedSetupViewModel by activityViewModels {viewModelFactory}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.setup_other,
            container,
            false
        )

        val repository = Repository()
        viewModelFactory = SharedSetupViewModelFactory(repository)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.eventNextOther.observe(viewLifecycleOwner, Observer { next ->
            if(next) {
                viewModel.eventNextOtherComplete()
                viewModel.updateOther(binding.turbidityLowNum.text.toString().toDouble(), binding.turbidityHighNum.text.toString().toDouble())
                findNavController().navigate(SetupOtherDirections.actionSetupOtherToFinalHomeScreen())
            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }
}