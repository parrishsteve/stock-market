package com.parrishsystems.stock.repo

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.parrishsystems.stock.model.*
import com.parrishsystems.stock.repo.rest_api.ApiCallback
import com.parrishsystems.stock.repo.rest_api.IntradayService
import com.parrishsystems.stock.repo.rest_api.LookupService
import com.parrishsystems.stock.repo.rest_api.QuoteService
import java.util.concurrent.atomic.AtomicBoolean

class StockMarketRepo private constructor(val context: Application) {

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "stock-symbols"
    private val KEY_NAME = "symbols"

    private val sharedPref: SharedPreferences

    private var symbols: Symbols

    var selectedSymbol: String = ""

    init {
        sharedPref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        symbols = loadSymbols()
        // Let's add indexes if they are not there
        getIndexes().forEach { index ->
            if (!symbols.symbols.contains(index)) symbols.symbols.add(index)
        }
    }

    companion object {
        private lateinit var INSTANCE: StockMarketRepo
        val instance: StockMarketRepo get() = INSTANCE
        private val initialized = AtomicBoolean()

        fun init(context: Application) {
            if (!initialized.getAndSet(true)) {
                INSTANCE = StockMarketRepo(context)
            }
        }
    }

    private fun getIndexes(): MutableList<String> {
        val indexes = mutableListOf<String>()
        indexes.add("^DJI")
        indexes.add("^IXIC")
        indexes.add("^GSPC")
        return indexes
    }

    private fun loadSymbols() : Symbols {

        val values = sharedPref.getString(KEY_NAME, "")

        var data: Symbols? = null
        try {
            data = Gson().fromJson(values, Symbols::class.java)
        }
        catch (e: JsonSyntaxException) {
            return Symbols() // Return an empty basket
        }
        catch (e2: JsonIOException) {
            return Symbols() // Return an empty basket
        }
        return data ?: Symbols()
    }

    val symbolLookup = SymbolLookup()

    inner class SymbolLookup {
        fun lookUp(searchTerm: String, pageNum: Int, callback: ApiCallback<LookupRoot>) {
            LookupService().lookup(searchTerm, pageNum, object: ApiCallback<LookupRoot> {
                override fun onComplete(desc: String, resp: LookupRoot) {
                    callback.onComplete(desc, resp)
                }

                override fun onError(errMsg: String) {
                    callback.onError(errMsg)
                }
            })
        }
    }

    val intraday = Intraday()
    inner class Intraday {

        private var range: Int = 1 // One day
        private var interval: Int = 15 // every fifteen minutes

        fun getData(symbol: String, callback: ApiCallback<QuoteRoot>) {
            QuoteService().getQuotes(symbol, object : ApiCallback<QuoteRoot> {
                override fun onComplete(desc: String, resp: QuoteRoot) {
                    if (resp.qoutes.isNotEmpty()) {
                        callback.onComplete(desc, resp)
                    }
                    else {
                        callback.onError("Failed to get data for $symbol. Try again later")
                    }
                }

                override fun onError(errMsg: String) {
                    callback.onError(errMsg)
                }

            })
        }

        fun getIntradayData(symbol: String, callback: ApiCallback<IntradayRoot>) {
            IntradayService().intradayData(symbol, range, interval,
                object : ApiCallback<IntradayRoot> {
                    override fun onComplete(desc: String, resp: IntradayRoot) {
                        if (!resp.isError()) {
                            callback.onComplete(desc, resp)
                        }
                        else {
                            callback.onError(resp.errorMessage!!)
                        }
                    }

                    override fun onError(errMsg: String) {
                        callback.onError(errMsg)
                    }
                })
        }
    }

    val symbolData = SymbolData()
    inner class SymbolData {

        var quoteDataListener: ApiCallback<QuoteRoot>? = null

        val networkCallback = object : ApiCallback<QuoteRoot> {
            override fun onError(errMsg: String) {
                quoteDataListener?.onError(errMsg)
            }

            override fun onComplete(desc: String, resp: QuoteRoot) {
                if (resp.isError()) {
                    quoteDataListener?.onError(resp.errorMessage ?: "Error! Please try again later")
                } else if (resp.qoutes.isNullOrEmpty()) {
                    quoteDataListener?.onError("Unknown Symbol $desc")
                } else {
                    // If the symbol is not in storage then add it.
                    resp.qoutes.forEach { q ->
                        q.symbol?.let {
                            addSymbolToPrefs(it)
                        }
                    }
                    quoteDataListener?.onComplete(desc, resp)
                }
            }
        }

        fun getAllSymbolData() {
            val data = symbols.symbols.map {
                Quote(it)
            }
            quoteDataListener?.onComplete("", QuoteRoot(data))
            val service = QuoteService()
            service.getQuotes(symbols.symbols, networkCallback)
        }

        fun refreshAllSymbolData() {
            val service = QuoteService()
            service.getQuotes(symbols.symbols, networkCallback)
        }

        fun deleteSymbol(value: String) {
            deleteSymbolFromPrefs(value)
        }

        fun addSymbol(value: String) {
            val service = QuoteService()
            service.getQuotes(value, networkCallback)
        }

        private fun addSymbolToPrefs(value: String) {
            if (!symbols.symbols.contains(value)) {
                symbols.symbols.add(value)
                // Write to shared prefs
                val data = Gson().toJson(symbols)
                sharedPref.edit().putString(KEY_NAME, data).apply()
            }
        }

        private fun deleteSymbolFromPrefs(value: String) {
            if (symbols.symbols.remove(value)) {
                val data = Gson().toJson(symbols)
                sharedPref.edit().putString(KEY_NAME, data).apply()
            }
        }
    }

}