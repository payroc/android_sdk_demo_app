package com.payroc.payrocsdktestapp.ui.manualtransaction

import android.arch.lifecycle.ViewModel
import com.payroc.sdk.PayrocSdk

class ManualTransactionViewModel : ViewModel() {
    var payrocSdk: PayrocSdk = PayrocSdk() // TODO - see if we can init this at a higher level
    // TODO - make this view model a shared one

    // Example usage of view models.
//    private lateinit var users: MutableLiveData<List<User>>
//
//    fun getUsers(): LiveData<List<User>> {
//        if (!::users.isInitialized) {
//            users = MutableLiveData()
//            loadUsers()
//        }
//        return users
//    }
//
//    private fun loadUsers() {
//        // Do an asynchronous operation to fetch users.
//    }
}
