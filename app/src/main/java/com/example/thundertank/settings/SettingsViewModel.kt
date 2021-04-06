package com.example.thundertank.settings


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

class SettingsViewModel(private val repository: Repository): ViewModel() {


    private val _postResponse = MutableLiveData<PostResponse>()
    val postResponse: LiveData<PostResponse>
        get() = _postResponse

    private val _eventRESET = MutableLiveData<Boolean>()
    val eventRESET: LiveData<Boolean>
        get() = _eventRESET

    private val _eventChangeName = MutableLiveData<Boolean>()
    val eventChangeName: LiveData<Boolean>
        get() = _eventChangeName

    private var viewModelJob = Job()
    private val coroutineScope= CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

    }

    fun postReset() {
        coroutineScope.launch {
            val obj = TanksRanges(1, 0, 0.0, 0.0, 150.0,
                0.0, 14.0, 0.0, 40.0)
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

    fun onRESET(){
        _eventRESET.value = true
    }
    fun eventRESETcomplete(){
        _eventRESET.value = false
    }
    fun onChangeName(){
        _eventChangeName.value = true
    }
    fun eventChangeNameComplete(){
        _eventChangeName.value = false
    }
}
