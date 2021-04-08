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
import kotlin.properties.Delegates
import kotlin.random.Random

class HomeScreenViewModel(private val repository: Repository) : ViewModel() {

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

    private val _eventFab = MutableLiveData<Boolean>()
    val eventFab: LiveData<Boolean>
        get() = _eventFab

    private val _eventNextTip = MutableLiveData<Boolean>()
    val eventNextTip: LiveData<Boolean>
        get() = _eventNextTip

    private val _currentTip = MutableLiveData<String>()
    val currentTip: LiveData<String>
        get() = _currentTip

    private val _currentVideo = MutableLiveData<String>()
    val currentVideo: LiveData<String>
        get() = _currentVideo

    var index: Int = 0

    private var tipsVideo = mutableListOf(
        "7fybZjqVgq4", //1: Water Change
        "ZagBfWiAwlk", // Maintain Fish Tank
        "dkTwdtySxOc", //Heater guide
        "",
        "",
        "J1DVP8-garg", //Regulate pH
        "BU8Umtj39DE" // Algea
    )
    private var tipsArray = mutableListOf(
        "Treat tap water with Water Conditioner before using it to fill your aquarium",
        "Different fish require different amounts of water to stay happy and healthy."
                + "A good rule of thumb for small fish is one gallon of water per inch of fish, " +
                "but larger fish can break this rule. Some Koi require 250 gallons per fish!",
        "To avoid harmful bacterial buildup, keep your tank between 65 and 85 degrees fahrenheit. " +
                "Tropical fish like water temperatures of 75 to 80 degrees fahrenheit.\n",
        "An air pump helps re-oxygenate the water of your aquarium and spread heat around",
        "Many filters come with bacteria cultures to help neutralize ammonia, make sure to change it often!",
        "Ammonia is naturally accumulated by waste. If you see your clarity dropping and pH rising, it might be time to cycle your water.",
        "Video that will help you get rid of Algae"
    )

    private lateinit var repeatCall: Job

    // private lateinit var repeatRangesCall: Job
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        _currentTip.value = tipsArray[0]
        _currentVideo.value = tipsVideo[0]
        _phRange.value = listOf(0f, 0f)
        _tempRange.value = listOf(0f, 0f)
        _clarityRange.value = listOf(0f, 0f)
    }

    private fun startReceivingJob(timeInterval: Long): Job {
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
                _getResponse.value = StringTanksProperties("-1", "-1", "-1")
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
            val obj = FeedingRate(1, fishNum.value!!)
            var postFeedingRateDeferred = repository.pushFeedingRateAsync(obj)
            try {
                var result = postFeedingRateDeferred.await()
                _postResponse.value = result
            } catch (e: Exception) {
                _postResponse.value?.success = "0"
                _postResponse.value?.message = "Did not send"

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

    //Color.parseColor("#fcf8f5")
    fun changeClarity(): Int {
        val clarityLow = clarityRange.value?.get(0)!!
        val clarityHigh = clarityRange.value?.get(1)!!
        val medium = (clarityHigh - (clarityHigh * .25)).toFloat()
        return when (clarity.value!!) {
            in clarityLow..medium -> {
                _clarityText.value = "CLEAN"; Color.GREEN
            }
            in medium..clarityHigh -> {
                _clarityText.value = "Approaching Dirtiness"; Color.YELLOW
            }
            in clarityHigh..4000f -> {
                _clarityText.value = "DIRTY"; Color.RED
            }
            else -> Color.GRAY
        }
    }

    fun changeProgressColor(start: Float, end: Float, current: Float): Int {
        var padding = (end - start) / 4
        if (padding != 0.toFloat()) {
            return when (current) {
                in start + padding..end - padding -> Color.GREEN
                in start..start + padding -> Color.YELLOW
                in end - padding..end -> Color.YELLOW
                else -> Color.RED
            }
        } else {
            if (current == end && current == start) return Color.GREEN
        }
        return Color.RED
    }

    fun updateProperties(pH: String, temp: String, clarity: String) {
        _pH.value = pH.toFloat()
        _temp.value = temp.toFloat()
        _clarity.value = clarity.toFloat()
    }

    fun updateFeedingRate(rotations: String) {
        _feedingRate.value = rotations.toInt()
        _fishNum.value = rotations.toInt()
    }

    fun updateRanges(
        phLow: String,
        phHigh: String,
        tempLow: String,
        tempHigh: String,
        clarityLow: String,
        clarityHigh: String,
        fishNum: String
    ) {
        _phRange.value = listOf(phLow.toFloat(), phHigh.toFloat())
        _tempRange.value = listOf(tempLow.toFloat(), tempHigh.toFloat())
        _clarityRange.value = listOf(clarityLow.toFloat(), clarityHigh.toFloat())
        _fishNum.value = fishNum.toInt()
    }

    fun restoreValues(
        ph: Float?,
        temp: Float?,
        clarity: Float?,
        fishNum: Int?,
        feedingRate: Int?,
        phLow: Float,
        phHigh: Float,
        tempLow: Float,
        tempHigh: Float,
        clarityLow: Float,
        clarityHigh: Float
    ) {
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

    private fun resetTips() {

        tipsArray = mutableListOf(
            "Treat tap water with Water Conditioner before using it to fill your aquarium",
            "Different fish require different amounts of water to stay happy and healthy."
                    + " A good rule of thumb for small fish is one gallon of water per inch of fish, " +
                    "but larger fish can break this rule. Some Koi require 250 gallons per fish!",
            "To avoid harmful bacterial buildup, keep your tank between 65 and 85 degrees fahrenheit. " +
                    "Tropical fish like water temperatures of 75 to 80 degrees fahrenheit.\n",
            "An air pump helps re-oxygenate the water of your aquarium and spread heat around",
            "Many filters come with bacteria cultures to help neutralize ammonia, make sure to change it often!",
            "Ammonia is naturally accumulated by waste. If you see your clarity dropping and pH rising, it might be time to cycle your water."
        )
        tipsVideo = mutableListOf(
            "7fybZjqVgq4", //1: Water Change
            "ZagBfWiAwlk", // Maintain Fish Tank
            "dkTwdtySxOc", //Heater guide
            "",
            "",
            "J1DVP8-garg", //Regulate pH
            "BU8Umtj39DE" // Algea
        )
    }

    fun nextTip() {
        index = Random.nextInt(tipsArray.size)
        _currentTip.value = tipsArray[index]
        _currentVideo.value = tipsVideo[index]
        tipsArray.removeAt(index)
        tipsVideo.removeAt(index)
        if (tipsArray.size == 0)
            resetTips()
    }

    fun onClarityPopUp() {
        _eventClarityPopUp.value = true
    }

    fun eventClarityPopUpComplete() {
        _eventClarityPopUp.value = false
    }

    fun onPhPopUp() {
        _eventPhPopUp.value = true
    }

    fun eventPhPopUpComplete() {
        _eventPhPopUp.value = false
    }

    fun onTempPopUp() {
        _eventTempPopUp.value = true
    }

    fun eventTempPopUpComplete() {
        _eventTempPopUp.value = false
    }

    fun onFab() {
        _eventFab.value = true
    }

    fun eventFabComplete() {
        _eventFab.value = false
    }

    fun onNextTip() {
        _eventNextTip.value = true
    }

    fun eventNextTipComplete() {
        _eventNextTip.value = false
    }

    override fun onCleared() {
        repeatCall.cancel()
        super.onCleared()
    }
}
