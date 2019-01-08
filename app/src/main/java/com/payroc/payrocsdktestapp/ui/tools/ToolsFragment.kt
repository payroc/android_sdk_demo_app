package com.payroc.payrocsdktestapp.ui.tools

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.payroc.payrocsdktestapp.R

class ToolsFragment : Fragment() {

    companion object {
        fun newInstance() = ToolsFragment()
    }

    private lateinit var viewModel: ToolsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.tools_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ToolsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
