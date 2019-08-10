package com.parrishsystems.stock.model

import com.google.gson.annotations.SerializedName

data class QuoteRoot(
    @SerializedName("data")
    val qoutes: List<Quote> = mutableListOf()
)
