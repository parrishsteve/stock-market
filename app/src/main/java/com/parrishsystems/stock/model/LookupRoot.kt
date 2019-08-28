package com.parrishsystems.stock.model

import com.google.gson.annotations.SerializedName

data class LookupRoot(
    @SerializedName("total_returned")
    val totalReturned: Int? = 0,

    @SerializedName("total_results")
    val totalResults: Int? = 0 ,

    @SerializedName("total_pages")
    val totalPages: Int? = 0 ,

    @SerializedName("page")
    val page: Int? = 0,

    @SerializedName("data")
    val symbols : List<LookupSymbol> = mutableListOf(),

    @SerializedName("message")
    val errorMessage : String?)
{
    fun isError(): Boolean {
        return !errorMessage.isNullOrEmpty()
    }
}