package com.payroc.payrocsdktestapp.ui.statusview

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.payroc.payrocsdktestapp.R
import com.payroc.sdk.enums.TxnViewState
import com.payroc.sdk.ui.paymentprocessing.PaymentStatusCustomerView

class StatusViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.transaction_status)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.status_view_activity)
        val custView = findViewById<PaymentStatusCustomerView>(R.id.payment_status_customer_view)
        custView.setMessageBy(TxnViewState.APPROVED)
        custView.setMessageBy(TxnViewState.RECEIPT_REQUEST)
        findViewById<Button>(R.id.payment_status_action_button).setOnClickListener {finish()}
        findViewById<View>(R.id.payment_status_image).setOnClickListener {finish()}
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