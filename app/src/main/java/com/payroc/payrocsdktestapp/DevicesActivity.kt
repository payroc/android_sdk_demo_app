package com.payroc.payrocsdktestapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.payroc.payrocsdktestapp.ui.devices.DeviceListFragment
import com.payroc.payrocsdktestapp.ui.devices.DevicesFragment
import com.payroc.payrocsdktestapp.ui.devices.dummy.DummyContent

class DevicesActivity : AppCompatActivity(), DeviceListFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        Toast.makeText(this, "Tapped item ${item!!.details}", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.title = "Select Device"

        setContentView(R.layout.devices_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DevicesFragment.newInstance())
                .commitNow()
        }
    }

}
