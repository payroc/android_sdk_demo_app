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
import com.payroc.payrocsdktestapp.R
import com.payroc.sdk.PLog
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

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view = inflater.inflate(R.layout.manual_transaction_fragment, container, false)
		val subLayout: ConstraintLayout = view.findViewById(R.id.manualForm)

		amount = subLayout.findViewById(R.id.txnAmount)
		cardNumber = subLayout.findViewById(R.id.cardNumber)
		expDate = subLayout.findViewById(R.id.expDate)
		cvNum = subLayout.findViewById(R.id.cvvNum)
		postal = subLayout.findViewById(R.id.postal)
		submit = subLayout.findViewById(R.id.submitManualTxn)

		submit.setOnClickListener{
			startTransaction()
		}

		// TODO - create fragment for pulling in card information so they don't need to build one. Send amount for either
		// TODO - implement listeners on text change that have full validations on each field.
//		cardNumber.addTextChangedListener(object: TextWatcher{
//			override fun afterTextChanged(s: Editable?) {
//				TODO("not implemented")
//			}
//
//			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//				TODO("not implemented")
//			}
//
//			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//				TODO("not implemented")
//			}
//		})


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
		createLineItems()

		val transaction = Transaction(lineItems, PaymentDeviceManual())
		transaction.cardData.cardNum = if (cardNumber.text.isNotEmpty()) cardNumber.text.toString() else "0"
		transaction.cardData.expDate = if (expDate.text.isNotEmpty()) expDate.text.toString() else "0"
		transaction.cardData.cvNum = if (cvNum.text.isNotEmpty()) cvNum.text.toString() else "0"
		transaction.cardAddress.postal = if (postal.text.isNotEmpty()) postal.text.toString() else "0"

		PLog.i(TAG, "Starting Transaction $transaction", null, true)

		// TODO - show
		viewModel.payrocSdk.startManualTransaction(transaction) { success, error ->
			PLog.i(TAG, "Transaction was successful $success", null, true)
			PLog.i(TAG, "Errors $error", null, true)
		}
	}

}
