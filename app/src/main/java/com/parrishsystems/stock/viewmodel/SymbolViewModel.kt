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

    // The views will observe on this data stream
    private val quoteData = MutableLiveData<List<Quote>>()
    val quotes: LiveData<List<PriceQuote>> = Transformations.map(quoteData) {
        // Prep the data for the view.
        it.map {i ->
            PriceQuote(i.symbol!!, i.name!!, Formatters.formatCurrency(i.price!!), Formatters.formatCurrency(i.open!!))
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

    val networkCallback = object: ApiCallback<QuoteRoot> {
        override fun onError(errMsg: String) {
            Toast.makeText(application, errMsg, Toast.LENGTH_LONG).show()
        }

        override fun onComplete(desc: String, resp: QuoteRoot) {
            if (resp.qoutes.isEmpty()) {
                Toast.makeText(application, "Unknown Symbol $desc", Toast.LENGTH_LONG).show()
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
    data class PriceQuote(
        val symbol: String,
        val name: String,
        val price: String,
        val open: String)
}