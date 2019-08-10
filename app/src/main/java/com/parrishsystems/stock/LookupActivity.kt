package com.parrishsystems.stock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LookupActivity : AppCompatActivity() {

    companion object {
        val REQUEST_CODE = 2
        val RESULT_CODE = 200
        val RESULT_KEY = "Sym"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lookup_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LookupFragment.newInstance())
                .commitNow()
        }
    }

}
