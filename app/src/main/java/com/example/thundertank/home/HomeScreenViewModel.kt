package com.example.thundertank.home


import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundertank.network.PostResponse
import com.example.thundertank.network.StringTanksProperties
import com.example.thundertank.network.TanksProperties
import com.example.thundertank.repository.Repository
import kotlinx.coroutines.*

import retrofit2.Response
import kotlin.math.floor

class HomeScreenViewModel(private val repository: Repository): ViewModel() {
    private val _postResponse = MutableLiveData<PostResponse>()
    val postResponse: LiveData<PostResponse>
        get() = _postResponse

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _getResponse = MutableLiveData<StringTanksProperties>()
    val getResponse: LiveData<StringTanksProperties>
        get() = _getResponse

    //Ranges for the different values
    private val _tempRange = MutableLiveData<List<Float>>()
    val tempRange: LiveData<List<Float>>
        get() = _tempRange

    //Ranges for the different values
    private val _clarityRange = MutableLiveData<List<Float>>()
    val clarityRange: LiveData<List<Float>>
        get() = _clarityRange

    //Ranges for the different values
    private val _phRange = MutableLiveData<List<Float>>()
    val phRange: LiveData<List<Float>>
        get() = _phRange

    //LiveData for temperature
    private val _temp = MutableLiveData<Float>()
    val temp: LiveData<Float>
        get() = _temp

    //LiveData for pH level
    private val _pH = MutableLiveData<Float>()
    val pH: LiveData<Float>
        get() = _pH

    //LiveData for temperature
    private val _clarity = MutableLiveData<Float>()
    val clarity: LiveData<Float>
        get() = _clarity


    private lateinit var repeatCall: Job
    private var viewModelJob = Job()
    private val coroutineScope= CoroutineScope(viewModelJob + Dispatchers.Main)


    init {

        Log.i("model", "ViewModel Created")
//        _pH.value = 10.0f
//        _temp.value = 80.0f
//        _clarity.value = 0.4f
//        _salt.value = 1.4f
        _phRange.value = listOf(9.5f,10.5f)
        _tempRange.value = listOf(70f,80f)
        _clarityRange.value = listOf(.5f,5f)
    }

    private fun startReceivingJob(timeInterval: Long): Job{
        return CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                // add your task here
                getTanksProperties()
                delay(timeInterval)
            }
        }
    }
    private fun getTanksProperties() {
        coroutineScope.launch {
            var getPropertiesDeferred = repository.getPropertiesAsync()
            try {
                var result = getPropertiesDeferred.await()
                _getResponse.value = result
            } catch (e: Exception) {
               // _response.value = "Failure: ${e.message}"
            }
        }
    }

    fun changeProgressColor(start:Float,end:Float,current: Float): Int{
        var padding = (end-start)/4
        if (padding != 0.toFloat()) {
            return when(current){
                in start+padding..end-padding -> Color.GREEN
                in start..start+padding -> Color.YELLOW
                in end-padding..end -> Color.YELLOW
                else -> Color.RED
            }
        }
        else{
            if (current == end && current == start) return Color.GREEN
        }
        return Color.RED
    }

    fun onClickTest(newNum: Editable){
        _pH.value = newNum.toString().toFloat()
    }

    fun updateProperties(pH: String, temp: String, clarity: String){
        _pH.value = pH.toFloat()
        _temp.value = temp.toFloat()
        _clarity.value = clarity.toFloat()

    }

    fun restoreValues(ph: Float?,temp: Float?,clarity: Float?){
        _pH.value = ph
        _temp.value = temp
        _clarity.value = clarity
      //  repeatCall = startReceivingJob(10000)
    }
    override fun onCleared() {
       // repeatCall.cancel()
        Log.i("model", "ViewModel Destroyed")
        super.onCleared()
    }
}
