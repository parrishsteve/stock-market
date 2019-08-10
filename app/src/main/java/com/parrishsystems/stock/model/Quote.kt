package com.parrishsystems.stock.model

import com.google.gson.annotations.SerializedName

open class Quote (
    @SerializedName("symbol") val symbol: String? = "",
    @SerializedName("price_open") val open: Float? = 0.00f,
    @SerializedName("day_high") val high: Float? = 0.00f,
    @SerializedName("day_low") val low: Float? = 0.00f,
    @SerializedName("price") val price: Float? = 0.00f,
    @SerializedName("stock_exchange_short") val exchange: String? = "",
    @SerializedName("name") val name: String? = ""): Comparable<Quote>
{
    override fun compareTo(other: Quote) = compareValuesBy(this, other,
        { it.symbol }
    )
}