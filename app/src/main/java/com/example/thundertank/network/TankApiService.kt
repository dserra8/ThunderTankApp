package com.example.thundertank.network


import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST


interface TankApiService {
    @GET("android_connect/db_view.php")
    fun getPropertiesAsync(): Deferred<StringTanksProperties>

    @GET("android_connect/db_view_row_ranges.php")
    fun getRangesAsync(): Deferred<StringTanksRanges>

    @Headers("Content-Type: application/json")
    @POST("android_connect/db_update_row_ranges.php")
    fun pushRangesAsync(
        @Body post: TanksRanges
    ): Deferred<PostResponse>
}