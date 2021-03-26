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
import com.example.thundertank.network.*
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

    private val _getRanges = MutableLiveData<StringTanksRanges>()
    val getRanges: LiveData<StringTanksRanges>
        get() = _getRanges

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

    //LiveData for temperature
    private val _fishNum = MutableLiveData<Int>()
    val fishNum: LiveData<Int>
        get() = _fishNum

    //LiveData for temperature
    private val _feedingRate = MutableLiveData<Float>()
    val feedingRate: LiveData<Float>
        get() = _feedingRate


    private lateinit var repeatCall: Job
   // private lateinit var repeatRangesCall: Job
    private var viewModelJob = Job()
    private val coroutineScope= CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        _phRange.value = listOf(0f,0f)
        _tempRange.value = listOf(0f,0f)
        _clarityRange.value = listOf(0f,0f)

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
    private fun startReceivingRanges(timeInterval: Long): Job{
        return CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                // add your task here
                getRecentRanges()
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
              // _getResponse.value = StringTanksProperties("${e.message}","1","1")
            }
        }
    }

    fun getRecentRanges() {
        coroutineScope.launch {
            var getRangesDeferred = repository.getRangesAsync()
            try {
                var result = getRangesDeferred.await()
                _getRanges.value = result
            } catch (e: Exception) {

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
    fun updateRanges(phLow: String, phHigh: String,  tempLow: String, tempHigh: String, clarityLow: String, clarityHigh: String, fishNum: String, feedingRate: String){
        _phRange.value = listOf(phLow.toFloat(), phHigh.toFloat())
        _tempRange.value = listOf(tempLow.toFloat(), tempHigh.toFloat())
        _clarityRange.value = listOf(clarityLow.toFloat(), clarityHigh.toFloat())
        _fishNum.value = fishNum.toInt()
        _feedingRate.value = feedingRate.toFloat()
    }

    fun restoreValues(ph: Float?,temp: Float?,clarity: Float?, fishNum: Int?, feedingRate: Float?, phLow: Float, phHigh : Float, tempLow: Float, tempHigh: Float, clarityLow: Float, clarityHigh: Float){
        _pH.value = ph
        _temp.value = temp
        _clarity.value = clarity
        _phRange.value = listOf(phLow, phHigh)
        _tempRange.value = listOf(tempLow, tempHigh)
        _clarityRange.value = listOf(clarityLow, clarityHigh)
        _fishNum.value = fishNum
        _feedingRate.value = feedingRate
        getRecentRanges()
        repeatCall = startReceivingJob(5000)
    }
    override fun onCleared() {
        repeatCall.cancel()
        super.onCleared()
    }
}
