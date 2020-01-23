package com.payroc.payrocsdktestapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.payroc.sdk.R
import com.payroc.sdk.ui.history.HistoryFragment


class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaction_history_activity)
        supportActionBar?.title = "Transaction List"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HistoryFragment.newInstance())
                .commitNow()
        }

/*        val styling = DefaultStyling()
        styling.bgColor = Color.BLUE
        styling.bgDrawable = R.drawable.bg_gradient_primary
        styling.bgAnimation = "background_animation.json"
        PayrocSdk.styling = styling*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
