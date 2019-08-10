package com.parrishsystems.stock.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.parrishsystems.stock.model.Quote
import com.parrishsystems.stock.model.QuoteRoot
import com.parrishsystems.stock.repo.SavedSymbols
import com.parrishsystems.stock.repo.rest_api.ApiCallback
import com.parrishsystems.stock.repo.rest_api.QuoteService
import java.util.*

class SymbolViewModel(application: Application) : AndroidViewModel(application) {

    val quoteMap = hashMapOf<String, Quote>()

    init {
        SavedSymbols.init(application)
    }

    private val quoteData = MutableLiveData<List<Quote>>()
    val quotes: LiveData<List<Quote>> = Transformations.map(quoteData) {
        it
    }

    fun initValues(symbols: List<String> ): List<Quote> {
        val l = mutableListOf<Quote>()
        for (s in symbols) {
            val q = Quote(s)
            quoteMap.put(s, q)
            l.add(Quote(s))
        }
        return l
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
                for (q in resp.qoutes) {
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
}