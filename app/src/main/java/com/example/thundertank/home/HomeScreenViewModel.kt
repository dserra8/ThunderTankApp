package com.example.thundertank.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thundertank.network.TanksApi
import com.example.thundertank.network.TanksProperties

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeScreenViewModel(): ViewModel() {

    //LiveData for temperature
    private val _temp = MutableLiveData<Int>()
    val temp: LiveData<Int>
        get() = _temp

    //LiveData for pH level
    private val _pH = MutableLiveData<Int>()
    val pH: LiveData<Int>
        get() = _pH

    //LiveData for temperature
    private val _clarity = MutableLiveData<Int>()
    val clarity: LiveData<Int>
        get() = _clarity

    //LiveData for temperature
    private val _salt = MutableLiveData<Int>()
    val salt: LiveData<Int>
        get() = _salt

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
       // getTanksProperties()
    }

    private fun getTanksProperties() {
     //   TODO("Not yet implemented")
        coroutineScope.launch {
            var getPropertiesDeferred = TanksApi.retrofitService.getProperties()
            try {
                var listResult = getPropertiesDeferred.await()
               // _response.value = "Success: ${listResult.size} Mars properties retrieved"
            } catch (e: Exception) {
               // _response.value = "Failure: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
