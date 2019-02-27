package com.payroc.payrocsdktestapp.ui.emvtransaction

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.payroc.payrocsdktestapp.R
import com.payroc.sdk.enums.ActivityResultTypes
import com.payroc.sdk.enums.SupportedDevice
import com.payroc.sdk.models.LineItem
import com.payroc.sdk.models.Transaction
import com.payroc.sdk.models.validators.TxnAmount
import com.payroc.sdk.ui.PaymentProcessingActivity
import java.math.BigDecimal

class EmvTransactionFragment : Fragment() {

    companion object {
        const val TAG = "EmvTransactionFragment"
        fun newInstance() = EmvTransactionFragment()
    }

    private lateinit var viewModel: EmvTransactionViewModel // Optional
    private lateinit var amount: EditText
    private lateinit var submit: AppCompatButton
    private var cancelTransaction: Boolean = false
    private var focusView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.emv_transaction_fragment, container, false)
        amount = view.findViewById(R.id.txnAmountEmv)
        submit = view.findViewById(R.id.submitEmvTxn)
        submit.setOnClickListener{
            isFormContentValid()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EmvTransactionViewModel::class.java) // Optional
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ActivityResultTypes.CREATE_TRANSACTION.ordinal -> {
                when (resultCode) {
                    RESULT_OK -> Toast.makeText(context, "Transaction successful", Toast.LENGTH_LONG).show()
                    RESULT_CANCELED -> Toast.makeText(context, "Transaction cancelled", Toast.LENGTH_LONG).show()
                    else -> {
                        val error = activity?.intent?.extras?.getString(getString(R.string.extra_error))
                        Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun isFormContentValid(){
        cancelTransaction = false
        amount.error = null
        val amountStr = amount.text.toString()

        if (!TxnAmount().isValid(amountStr)) setErrorOnInput(amount, getString(R.string.error_invalid_amount))
        if (cancelTransaction) focusView?.requestFocus() else startTransaction()
    }

    private fun setErrorOnInput(editText: EditText, error:String){
        editText.error = error
        focusView = amount
        cancelTransaction = true
    }

    /**
     * The following function illustrates everything you need to create a transaction, the SDK will handle the rest.
     *
     * @since 1.0.0
     *
     */
    private fun startTransaction(){
        // Create line items
        val lineItem = LineItem()
        lineItem.amount = BigDecimal(amount.text.toString())
        lineItem.description = "Cool product"

        // Add line item(s) to array
        val lineItems: ArrayList<LineItem> = arrayListOf()
        lineItems.add(lineItem)

        // Create transaction object
        val transaction = Transaction()
        transaction.lineItems = lineItems

        // Send transaction object to sdk activity and await result
        val intent = Intent(context, PaymentProcessingActivity::class.java)
        intent.putExtra(getString(R.string.extra_transaction), transaction)
        intent.putExtra(getString(R.string.extra_device), SupportedDevice.Simcent.name)
        startActivityForResult(intent, ActivityResultTypes.CREATE_TRANSACTION.ordinal)
    }

}
