package com.parrishsystems.stock.model

import com.google.gson.annotations.SerializedName

data class LookupSymbol (
    @SerializedName("symbol")
    val symbol: String? = "",

    @SerializedName("name")
    val name: String? = "",

    @SerializedName("stock_exchange_short")
    val stockExchange: String? = "",

    @SerializedName("price")
    val price: Float? = 0.0f)
{
    override fun equals(other: Any?): Boolean {
        if(other == null || other !is LookupSymbol)
            return false
        return symbol.equals(other.symbol, ignoreCase = true)
    }

    override fun hashCode(): Int =
        symbol.hashCode()

}
