package com.payroc.payrocsdktestapp.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    val apiUsername: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val apiPassword: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val gatewayId: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val apiEnvironment: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val apiGateway: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
}
