package com.example.thundertank.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thundertank.repository.Repository

class HomeScreenViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            return HomeScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}