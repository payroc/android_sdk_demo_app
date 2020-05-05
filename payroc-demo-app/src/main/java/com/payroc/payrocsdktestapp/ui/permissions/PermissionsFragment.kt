package com.payroc.payrocsdktestapp.ui.permissions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.payroc.payrocsdktestapp.R
import com.payroc.sdk.PayrocSdk.Companion.permissions

class PermissionsFragment: Fragment(),
    CompoundButton.OnCheckedChangeListener {

    companion object {
        const val TAG = "PermissionsFragment"
        fun newInstance() = PermissionsFragment()
    }

    private lateinit var viewModel: PermissionsViewModel
    private lateinit var permissionsLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {

        val view = inflater.inflate(R.layout.permissions_fragment, container, false)
        permissionsLayout = view.findViewById(R.id.permissions_layout)
        fillPermissions()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PermissionsViewModel::class.java)
    }

    private fun fillPermissions() {
        permissionsLayout.let {
            it.removeAllViews()
            addPermissionItem(it, R.string.enable_transaction, permissions.isRunningTransaction)
            addPermissionItem(it, R.string.enable_history, permissions.isViewingTransactionHistory)
            addPermissionItem(it, R.string.enable_resending_receipts, permissions.isResendingReceipts)
            addPermissionItem(it, R.string.enable_refund, permissions.isRefundingTransactions)
            addPermissionItem(it, R.string.enable_void, permissions.isVoidingTransactions)
        }
    }

    private fun addPermissionItem(it: LinearLayout, textId: Int, value: Boolean) {
        val view = layoutInflater.inflate(R.layout.permissions_checkbox_item, null, false)
        view.findViewById<TextView>(R.id.settings_label).setText(textId)
        val switchCompat = view.findViewById<SwitchCompat>(R.id.settings_switch)
        switchCompat.id = textId
        switchCompat.isChecked = value
        switchCompat.setOnCheckedChangeListener(this)
        it.addView(view)
    }

    override fun onCheckedChanged(view: CompoundButton?, isChecked: Boolean) {
        view?.let {
            when (it.id) {
                R.string.enable_transaction -> permissions.isRunningTransaction = isChecked
                R.string.enable_history -> permissions.isViewingTransactionHistory = isChecked
                R.string.enable_resending_receipts -> permissions.isResendingReceipts = isChecked
                R.string.enable_refund -> permissions.isRefundingTransactions = isChecked
                R.string.enable_void -> permissions.isVoidingTransactions = isChecked
            }
            permissions.saveToPrefs(it.context)
        }
    }
}
