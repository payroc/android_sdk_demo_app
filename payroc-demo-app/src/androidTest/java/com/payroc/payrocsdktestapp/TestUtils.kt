package com.payroc.payrocsdktestapp

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.View
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

fun waitComponent(viewInteraction: ViewInteraction): Boolean {
    var count = 1000
    while (count > 0) {
        try {
            viewInteraction.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            break
        } catch (ex: RuntimeException) {
            Thread.sleep(100)
            count--
        }
    }
    return count > 0
}

fun clearSharedPreferences(ctx: Context) {
    clearSDKSharedPreferences(ctx)
}

fun clearSDKSharedPreferences(ctx: Context) {
    val prefs = ctx.getSharedPreferences(
        ctx.getString(R.string.shared_prefs_key),
        MODE_PRIVATE
    )
    val editor = prefs.edit()
    editor.clear()
    editor.commit()
}


fun getCurrentActivity(): Activity? {
    var currentActivity: Activity? = null
    getInstrumentation().runOnMainSync {
        run {
            currentActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                Stage.RESUMED
            ).elementAtOrNull(0)
        }
    }
    return currentActivity
}

fun getElementFromMatchAtPosition(
    matcher: Matcher<View>,
    position: Int
): Matcher<View?>? {
    return object : BaseMatcher<View?>() {
        var counter = 0
        override fun matches(item: Any): Boolean {
            if (matcher.matches(item)) {
                if (counter == position) {
                    counter++
                    return true
                }
                counter++
            }
            return false
        }

        override fun describeTo(description: Description) {
            description.appendText("Element at hierarchy position $position")
        }
    }
}