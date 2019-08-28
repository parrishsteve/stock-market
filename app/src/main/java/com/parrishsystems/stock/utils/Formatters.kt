package com.parrishsystems.stock.utils

import java.text.DecimalFormat
import java.text.NumberFormat

object Formatters {

    fun formatCurrency(value: Float): String {
        val defaultFormat = NumberFormat.getCurrencyInstance()
        return defaultFormat.format(value)
    }

    fun formatPercentage(value: String): String {
        if (!value.contains('-')) return "+$value%"
        return "$value%"
    }
    fun formatStockValue(value: Float?): String {
        return value?.let {
            "%.2f".format(it)
        } ?: ""
    }
}