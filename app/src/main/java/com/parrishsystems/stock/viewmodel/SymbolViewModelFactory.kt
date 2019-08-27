package com.parrishsystems.stock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parrishsystems.stock.repo.SavedSymbols

class SymbolViewModelFactory(private val repo: SavedSymbols): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SymbolViewModel(repo) as T
    }
}
