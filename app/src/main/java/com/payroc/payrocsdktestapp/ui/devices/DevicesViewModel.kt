package com.payroc.payrocsdktestapp.ui.devices

import android.arch.lifecycle.ViewModel
import android.bluetooth.BluetoothAdapter
import com.payroc.sdk.enums.SupportedDevice

class DevicesViewModel : ViewModel() {
    var supportedDevices: ArrayList<SupportedDevice> = arrayListOf()
    val mBtAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val pairedDevices = mBtAdapter.bondedDevices!!

}
