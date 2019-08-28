package com.parrishsystems.stock.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.parrishsystems.stock.model.Intraday
import com.parrishsystems.stock.model.IntradayRoot
import com.parrishsystems.stock.model.Quote
import com.parrishsystems.stock.model.QuoteRoot
import com.parrishsystems.stock.repo.StockMarketRepo
import com.parrishsystems.stock.repo.rest_api.ApiCallback
import com.parrishsystems.stock.utils.Formatters

class StockDetailsViewModel(private val repo: StockMarketRepo) : ViewModel() {

    // The views will observe on this data stream
    private val info = MutableLiveData<Quote>()
    private val intraday = MutableLiveData<IntradayRoot>()

    val data: LiveData<CompanyView> = Transformations.map(info) {
        // Prep for the view
        CompanyView(it)
    }

    // A data path for errors the view should show.
    val errorMsg = MutableLiveData<String>()

    val intradayData: LiveData<List<IntradayView>> = Transformations.map(intraday) { root ->
        root.list.map { dayData ->
            IntradayView(dayData)
        }
    }

    data class IntradayView(var day: Intraday) {
        val price: Float = day.close ?: 0.0f
        var time: String = day.timeStamp.substringAfter(" ").dropLast(3)
    }

    data class CompanyView(
        val q: Quote
    ) {
        val symbol: String = q.symbol ?: ""
        val name: String = q.name ?: ""
        val exchange: String = q.exchange ?: ""
        val price: String = Formatters.formatStockValue(q.price)
        val open: String = Formatters.formatStockValue(q.open)
        val dayChange: String = q.dayChange ?: ""
        val dayChangePct: String = Formatters.formatPercentage(q.changePercentage!!)
        val prevClose: String = Formatters.formatStockValue(q.closeYesterday)
        val volume: String = q.volume ?: ""
        val other: String = q.volumeAvg ?: ""
        private val dayLow: String = Formatters.formatStockValue(q.low)
        private val dayHigh: String = Formatters.formatStockValue(q.high)
        private val yearLow: String = Formatters.formatStockValue(q.yearLow)
        private val yearHigh: String = Formatters.formatStockValue(q.yearHigh)
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
        repo.intraday.getData(repo.selectedSymbol, object : ApiCallback<QuoteRoot> {
            override fun onComplete(desc: String, resp: QuoteRoot) {
                info.value = resp.qoutes.get(0)
            }

            override fun onError(errMsg: String) {
                errorMsg.value = errMsg
            }
        })
    }

    fun getIntradayData() {
        repo.intraday.getIntradayData(repo.selectedSymbol, object : ApiCallback<IntradayRoot> {
            override fun onComplete(desc: String, resp: IntradayRoot) {
                intraday.value = resp
            }

            override fun onError(errMsg: String) {
                errorMsg.value = errMsg
            }
        })
    }
}
