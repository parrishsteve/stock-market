package com.parrishsystems.stock.repo.rest_api

import com.parrishsystems.stock.model.LookupRoot
import com.parrishsystems.stock.model.LookupSymbol
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LookupService: BaseService() {

    private lateinit var _symbol: String

    private val SEARCH_BY = "symbol,name"
    private val STOCK_EXCHANGE = "NASDAQ,NYSE"
    private val SORT_BY = "symbol"

    fun lookup(symbol: String, page: Int, callback: ApiCallback<LookupRoot>) {
        _symbol = symbol
        val call = service.lookup(symbol, SEARCH_BY, STOCK_EXCHANGE, SORT_BY, page, API_KEY)
        call.enqueue(object : Callback<LookupRoot> {
            override fun onFailure(call: Call<LookupRoot>, t: Throwable) {
                callback.onError("Failed")
            }

            override fun onResponse(call: Call<LookupRoot>, response: Response<LookupRoot>) {
                response.body()?.let {
                    callback.onComplete(_symbol, it)
                } ?: callback.onError("Could not get $_symbol at this time")
            }
        })
    }
}