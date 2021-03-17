package com.example.thundertank.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.thundertank.repository.Repository


class SharedSetupViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedSetupViewModel::class.java)) {
            return SharedSetupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}