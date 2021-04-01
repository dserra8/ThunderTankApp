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
    fun getFeedingRateAsync(): Deferred<StringFeedingRate>{
        return RetrofitInstance.api.getFeedingRateAsync()
    }
    fun pushRangesAsync(post: TanksRanges): Deferred<PostResponse>{
        return RetrofitInstance.api.pushRangesAsync(post)
    }
    fun pushFeedingRateAsync(post: FeedingRate): Deferred<PostResponse>{
        return RetrofitInstance.api.pushFeedingRateAsync(post)
    }


}