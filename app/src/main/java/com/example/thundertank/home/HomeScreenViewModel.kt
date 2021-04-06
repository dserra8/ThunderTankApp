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

     // The internal MutableLiveData String that stores the status of the most recent request
    private val _getResponse = MutableLiveData<StringTanksProperties>()
    val getResponse: LiveData<StringTanksProperties>
        get() = _getResponse

    private val _getRotations = MutableLiveData<StringFeedingRate>()
    val getRotations: LiveData<StringFeedingRate>
        get() = _getRotations

    private val _postResponse = MutableLiveData<PostResponse>()
    val postResponse: LiveData<PostResponse>
        get() = _postResponse

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
    private val _clarityText = MutableLiveData<String>()
    val clarityText: LiveData<String>
        get() = _clarityText

    //LiveData for temperature
    private val _fishNum = MutableLiveData<Int>()
    val fishNum: LiveData<Int>
        get() = _fishNum

    //LiveData for temperature
    private val _feedingRate = MutableLiveData<Int>()
    val feedingRate: LiveData<Int>
        get() = _feedingRate

    private val _eventClarityPopUp = MutableLiveData<Boolean>()
    val eventClarityPopUp: LiveData<Boolean>
        get() = _eventClarityPopUp

    private val _eventPhPopUp = MutableLiveData<Boolean>()
    val eventPhPopUp: LiveData<Boolean>
        get() = _eventPhPopUp

    private val _eventTempPopUp = MutableLiveData<Boolean>()
    val eventTempPopUp: LiveData<Boolean>
        get() = _eventTempPopUp

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

    private fun getTanksProperties() {
        coroutineScope.launch {
            var getPropertiesDeferred = repository.getPropertiesAsync()
            try {
                var result = getPropertiesDeferred.await()
                _getResponse.value = result
            } catch (e: Exception) {
               _getResponse.value = StringTanksProperties("-1","-1","-1")
            }
        }
    }

    fun getRotations() {
        coroutineScope.launch {
            var getFeedingRateDeferred = repository.getFeedingRateAsync()
            try {
                var result = getFeedingRateDeferred.await()
                _getRotations.value = result
            } catch (e: Exception) {
                _getRotations.value = StringFeedingRate("-2")
            }
        }
    }

    fun postFeedingRate() {
        coroutineScope.launch {
            val obj = FeedingRate(1,fishNum.value!!)
            var postFeedingRateDeferred = repository.pushFeedingRateAsync(obj)
            try {
                var result = postFeedingRateDeferred.await()
                _postResponse.value = result
            } catch (e: Exception) {
                _postResponse.value?.success= "0"
                _postResponse.value?.message= "Did not send"

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
                _getRanges.value = StringTanksRanges("-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1")
            }
        }
    }

    fun changeClarity(): Int{
        val clarityLow = clarityRange.value?.get(0)!!
        val clarityHigh = clarityRange.value?.get(1)!!
        val medium = (clarityHigh-(clarityHigh*.25)).toFloat()
        return when(clarity.value!!){
            in clarityLow .. medium->{ _clarityText.value = "CLEAN"; Color.parseColor("#fcf8f5") }
            in medium..clarityHigh -> {_clarityText.value = "Approaching Dirtiness"; Color.YELLOW}
            in clarityHigh..4000f -> {_clarityText.value = "DIRTY"; Color.RED}
            else -> Color.GRAY
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
    fun updateProperties(pH: String, temp: String, clarity: String){
        _pH.value = pH.toFloat()
        _temp.value = temp.toFloat()
        _clarity.value = clarity.toFloat()
    }
    fun updateFeedingRate(rotations: String){
      _feedingRate.value = rotations.toInt()
    }
    fun updateRanges(phLow: String, phHigh: String,  tempLow: String, tempHigh: String, clarityLow: String, clarityHigh: String, fishNum: String){
        _phRange.value = listOf(phLow.toFloat(), phHigh.toFloat())
        _tempRange.value = listOf(tempLow.toFloat(), tempHigh.toFloat())
        _clarityRange.value = listOf(clarityLow.toFloat(), clarityHigh.toFloat())
        _fishNum.value = fishNum.toInt()
    }
    fun restoreValues(ph: Float?,temp: Float?,clarity: Float?, fishNum: Int?, feedingRate: Int?, phLow: Float, phHigh : Float, tempLow: Float, tempHigh: Float, clarityLow: Float, clarityHigh: Float){
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

    fun onClarityPopUp() {
        _eventClarityPopUp.value = true
    }
    fun eventClarityPopUpComplete(){
        _eventClarityPopUp.value = false
    }

    fun onPhPopUp() {
        _eventPhPopUp.value = true
    }
    fun eventPhPopUpComplete(){
        _eventPhPopUp.value = false
    }

    fun onTempPopUp() {
        _eventTempPopUp.value = true
    }
    fun eventTempPopUpComplete(){
        _eventTempPopUp.value = false
    }
    override fun onCleared() {
        repeatCall.cancel()
        super.onCleared()
    }
}
