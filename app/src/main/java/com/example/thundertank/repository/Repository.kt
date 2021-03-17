package com.example.thundertank.repository

import com.example.thundertank.network.*
import kotlinx.coroutines.Deferred

class Repository {

    fun getPropertiesAsync(): Deferred<StringTanksProperties>{
        return RetrofitInstance.api.getPropertiesAsync()
    }

    fun getRangesAsync(): Deferred<StringTanksRanges>{
        return RetrofitInstance.api.getRangesAsync()
    }
    fun pushRangesAsync(post: TanksRanges): Deferred<PostResponse>{
        return RetrofitInstance.api.pushRangesAsync(post)
    }

}