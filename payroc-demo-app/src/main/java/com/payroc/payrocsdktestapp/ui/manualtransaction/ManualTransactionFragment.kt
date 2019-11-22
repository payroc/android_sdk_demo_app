package com.payroc.payrocsdktestapp.ui.manualtransaction

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.AppCompatButton
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.payroc.payrocsdktestapp.BuildConfig
import com.payroc.payrocsdktestapp.R
import com.payroc.sdk.enums.ActivityResultTypes
import com.payroc.sdk.enums.CardType
import com.payroc.sdk.enums.SupportedDevice
import com.payroc.sdk.helpers.CardUtils
import com.payroc.sdk.models.LineItem
import com.payroc.sdk.models.Transaction
import com.payroc.sdk.models.validators.*
import com.payroc.sdk.ui.paymentprocessing.PaymentProcessingActivity
import java.math.BigDecimal
import kotlin.concurrent.thread

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
	private lateinit var cardTypeImage: ImageView
	private lateinit var amount: EditText
	private lateinit var cardNumber: EditText
	private lateinit var expDate: EditText
	private lateinit var cvNum: EditText
	private lateinit var postal: EditText
	private lateinit var submit: AppCompatButton
	private lateinit var txnResult: TextView
	private var cardType: CardType = CardType.Default


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view = inflater.inflate(R.layout.manual_transaction_fragment, container, false)
		val subLayout: ConstraintLayout = view.findViewById(R.id.manualForm)

		amount = subLayout.findViewById(R.id.txnAmount)
		cardTypeImage = subLayout.findViewById(R.id.cardTypeImage)
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

		cardNumber.addTextChangedListener(object: TextWatcher{
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

			override fun afterTextChanged(s: Editable?) {
				// Optimization - no need to check card type after first six digits.
				if (s!!.length >= 6) {
					thread(start = true) {

						cardType = CardUtils.getCardType(s.toString())
						activity!!.runOnUiThread {
							cardTypeImage.setImageResource(cardType.image)
						}
					}
				}
			}
		})

		submit.setOnClickListener{
			viewModel.txnResult.value = ""
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
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		when (requestCode) {
			ActivityResultTypes.CREATE_TXN.ordinal -> {
				when (resultCode) {
					Activity.RESULT_OK -> {
						val message = data?.getStringExtra("message")
						viewModel.txnResult.value = "Transaction successful $message"
					} // TODO - clear the form for next payment
					Activity.RESULT_CANCELED -> {
						val error = data?.getStringExtra("error")
						viewModel.txnResult.value = "Transaction cancelled $error"
					} // TODO - preserve form to retry?
					else -> {
						val error = activity?.intent?.extras?.getString(getString(R.string.extra_error))
						Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
					}
				}
			}
		}
	}

	private fun createLineItems(){
		lineItems.clear()
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
		transaction.cardData.cardName = cardType

        val intent = Intent(context, PaymentProcessingActivity::class.java)
        intent.putExtra(getString(R.string.extra_transaction), transaction)
        intent.putExtra(getString(R.string.extra_device), SupportedDevice.Manual.name)
        startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
	}

}
