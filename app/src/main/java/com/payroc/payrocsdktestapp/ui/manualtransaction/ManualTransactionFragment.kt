package com.payroc.payrocsdktestapp.ui.manualtransaction

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.payroc.payrocsdktestapp.BuildConfig
import com.payroc.payrocsdktestapp.R
import com.payroc.sdk.PLog
import com.payroc.sdk.enums.Environment
import com.payroc.sdk.enums.Gateways
import com.payroc.sdk.models.LineItem
import com.payroc.sdk.models.Transaction
import com.payroc.sdk.models.pos.PaymentDeviceManual
import java.math.BigDecimal

class ManualTransactionFragment : Fragment() {

	companion object {
		const val TAG = "ManualTransactionFragment"
		fun newInstance() = ManualTransactionFragment()
	}

	private var lineItems: ArrayList<LineItem> = arrayListOf()
	private lateinit var viewModel: ManualTransactionViewModel
	private lateinit var amount: EditText
	private lateinit var cardNumber: EditText
	private lateinit var expDate: EditText
	private lateinit var cvNum: EditText
	private lateinit var postal: EditText
	private lateinit var submit: AppCompatButton
	private lateinit var resultBody: TextView

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view = inflater.inflate(R.layout.manual_transaction_fragment, container, false)
		val subLayout: ConstraintLayout = view.findViewById(R.id.manualForm)

		amount = subLayout.findViewById(R.id.txnAmount)
		cardNumber = subLayout.findViewById(R.id.cardNumber)
		expDate = subLayout.findViewById(R.id.expDate)
		cvNum = subLayout.findViewById(R.id.cvvNum)
		postal = subLayout.findViewById(R.id.postal)
		submit = subLayout.findViewById(R.id.submitManualTxn)
		resultBody = subLayout.findViewById(R.id.manualResultBody)

		// TODO - remove this once we are ready to go live.
		// Pre-fill test params - because I am lazy.
		if (BuildConfig.DEBUG){
			amount.setText(getString(R.string.testAmount))
			cardNumber.setText(getString(R.string.testCard))
			expDate.setText(getString(R.string.testExpDate).replace("/", "")) // IBX wants no slash // TODO - check formatting on all data points
			cvNum.setText(getString(R.string.testCvv))
			postal.setText(getString(R.string.testPostal))
		}

		submit.setOnClickListener{
			startTransaction()
		}

		// TODO - implement listeners on text change that have full validations on each field.

		return view

	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		viewModel = ViewModelProviders.of(this).get(ManualTransactionViewModel::class.java)
	}

	private fun createLineItems(){
		val amount = if (amount.text.isNotEmpty()) amount.text.toString() else "0.00"
		val lineItem = LineItem(BigDecimal(amount), "Line Item 1", 1, "", Bundle.EMPTY)
		lineItems.add(lineItem)
	}

	private fun startTransaction() {
		// Normally initialized elsewhere.
		viewModel.payrocSdk.setGateway("payr9197","Sandbox1", Gateways.IBX, Environment.Stage)
		createLineItems()

		// Some rudimentary checks - should provide validators internally
		val transaction = Transaction(lineItems, PaymentDeviceManual())
		transaction.cardData.cardNum = if (cardNumber.text.isNotEmpty()) cardNumber.text.toString() else "0"
		transaction.cardData.expDate = if (expDate.text.isNotEmpty()) expDate.text.toString() else "0"
		transaction.cardData.cvNum = if (cvNum.text.isNotEmpty()) cvNum.text.toString() else "0"
		transaction.cardAddress.postal = if (postal.text.isNotEmpty()) postal.text.toString() else "0"

		PLog.i(TAG, "Starting Transaction\n${transaction.toHashMap(Gateways.IBX)}", null, BuildConfig.DEBUG)

		// TODO - consider passing a simple message and the response object as well.
		viewModel.payrocSdk.startManualTransaction(transaction) { success, msg ->
			PLog.i(TAG, "Transaction was successful $success \nPayload returned $msg", null, BuildConfig.DEBUG)
			// Do something with the response for records
			activity!!.runOnUiThread {
				resultBody.text = msg
				// TODO - figure out why it isn't updating the UI thread.
			}
		}
	}

}
