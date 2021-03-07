package com.example.thundertank.setup


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.example.thundertank.R
import com.example.thundertank.databinding.FragmentSetupScreen1Binding



class SetupScreen1Fragment : Fragment() {

    private lateinit var binding: FragmentSetupScreen1Binding


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

        // Inflate the layout for this fragment
        return binding.root
    }
}