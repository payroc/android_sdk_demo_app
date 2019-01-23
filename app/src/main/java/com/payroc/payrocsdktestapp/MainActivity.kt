package com.payroc.payrocsdktestapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.payroc.sdk.PayrocSdk
import com.payroc.sdk.enums.ActivityResultTypes
import com.payroc.sdk.enums.TransactionModes
import com.payroc.sdk.ui.DevicesActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "TBD", Snackbar.LENGTH_LONG).setAction("Action", null).show()
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
                intent.putExtra("mode", TransactionModes.MANUAL.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TRANSACTION.ordinal)
            }
            R.id.nav_emv -> {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("mode", TransactionModes.EMV.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TRANSACTION.ordinal)
            }
            R.id.nav_multi_line -> {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("mode", TransactionModes.MULTI_LINE.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TRANSACTION.ordinal)
            }
            R.id.nav_ingest -> {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("mode", TransactionModes.INGEST.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TRANSACTION.ordinal)
            }
            R.id.nav_inventory -> {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("mode", TransactionModes.INVENTORY.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TRANSACTION.ordinal)
            }
            R.id.nav_devices -> {
                PayrocSdk().findDevice(this)
            }
            R.id.nav_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivityForResult(intent, ActivityResultTypes.GET_HISTORY.ordinal)
            }
            R.id.nav_tools -> {
                val intent = Intent(this, ToolsActivity::class.java)
                startActivity(intent)
            }

            // TODO - add card ingest and multi line transaction types.
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
