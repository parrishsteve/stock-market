package com.parrishsystems.stock.viewmodel

import android.app.Application
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.*
import com.parrishsystems.stock.model.LookupRoot
import com.parrishsystems.stock.model.LookupSymbol
import com.parrishsystems.stock.repo.rest_api.ApiCallback
import com.parrishsystems.stock.repo.rest_api.LookupService

class LookupViewModel(application: Application) : AndroidViewModel(application) {
    private val searchData = MutableLiveData<List<LookupSymbol>>()

    // These values are used to throttle symbol search requests.  Typically
    // the search routine is called by the view for every keystoke but we want
    // to wait and collect a few chars before we fire the request.
    private val handler: Handler = Handler()
    private val TIME_TO_WAIT_FOR_SEARCH_INPUT_MS = 1500L

    val search: LiveData<List<LookupSymbol>> = Transformations.map(searchData) {
        it
    }

    private val moreData = MutableLiveData<Boolean>()
    val isMore: LiveData<Boolean> = Transformations.map(moreData) {
        it
    }

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
            Toast.makeText(application, "Search failed", Toast.LENGTH_LONG).show()
            moreData.value = false
        }
    }

    var searchRunnable = Runnable {
        val service = LookupService()
        service.lookup(searchTerm, pageNum + 1, networkCallback)
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

    fun cancelSearch() {
        handler.removeCallbacks(searchRunnable)
        pageNum = 0
        searchTerm = ""
    }

    fun searchMore() {
        val service = LookupService()
        service.lookup(searchTerm, pageNum + 1, networkCallback)
    }
}
