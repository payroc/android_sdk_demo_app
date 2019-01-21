package com.payroc.payrocsdktestapp.ui.devices

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.payroc.payrocsdktestapp.R
import com.payroc.payrocsdktestapp.ui.devices.dummy.DummyContent
import com.payroc.payrocsdktestapp.ui.devices.dummy.DummyContent.DummyItem
import com.payroc.sdk.PLog

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DeviceListFragment.OnListFragmentInteractionListener] interface.
 */
class DeviceListFragment : Fragment() {

    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    private var mPairedDevicesArrayAdapter: ArrayAdapter<String>? = null
    private var mReceiver: BroadcastReceiver? = null
    private var mDeviceNamesList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        activity!!.setResult(Activity.RESULT_CANCELED)

        displayListOfFoundDevices()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_devicelist_list, container, false)

        // TODO - create an array adapter for the bluetooth results.
        // TODO - get the appropriate stuff here.
//        var arrayAdapter = PayrocSdk().findDevice {
//            PLog.i(TAG, "It looks like it worked $it")
//        }

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyDeviceListRecyclerViewAdapter(DummyContent.ITEMS, listener)
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
//        mBtAdapter.cancelDiscovery()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem?)
    }

//    private val mDeviceClickListener = AdapterView.OnItemClickListener { _, v, arg2, _ ->
//        mBtAdapter!!.cancelDiscovery()
//
//        val count = mPairedDevicesArrayAdapter!!.count
//        val info = (v as TextView).text.toString()
//        val address = info.substringAfter("\n")
//        val name = info.substringBefore("\n")
//
//        val intent = Intent()
//        intent.putExtra(extraDeviceName, name)
//        intent.putExtra(extraDeviceAddress, address)
//        intent.putExtra(extraDevicePendingConnection, true)
//        activity!!.setResult(Activity.RESULT_OK, intent)
//        activity!!.finish()
//    }

    private fun displayListOfFoundDevices() {

//        if (mBtAdapter!!.isDiscovering) {
//            mBtAdapter!!.cancelDiscovery()
//        }

////        val pairedDevices = mBtAdapter!!.bondedDevices
//        if (pairedDevices.size > 0) {
//            // TODO - perhaps filter out non - Bluetooth readers
//            for (device in pairedDevices) {
//                val deviceDisplayName = device.name + "\n" + device.address
//                mDeviceNamesList.add(deviceDisplayName)
//                mPairedDevicesArrayAdapter!!.add(deviceDisplayName)
//            }
//
//        }

//        mBtAdapter!!.startDiscovery()

        val pairedListView = activity!!.findViewById<RecyclerView>(R.id.paired_devices)
//        pairedListView.adapter = mPairedDevicesArrayAdapter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action

                if (BluetoothDevice.ACTION_FOUND == action) {

                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device.name != null) {
                        // TODO - add indicator next to devices that were already paired vs found
                        val newDeviceName = device.name + "\n" + device.address

                        if (!mDeviceNamesList.contains(newDeviceName)){
                            mDeviceNamesList.add(newDeviceName)
                            mPairedDevicesArrayAdapter!!.add(newDeviceName)
                            PLog.i(TAG, "Bluetooth device added name $newDeviceName")
                        } else {
                            PLog.i(TAG, "It looks like there was a duplicate bluetooth device")
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity!!.registerReceiver(mReceiver, filter)
    }

    companion object {
        const val TAG = "DeviceListFragment"
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            DeviceListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }

        const val extraDeviceName = "device_name"
        const val extraDeviceAddress = "device_address"
        const val extraDevicePendingConnection = "device_pending_connection"
    }
}
