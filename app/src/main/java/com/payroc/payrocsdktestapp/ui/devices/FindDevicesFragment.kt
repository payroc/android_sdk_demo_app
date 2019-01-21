package com.payroc.payrocsdktestapp.ui.devices

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.payroc.payrocsdktestapp.R
import com.payroc.sdk.enums.SupportedDevice
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import com.payroc.payrocsdktestapp.BuildConfig
import com.payroc.sdk.PLog

class FindDevicesFragment : Fragment() {

    companion object {
        fun newInstance() = FindDevicesFragment()
        const val TAG = "FindDevicesFragment"
    }

    private lateinit var viewModel: DevicesViewModel
    private lateinit var unpairedDeviceListView: ListView
    private val deviceArrayList = ArrayList<String>()

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context!!.registerReceiver(mReceiver, filter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.devices_fragment, container, false)
        unpairedDeviceListView = view.findViewById(R.id.paired_devices)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        viewModel.supportedDevices = arrayListOf(SupportedDevice.Simcent)

        if (viewModel.mBtAdapter.isDiscovering) {
            viewModel.mBtAdapter.cancelDiscovery()
        }
        viewModel.mBtAdapter.startDiscovery()

        getPairedDevices()
    }

    override fun onPause() {
        viewModel.mBtAdapter.cancelDiscovery()
        context!!.unregisterReceiver(mReceiver)
        super.onPause()
    }

    private fun getPairedDevices(){
        if (!viewModel.mBtAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }

        if (deviceArrayList.count() == 0){
            deviceArrayList.add(getString(R.string.no_bonded_devices_found))
        }

        unpairedDeviceListView.adapter = ArrayAdapter<String>(context!!, R.layout.device_name, deviceArrayList)
        unpairedDeviceListView.onItemClickListener =  AdapterView.OnItemClickListener { _, v, arg2, _ ->
            val info = (v as TextView).text.toString()
            val address = info.substringAfter("\n")
            val name = info.substringBefore("\n")

            when (name) {
                getString(R.string.no_bonded_devices_found) -> {
                    Toast.makeText(context!!, "Disable this button dynamically", Toast.LENGTH_LONG).show()
                }
                else -> {
                    viewModel.mBtAdapter.cancelDiscovery()
                    Toast.makeText(context!!, "Bluetooth Device Tapped $name", Toast.LENGTH_LONG).show()

                    val intent = Intent()
                    intent.putExtra("bt_device_name", name)
                    intent.putExtra("bt_device_address", address)
                    activity!!.setResult(Activity.RESULT_OK, intent)
                    activity!!.finish()
                }
            }
        }
    }

    // TODO - this receiver isn't triggered, i think because its registered to the activity.
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            PLog.i(TAG, "test 123", null, BuildConfig.DEBUG)
            if (BluetoothDevice.ACTION_FOUND == action) {
                val extraDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                PLog.i(TAG, extraDevice.name, null, BuildConfig.DEBUG)
                for(device in viewModel.supportedDevices){
                    for(prefix in device.defaultNamePrefixes){
                        if (extraDevice.name.contains(prefix, true) && !deviceArrayList.contains(extraDevice.name)){
                            deviceArrayList.add(extraDevice.name + "\n" + extraDevice.address)
                        }
                    }
                }

                PLog.i(TAG, "Device found: " + extraDevice.name + "; MAC " + extraDevice.address, null, BuildConfig.DEBUG)
            }
        }
    }
}