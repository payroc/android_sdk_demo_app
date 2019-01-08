package com.payroc.payrocsdktestapp

import android.support.v7.app.AppCompatActivity
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
    }

}
