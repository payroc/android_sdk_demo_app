# Payroc Android SDK Demo App
Application demonstrating / testing the mobile SDK functions. 

This application is intended to give a baseline implementation for each of the individual functions that the [Payroc Mobile SDK](https://github.com/payroc/android_pos_sdk) offers. This is in an effort to offer a semi-integrated solution certified with TSYS or Vantiv respectively. 

## Dependencies
- Android Studio (target sdk 28, min 19)
- Kotliln
- DOKKA (used on SDK for documentation - see if we can remove)
- JUnit4
- TBD

## Getting started
Once you have the repo cloned, then you should be able to run the app right out of the box! (once our library is in Maven or another gradle ready public repo) 

We utilize the strengths of Fragments in Android to cleanly organize each typical use case for the SDK. This should give you a solid feel for how this can be applied to your App, or in some cases, 
you should be able to copy fragments, and views to your app and be able to use them out of the box with little added effort. They are intentionally left "bland" to aid in ease-of-styling if you do decide to use our template.

In order for your for your app to run correctly with vector images you will need to add the following to your android build config section for your app's build.gradle file. 
```groovy
android{
//...
    defaultConfig{
    //...
        vectorDrawables.useSupportLibrary = true
    //...
    }
//...
}
```

## Local Development
Make sure you download [Payroc Mobile SDK](https://github.com/payroc/android_pos_sdk) locally and change your gradle.properties file to look like the following:
```groovy
include ':app'
include ':payroc-sdk'
project(':payroc-sdk').projectDir = new File('{{YOUR_DRIVE_OR_FOLDER_HERE}}/GitHub/android_pos_sdk/payroc-sdk')
```

## Running activities as Dialog 
Sometimes for styling we want to run activities ad dialogs instead of full on activities. To do so add the following attributes to the activity of your choice on your android manifest. 
```xml
<activity android:name="com.payroc.sdk.ui.xyz"
                  android:label="Your page name"
                  android:theme="@style/Theme.AppCompat.Light.Dialog"
                  android:excludeFromRecents="true"/>
```

