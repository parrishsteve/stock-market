package com.parrishsystems.stock.viewmodel

import androidx.lifecycle.*
import com.parrishsystems.stock.model.Quote
import com.parrishsystems.stock.model.QuoteRoot
import com.parrishsystems.stock.repo.StockMarketRepo
import com.parrishsystems.stock.repo.rest_api.ApiCallback
import com.parrishsystems.stock.utils.Formatters

class SymbolViewModel(val repo: StockMarketRepo) : ViewModel() {

    val quoteMap = hashMapOf<String, Quote>()

    // A data path for errors that the view should show.
    val errorMsg = MutableLiveData<String>()

    // The views will observe on this data stream
    private val quoteData = MutableLiveData<List<Quote>>()
    val quotes: LiveData<List<PriceQuote>> = Transformations.map(quoteData) {
        // Prep the data for the view.
        it.map {i ->
            PriceQuote(i)
        }
    }

    val dataCallback = object: ApiCallback<QuoteRoot> {
        override fun onError(errMsg: String) {
            errorMsg.value = errMsg
        }

        override fun onComplete(desc: String, resp: QuoteRoot) {
            if (resp.isError()) {
                errorMsg.value = resp.errorMessage
            } else if (resp.qoutes.isNullOrEmpty()) {
                errorMsg.value = "Unknown Symbol $desc"
            } else {
                // If the symbol is not in storage then add it.
                resp.qoutes.forEach { q ->
                    quoteMap.put(q.symbol!!, q)
                }
            }
            quoteData.value = quoteMap.values.sorted()
        }
    }

    init {
        repo.symbolData.quoteDataListener = dataCallback
    }

    fun getSymbols() {
        repo.symbolData.getAllSymbolData()
    }

    fun refreshSymbols() {
        repo.symbolData.refreshAllSymbolData()
    }

    fun deleteSymbol(value: String) {
        quoteMap.remove(value)
        repo.symbolData.deleteSymbol(value)
        quoteData.value = quoteMap.values.sorted()
    }

    fun addSymbol(value: String) {
        repo.symbolData.addSymbol(value)
    }

    fun selectSymbol(symbol: String) {
        repo.selectedSymbol = symbol
    }

    /**
     * Views will use this
     */
    data class PriceQuote(val q: Quote) {
        val symbol: String = q.symbol ?: ""
        val name: String = q.name ?: ""
        val price: String = Formatters.formatStockValue(q.price)
        val open: String = Formatters.formatStockValue(q.open)
        val low: String = Formatters.formatStockValue(q.low)
        val high: String = Formatters.formatStockValue(q.high)
        val dayChange: String = q.dayChange ?: ""
        val dayChangePct: String = Formatters.formatPercentage(q.changePercentage!!)
        val isPriceUp: Boolean
        get() {
            q.price?.let {
                if (it >= q.closeYesterday!!) return true
            }
            return false
        }
    }
}