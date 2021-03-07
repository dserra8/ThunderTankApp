package com.example.thundertank.network


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface TankApiService {
    @GET("db_view.php")
    suspend fun getProperties(): Response<TanksProperties>


    @POST("v1/?op=addProperties")
    suspend fun pushPost(
        @Body post: TanksProperties
    ): Response<TanksProperties>
}
