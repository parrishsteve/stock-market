package com.parrishsystems.stock.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.parrishsystems.stock.model.Quote
import com.parrishsystems.stock.model.QuoteRoot
import com.parrishsystems.stock.repo.SavedSymbols
import com.parrishsystems.stock.repo.rest_api.ApiCallback
import com.parrishsystems.stock.repo.rest_api.QuoteService
import com.parrishsystems.stock.utils.Formatters

class SymbolViewModel(application: Application) : AndroidViewModel(application) {

    val quoteMap = hashMapOf<String, Quote>()

    init {
        SavedSymbols.init(application)
    }

    val errorMsg = MutableLiveData<String>()

    // The views will observe on this data stream
    private val quoteData = MutableLiveData<List<Quote>>()
    val quotes: LiveData<List<PriceQuote>> = Transformations.map(quoteData) {
        // Prep the data for the view.
        it.map {i ->
            PriceQuote(i)
        }
    }

    fun initValues(symbols: List<String> ): List<Quote> {
        val ret = symbols.map { Quote(it) }
        // insert the symbol in the hash
        ret.forEach {
            quoteMap.put(it.symbol!!, it)
        }
       return ret
    }

    fun getSymbols() {
        val syms = SavedSymbols.instance.getSymbols()
        val service = QuoteService()
        service.getQuotes(syms, networkCallback)
        quoteData.value = initValues(syms)
    }

    fun deleteSymbol(value: String) {
        quoteMap.remove(value)
        SavedSymbols.instance.deleteSymbol(value)
        quoteData.value = quoteMap.values.sorted()
    }

    fun addSymbol(value: String) {
        val service = QuoteService()
        service.getQuotes(value, networkCallback)
    }

    fun selectSymbol(symbol: String) {
        SavedSymbols.instance.selectedSymbol = symbol
    }

    val networkCallback = object: ApiCallback<QuoteRoot> {
        override fun onError(errMsg: String) {
            Toast.makeText(application, errMsg, Toast.LENGTH_LONG).show()
        }

        override fun onComplete(desc: String, resp: QuoteRoot) {
            if (resp.qoutes.isEmpty()) {
                errorMsg.value = "Unknown Symbol $desc"
            }
            else {
                // If the symbol is not in storage then add it.
                resp.qoutes.forEach { q ->
                    q.symbol?.let {
                        if (!quoteMap.containsKey(it)) {
                            SavedSymbols.instance.addSymbol(it)
                        }
                        quoteMap.put(it, q)
                    }
                }
                quoteData.value = quoteMap.values.sorted()
            }
        }
    }

    /**
     * Views will use this
     */
    data class PriceQuote(val q: Quote) {
        val symbol: String = q.symbol ?: ""
        val name: String = q.name ?: ""
        val price: String = Formatters.formatCurrency(q.price!!)
        val open: String = Formatters.formatCurrency(q.open!!)
        val low: String = Formatters.formatCurrency(q.low!!)
        val high: String = Formatters.formatCurrency(q.high!!)
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