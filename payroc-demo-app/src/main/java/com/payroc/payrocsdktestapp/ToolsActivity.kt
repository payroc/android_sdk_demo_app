package com.payroc.payrocsdktestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.payroc.payrocsdktestapp.ui.tools.ToolsFragment

class ToolsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.title = "Tools"

        setContentView(R.layout.tools_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ToolsFragment.newInstance())
                .commitNow()
        }

        // TODO - Make tools
        // TODO - create tests of amount / card name / instructions from cert
        // See if i can add wait for events
        // TODO - amount values return different return values
        // Receipt generation - can we allow
//        files with the name of the test to help organize receipts for vantiv

        // TODO - sdk hooks for email
    }

}
