package com.parrishsystems.stock.model

import com.google.gson.annotations.SerializedName

data class Intraday(
    @SerializedName("open")
    val open: Float?,
    @SerializedName("close")
    val close: Float? = 0.0f,
    @SerializedName("high")
    val high: Float? = 0.0f,
    @SerializedName("low")
    val low: Float? = 0.0f,
    @SerializedName("volume")
    val volume: String? = "") {

    var timeStamp: String = ""
}