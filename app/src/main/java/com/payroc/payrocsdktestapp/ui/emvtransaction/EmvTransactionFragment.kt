package com.payroc.payrocsdktestapp.ui.emvtransaction

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.payroc.payrocsdktestapp.R

class EmvTransactionFragment : Fragment() {

    companion object {
        fun newInstance() = EmvTransactionFragment()
    }

    private lateinit var viewModel: EmvTransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.emv_transaction_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EmvTransactionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
