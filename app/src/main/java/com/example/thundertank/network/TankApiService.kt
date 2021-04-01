package com.example.thundertank.network


import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST


interface TankApiService {
    @GET("tofromapp/db_view.php")
    fun getPropertiesAsync(): Deferred<StringTanksProperties>

    @GET("tofromapp/db_view_row_ranges.php")
    fun getRangesAsync(): Deferred<StringTanksRanges>

    @GET("tofromapp/view_feeding_rate.php")
    fun getFeedingRateAsync(): Deferred<StringFeedingRate>

    @Headers("Content-Type: application/json")
    @POST("tofromapp/db_update_row_ranges.php")
    fun pushRangesAsync(
        @Body post: TanksRanges
    ): Deferred<PostResponse>

    @Headers("Content-Type: application/json")
    @POST("tofromapp/update_feeding_rate.php")
    fun pushFeedingRateAsync(
        @Body post: FeedingRate
    ): Deferred<PostResponse>
}