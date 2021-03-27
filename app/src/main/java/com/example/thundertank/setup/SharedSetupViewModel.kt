package com.example.thundertank.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thundertank.network.PostResponse
import com.example.thundertank.network.StringTanksProperties
import com.example.thundertank.network.StringTanksRanges
import com.example.thundertank.network.TanksRanges
import com.example.thundertank.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SharedSetupViewModel(private val repository: Repository): ViewModel() {


    private val _postResponse = MutableLiveData<PostResponse>()
    val postResponse: LiveData<PostResponse>
        get() = _postResponse

    private val _getResponse = MutableLiveData<StringTanksRanges>()
    val getResponse: LiveData<StringTanksRanges>
        get() = _getResponse

    private val _fishNum = MutableLiveData<Int>()
    val fishNum: LiveData<Int>
        get() = _fishNum

    //Ranges for the different values
    private val _tempRange = MutableLiveData<List<Double>>()
    val tempRange: LiveData<List<Double>>
        get() = _tempRange

    //Ranges for the different values
    private val _clarityRange = MutableLiveData<List<Double>>()
    val clarityRange: LiveData<List<Double>>
        get() = _clarityRange

    //Ranges for the different values
    private val _phRange = MutableLiveData<List<Double>>()
    val phRange: LiveData<List<Double>>
        get() = _phRange

    private val _feedingRate = MutableLiveData<Int>()
    val feedingRate: LiveData<Int>
        get() = _feedingRate

    private val _eventPreset = MutableLiveData<Boolean>()
    val eventPreset: LiveData<Boolean>
        get() = _eventPreset

    private val _eventConfirm = MutableLiveData<Boolean>()
    val eventConfirm: LiveData<Boolean>
        get() = _eventConfirm

    private val _whichFish = MutableLiveData<Int>()
    val whichFish: LiveData<Int>
        get() = _whichFish


    var id: Int = 1
    var error: String = ""
    private var viewModelJob = Job()
    private val coroutineScope= CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

    }

    fun postRanges() {
        coroutineScope.launch {
            val obj = TanksRanges(id, fishNum.value!!, 3.0, tempRange.value?.get(0)!!, tempRange.value?.get(1)!!,
                phRange.value?.get(0)!!, phRange.value?.get(1)!!, clarityRange.value?.get(0)!!, clarityRange.value?.get(0)!!)
            var postRangesDeferred = repository.pushRangesAsync(obj)
            try {
                var result = postRangesDeferred.await()
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
                _getResponse.value = result

            } catch (e: Exception) {
                error = e.toString()
            }
        }
    }

    fun mergeRanges(): Boolean{

        val newTempRange = checkRanges(
            getResponse.value?.tempLow?.toDouble()!!, getResponse.value?.tempHigh?.toDouble()!!,
            tempRange.value?.get(0)!!, tempRange.value?.get(1)!!)
        val newPhRange = checkRanges(
            getResponse.value?.phLow?.toDouble()!!, getResponse.value?.phHigh?.toDouble()!!,
            phRange.value?.get(0)!!, phRange.value?.get(1)!!)
        val newClarityRange = checkRanges(
            getResponse.value?.clarityLow?.toDouble()!!, getResponse.value?.clarityHigh?.toDouble()!!,
            clarityRange.value?.get(0)!!, clarityRange.value?.get(1)!!)

        if (newTempRange[0] != -1.0 && newTempRange[1] != -1.0 && newPhRange[0] != -1.0 && newPhRange[1] != -1.0 && newClarityRange[0] != -1.0 && newClarityRange[1] != -1.0)
        {
            _fishNum.value = _getResponse.value?.fishNum?.toInt()?.plus(_fishNum.value!!)
            _phRange.value =  newPhRange
            _tempRange.value = newTempRange
            _clarityRange.value = newClarityRange
            return true
        }
        return false
    }

    private fun checkRanges(oldLow: Double, oldHigh: Double, newLow: Double, newHigh: Double ): List<Double> {
        val newRange = mutableListOf<Double>((-1).toDouble(), (-1).toDouble())
        when{
            (newLow >= oldLow) && (newLow <= oldHigh) ->  newRange[0] = newLow
            (newLow < oldLow) -> newRange[0] = oldLow
            else -> newRange[0] = (-1).toDouble()
        }
        when{
            (newHigh <= oldHigh) && (newHigh >= oldLow) ->  newRange[1] = newHigh
            (newHigh > oldHigh) -> newRange[1] = oldHigh
            else -> newRange[1] = (-1).toDouble()
        }

        return newRange
    }

    fun eventFishAngel(){
        onPreset()
        _whichFish.value = 0
        _phRange.value = listOf(6.8,7.8)
        _tempRange.value = listOf(76.0,85.0)
        _clarityRange.value = listOf(0.0,25.0)
    }
    fun eventFishBetta(){
        onPreset()
        _whichFish.value = 1
        _phRange.value = listOf(6.8,7.5)
        _tempRange.value = listOf(78.0,80.0)
        _clarityRange.value = listOf(0.0,25.0)
    }
    fun eventFishGold(){
        onPreset()
        _whichFish.value = 2
        _phRange.value = listOf(7.2,7.6)
        _tempRange.value = listOf(68.0,74.0)
        _clarityRange.value = listOf(0.0,25.0)
    }
    fun eventFishGuppies(){
        onPreset()
        _whichFish.value = 3
        _phRange.value = listOf(6.8,7.8)
        _tempRange.value = listOf(72.0,78.0)
        _clarityRange.value = listOf(0.0,25.0)
    }
    fun eventFishMollies(){
        onPreset()
        _whichFish.value = 4
        _phRange.value = listOf(7.5,8.5)
        _tempRange.value = listOf(72.0,78.0)
        _clarityRange.value = listOf(0.0,25.0)
    }
    fun eventFishNeon(){
        onPreset()
        _whichFish.value = 5
        _phRange.value = listOf(6.0,7.0)
        _tempRange.value = listOf(70.0,81.0)
        _clarityRange.value = listOf(0.0,25.0)
    }

    private fun onPreset() {
        _eventPreset.value = true
    }
    fun eventPresetComplete(){
        _eventPreset.value = false
    }
    fun onConfirm() {
        _eventConfirm.value = true
    }
    fun eventConfirmComplete(){
        _eventConfirm.value = false
    }
    fun updateFishNum(newFishNum: Int){
        _fishNum.value = newFishNum
    }

}
