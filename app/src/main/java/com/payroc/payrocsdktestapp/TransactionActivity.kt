package com.payroc.payrocsdktestapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.payroc.payrocsdktestapp.ui.emvtransaction.EmvTransactionFragment
import com.payroc.payrocsdktestapp.ui.manualtransaction.ManualTransactionFragment



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
		} else {
			setContentView(R.layout.emv_transaction_activity)
			if (savedInstanceState == null) {
				supportFragmentManager.beginTransaction()
					.replace(R.id.container, EmvTransactionFragment.newInstance())
					.commitNow()
			}
		}
	}

}
