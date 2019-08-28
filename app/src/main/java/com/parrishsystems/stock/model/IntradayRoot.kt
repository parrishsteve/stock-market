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
    val list: List<Intraday>
            get() {
                val keys = intradayData.keySet().sorted()
                return keys.map { time ->
                    val v = intradayData.get(time)
                    val json = Gson().toJson(v)
                    val intraday = Gson().fromJson(json, Intraday::class.java)
                    intraday.timeStamp = time
                    intraday
                }
            }

    fun isError(): Boolean {
        return !errorMessage.isNullOrEmpty()
    }

}