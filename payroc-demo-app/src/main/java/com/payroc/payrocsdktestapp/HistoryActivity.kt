package com.payroc.payrocsdktestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.payroc.payrocsdktestapp.ui.history.HistoryFragment

class HistoryActivity : AppCompatActivity() {
    // TODO - make sure to add function for re-sending receipts.
    // TODO - for receipts, perhaps we give them a way to hook into the process in addition to us sending a receipt on their behalf?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.title = "History"

        setContentView(R.layout.history_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HistoryFragment.newInstance())
                .commitNow()
        }
    }

}
