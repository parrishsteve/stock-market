package com.parrishsystems.stock.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.parrishsystems.stock.model.IntradayRoot
import com.parrishsystems.stock.model.Quote
import com.parrishsystems.stock.model.QuoteRoot
import com.parrishsystems.stock.repo.SavedSymbols
import com.parrishsystems.stock.repo.rest_api.ApiCallback
import com.parrishsystems.stock.repo.rest_api.IntradayService
import com.parrishsystems.stock.repo.rest_api.QuoteService

class StockDetailsViewModel(private val repo: SavedSymbols) : ViewModel() {

    // The views will observe on this data stream
    private val info = MutableLiveData<Quote>()
    private val intraday = MutableLiveData<IntradayRoot>()

    var range: Int = 1
    var interval: Int = 60

    val data: LiveData<CompanyView> = Transformations.map(info) {
        // Prep for the view
        CompanyView(it)
    }

    val errorMsg = MutableLiveData<String>()

    val intradayData: LiveData<List<IntradayView>> = Transformations.map(intraday) {

        // Prep for the view
        val list = mutableListOf<IntradayView>()
        val values = it.intradayData.keySet().sorted()
        values.forEach { time ->
            val value = it.getIntraday(time)
            value?.let { dayData ->
                list.add(
                    IntradayView(
                        dayData.close!!,
                        time
                    )
                )
            }
        }
        list
    }

    data class IntradayView(val price: Float, val time: String) {

    }

    data class CompanyView(
        val q: Quote
    ) {
        val symbol: String = q.symbol ?: ""
        val name: String = q.name ?: ""
        val exchange: String = q.exchange ?: ""
        val price: String = q.price?.toString() ?: ""
        val open: String = q.open?.toString() ?: ""
        val dayChange: String = q.dayChange ?: ""
        val dayChangePct: String = q.changePercentage ?: ""
        val prevClose: String = q.closeYesterday?.toString() ?: ""
        val volume: String = q.volume ?: ""
        val other: String = q.volumeAvg ?: ""
        private val dayLow: String = q.low?.toString() ?: ""
        private val dayHigh: String = q.high?.toString() ?: ""
        private val yearLow: String = q.yearLow?.toString() ?: ""
        private val yearHigh: String = q.yearHigh?.toString() ?: ""
        val isPriceUp: Boolean
            get() {
                q.price?.let {
                    if (it >= q.closeYesterday!!) return true
                }
                return false
            }

        val dayRange: String
            get() {
                return "$dayLow - $dayHigh"
            }
        val yearRange: String
            get() {
                return "$yearLow - $yearHigh"
            }
    }

    fun getData() {
        QuoteService().getQuotes(repo.selectedSymbol, object : ApiCallback<QuoteRoot> {
            override fun onComplete(desc: String, resp: QuoteRoot) {
                if (resp.qoutes.isNotEmpty()) {
                    info.value = resp.qoutes.get(0)
                }
            }

            override fun onError(errMsg: String) {
                errorMsg.value = errMsg
            }

        })
    }

    fun getIntradayData() {
        IntradayService().intradayData(repo.selectedSymbol, range, interval,
            object : ApiCallback<IntradayRoot> {
                override fun onComplete(desc: String, resp: IntradayRoot) {
                    if (!resp.isError()) {
                        intraday.value = resp
                    }
                    else {
                        errorMsg.value = resp.errorMessage
                    }
                }

                override fun onError(errMsg: String) {
                    errorMsg.value = errMsg
                }
            })
    }

}
