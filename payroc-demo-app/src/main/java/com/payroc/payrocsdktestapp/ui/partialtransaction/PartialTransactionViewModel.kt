package com.payroc.payrocsdktestapp.ui.emvtransaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PartialTransactionViewModel : ViewModel() {
    val txnResult: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}
