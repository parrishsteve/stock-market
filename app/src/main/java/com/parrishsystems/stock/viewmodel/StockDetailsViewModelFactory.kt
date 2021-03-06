package com.parrishsystems.stock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parrishsystems.stock.repo.StockMarketRepo

class StockDetailsViewModelFactory(private val repo: StockMarketRepo): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StockDetailsViewModel(repo) as T
    }
}