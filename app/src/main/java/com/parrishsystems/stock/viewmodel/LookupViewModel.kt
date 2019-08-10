package com.parrishsystems.stock.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.parrishsystems.stock.model.LookupRoot
import com.parrishsystems.stock.model.LookupSymbol
import com.parrishsystems.stock.repo.rest_api.ApiCallback
import com.parrishsystems.stock.repo.rest_api.LookupService

class LookupViewModel(application: Application) : AndroidViewModel(application) {
    private val searchData = MutableLiveData<List<LookupSymbol>>()

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

    fun search(term: String) {
        pageNum = 0
        searchResults.clear()
        val service = LookupService()
        service.lookup(term, pageNum + 1, networkCallback)
        searchTerm = term
        moreData.value = false
        searchData.value = searchResults.toList()
    }

    fun searchMore() {
        val service = LookupService()
        service.lookup(searchTerm, pageNum + 1, networkCallback)
    }
}
