package com.payroc.payrocsdktestapp.ui.emvtransaction

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.payroc.sdk.PayrocSdk

class EmvTransactionViewModel : ViewModel() {
    val txnResult: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}
