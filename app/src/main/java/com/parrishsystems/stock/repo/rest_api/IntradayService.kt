package com.parrishsystems.stock.repo.rest_api

import com.parrishsystems.stock.BuildConfig
import com.parrishsystems.stock.model.IntradayRoot
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IntradayService() {
    private lateinit var _symbol: String

    protected val INTRADAY_URL = "https://intraday.worldtradingdata.com/api/v1/"
    protected val API_KEY = BuildConfig.STOCK_API_KEY
    private val client = RetrofitClient(INTRADAY_URL, BuildConfig.DEBUG).create()
    protected val service = client.create(Apis::class.java)

    fun intradayData(symbol: String, range: Int, interval: Int, callback: ApiCallback<IntradayRoot>) {
        _symbol = symbol
        val call = service.intraday(symbol, range, interval, API_KEY)
        call.enqueue(object : Callback<IntradayRoot> {
            override fun onFailure(call: Call<IntradayRoot>, t: Throwable) {
                callback.onError("Failed")
            }

            override fun onResponse(call: Call<IntradayRoot>, response: Response<IntradayRoot>) {
                response.body()?.let {
                    callback.onComplete(_symbol, it)
                } ?: callback.onError("Could not get $_symbol at this time")
            }
        })
    }
}