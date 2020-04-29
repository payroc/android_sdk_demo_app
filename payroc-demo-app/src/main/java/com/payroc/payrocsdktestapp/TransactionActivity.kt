package com.payroc.payrocsdktestapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.payroc.android_core.PLog
import com.payroc.android_core.enums.TxnModes
import com.payroc.payrocsdktestapp.ui.emvtransaction.EmvTransactionFragment
import com.payroc.payrocsdktestapp.ui.inventory.InventoryFragment
import com.payroc.payrocsdktestapp.ui.multilinetransaction.MultiLineTransactionFragment
import com.payroc.payrocsdktestapp.ui.partialtransaction.PartialTransactionFragment
import com.payroc.sdk.ui.cardingest.CardIngestFragment


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
			TxnModes.MANUAL.name -> PLog.i(TAG, "Use NumberPadFragment with extra_device=Manual")
			TxnModes.EMV.name -> startFragment(savedInstanceState, R.layout.emv_transaction_activity, EmvTransactionFragment.newInstance())
			TxnModes.MULTI_LINE.name -> startFragment(savedInstanceState, R.layout.multi_line_transaction_activity, MultiLineTransactionFragment.newInstance())
			TxnModes.INVENTORY.name -> startFragment(savedInstanceState, R.layout.inventory_transaction_activity, InventoryFragment.newInstance())
			TxnModes.INGEST.name -> startFragment(savedInstanceState, R.layout.card_ingest_fragment, CardIngestFragment.newInstance()) // TODO - uses form we control completely. make sure it doesn't blow up
			TxnModes.PARTIAL.name -> startFragment(savedInstanceState, R.layout.partial_transaction_activity, PartialTransactionFragment.newInstance())
			else -> PLog.i(TAG, "No transaction type was specified.")
		}

	}

	private fun startFragment(savedInstanceState: Bundle?, viewId: Int, fragment: Fragment){
		setContentView(viewId)
		if (savedInstanceState == null) {
			supportFragmentManager.beginTransaction()
				.replace(R.id.container, fragment)
				.commitNow()
		}
	}

}
