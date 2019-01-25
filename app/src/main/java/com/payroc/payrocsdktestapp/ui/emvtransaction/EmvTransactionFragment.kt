package com.payroc.payrocsdktestapp.ui.emvtransaction

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.payroc.payrocsdktestapp.BuildConfig
import com.payroc.payrocsdktestapp.R
import com.payroc.payrocsdktestapp.ui.manualtransaction.ManualTransactionFragment
import com.payroc.sdk.PLog
import com.payroc.sdk.enums.Environment
import com.payroc.sdk.enums.Gateways
import com.payroc.sdk.models.DefaultStyling
import com.payroc.sdk.models.LineItem
import com.payroc.sdk.models.Transaction
import com.payroc.sdk.models.pos.manual.PaymentDeviceManual
import stringLiveData
import java.math.BigDecimal
import com.payroc.sdk.models.validators.TxnAmount


class EmvTransactionFragment : Fragment() {

    companion object {
        const val TAG = "EmvTransactionFragment"
        fun newInstance() = EmvTransactionFragment()
    }

    private var lineItems: ArrayList<LineItem> = arrayListOf()
    private var cancelTransaction: Boolean = false
    private var focusView: View? = null
    private lateinit var viewModel: EmvTransactionViewModel
    private lateinit var prefs: SharedPreferences
    private lateinit var deviceStatus: TextView
    private lateinit var amount: EditText
    private lateinit var submit: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.emv_transaction_fragment, container, false)
        deviceStatus = view.findViewById(R.id.deviceStatusTextView)
        amount = view.findViewById(R.id.txnAmountEmv)
        submit = view.findViewById(R.id.submitEmvTxn)
        submit.setOnClickListener{
            isFormContentValid()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EmvTransactionViewModel::class.java)

        prefs = activity!!.getSharedPreferences(getString(R.string.shared_prefs_key), Context.MODE_PRIVATE)!!
        val gatewayPos = prefs.getInt(getString(R.string.shared_prefs_api_gateway_key), 0)
        val envPos = prefs.getInt(getString(R.string.shared_prefs_api_environment_key), 0)

        viewModel.payrocSdk.setGateway(
            prefs.getString(getString(R.string.shared_prefs_api_username_key), "")!!,
            prefs.getString(getString(R.string.shared_prefs_api_password_key), "")!!,
            Gateways.values()[gatewayPos],
            Environment.values()[envPos],
            DefaultStyling()
        )

        viewModel.txnResult.observe(this, Observer<String> { status ->
            deviceStatus.text = status
        })

        prefs.stringLiveData(getString(R.string.shared_prefs_device_name_key), "").observe(this, Observer<String> { name ->
            val newString = "Device set: $name"
            viewModel.activeDeviceName = name!!
            deviceStatus.text = newString
        })

        prefs.stringLiveData(getString(R.string.shared_prefs_device_address_key), "").observe(this, Observer<String> { address ->
            viewModel.activeDeviceAddress = address!!
        })

        prefs.stringLiveData(getString(R.string.shared_prefs_device_type_key), "").observe(this, Observer<String> { type ->
            viewModel.activeDeviceType = type!!
        })
    }

    private fun createLineItems(){
        val amount = if (amount.text.isNotEmpty()) amount.text.toString() else getString(R.string.zero_dollars)
        val lineItem = LineItem(BigDecimal(amount), "Line Item 1", 1, "", Bundle.EMPTY)
        lineItems.add(lineItem)
    }

    private fun isFormContentValid(){
        amount.error = null
        val amountStr = amount.text.toString()

        if (!TxnAmount().isValid(amountStr)) setErrorOnInput(amount, getString(R.string.error_invalid_amount))

        if (cancelTransaction) {
            focusView?.requestFocus()
        } else {
            startTransaction()
        }
    }

    private fun setErrorOnInput(editText: EditText, error:String){
        editText.error = error
        focusView = amount
        cancelTransaction = true
    }

    private fun startTransaction(){
        createLineItems()

        val transaction = Transaction(lineItems, PaymentDeviceManual())
        transaction.taxPercent

        PLog.i(ManualTransactionFragment.TAG, "Starting Transaction\n${transaction.toHashMap(Gateways.IBX)}", null, BuildConfig.DEBUG)

        // TODO - consider passing a simple message and the response object as well.
        // TODO - figure out why this won't update the UI
        viewModel.payrocSdk.startTransaction(transaction) { success, msg ->
            PLog.i(ManualTransactionFragment.TAG, "Transaction status: $success \nPayload returned: $msg", null, BuildConfig.DEBUG)
            viewModel.txnResult.value = msg
        }
    }

}
