package com.payroc.payrocsdktestapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.payroc.payrocsdktestapp.ui.emvtransaction.EmvTransactionFragment
import com.payroc.payrocsdktestapp.ui.inventory.InventoryFragment
import com.payroc.payrocsdktestapp.ui.manualtransaction.ManualTransactionFragment
import com.payroc.payrocsdktestapp.ui.multilinetransaction.MultiLineTransactionFragment
import com.payroc.sdk.PLog
import com.payroc.sdk.enums.TxnModes
import com.payroc.sdk.ui.transaction.CardIngestFragment


class TransactionActivity : AppCompatActivity() {

	companion object {
		const val TAG = "TransactionActivity"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val intent = intent
		val txnType = intent.getStringExtra("mode")

		supportActionBar!!.title = "$txnType Transaction"

		when (txnType) {
			TxnModes.MANUAL.name -> startFragment(savedInstanceState, R.layout.manual_transaction_activity, ManualTransactionFragment.newInstance())
			TxnModes.EMV.name -> startFragment(savedInstanceState, R.layout.emv_transaction_activity, EmvTransactionFragment.newInstance())
			TxnModes.MULTI_LINE.name -> startFragment(savedInstanceState, R.layout.multi_line_transaction_activity, MultiLineTransactionFragment.newInstance())
			TxnModes.INVENTORY.name -> startFragment(savedInstanceState, R.layout.inventory_transaction_activity, InventoryFragment.newInstance())
			TxnModes.INGEST.name -> startFragment(savedInstanceState, R.layout.card_ingest_fragment, CardIngestFragment.newInstance()) // TODO - uses form we control completely. make sure it doesn't blow up
			else -> PLog.i(TAG, "No transaction type was specified.")
		}

	}

	private fun startFragment(savedInstanceState: Bundle?, viewId: Int, fragment: Fragment){
		setContentView(R.layout.multi_line_transaction_activity)
		if (savedInstanceState == null) {
			supportFragmentManager.beginTransaction()
				.replace(R.id.container, fragment)
				.commitNow()
		}
	}

}
