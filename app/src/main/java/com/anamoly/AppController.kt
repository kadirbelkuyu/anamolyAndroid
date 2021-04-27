package com.anamoly

import Config.BaseURL
import Database.NotificationData
import Models.NotificationModel
import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import com.droidnet.DroidNet
import com.anamoly.view.splash.SplashActivity
import com.onesignal.*
import org.json.JSONObject
import utils.LanguagePrefs
import utils.SessionManagement

class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        setTheme(R.style.AppTheme)

        DroidNet.init(this)

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(BuildConfig.ONESIGNAL_APP_ID)

        OneSignal.setNotificationOpenedHandler(ExampleNotificationOpenedHandler())
        OneSignal.setNotificationWillShowInForegroundHandler(NotificationWillShowInForegroundHandler())

        LanguagePrefs(this)
        BaseURL.HEADER_LANG = when {
            LanguagePrefs.getLang(this).equals("sv") -> "dutch"
            LanguagePrefs.getLang(this).equals("ar") -> "arabic"
            LanguagePrefs.getLang(this).equals("tr") -> "turkish"
            LanguagePrefs.getLang(this).equals("de") -> "german"
            else -> "english"
        }

        initTheme()

    }

    companion object {
        @get:Synchronized
        var instance: AppController? = null
            private set

        var headerColor: Int = 0
        var headerTextColor: Int = 0
        var buttonColor: Int = 0
        var buttonTextColor: Int = 0
        var secondButtonColor: Int = 0
        var secondButtonTextColor: Int = 0
        var decorativeTextOne: Int = 0
        var decorativeTextTwo: Int = 0
        var defaultTextColor: Int = 0
        var infoBoxBg: Int = 0
        var headerLogo: String = ""
        var loginTopImage: String = ""

        fun initTheme() {
            val headerColorSaved = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "header_color"
            )
            headerColor = if (headerColorSaved.isEmpty()) {
                ContextCompat.getColor(instance?.applicationContext!!, R.color.colorPrimary)
            } else {
                Color.parseColor(headerColorSaved)
            }

            val headerTextColorSaved = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "header_text_color"
            )
            headerTextColor = if (headerTextColorSaved.isEmpty()) {
                ContextCompat.getColor(instance?.applicationContext!!, R.color.colorWhite)
            } else {
                Color.parseColor(headerTextColorSaved)
            }

            val buttonColorSaved = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "button_color"
            )
            buttonColor = if (buttonColorSaved.isEmpty()) {
                ContextCompat.getColor(instance?.applicationContext!!, R.color.colorPrimary)
            } else {
                Color.parseColor(buttonColorSaved)
            }

            val buttonTextColorSaved = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "button_text_color"
            )
            buttonTextColor = if (buttonTextColorSaved.isEmpty()) {
                ContextCompat.getColor(instance?.applicationContext!!, R.color.colorWhite)
            } else {
                Color.parseColor(buttonTextColorSaved)
            }

            val secondButtonColorSaved = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "second_button_color"
            )
            secondButtonColor = if (secondButtonColorSaved.isEmpty()) {
                ContextCompat.getColor(instance?.applicationContext!!, R.color.colorOrange)
            } else {
                Color.parseColor(secondButtonColorSaved)
            }

            val secondButtonTextColorSaved = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "second_button_text_color"
            )
            secondButtonTextColor = if (secondButtonTextColorSaved.isEmpty()) {
                ContextCompat.getColor(instance?.applicationContext!!, R.color.colorWhite)
            } else {
                Color.parseColor(secondButtonTextColorSaved)
            }

            val decorativeTextOneSaved = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "decorative_text_one"
            )
            decorativeTextOne = if (decorativeTextOneSaved.isEmpty()) {
                ContextCompat.getColor(instance?.applicationContext!!, R.color.colorPrimary)
            } else {
                Color.parseColor(decorativeTextOneSaved)
            }

            val decorativeTextTwoSaved = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "decorative_text_two"
            )
            decorativeTextTwo = if (decorativeTextTwoSaved.isEmpty()) {
                ContextCompat.getColor(instance?.applicationContext!!, R.color.colorOrange)
            } else {
                Color.parseColor(decorativeTextTwoSaved)
            }

            val defaultTextColorSaved = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "default_text_color"
            )
            defaultTextColor = if (defaultTextColorSaved.isEmpty()) {
                ContextCompat.getColor(instance?.applicationContext!!, R.color.colorText)
            } else {
                Color.parseColor(defaultTextColorSaved)
            }

            val infoBoxBgSaved = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "info_box_bg"
            )
            infoBoxBg = if (infoBoxBgSaved.isEmpty()) {
                ContextCompat.getColor(instance?.applicationContext!!, R.color.colorPurpleLight)
            } else {
                Color.parseColor(infoBoxBgSaved)
            }

            headerLogo = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "header_logo"
            )
            loginTopImage = SessionManagement.PermanentData.getSession(
                instance?.applicationContext!!,
                "login_top_image"
            )
        }

    }

    @Synchronized
    fun getInstance(): AppController? {
        return instance
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (LanguagePrefs.getLang(this) != null) {
            LanguagePrefs(this)
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        DroidNet.getInstance().removeAllInternetConnectivityChangeListeners()
    }

    inner class NotificationWillShowInForegroundHandler :
        OneSignal.OSNotificationWillShowInForegroundHandler {
        override fun notificationWillShowInForeground(notificationReceivedEvent: OSNotificationReceivedEvent?) {
            notificationReceivedEvent?.complete(notificationReceivedEvent.notification)
        }
    }

    inner class ExampleNotificationOpenedHandler : OneSignal.OSNotificationOpenedHandler {
        override fun notificationOpened(result: OSNotificationOpenedResult) {
            val actionType = result.action.type
            val notificationID = result.notification.notificationId

            val data = result.notification.additionalData
            val launchUrl =
                result.notification.launchURL // update docs launchUrl
            var customKey: String? = null
            var openURL: String? = null
            val activityToLaunch: Any = SplashActivity::class.java
            if (data != null) {
                Log.i("OneSignalExample", "data::" + data.toString())
                customKey = data.optString("customkey", null)
                openURL = data.optString("openURL", null)
                if (customKey != null) Log.i(
                    "OneSignalExample",
                    "customkey set with value: $customKey"
                )
                if (openURL != null) Log.i(
                    "OneSignalExample",
                    "openURL to webview with URL value: $openURL"
                )
            }

            when {
                launchUrl != null -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(launchUrl))
                    browserIntent.flags =
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(browserIntent)
                }
                customKey != null -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(customKey))
                    browserIntent.flags =
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(browserIntent)
                }
                else -> {
                    val intent =
                        Intent(instance, activityToLaunch as Class<*>)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                    if (data != null) {
                        intent.putExtra("data", data.toString())
                    }
                    Log.i("OneSignalExample", "openURL =$openURL")
                    startActivity(intent)
                }
            }
        }
    }

}