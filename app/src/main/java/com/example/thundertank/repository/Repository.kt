package com.example.thundertank.repository

import com.example.thundertank.network.RetrofitInstance
import com.example.thundertank.network.TanksProperties
import retrofit2.Response

class Repository {

    suspend fun getProperties(): Response<TanksProperties>{
        return RetrofitInstance.api.getProperties()
    }

    suspend fun pushPost(post: TanksProperties): Response<TanksProperties>{
        return RetrofitInstance.api.pushPost(post)
    }

}