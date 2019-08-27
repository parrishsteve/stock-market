package com.parrishsystems.stock.model

import com.google.gson.annotations.SerializedName

data class Intraday(
    @SerializedName("open")
    val open: Float?,
    @SerializedName("close")
    val close: Float?,
    @SerializedName("high")
    val high: Float?,
    @SerializedName("low")
    val low: Float?,
    @SerializedName("volume")
    val volume: String?) {
}