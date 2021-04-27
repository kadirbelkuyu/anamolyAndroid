package utils

import Config.BaseURL
import Config.BaseURL.IS_LOGIN
import Config.BaseURL.KEY_EMAIL
import Config.BaseURL.KEY_ID
import Config.BaseURL.KEY_IMAGE
import Config.BaseURL.KEY_MOBILE
import Config.BaseURL.KEY_NAME
import Config.BaseURL.KEY_TYPE_ID
import android.content.Context
import android.content.Intent
import com.securesharedpreferences.SecureSharedPreferences
import com.anamoly.view.choose_language.ChooseLanguageActivity
import com.anamoly.view.home.MainActivity

open class SessionManagement(val context: Context) {

    val PRIVATE_MODE = 0

    val prefs = SecureSharedPreferences(context, BaseURL.PREFS_NAME, BaseURL.ENCRYPTED_PASSWORD)
    val editor = prefs.edit()

    fun createLoginSession(
        id: String, user_type_id: String, email: String, name: String, mobile: String,
        image: String
    ) {

        if (id.isNotEmpty()) {
            editor.putBoolean(IS_LOGIN, true)
        }

        editor.putString(KEY_ID, id)
        editor.putString(KEY_TYPE_ID, user_type_id)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_MOBILE, mobile)
        editor.putString(KEY_IMAGE, image)

        editor.commit()
    }

    fun logoutSession() {
        editor.clear()
        editor.commit()

        val logout = Intent(context, MainActivity::class.java)
        logout.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(logout)
    }

    fun logoutSessionLogin() {
        editor.clear()
        editor.commit()

        val logout = Intent(context, ChooseLanguageActivity::class.java)
        logout.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(logout)
    }

    fun clearSession() {
        editor.clear()
        editor.commit()
    }

    // Get Login State
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGIN, false)
        /*try {
            val decreptedData =
                AESCrypt.decrypt(BaseURL.ENCRYPTED_PASSWORD, prefs.getString(IS_LOGIN, ""))
            return if (decreptedData.isNullOrEmpty()) {
                false
            } else {
                decreptedData.toBoolean()
            }
        } catch (e: ClassCastException) {
            return false
        }*/
    }

    object UserData {

        fun setSession(context: Context, key: String, value: String) {
            val session =
                SecureSharedPreferences(context, BaseURL.PREFS_NAME, BaseURL.ENCRYPTED_PASSWORD)
            session.edit().putString(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Boolean) {
            val session =
                SecureSharedPreferences(context, BaseURL.PREFS_NAME, BaseURL.ENCRYPTED_PASSWORD)
            session.edit().putBoolean(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Int) {
            val session =
                SecureSharedPreferences(context, BaseURL.PREFS_NAME, BaseURL.ENCRYPTED_PASSWORD)
            session.edit().putInt(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Double) {
            val session =
                SecureSharedPreferences(context, BaseURL.PREFS_NAME, BaseURL.ENCRYPTED_PASSWORD)
            session.edit().putLong(key, java.lang.Double.doubleToRawLongBits(value)).apply()
        }

        fun getSession(context: Context, key: String): String {
            val session =
                SecureSharedPreferences(context, BaseURL.PREFS_NAME, BaseURL.ENCRYPTED_PASSWORD)
            return session.getString(key, "")!!
        }

        fun getSessionBoolean(context: Context, key: String): Boolean {
            val session =
                SecureSharedPreferences(context, BaseURL.PREFS_NAME, BaseURL.ENCRYPTED_PASSWORD)
            return session.getBoolean(key, false)
        }

        fun getSessionInt(context: Context, key: String): Int {
            val session =
                SecureSharedPreferences(context, BaseURL.PREFS_NAME, BaseURL.ENCRYPTED_PASSWORD)
            return session.getInt(key, 0)
        }

        fun getSessionDouble(context: Context, key: String): Double {
            val session =
                SecureSharedPreferences(context, BaseURL.PREFS_NAME, BaseURL.ENCRYPTED_PASSWORD)
            return java.lang.Double.longBitsToDouble(session.getLong(key, 0))
        }

        fun isLogin(context: Context): Boolean {
            val session =
                SecureSharedPreferences(context, BaseURL.PREFS_NAME, BaseURL.ENCRYPTED_PASSWORD)
            return session.getBoolean(BaseURL.IS_LOGIN, false)
        }

    }

    object CommonData {

        fun setSession(context: Context, key: String, value: String) {
            val session = context.getSharedPreferences(BaseURL.PREFS_NAME, 0)
            session.edit().putString(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Boolean) {
            val session = context.getSharedPreferences(BaseURL.PREFS_NAME, 0)
            session.edit().putBoolean(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Int) {
            val session = context.getSharedPreferences(BaseURL.PREFS_NAME, 0)
            session.edit().putInt(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Double) {
            val session = context.getSharedPreferences(BaseURL.PREFS_NAME, 0)
            session.edit().putLong(key, java.lang.Double.doubleToRawLongBits(value)).apply()
        }

        fun getSession(context: Context, key: String): String {
            val session = context.getSharedPreferences(BaseURL.PREFS_NAME, 0)
            return session.getString(key, "")!!
        }

        fun getSessionBoolean(context: Context, key: String): Boolean {
            val session = context.getSharedPreferences(BaseURL.PREFS_NAME, 0)
            return session.getBoolean(key, false)
        }

        fun getSessionInt(context: Context, key: String): Int {
            val session = context.getSharedPreferences(BaseURL.PREFS_NAME, 0)
            return session.getInt(key, 0)
        }

        fun getSessionDouble(context: Context, key: String): Double {
            val session = context.getSharedPreferences(BaseURL.PREFS_NAME, 0)
            return java.lang.Double.longBitsToDouble(session.getLong(key, 0))
        }
    }

    object PermanentData {

        fun setSession(context: Context, key: String, value: String) {
            val session = context.getSharedPreferences(BaseURL.PERMANENT_PREFS_NAME, 0)
            session.edit().putString(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Boolean) {
            val session = context.getSharedPreferences(BaseURL.PERMANENT_PREFS_NAME, 0)
            session.edit().putBoolean(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Int) {
            val session = context.getSharedPreferences(BaseURL.PERMANENT_PREFS_NAME, 0)
            session.edit().putInt(key, value).apply()
        }

        fun setSession(context: Context, key: String, value: Double) {
            val session = context.getSharedPreferences(BaseURL.PERMANENT_PREFS_NAME, 0)
            session.edit().putLong(key, java.lang.Double.doubleToRawLongBits(value)).apply()
        }

        fun getSession(context: Context, key: String): String {
            val session = context.getSharedPreferences(BaseURL.PERMANENT_PREFS_NAME, 0)
            return session.getString(key, "")!!
        }

        fun getSessionBoolean(context: Context, key: String): Boolean {
            val session = context.getSharedPreferences(BaseURL.PERMANENT_PREFS_NAME, 0)
            return session.getBoolean(key, false)
        }

        fun getSessionInt(context: Context, key: String): Int {
            val session = context.getSharedPreferences(BaseURL.PERMANENT_PREFS_NAME, 0)
            return session.getInt(key, 0)
        }

        fun getSessionDouble(context: Context, key: String): Double {
            val session = context.getSharedPreferences(BaseURL.PERMANENT_PREFS_NAME, 0)
            return java.lang.Double.longBitsToDouble(session.getLong(key, 0))
        }
    }

}