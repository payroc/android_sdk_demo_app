package com.payroc.payrocsdktestapp.ui.tools

import android.arch.lifecycle.ViewModel
import com.payroc.sdk.PayrocSdk

class ToolsViewModel : ViewModel() {
    val payrocSdk: PayrocSdk = PayrocSdk()
}
