package com.example.thundertank.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.thundertank.repository.Repository


class SettingsViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}