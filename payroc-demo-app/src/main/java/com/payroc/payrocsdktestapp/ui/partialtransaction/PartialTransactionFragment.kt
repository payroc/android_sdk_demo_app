package com.payroc.payrocsdktestapp.ui.partialtransaction

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
import com.payroc.payrocsdktestapp.ui.emvtransaction.PartialTransactionViewModel
import com.payroc.sdk.enums.ActivityResultTypes
import com.payroc.sdk.models.validators.TxnAmount
import com.payroc.sdk.ui.multipart_payment.PartialPaymentActivity

class PartialTransactionFragment : Fragment() {

    companion object {
        const val TAG = "PartialTransactionFragment"
        fun newInstance() = PartialTransactionFragment()
    }

    private lateinit var viewModel: PartialTransactionViewModel // Optional
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
        viewModel = ViewModelProviders.of(this).get(PartialTransactionViewModel::class.java) // Optional
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ActivityResultTypes.CREATE_TXN.ordinal -> {
                when (resultCode) {
                    RESULT_OK -> Toast.makeText(context, "Transaction successful", Toast.LENGTH_LONG).show()
                    RESULT_CANCELED -> Toast.makeText(context, "Transaction cancelled ${data?.extras?.getString(getString(R.string.extra_error))}", Toast.LENGTH_LONG).show()
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
        if (cancelTransaction) focusView?.requestFocus() else startBatchTransaction()
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
    private fun startBatchTransaction(){
        val intent = Intent(context, PartialPaymentActivity::class.java)
        intent.putExtra(getString(R.string.extra_batch_amount), amount.text.toString())
        startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
    }

}
