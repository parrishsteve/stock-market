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
    var list = mutableListOf<Intraday>()


    init {
        val keys = intradayData.keySet().sorted()
        keys.forEach {
            val v = intradayData.get(it)
            val json = Gson().toJson(v)
            list.add(Gson().fromJson(json, Intraday::class.java))
        }
    }

    fun getIntraday(key: String) : Intraday? {
        val v = intradayData.get(key)
        if (v != null) {
            val json = Gson().toJson(v)
            return Gson().fromJson(json, Intraday::class.java)
        }
        return null
    }

    fun isError(): Boolean {
        return !errorMessage.isNullOrEmpty()
    }

}