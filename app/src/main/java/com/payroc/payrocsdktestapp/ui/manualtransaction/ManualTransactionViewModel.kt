package com.payroc.payrocsdktestapp.ui.manualtransaction

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.payroc.sdk.PayrocSdk
import com.payroc.sdk.enums.Environment
import com.payroc.sdk.enums.Gateways

class ManualTransactionViewModel : ViewModel() {
    lateinit var paymentResult:  MutableLiveData<String>

    fun getPaymentResult(): LiveData<String>{
        if (!::paymentResult.isInitialized) {
            paymentResult = MutableLiveData()
        }

        return paymentResult
    }

    val txnResult: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}
