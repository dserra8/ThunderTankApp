package com.example.thundertank.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thundertank.network.PostResponse
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


    private val _fishNum = MutableLiveData<Int>()
    val fishNum: LiveData<Int>
        get() = _fishNum

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

    private var viewModelJob = Job()
    private val coroutineScope= CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

    }

    fun postRanges() {
        coroutineScope.launch {
            val obj = TanksRanges(1, fishNum.value!!, 3.0f, tempRange.value?.get(0)!!, tempRange.value?.get(1)!!,
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

    fun eventFishAngel(){
        onPreset()
        _whichFish.value = 0
        _phRange.value = listOf(6.8f,7.8f)
        _tempRange.value = listOf(76f,85f)
        _clarityRange.value = listOf(0f,25f)
    }
    fun eventFishBetta(){
        onPreset()
        _whichFish.value = 1
        _phRange.value = listOf(6.8f,7.5f)
        _tempRange.value = listOf(78f,80f)
        _clarityRange.value = listOf(0f,25f)
    }
    fun eventFishGold(){
        onPreset()
        _whichFish.value = 2
        _phRange.value = listOf(7.2f,7.6f)
        _tempRange.value = listOf(68f,74f)
        _clarityRange.value = listOf(0f,25f)
    }
    fun eventFishGuppies(){
        onPreset()
        _whichFish.value = 3
        _phRange.value = listOf(6.8f,7.8f)
        _tempRange.value = listOf(72f,78f)
        _clarityRange.value = listOf(0f,25f)
    }
    fun eventFishMollies(){
        onPreset()
        _whichFish.value = 4
        _phRange.value = listOf(7.5f,8.5f)
        _tempRange.value = listOf(72f,78f)
        _clarityRange.value = listOf(0f,25f)
    }
    fun eventFishNeon(){
        onPreset()
        _whichFish.value = 5
        _phRange.value = listOf(6.0f,7.0f)
        _tempRange.value = listOf(70f,81f)
        _clarityRange.value = listOf(0f,25f)
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

}
