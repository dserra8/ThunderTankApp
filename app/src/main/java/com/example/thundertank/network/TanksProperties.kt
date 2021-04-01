package com.example.thundertank.network




data class TanksProperties(
    val temp: Float,
    val pH: Float,
    val clear: Float
)

data class FeedingRate(
    val rotations: Int
)
data class StringFeedingRate(
    val rotationsApp: Int,
    val rotationsPi: Int
)
data class StringTanksProperties(
    val temp: String,
    val pH: String,
    val clear: String
)

data class StringTanksRanges(
    val fishNum: String,
    val feedingRate: String,
    val tempLow: String,
    val tempHigh: String,
    val phLow: String,
    val phHigh: String,
    val clarityLow: String,
    val clarityHigh: String
)

data class TanksRanges(
    val id: Int,
    val fishNum: Int,
    val feedingRate: Double,
    val tempLow: Double,
    val tempHigh: Double,
    val phLow: Double,
    val phHigh: Double,
    val clarityLow: Double,
    val clarityHigh: Double
)

data class PostResponse (
    var success: String,
    var message: String
)