package com.payroc.payrocsdktestapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.payroc.android_core.PLog
import com.payroc.android_core.enums.*
import com.payroc.android_core.helpers.PrefHelper
import com.payroc.android_core.interfaces.GatewayCallback
import com.payroc.android_core.models.LineItem
import com.payroc.android_core.models.Transaction
import com.payroc.android_core.models.ibx.response.PaymentCardResponse
import com.payroc.payrocsdktestapp.ui.permissions.PermissionsActivity
import com.payroc.payrocsdktestapp.ui.statusview.StatusViewActivity
import com.payroc.sdk.PayrocSdk
import com.payroc.sdk.models.PayrocSdkTxnResponse
import com.payroc.sdk.models.TransactionBatch.CREATOR.isExistUnCompleteBatch
import com.payroc.sdk.ui.email.RequestEmailActivity
import com.payroc.sdk.ui.multipart_payment.PartialPaymentActivity
import com.payroc.sdk.ui.numberpad.NumberPadActivity
import com.payroc.sdk.ui.review.TxnReviewActivity
import com.payroc.sdk.ui.signature.SignatureActivity
import com.payroc.sdk.ui.tip.TipActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.math.BigDecimal


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        const val TAG = "DemoMainActivity"
    }

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var mainView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        setContentView(R.layout.activity_main)
        mainView = findViewById(R.id.main_content)
        PayrocSdk.layoutCheck(this)
        PayrocSdk.merchantSettings.enable_background_animations =
            PrefHelper.getPrefs(this).getBoolean(getString(com.payroc.sdk.R.string.shared_prefs_background_animation_enabled), true)

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, NumberPadFragment.newInstance())
//                .commitNow()
//        }

        setSupportActionBar(toolbar)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        fab.setOnClickListener { view ->
//            val intent = Intent(this, TransactionActivity::class.java)
//            intent.putExtra("mode", TxnModes.MANUAL.name)
//            startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)

            val intent = Intent(this, NumberPadActivity::class.java)
            intent.putExtra(getString(R.string.extra_tax_enabled), false)
            intent.putExtra(getString(R.string.extra_tax_percent), "0")
            startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        if (isExistUnCompleteBatch(this)) {
            val intent = Intent(this, PartialPaymentActivity::class.java)
            startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
        }
        nav_view.setNavigationItemSelectedListener(this)
        val editor = PrefHelper.getPrefs(this).edit()
        editor.putBoolean(PrefHelper.IS_WAIT_TO_REMOVE_CARD, true)
        editor.putBoolean(PrefHelper.IS_SURCHARGE_ENABLED, false)
        editor.commit()
        PayrocSdk.merchantSettings.is_wait_to_remove_card = PrefHelper.getPrefs(this).getBoolean(PrefHelper.IS_WAIT_TO_REMOVE_CARD, true)
        PayrocSdk.merchantSettings.getSurchargeInfo(this)
        PayrocSdk.permissions.updateFromPrefs(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_devices -> {
                PayrocSdk().findDevice(this)
            }
            R.id.nav_numpad -> {
                val intent = Intent(this, NumberPadActivity::class.java)
                intent.putExtra(getString(R.string.extra_tax_enabled), true)
                intent.putExtra(getString(R.string.extra_tip_enabled), true)
                intent.putExtra(getString(R.string.extra_tax_percent), "6.5")
                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
            }
            R.id.nav_partial -> {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("mode", TxnModes.PARTIAL.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
            }
            R.id.nav_emv -> {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("mode", TxnModes.EMV.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
            }
            R.id.nav_capture_all -> {
                val payrocSdk = PayrocSdk()
                payrocSdk.setGatewayFromPreferences(this)
                val callbacks = object : GatewayCallback {
                    override fun gatewayCallbackTxnApproved(response: PaymentCardResponse) {
                        Snackbar.make(mainView, R.string.approved, Snackbar.LENGTH_LONG).show()
                    }

                    override fun gatewayCallbackTxnDeclined(response: PaymentCardResponse) {
                        Snackbar.make(mainView, R.string.declined, Snackbar.LENGTH_LONG).show()
                    }

                    override fun gatewayCallbackTxnTimeout(response: PaymentCardResponse) {
                        Snackbar.make(mainView, R.string.time_out, Snackbar.LENGTH_LONG).show()
                    }

                    override fun gatewayCallbackUnauthorized() {
                        Snackbar.make(mainView, R.string.please_wait, Snackbar.LENGTH_INDEFINITE).show()
                    }
                }
                Snackbar.make(mainView, R.string.please_wait, Snackbar.LENGTH_INDEFINITE).show()
                payrocSdk.activeGateway.closeBatch(callbacks, payrocSdk.activeAuth)
            }
            R.id.nav_manual -> {
                val intent = Intent(this, NumberPadActivity::class.java)
                intent.putExtra(getString(R.string.extra_device), SupportedDevice.Manual.name)
                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
            }
//            R.id.nav_ingest -> {
//                val intent = Intent(this, TransactionActivity::class.java)
//                intent.putExtra("mode", TxnModes.INGEST.name)
//                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
//            }
//            R.id.nav_multi_line -> {
//                val intent = Intent(this, TransactionActivity::class.java)
//                intent.putExtra("mode", TxnModes.MULTI_LINE.name)
//                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
//            }
//            R.id.nav_inventory -> {
//                val intent = Intent(this, TransactionActivity::class.java)
//                intent.putExtra("mode", TxnModes.INVENTORY.name)
//                startActivityForResult(intent, ActivityResultTypes.CREATE_TXN.ordinal)
//            }
            R.id.nav_history -> {
                val intent = Intent(this, com.payroc.sdk.ui.history.HistoryActivity::class.java)
                startActivityForResult(intent, ActivityResultTypes.GET_HISTORY.ordinal)
            }
            R.id.nav_permissions -> {
                val intent = Intent(this, PermissionsActivity::class.java)
                startActivity(intent)
            }
            R.id.signature_test -> {
                val lineItems = LineItem(BigDecimal.TEN)
                val transaction = Transaction(arrayListOf(lineItems))
                val intent = Intent(this, SignatureActivity::class.java)
                intent.putExtra(getString(R.string.extra_transaction), transaction)
                startActivityForResult(intent, ActivityResultTypes.GET_SIGNATURE.ordinal)
            }
            R.id.txn_review_test -> {
                val transaction = Transaction(arrayListOf())
                transaction.lineItems.add(LineItem(BigDecimal.TEN, "Meat"))
                transaction.lineItems.add(LineItem(BigDecimal.TEN, "Fish"))
                transaction.lineItems.add(LineItem(BigDecimal(3)))
                transaction.lineItems.add(LineItem(BigDecimal(23)))
                transaction.taxPercent = BigDecimal(5)
                val intent = Intent(this, TxnReviewActivity::class.java)
                intent.putExtra(getString(R.string.extra_transaction), transaction)
                intent.putExtra(getString(R.string.extra_tax_enabled), true)
                intent.putExtra(getString(R.string.extra_tip_enabled), true)
                startActivityForResult(intent, ActivityResultTypes.TXN_REVIEW.ordinal)
            }
            R.id.email_test -> {
                val intent = Intent(this, RequestEmailActivity::class.java)
                startActivityForResult(intent, ActivityResultTypes.GET_EMAIL.ordinal)
            }
            R.id.tip_test -> {
                val lineItems = LineItem(BigDecimal.TEN)
                val transaction = Transaction(arrayListOf(lineItems))
                val intent = Intent(this, TipActivity::class.java)
                intent.putExtra(getString(R.string.extra_transaction), transaction)
                startActivityForResult(intent, ActivityResultTypes.GET_TIP.ordinal)
            }
            R.id.nav_status_view_test -> {
                val intent = Intent(this, StatusViewActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings_tools -> {
                val intent = Intent(this, ToolsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_nuke_data -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    PayrocSdk().nukeAllData(this)
                } else {
                    PLog.d("Main Activity", "SDK version of your device does not support this feature", null, BuildConfig.DEBUG)
                }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val payrocSdkTxnResponse = data?.getParcelableExtra<PayrocSdkTxnResponse>(getString(R.string.extras_sdk_response))

        when (requestCode) {
            // If you want to handle any additional responses you are able to for different ActivityResultTypes.
            ActivityResultTypes.CREATE_TXN.ordinal -> {
                when (payrocSdkTxnResponse?.code){
                    SdkResponseCode.APPROVED -> Log.d(TAG, "Payroc SDK Response:\n ${payrocSdkTxnResponse.code.description}!!")
                    SdkResponseCode.APPROVED_PARTIAL -> {
                        val remainingAmount = BigDecimal(payrocSdkTxnResponse.totalAmount) - BigDecimal(payrocSdkTxnResponse.partialAuthorizedAmount)
                        if (remainingAmount > BigDecimal.ZERO){
                            // Start new payment with remaining amount.
                        }
                    }
                    SdkResponseCode.DECLINED -> Log.d(TAG, "Payroc SDK Response:\n ${payrocSdkTxnResponse.code.description}!!")
                    SdkResponseCode.UNKNOWN -> Log.d(TAG, "Payroc SDK Response:\n ${payrocSdkTxnResponse.code.description}!!")
                }
            }
            ActivityResultTypes.FIND_DEVICE.ordinal -> { } // Just as example
            else -> Log.d(TAG, "Payroc SDK Response:\n $payrocSdkTxnResponse!!")
        }
    }
}
