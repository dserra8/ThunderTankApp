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
import com.example.thundertank.databinding.SetupRangesBinding
import com.example.thundertank.repository.Repository


class SetupRanges : Fragment() {

    private lateinit var binding: SetupRangesBinding

    private lateinit var viewModelFactory: SharedSetupViewModelFactory
    private val viewModel: SharedSetupViewModel by activityViewModels {viewModelFactory}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.setup_ranges,
            container,
            false
        )

        val repository = Repository()
        viewModelFactory = SharedSetupViewModelFactory(repository)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.eventNextRanges.observe(viewLifecycleOwner, Observer { next ->
            if(next) {
                viewModel.eventNextRangesComplete()
                viewModel.updateRanges(binding.phLowNum.text.toString().toDouble(), binding.phHighNum.text.toString().toDouble(),
                    binding.tempLowNum.text.toString().toDouble(), binding.tempHighNum.text.toString().toDouble())
                findNavController().navigate(SetupRangesDirections.actionSetupRangesToSetupOther())
            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }
}