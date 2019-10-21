package com.payroc.payrocsdktestapp.ui.emvtransaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.payroc.sdk.PayrocSdk

class EmvTransactionViewModel : ViewModel() {
    val txnResult: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}
