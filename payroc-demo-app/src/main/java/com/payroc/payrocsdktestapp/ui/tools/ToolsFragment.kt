package com.payroc.payrocsdktestapp.ui.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.analytics.FirebaseAnalytics
import com.payroc.android_core.PLog
import com.payroc.android_core.helpers.PrefHelper
import com.payroc.payrocsdktestapp.FirebaseAnalyticsEvents
import com.payroc.payrocsdktestapp.R
import com.payroc.sdk.PayrocSdk

class ToolsFragment : Fragment() {

    companion object {
        const val TAG = "ToolsFragment"
        fun newInstance() = ToolsFragment()
    }

    private lateinit var viewModel: ToolsViewModel
    private lateinit var enableBackgroundAnimation: Switch

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {

        val view = inflater.inflate(R.layout.tools_fragment, container, false)
        enableBackgroundAnimation = view.findViewById(R.id.background_anim_switch)
        enableBackgroundAnimation.isChecked = PrefHelper.getPrefs(this.requireContext()).getBoolean(getString(com.payroc.sdk.R.string.shared_prefs_background_animation_enabled), true)
        enableBackgroundAnimation.setOnCheckedChangeListener { buttonView, isChecked ->
            PrefHelper.getPrefs(this.requireContext()).edit().putBoolean(getString(com.payroc.sdk.R.string.shared_prefs_background_animation_enabled), isChecked).apply()
            PayrocSdk.merchantSettings.enable_background_animations = isChecked
        }

        val emailSupport = view.findViewById<AppCompatButton>(R.id.toolsEmailSupport)
        emailSupport.setOnClickListener {
            FirebaseAnalytics.getInstance(it.context).logEvent(FirebaseAnalyticsEvents.TAP_CONTACT_US, null)
            viewModel.payrocSdk.emailSupport(view.context, "Test 123" ){ success, error ->
                PLog.i(TAG, "Email result: $success \nMessage: $error")
            }
        }

        val callSupport = view.findViewById<AppCompatButton>(R.id.toolsCallSupport)
        callSupport.setOnClickListener {
            FirebaseAnalytics.getInstance(it.context).logEvent(FirebaseAnalyticsEvents.TAP_CONTACT_US, null)
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
