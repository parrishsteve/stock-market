package com.parrishsystems.stock.repo.rest_api

import com.parrishsystems.stock.model.QuoteRoot
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuoteService: BaseService() {

    // The API imposes a limit when using their 'free' account.
    // Only 4 symbols can be submitted with for a price quote
    val MAX_NUM_SYMS_TO_REQUEST = 4

    private lateinit var _symbol: String

    private fun getQuote(symbol: String, callback: ApiCallback<QuoteRoot>) {
        val call = service.getQuote(symbol, API_KEY)
        call.enqueue(object : Callback<QuoteRoot> {
            override fun onFailure(call: Call<QuoteRoot>, t: Throwable) {
                callback.onError("Failed to get qoute for $_symbol")
            }

            override fun onResponse(call: Call<QuoteRoot>, response: Response<QuoteRoot>) {
                response.body()?.let {
                    callback.onComplete(_symbol, it)
                } ?: callback.onError("Could not get $_symbol at this time")
            }
        })
    }

    private fun concatSymbols(symbols: List<String>) : String {
        return symbols.joinToString().replace(" ","")
    }

    fun getQuotes(symbol: String, callback: ApiCallback<QuoteRoot>) {
        _symbol = symbol
        getQuote(symbol, callback)
    }

    fun getQuotes(symbols: List<String>, callback: ApiCallback<QuoteRoot>) {
        _symbol = symbols.joinToString()
        val symArgs = mutableListOf<String>()
        for ((i, s) in symbols.withIndex()) {
            if (i != 0 && i % MAX_NUM_SYMS_TO_REQUEST == 0) {
                symArgs.add(s)
                val syms = concatSymbols(symArgs)
                getQuote(syms, callback)
                symArgs.clear()
            }
            else {
                symArgs.add(s)
            }
        }
        if (symArgs.isNotEmpty()) {
            val s = concatSymbols(symArgs)
            getQuote(s, callback)
        }
    }
}