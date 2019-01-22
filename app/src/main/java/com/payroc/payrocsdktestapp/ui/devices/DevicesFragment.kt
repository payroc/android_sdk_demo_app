package com.payroc.payrocsdktestapp.ui.devices

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.payroc.payrocsdktestapp.R
import com.payroc.sdk.enums.SupportedDevice
import kotlin.collections.ArrayList

class DevicesFragment : Fragment() {

    companion object {
        fun newInstance() = DevicesFragment()
    }

    private lateinit var viewModel: DevicesViewModel
    private lateinit var pairedDeviceListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.devices_fragment, container, false)
        pairedDeviceListView = view.findViewById(R.id.paired_devices)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        // TODO - pass in gateway supported devices list and use that here.
        viewModel.supportedDevices = arrayListOf(SupportedDevice.Simcent)
        getPairedDevices()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.mBtAdapter.cancelDiscovery()
    }

    private fun getPairedDevices(){
        // TODO - look into adding to device array in view model instead using hte new LiveData stuff
        val deviceArrayList = ArrayList<String>()

        // TODO - brute forced - find a way to make this a simpler / less expensive loop
        for (bt in viewModel.pairedDevices){
            for(device in viewModel.supportedDevices){
                for(prefix in device.defaultNamePrefixes){
                    if (bt.name.contains(prefix, true)){
                        deviceArrayList.add(bt.name + "\n" + bt.address)
                    }
                }
            }
        }

        if (deviceArrayList.count() == 0){
            deviceArrayList.add(getString(R.string.bt_no_bonded_devices_found))
        }

        // TODO - find a different way to insert a button or other data
        deviceArrayList.add(getString(R.string.bt_pair_new_device_action))
        // TODO - add unpaired to the list if they don't exist already.

        pairedDeviceListView.adapter = ArrayAdapter<String>(context!!, R.layout.device_name, deviceArrayList)
        pairedDeviceListView.onItemClickListener =  AdapterView.OnItemClickListener { _, v, arg2, _ ->
            viewModel.mBtAdapter.cancelDiscovery()

            // TODO - find a cleaner way to pair this data without string shoving in the array adapter
            val info = (v as TextView).text.toString()
            val address = info.substringAfter("\n")
            val name = info.substringBefore("\n")

            when (name) {
                getString(R.string.bt_pair_new_device_action) -> {
                    val ft = fragmentManager!!.beginTransaction()
                    ft.replace(R.id.container, FindDevicesFragment(), "FindDevicesFragmentTag")
                    ft.addToBackStack("find_devices")
                    ft.commit()
                }
                getString(R.string.bt_no_bonded_devices_found) -> {
                    // TODO - make sure this button is not clickable
                    Toast.makeText(context!!, "Disable this button dynamically", Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(context!!, "Bluetooth Device Tapped $name", Toast.LENGTH_LONG).show()

                    val sp = activity!!.getSharedPreferences(getString(R.string.shared_prefs_key), Context.MODE_PRIVATE)
                    sp.edit().putString(getString(R.string.shared_prefs_device_name_key), name).apply()
                    sp.edit().putString(getString(R.string.shared_prefs_device_address_key), address).apply()
                    sp.edit().putString(getString(R.string.shared_prefs_device_type_key), "Simcent").apply() // TODO - eventually I want to get this so later we can get the right stuff based on the device.

                    activity!!.setResult(Activity.RESULT_OK, Intent())
                    activity!!.finish()
                }
            }
        }
    }

}
