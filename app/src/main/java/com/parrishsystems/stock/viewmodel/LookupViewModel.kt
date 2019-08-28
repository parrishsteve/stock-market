package com.parrishsystems.stock.viewmodel

import android.os.Handler
import androidx.lifecycle.*
import com.parrishsystems.stock.model.LookupRoot
import com.parrishsystems.stock.model.LookupSymbol
import com.parrishsystems.stock.repo.StockMarketRepo
import com.parrishsystems.stock.repo.rest_api.ApiCallback
import com.parrishsystems.stock.utils.Formatters

class LookupViewModel(private val repo: StockMarketRepo) : ViewModel() {
    // These values are used to throttle symbol search requests.  Typically
    // the search routine is called by the view for every keystoke but we want
    // to wait and collect a few chars before we fire the request.
    private val handler: Handler = Handler()
    private val TIME_TO_WAIT_FOR_SEARCH_INPUT_MS = 1000L

    // The data that the view will observe on
    private val searchData = MutableLiveData<List<LookupSymbol>>()
    val search: LiveData<List<Symbol>> = Transformations.map(searchData) {
        it.map {i ->
            // Prep the data for the view.
            Symbol(i.symbol!!, i.name!!, i.stockExchange!!, Formatters.formatCurrency(i.price!!))
        }
    }

    // View will listen to this to see if it should include the option to requet more data.
    val moreData = MutableLiveData<Boolean>()

    // A data path for errors that the view should show.
    val errorMsg = MutableLiveData<String>()

    var pageNum: Int = 0
    var searchTerm: String = ""

    var searchResults = mutableSetOf<LookupSymbol>()

    val networkCallback = object: ApiCallback<LookupRoot> {
        override fun onComplete(desc: String, resp: LookupRoot) {
            var hasMorePages = false
            if (resp.page != null && resp.totalPages != null) {
                pageNum = resp.page
                if (pageNum < resp.totalPages) {
                    hasMorePages = true
                }
            }
            moreData.value = hasMorePages
            searchResults.addAll(resp.symbols)
            searchData.value = searchResults.toList()
        }

        override fun onError(errMsg: String) {
            errorMsg.value = errMsg
            moreData.value = false
        }
    }

    var searchRunnable = Runnable {
        repo.symbolLookup.lookUp(searchTerm, pageNum + 1, networkCallback)
    }

    /**
     * Will search for symbols. If the throttle is false then the requet is sent right away
     * Otherwise the request will be sent 1500ms after the last time this routine was called.
     */
    fun search(term: String, throttle: Boolean = true) {
        handler.removeCallbacks(searchRunnable)
        pageNum = 0
        searchTerm = term
        if (!term.isNullOrBlank()) {
            searchResults.clear()
            moreData.value = false
            searchData.value = searchResults.toList()
            if (throttle) {
                handler.postDelayed(searchRunnable, TIME_TO_WAIT_FOR_SEARCH_INPUT_MS)
            } else {
                handler.post(searchRunnable)
            }
        }
    }

    fun searchMore() {
        repo.symbolLookup.lookUp(searchTerm, pageNum + 1, networkCallback)
    }

    /**
     * This is what is presented to the view.
     */
    data class Symbol(val symbol: String,
               val name: String,
               val stockExchange: String,
               val price: String)
}
