package com.parrishsystems.stock.repo

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import java.util.concurrent.atomic.AtomicBoolean

class SavedSymbols private constructor(val context: Application) {

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "stock-symbols"
    private val KEY_NAME = "symbols"

    private val sharedPref: SharedPreferences

    private var symbols: Symbols

    init {
        sharedPref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        //symbols = Symbols() //loadSymbols()
        symbols = loadSymbols()
        //symbols.symbols.add("MSFT")
        //symbols.symbols.add("ALB")
        //symbols.symbols.add("AMZN")
    }

    companion object {
        private lateinit var INSTANCE: SavedSymbols
        val instance: SavedSymbols get() = INSTANCE
        private val initialized = AtomicBoolean()

        fun init(context: Application) {
            if (!initialized.getAndSet(true)) {
                INSTANCE = SavedSymbols(context)
            }
        }
    }


    fun getSymbols(): List<String> {
        return symbols.symbols
    }


    fun addSymbol(value: String) {
        symbols.symbols.add(value)
        // Write to shared prefs
        val data = Gson().toJson(symbols)
        sharedPref.edit().putString(KEY_NAME, data).apply()
    }

    fun deleteSymbol(value: String) {
        if (symbols.symbols.remove(value)) {
            val data = Gson().toJson(symbols)
            sharedPref.edit().putString(KEY_NAME, data).apply()
        }
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

}