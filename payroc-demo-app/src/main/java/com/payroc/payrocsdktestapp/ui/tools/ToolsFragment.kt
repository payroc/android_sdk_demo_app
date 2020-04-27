package com.payroc.payrocsdktestapp.ui.tools

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.payroc.payrocsdktestapp.R
import com.payroc.core.PLog

class ToolsFragment : Fragment() {

    companion object {
        const val TAG = "ToolsFragment"
        fun newInstance() = ToolsFragment()
    }

    private lateinit var viewModel: ToolsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {

        val view = inflater.inflate(R.layout.tools_fragment, container, false)

        val emailSupport = view.findViewById<AppCompatButton>(R.id.toolsEmailSupport)
        emailSupport.setOnClickListener {
            viewModel.payrocSdk.emailSupport(view.context, "Test 123" ){ success, error ->
                PLog.i(TAG, "Email result: $success \nMessage: $error")
            }
        }

        val callSupport = view.findViewById<AppCompatButton>(R.id.toolsCallSupport)
        callSupport.setOnClickListener {
            viewModel.payrocSdk.callSupport(view.context) { success, error ->
                PLog.i(TAG, "Email result: $success \nMessage: $error")
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ToolsViewModel::class.java)
    }

}
