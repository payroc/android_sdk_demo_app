package com.payroc.payrocsdktestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.payroc.payrocsdktestapp.ui.inventory.InventoryFragment

class InventoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inventory_transaction_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, InventoryFragment.newInstance())
                .commitNow()
        }
    }

}
