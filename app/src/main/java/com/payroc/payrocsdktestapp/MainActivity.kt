package com.payroc.payrocsdktestapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import com.payroc.sdk.PLog
import com.payroc.sdk.PayrocSdk
import com.payroc.sdk.enums.ActivityResultTypes
import com.payroc.sdk.enums.TxnModes
import com.payroc.sdk.models.LineItem
import com.payroc.sdk.models.Transaction
import com.payroc.sdk.ui.numberpad.NumberPadActivity
import com.payroc.sdk.ui.numberpad.NumberPadFragment
import com.payroc.sdk.ui.review.TxnReviewActivity
import com.payroc.sdk.ui.signature.SignatureActivity
import com.payroc.sdk.ui.tip.TipActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.math.BigDecimal

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, NumberPadFragment.newInstance())
//                .commitNow()
//        }

        setSupportActionBar(toolbar)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        fab.setOnClickListener { view ->
            val intent = Intent(this, TransactionActivity::class.java)
            intent.putExtra("mode", TxnModes.MANUAL.name)
            startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_manual -> {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("mode", TxnModes.MANUAL.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
            }
            R.id.nav_emv -> {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("mode", TxnModes.EMV.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
            }
            R.id.nav_multi_line -> {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("mode", TxnModes.MULTI_LINE.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
            }
            R.id.nav_ingest -> {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("mode", TxnModes.INGEST.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
            }
            R.id.nav_inventory -> {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("mode", TxnModes.INVENTORY.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
            }
            R.id.nav_devices -> {
                PayrocSdk().findDevice(this)
            }
            R.id.nav_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivityForResult(intent, ActivityResultTypes.GET_HISTORY.ordinal)
            }
            R.id.signature_test -> {
                val lineItems = LineItem(BigDecimal.TEN)
                val transaction = Transaction(arrayListOf(lineItems))
                val intent = Intent(this, SignatureActivity::class.java)
                intent.putExtra(getString(R.string.extra_transaction), transaction)
                startActivityForResult(intent, ActivityResultTypes.GET_SIGNATURE.ordinal)
            }
            R.id.tip_test -> {
                val lineItems = LineItem(BigDecimal.TEN)
                val transaction = Transaction(arrayListOf(lineItems))
                val intent = Intent(this, TipActivity::class.java)
                intent.putExtra(getString(R.string.extra_transaction), transaction)
                startActivityForResult(intent, ActivityResultTypes.GET_TIP.ordinal)
            }
            R.id.txn_review_test -> {
                val lineItems = LineItem(BigDecimal.TEN)
                val transaction = Transaction(arrayListOf(lineItems))
                val intent = Intent(this, TxnReviewActivity::class.java)
                intent.putExtra(getString(R.string.extra_transaction), transaction)
                startActivityForResult(intent, ActivityResultTypes.TXN_REVIEW.ordinal)
            }
            R.id.numpad_test -> {
                val intent = Intent(this, NumberPadActivity::class.java)
                intent.putExtra(getString(R.string.extra_tax_enabled), true)
                intent.putExtra(getString(R.string.extra_tax_percent), "6.5")
                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
            }
            R.id.nav_tools -> {
                val intent = Intent(this, ToolsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_nuke_data -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    PayrocSdk().nukeAllData(this)
                } else {
                    PLog.d("Main Activity", "SDK version of your device does not support this feature", null, BuildConfig.DEBUG)
                }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
