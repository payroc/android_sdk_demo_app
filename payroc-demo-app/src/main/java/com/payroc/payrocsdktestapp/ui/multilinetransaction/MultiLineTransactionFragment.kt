package com.payroc.payrocsdktestapp.ui.multilinetransaction

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.payroc.payrocsdktestapp.R

class MultiLineTransactionFragment : Fragment() {

    companion object {
        fun newInstance() = MultiLineTransactionFragment()
    }

    private lateinit var viewModel: MultiLineTransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.multi_line_transaction_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MultiLineTransactionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
