package com.payroc.payrocsdktestapp.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.payroc.payrocsdktestapp.BuildConfig
import com.payroc.payrocsdktestapp.R
import com.payroc.core.PLog
import com.payroc.core.enums.Environment
import com.payroc.core.enums.Gateways
import stringLiveData

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
        const val TAG = "SettingsFragment"
    }

    private lateinit var viewModel: SettingsViewModel
    private lateinit var prefs: SharedPreferences
    private lateinit var apiUsername: EditText
    private lateinit var apiPassword: EditText
    private lateinit var gatewayId: EditText
    private lateinit var environmentSpinner: Spinner
    private lateinit var gatewaySpinner: Spinner
    private lateinit var updateButton: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)

        // TODO - validate the username and password at some point.
        apiUsername = view.findViewById(R.id.apiUsername)
        apiPassword = view.findViewById(R.id.apiPassword)
        gatewayId = view.findViewById(R.id.gatewayId)
        environmentSpinner = view.findViewById(R.id.settingsEnvironmentSpinner)
        environmentSpinner.adapter = ArrayAdapter<Environment>(context!!, R.layout.support_simple_spinner_dropdown_item, Environment.values())
        gatewaySpinner = view.findViewById(R.id.settingsGatewaySpinner)
        gatewaySpinner.adapter = ArrayAdapter<Gateways>(context!!, R.layout.support_simple_spinner_dropdown_item, Gateways.values())
        updateButton = view.findViewById(R.id.saveApiSettings)
        updateButton.setOnClickListener {
            updateCredentials()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        prefs = activity!!.getSharedPreferences(getString(R.string.shared_prefs_key), Context.MODE_PRIVATE)!!

        setupFieldObserver(getString(com.payroc.sdk.R.string.shared_prefs_api_username_key), viewModel.apiUsername, apiUsername)
        setupFieldObserver(getString(com.payroc.sdk.R.string.shared_prefs_api_password_key), viewModel.apiPassword, apiPassword)
        setupFieldObserver(getString(com.payroc.sdk.R.string.shared_prefs_gateway_id), viewModel.gatewayId, gatewayId)
        setupFieldObserver(getString(com.payroc.sdk.R.string.shared_prefs_api_environment_key), viewModel.apiEnvironment, environmentSpinner)
        setupFieldObserver(getString(com.payroc.sdk.R.string.shared_prefs_api_gateway_key), viewModel.apiGateway, gatewaySpinner)
    }

    private fun setupFieldObserver(prefKey: String, modelField: MutableLiveData<Int>, uiField: Spinner){
        modelField.value = prefs.getInt(prefKey, 0)
        uiField.setSelection(modelField.value!!)
    }

    private fun setupFieldObserver(prefKey: String, modelField: MutableLiveData<String>, uiField: EditText){
        prefs.stringLiveData(prefKey, "").observe(viewLifecycleOwner, Observer<String> { string ->
            modelField.value = string
        })
        modelField.value = prefs.getString(prefKey, "")
        uiField.setText(modelField.value)
    }

    private fun updateCredentials(){
        prefs.edit().putString(getString(com.payroc.sdk.R.string.shared_prefs_api_username_key), apiUsername.text.toString()).apply()
        prefs.edit().putString(getString(com.payroc.sdk.R.string.shared_prefs_api_password_key), apiPassword.text.toString()).apply()
        prefs.edit().putString(getString(com.payroc.sdk.R.string.shared_prefs_gateway_id), gatewayId.text.toString()).apply()
        prefs.edit().putInt(getString(com.payroc.sdk.R.string.shared_prefs_api_environment_key), environmentSpinner.selectedItemPosition).apply()
        prefs.edit().putInt(getString(com.payroc.sdk.R.string.shared_prefs_api_gateway_key), gatewaySpinner.selectedItemPosition).apply()

        val msg = "Settings updated"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        PLog.v(TAG, msg, null, BuildConfig.DEBUG)

        activity!!.setResult(Activity.RESULT_OK, Intent())
        activity!!.finish()
    }

}
