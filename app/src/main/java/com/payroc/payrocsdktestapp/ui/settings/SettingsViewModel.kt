package com.payroc.payrocsdktestapp.ui.settings

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    val apiUsername: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val apiPassword: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val apiEnvironment: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val apiGateway: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
}
