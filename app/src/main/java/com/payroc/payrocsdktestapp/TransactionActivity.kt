package com.payroc.payrocsdktestapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.payroc.payrocsdktestapp.ui.emvtransaction.EmvTransactionFragment
import com.payroc.payrocsdktestapp.ui.manualtransaction.ManualTransactionFragment
import com.payroc.payrocsdktestapp.ui.multilinetransaction.MultiLineTransactionFragment
import com.payroc.sdk.PLog


class TransactionActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val intent = intent
		val txnType = intent.getStringExtra("mode")

		supportActionBar!!.title = "$txnType Transaction"

		if (txnType == TransactionModes.MANUAL.name){
			setContentView(R.layout.manual_transaction_activity)
			if (savedInstanceState == null) {
				supportFragmentManager.beginTransaction()
					.replace(R.id.container, ManualTransactionFragment.newInstance())
					.commitNow()
			}
		} else if (txnType == TransactionModes.EMV.name) {
			setContentView(R.layout.emv_transaction_activity)
			if (savedInstanceState == null) {
				supportFragmentManager.beginTransaction()
					.replace(R.id.container, EmvTransactionFragment.newInstance())
					.commitNow()
			}
		} else if (txnType == TransactionModes.INGEST.name) {
			// TODO - uses form we control completely. make sure it doesn't blow up
			// Seems to be something funky with importing from the sdk
//			setContentView(R.layout.card_ingest_fragment)
//			if (savedInstanceState == null) {
//				supportFragmentManager.beginTransaction()
//					.replace(R.id.container, CardIngest.newInstance())
//					.commitNow()
//			}
		} else if (txnType == TransactionModes.MULTI_LINE.name){
			setContentView(R.layout.multi_line_transaction_activity)
			if (savedInstanceState == null) {
				supportFragmentManager.beginTransaction()
					.replace(R.id.container, MultiLineTransactionFragment.newInstance())
					.commitNow()
			}
		} else {
			PLog.i(TAG, "No transaction type was specified.")
		}
	}

	// TODO - make sure this doesn't create any zombie objects or mess with reference counting
	companion object {
	    const val TAG = "TransactionActivity"
	}

}
