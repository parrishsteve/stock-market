package com.parrishsystems.stock.model

import com.google.gson.annotations.SerializedName

data class QuoteRoot(
    @SerializedName("data")
    val qoutes: List<Quote> = mutableListOf(),
    @SerializedName("message")
    val errorMessage: String?) {

    fun isError(): Boolean {
        return !errorMessage.isNullOrEmpty()
    }
}
