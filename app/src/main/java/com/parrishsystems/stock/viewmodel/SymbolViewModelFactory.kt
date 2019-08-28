package com.parrishsystems.stock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parrishsystems.stock.repo.StockMarketRepo

class SymbolViewModelFactory(private val repo: StockMarketRepo): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SymbolViewModel(repo) as T
    }
}
