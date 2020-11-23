package com.payroc.payrocsdktestapp

import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*


class DemoAppTest {
    companion object {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val API_USERNAME = "payr9197"
        val API_PASSWORD = "Sandbox1"
        val GATEWAY_ID = "1131"
    }
    @get:Rule
    var splash = ActivityTestRule(SplashActivity::class.java, false, true)

    @Before
    fun setUp() {
        clearSharedPreferences(context)
    }

    @After
    fun tearDown() {
        clearSharedPreferences(context)
    }

    @Test
    fun paymentActivityTest() {
        //Intro
        Locale.setDefault(Locale.US)
        waitComponent(onView(withText("Payroc SDK Demo App")))
        openActionBarOverflowOrOptionsMenu(context)
        onView(withText(R.string.action_settings)).perform(click())
        waitComponent(onView(withText(R.string.action_settings)))
        onView(withId(R.id.apiUsername)).perform(clearText()).perform(typeText(API_USERNAME))
        onView(withId(R.id.apiPassword)).perform(clearText()).perform(typeText(API_PASSWORD))
        onView(withId(R.id.gatewayId)).perform(clearText()).perform(typeText(GATEWAY_ID))
        onView(withId(R.id.settingsGatewaySpinner)).perform(click())
        onView(withText("IBX")).perform(click())
        onView(withId(R.id.settingsEnvironmentSpinner)).perform(click())
        onView(withText("Stage")).perform(click())
        onView(withId(R.id.saveApiSettings)).perform(scrollTo(), click())

        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.numpadAmount)).check(matches(isDisplayed()))
        backToMainScreen()

        navigateTo(R.id.nav_devices)
        onView(withText("Select Device")).check(matches(isDisplayed()))
        backToMainScreen()

        navigateTo(R.id.nav_numpad)
        onView(withId(R.id.numpadAmount)).check(matches(isDisplayed()))
        backToMainScreen()

        navigateTo(R.id.nav_history)
        onView(withText("Transaction List")).check(matches(isDisplayed()))
        backToMainScreen()

        navigateTo(R.id.nav_partial)
        onView(withText("PARTIAL Transaction")).check(matches(isDisplayed()))
        backToMainScreen()

        navigateTo(R.id.nav_manual)
        onView(withId(R.id.numpadAmount)).check(matches(isDisplayed()))
        backToMainScreen()

        navigateTo(R.id.txn_review_test)
        onView(withText(context.getString(R.string.order_details).toUpperCase())).check(matches(isDisplayed()))
        backToMainScreen()

        navigateTo(R.id.signature_test)
        onView(withText(R.string.signature_sign_here)).check(matches(isDisplayed()))
        backToMainScreen()

        navigateTo(R.id.email_test)
        onView(withId(R.id.request_email_title)).check(matches(isDisplayed()))
        onView(isRoot()).perform(closeSoftKeyboard())
        backToMainScreen()

        navigateTo(R.id.tip_test)
        onView(withText("Buttons Tip Type")).check(matches(isDisplayed()))
        onView(withText("Wheel Tip Type")).check(matches(isDisplayed()))
        backToMainScreen()

        navigateTo(R.id.nav_settings_tools)
        onView(withText("Tools")).check(matches(isDisplayed()))
        backToMainScreen()


        navigateTo(R.id.nav_manual)
        onView(withId(R.id.numpadAmount)).check(matches(isDisplayed()))
        onView(withId(R.id.numpad1)).perform(click())
        onView(withId(R.id.numpad00)).perform(click())
        onView(withId(R.id.numpadAmount))
            .check(matches(isDisplayed()))
            .check(matches(withText("$1.00")))
        onView(withId(R.id.numpad_charge)).perform(click())
        assertTrue(waitComponent(onView(withId(R.id.line_item_submit))))
        onView(withId(R.id.line_item_submit)).perform(click())
        assertTrue(waitComponent(onView(withId(R.id.cardNumber))))
        onView(withId(R.id.cardNumber)).perform(typeText("4111111111111111"))
        onView(withId(R.id.expDate)).perform(typeText("01/25"))
        onView(withId(R.id.postal)).perform(typeText("99999"))
        onView(withId(R.id.cvvNum)).perform(typeText("999"))
        Espresso.closeSoftKeyboard()
        assertTrue(waitComponent(onView(withId(R.id.submitManualTxn))))
        onView(withId(R.id.submitManualTxn)).perform(click())
        assertTrue(waitComponent(onView(withId(R.id.signaturePad))))
        onView(withId(R.id.signTxnAmount)).check(matches(isDisplayed())).check(matches(withText("$1.00")))
        onView(withId(R.id.signaturePad)).perform(click())
        onView(withId(R.id.signAcceptButton)).perform(click())
        val sendReceipt = onView(allOf(withId(R.id.payment_status_action_button), withEffectiveVisibility(Visibility.VISIBLE)))
        assertTrue(waitComponent(sendReceipt))
        onView(allOf(withId(R.id.payment_status_cust_email), withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(isDisplayed()))
            .perform(typeText("test@gmail.com"))
        Espresso.closeSoftKeyboard()
        sendReceipt.check(matches(isDisplayed())).check(matches(withText(R.string.send_receipt)))
        sendReceipt.perform(click())
        val finish = onView(allOf(withId(R.id.payment_status_action_button), withEffectiveVisibility(Visibility.VISIBLE),
            withText(R.string.finish)
        ))
        assertTrue(waitComponent(finish))
        finish.perform(click())

        //History
        navigateTo(R.id.nav_history)
        waitComponent(onView(getElementFromMatchAtPosition(withText("$1.00"), 0)))
        onView(withId(R.id.recycler)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        waitComponent(onView(withId(R.id.receipt_merchant_logo)))
        onView(withText("(1x$1.00)")).check(matches(isDisplayed()))
        Espresso.pressBack()
        backToMainScreen()

        navigateTo(R.id.nav_nuke_data)
        openActionBarOverflowOrOptionsMenu(context)
        onView(withText(R.string.action_settings)).perform(click())
        onView(withId(R.id.apiUsername)).check(matches(withText("")))
/*
        onView(withId(R.id.introNextButton)).perform(click())
        waitComponent(onView(withText("ARE YOU")))
        onView(withId(R.id.introNextButton)).perform(click())
        waitComponent(onView(withText("LOREM IPS")))
        onView(withId(R.id.get_start)).perform(click())


        //Login
        val appPrefs = PrefHelper.appPrefs.edit()
        appPrefs.putInt(PrefHelper.ENVIRONMENT_POS, Environment.valueOf(BuildConfig.TEST_IBX_SERVER).ordinal)
        appPrefs.apply()
        waitComponent(onView(withId(R.id.login)))
        onView(withId(R.id.username)).perform(typeText(BuildConfig.TEST_IBX_LOGIN))
        onView(withId(R.id.password)).perform(typeText(BuildConfig.TEST_IBX_PASSWORD))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.login)).perform(click())

        //Enter gatewayId
        waitComponent(onView(withText(context.getString(R.string.please_enter_your_gateway_id).toUpperCase())))
        onView(withId(R.id.gateway_id)).perform(typeText(BuildConfig.TEST_IBX_RPNUM))
        onView(withId(R.id.gateway_id_save)).perform(click())

        //Pay
        val activity = getCurrentActivity()
        assert(activity != null)
        val i = activity!!.intent
        i.putExtra(context.getString(R.string.extra_device), SupportedDevice.Manual.name);
        waitComponent(onView(withId(R.id.numberpad)))
        onView(withId(R.id.navigation_pay)).perform(click())
        onView(withId(R.id.numpad1)).perform(click())
        onView(withId(R.id.numpad00)).perform(click())
        onView(withId(R.id.numpadAmount))
            .check(matches(isDisplayed()))
            .check(matches(withText("$1.00")))
        onView(withId(R.id.numpad_charge)).perform(click())
        assertTrue(waitComponent(onView(withId(R.id.line_item_submit))))
        onView(withId(R.id.line_item_submit)).perform(click())
        assertTrue(waitComponent(onView(withId(R.id.cardNumber))))
        onView(withId(R.id.cardNumber)).perform(typeText("4111111111111111"))
        onView(withId(R.id.expDate)).perform(typeText("01/25"))
        onView(withId(R.id.postal)).perform(typeText("99999"))
        onView(withId(R.id.cvvNum)).perform(typeText("999"))
        Espresso.closeSoftKeyboard()
        assertTrue(waitComponent(onView(withId(R.id.submitManualTxn))))
        onView(withId(R.id.submitManualTxn)).perform(click())
        assertTrue(waitComponent(onView(withId(R.id.signaturePad))))
        onView(withId(R.id.signTxnAmount)).check(matches(isDisplayed())).check(matches(withText("$1.00")))
        onView(withId(R.id.signaturePad)).perform(click())
        onView(withId(R.id.signAcceptButton)).perform(click())
        val sendReceipt = onView(allOf(withId(R.id.payment_status_action_button), withEffectiveVisibility(Visibility.VISIBLE)))
        assertTrue(waitComponent(sendReceipt))
        onView(allOf(withId(R.id.payment_status_cust_email), withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(isDisplayed()))
            .perform(typeText("test@gmail.com"))
        Espresso.closeSoftKeyboard()
        sendReceipt.check(matches(isDisplayed())).check(matches(withText(R.string.send_receipt)))
        sendReceipt.perform(click())
        val finish = onView(allOf(withId(R.id.payment_status_action_button), withEffectiveVisibility(Visibility.VISIBLE),
            withText(R.string.finish)
            ))
        assertTrue(waitComponent(finish))
        finish.perform(click())

        //History
        waitComponent(onView(withId(R.id.navigation_history)))
        onView(withId(R.id.navigation_history)).perform(click())
        waitComponent(onView(getElementFromMatchAtPosition(withText("$1.00"), 0)))
        onView(withId(R.id.recycler)).perform(actionOnItemAtPosition<HistoryItemViewHolder>(0, click()))
        waitComponent(onView(withId(R.id.receipt_merchant_logo)))
        onView(withText("(1x$1.00)")).check(matches(isDisplayed()))
        Espresso.pressBack()


        //Settings
        onView(withId(R.id.navigation_settings)).perform(click())
        waitComponent(onView(withId(R.id.merchant_logo)))
        onView(withId(R.id.merchant_name)).check(matches(withText(BuildConfig.TEST_IBX_LOGIN)))
        onView(withId(R.id.environment)).check(matches(withText(context.getString(R.string.settings_environment,
            context.resources.getStringArray(R.array.environment_select)[Environment.valueOf(BuildConfig.TEST_IBX_SERVER).ordinal]))))
        onView(withId(R.id.gateway_id)).check(matches(withText(context.getString(R.string.settings_gateway_id, BuildConfig.TEST_IBX_RPNUM))))
        onView(withId(R.id.settings_logout))
            .perform(scrollTo(), click())


        waitComponent(onView(withId(R.id.login)))
*/
    }

    private fun backToMainScreen() {
        onView(isRoot()).perform(ViewActions.pressBack())
        waitComponent(onView(withText("Payroc SDK Demo App")))
    }

    private fun navigateTo(navId :Int) {
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.LEFT)))
            .perform(DrawerActions.open())
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(navId))
    }
}