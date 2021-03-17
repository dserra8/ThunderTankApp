package com.example.thundertank.network




data class TanksProperties(
    val temp: Float,
    val pH: Float,
    val clear: Float
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
    val feedingRate: Float,
    val tempLow: Float,
    val tempHigh: Float,
    val phLow: Float,
    val phHigh: Float,
    val clarityLow: Float,
    val clarityHigh: Float
)

data class PostResponse (
    var success: String,
    var message: String
)