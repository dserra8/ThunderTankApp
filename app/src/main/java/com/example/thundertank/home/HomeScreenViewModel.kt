package com.example.thundertank.home


import android.graphics.Color
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thundertank.network.TanksProperties
import com.example.thundertank.repository.Repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.math.floor

class HomeScreenViewModel(private val repository: Repository): ViewModel() {
    private val _response2 = MutableLiveData<Response<TanksProperties>>()
    val response2: LiveData<Response<TanksProperties>>
        get() = _response2

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _response = MutableLiveData<Response<TanksProperties>>()
    val response: LiveData<Response<TanksProperties>>
        get() = _response

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
    private val _salt = MutableLiveData<Float>()
    val salt: LiveData<Float>
        get() = _salt

    init {

        _pH.value = 0.0f
        _temp.value = 0.0f
        _clarity.value = 0.0f
        _salt.value = 0.0f
        _phRange.value = listOf(9.5f,10.5f)
        getTanksProperties()
    }

    private fun getTanksProperties() {
        viewModelScope.launch {
            _response.value = repository.getProperties()
        }
    }
    fun pushPost(post: TanksProperties){
        viewModelScope.launch {
            val response2 = repository.pushPost(post)
            _response2.value = response2
        }
    }

    fun changeProgressColor(start:Float,end:Float,current: Float): Int{
        var padding = (end-start)/4
        if (padding != 0.toFloat()) {
            return when(current){
                in start+padding..end-padding -> Color.GREEN
                in start..start+padding -> Color.rgb(255,69,0)
                in end-padding..end -> Color.rgb(255,69,0)
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

    fun updateProperties(pH: Double, temp: Double, salt:Double, clarity: Double){
        _pH.value = pH.toFloat()
        _temp.value = temp.toFloat()
        _salt.value = salt.toFloat()
        _clarity.value = clarity.toFloat()

    }

    override fun onCleared() {
        super.onCleared()
    }


}
