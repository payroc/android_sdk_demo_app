package com.payroc.payrocsdktestapp.ui.emvtransaction

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class PartialTransactionViewModel : ViewModel() {
    val txnResult: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}
