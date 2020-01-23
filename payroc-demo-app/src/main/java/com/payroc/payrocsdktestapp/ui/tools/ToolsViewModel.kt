package com.payroc.payrocsdktestapp.ui.tools

import androidx.lifecycle.ViewModel
import com.payroc.sdk.PayrocSdk

class ToolsViewModel : ViewModel() {
    val payrocSdk: PayrocSdk = PayrocSdk()
}
