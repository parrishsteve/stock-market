package com.parrishsystems.stock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class StockDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stock_details_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, StockDetailsFragment.newInstance())
                .commitNow()
        }
    }
}
