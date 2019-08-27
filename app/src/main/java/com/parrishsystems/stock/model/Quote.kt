package com.parrishsystems.stock.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Quote (
    @SerializedName("symbol") val symbol: String? = "",
    @SerializedName("price_open") val open: Float? = 0.00f,
    @SerializedName("day_high") val high: Float? = 0.00f,
    @SerializedName("day_low") val low: Float? = 0.00f,
    @SerializedName("price") val price: Float? = 0.00f,
    @SerializedName("52_week_high") val yearHigh: Float? = 0.00f,
    @SerializedName("52_week_low") val yearLow: Float? = 0.00f,
    @SerializedName("close_yesterday") val closeYesterday: Float? = 0.00f,
    @SerializedName("day_change") val dayChange: String? = "",
    @SerializedName("change_pct") val changePercentage: String? = "",
    @SerializedName("stock_exchange_short") val exchange: String? = "",
    @SerializedName("market_cap") val marketCap: String? = "",
    @SerializedName("volume") val volume: String? = "",
    @SerializedName("volume_avg") val volumeAvg: String? = "",
    @SerializedName("shares") val shares: String? = "",
    @SerializedName("name") val name: String? = ""): Comparable<Quote>, Serializable
{
    override fun compareTo(other: Quote) = compareValuesBy(this, other,
        { it.symbol }
    )
}
