package utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.preference.PreferenceManager

import java.util.Locale

import Config.BaseURL

/**
 * Created by Rajesh on 2018-03-29.
 */

class LanguagePrefs(private val context: Context) {

    init {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val language = sharedPreferences!!.getString(PREFS_LANGUAGE, DEFAULT_LANG)

        if (language != null) {
            initLanguage(language)
        } else {
            saveLanguage(DEFAULT_LANG)
            initLanguage(DEFAULT_LANG)
        }

    }

    fun saveLanguage(language: String) {
        var language = language
        try {
            val editor = sharedPreferences!!.edit()
            editor.putString(PREFS_LANGUAGE, language)

            editor.apply()
            language = if (language.equals("ar", ignoreCase = true)) "Arabic" else "English"
            //Toast.makeText(context, language + " is your preferred language.", Toast.LENGTH_SHORT).show();
        } catch (exc: Exception) {

        }

    }

    fun initLanguage(lang: String) {
        val res = context.resources
        val newConfig = Configuration(res.configuration)
        val locale = Locale(lang)
        newConfig.setLocale(locale)
        newConfig.setLayoutDirection(locale)
        val displayMetrics = context.resources.displayMetrics
        res.updateConfiguration(newConfig, displayMetrics)
    }

    companion object {

        private var sharedPreferences: SharedPreferences? = null

        private val PREFS_LANGUAGE = "languages"

        val COUNTRY_ID = "countryID"
        val COUNTRY_NAME = "countryName"
        val COUNTRY_CODE = "code"
        val CURRENCY_CODE = "currencyCode"
        val DAILING_CODE = "dailingCode"

        val DEFAULT_LANG = "sv"

        fun getLang(context: Context): String? {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences!!.getString(PREFS_LANGUAGE, DEFAULT_LANG)
        }

        fun isLangEnglish(context: Context): Boolean {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return (sharedPreferences!!.getString(PREFS_LANGUAGE, DEFAULT_LANG)!!.equals("en"))
        }

        fun getLangID(context: Context): Int {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            if (sharedPreferences!!.getString(PREFS_LANGUAGE, DEFAULT_LANG)!!.equals("en")) {
                return 1
            } else {
                return 2
            }
        }

        fun clearLanguage(context: Context) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply()
        }

        fun setSession(context: Context, key: String, value: String) {
            val session = context.getSharedPreferences(PREFS_LANGUAGE, 0)
            session.edit().putString(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Boolean) {
            val session = context.getSharedPreferences(PREFS_LANGUAGE, 0)
            session.edit().putBoolean(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Int) {
            val session = context.getSharedPreferences(PREFS_LANGUAGE, 0)
            session.edit().putInt(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Double) {
            val session = context.getSharedPreferences(PREFS_LANGUAGE, 0)
            session.edit().putLong(key, java.lang.Double.doubleToRawLongBits(value)).apply()
        }

        fun getSession(context: Context, key: String): String {
            val session = context.getSharedPreferences(PREFS_LANGUAGE, 0)
            return session.getString(key, "")!!
        }

        fun getSessionBoolean(context: Context, key: String): Boolean {
            val session = context.getSharedPreferences(PREFS_LANGUAGE, 0)
            return session.getBoolean(key, false)
        }

        fun getSessionInt(context: Context, key: String): Int {
            val session = context.getSharedPreferences(PREFS_LANGUAGE, 0)
            return session.getInt(key, 0)
        }

        fun getSessionDouble(context: Context, key: String): Double {
            val session = context.getSharedPreferences(PREFS_LANGUAGE, 0)
            return java.lang.Double.longBitsToDouble(session.getLong(key, 0))
        }

    }

}
