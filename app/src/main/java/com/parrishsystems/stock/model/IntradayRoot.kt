package com.parrishsystems.stock.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class IntradayRoot(
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("intraday")
    val intradayData: JsonObject,
    @SerializedName("message")
    val errorMessage: String?)
{
    var list: List<Intraday>? = null
    init {
        val keys = intradayData.keySet().sorted()
        list = keys.map {
            val v = intradayData.get(it)
            val json = Gson().toJson(v)
            val intraday = Gson().fromJson(json, Intraday::class.java)
            intraday.timeStamp = it
            intraday
        }
    }

    fun isError(): Boolean {
        return !errorMessage.isNullOrEmpty()
    }

}