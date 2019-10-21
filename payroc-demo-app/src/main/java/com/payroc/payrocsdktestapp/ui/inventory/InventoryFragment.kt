package com.payroc.payrocsdktestapp.ui.inventory

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.payroc.payrocsdktestapp.R

class InventoryFragment : Fragment() {

    companion object {
        fun newInstance() = InventoryFragment()
    }

    private lateinit var viewModel: InventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.inventory_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(InventoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
