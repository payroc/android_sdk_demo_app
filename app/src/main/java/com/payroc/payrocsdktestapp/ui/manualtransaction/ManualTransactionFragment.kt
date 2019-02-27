package com.payroc.payrocsdktestapp.ui.manualtransaction

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.payroc.payrocsdktestapp.BuildConfig
import com.payroc.payrocsdktestapp.R
import com.payroc.sdk.PLog
import com.payroc.sdk.enums.Environment
import com.payroc.sdk.enums.Gateways
import com.payroc.sdk.models.DefaultStyling
import com.payroc.sdk.models.LineItem
import com.payroc.sdk.models.Transaction
import com.payroc.sdk.models.pos.manual.PaymentDeviceManual
import com.payroc.sdk.models.validators.*
import java.math.BigDecimal

class ManualTransactionFragment : Fragment() {

	companion object {
		const val TAG = "ManualTransactionFragment"
		fun newInstance() = ManualTransactionFragment()
	}

	private var lineItems: ArrayList<LineItem> = arrayListOf()
	private var cancelTransaction: Boolean = false
	private var focusView: View? = null
	private lateinit var viewModel: ManualTransactionViewModel
	private lateinit var prefs: SharedPreferences
	private lateinit var amount: EditText
	private lateinit var cardNumber: EditText
	private lateinit var expDate: EditText
	private lateinit var cvNum: EditText
	private lateinit var postal: EditText
	private lateinit var submit: AppCompatButton
	private lateinit var txnResult: TextView


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view = inflater.inflate(R.layout.manual_transaction_fragment, container, false)
		val subLayout: ConstraintLayout = view.findViewById(R.id.manualForm)

		amount = subLayout.findViewById(R.id.txnAmount)
		cardNumber = subLayout.findViewById(R.id.cardNumber)
		expDate = subLayout.findViewById(R.id.expDate)
		cvNum = subLayout.findViewById(R.id.cvvNum)
		postal = subLayout.findViewById(R.id.postal)
		submit = subLayout.findViewById(R.id.submitManualTxn)
		txnResult = subLayout.findViewById(R.id.manualResultBody)

		// TODO - remove this once we are ready to go live.
		// Pre-fill test params - because I am lazy.
		if (BuildConfig.DEBUG){
			amount.setText(getString(R.string.testAmount))
			cardNumber.setText(getString(R.string.testCard))
			expDate.setText(getString(R.string.testExpDate)) // IBX wants no slash
			cvNum.setText(getString(R.string.testCvv))
			postal.setText(getString(R.string.testPostal))
		}

		// TODO - implement listeners on text change that have full validations on each field.
		submit.setOnClickListener{
			isFormContentValid()
		}

		return view
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		viewModel = ViewModelProviders.of(this).get(ManualTransactionViewModel::class.java)
		prefs = activity!!.getSharedPreferences(getString(R.string.shared_prefs_key), Context.MODE_PRIVATE)!!

		viewModel.txnResult.observe(this, Observer<String> { status ->
			txnResult.text = status
		})

		val gatewayPos = prefs.getInt(getString(R.string.shared_prefs_api_gateway_key), 0)
		val envPos = prefs.getInt(getString(R.string.shared_prefs_api_environment_key), 0)

		viewModel.payrocSdk.setGateway(
			prefs.getString(getString(R.string.shared_prefs_api_username_key), "")!!,
			prefs.getString(getString(R.string.shared_prefs_api_password_key), "")!!,
			Gateways.values()[gatewayPos],
			Environment.values()[envPos],
			DefaultStyling()
		)

		viewModel.payrocSdk.setPaymentDevice(PaymentDeviceManual())
	}

	private fun createLineItems(){
		val amount = if (amount.text.isNotEmpty()) amount.text.toString() else "0.00"
		val lineItem = LineItem(BigDecimal(amount), "Line Item 1", 1)
		lineItems.add(lineItem)
	}

	private fun isFormContentValid(){
		// Reset Validation
		amount.error = null
		cardNumber.error = null
		expDate.error = null
		cvNum.error = null
		postal.error = null
		focusView = null
		cancelTransaction = false

		val amountStr = amount.text.toString()
		val cardNumberStr = cardNumber.text.toString()
		val expDateStr = expDate.text.toString()
		val cvNumStr = cvNum.text.toString()
		val postalStr = postal.text.toString()

		if (!TxnAmount().isValid(amountStr)) setErrorOnInput(amount, getString(R.string.error_invalid_amount))
		if (!CardNumber().isValid(cardNumberStr)) setErrorOnInput(cardNumber, getString(R.string.error_invalid_card_number))
		if (!TxnExpDate().isValid(expDateStr)) setErrorOnInput(expDate, getString(R.string.error_invalid_exp_date))
		if (!TxnCvNum().isValid(cvNumStr)) setErrorOnInput(cvNum, getString(R.string.error_invalid_cv_num))
		if (!Postal().isValid(postalStr)) setErrorOnInput(postal, getString(R.string.error_invalid_postal))
		if (cancelTransaction) focusView?.requestFocus() else startTransaction()
	}

	private fun setErrorOnInput(editText: EditText, error:String){
		editText.error = error
		focusView = amount
		cancelTransaction = true
	}

	private fun startTransaction() {
		createLineItems()

		val transaction = Transaction()
		transaction.lineItems = lineItems
		transaction.cardData.cardNum = if (cardNumber.text.isNotEmpty()) cardNumber.text.toString() else "0"
		transaction.cardData.expDate = if (expDate.text.isNotEmpty()) expDate.text.toString() else "0"
		transaction.cardData.cvNum = if (cvNum.text.isNotEmpty()) cvNum.text.toString() else "0"
		transaction.cardAddress.postal = if (postal.text.isNotEmpty()) postal.text.toString() else "0"

		PLog.i(TAG, "Starting Transaction\n${transaction.toHashMap(Gateways.IBX)}", null, BuildConfig.DEBUG)

		// TODO - update this model to use the same on we use on EMV.
		viewModel.payrocSdk.startTransaction(context!!, transaction)
		// TODO  - Move this to its own controlled fragment.
	}

}
