package com.anamoly.view.login

import android.app.Application
import android.content.Context
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse
import org.json.JSONObject
import utils.SessionManagement


/**
 * Created on 11-02-2020.
 */
class LoginViewModel(application: Application) :
    AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val projectRepository: ProjectRepository = ProjectRepository()
    lateinit var loginResponseLiveData: LiveData<CommonResponse?>
    val attemptLoginLiveData = MutableLiveData<Boolean>()
    val dataStoreLiveData = MutableLiveData<Boolean>()

    init {

    }

    fun checkLogin(params: Map<String, String>) {
        loginResponseLiveData = projectRepository.checkLogin(params)
    }

    fun makeCartList(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.getCartList(params)
    }

    fun attemptLogin(et_login_email: EditText, et_login_password: EditText) {

        et_login_email.error = null
        et_login_password.error = null

        var focuseView: View? = null
        var cancel = false

        val email = et_login_email.text.toString()
        val password = et_login_password.text.toString()

        if (password.isEmpty()) {
            et_login_password.setError(
                context.resources.getString(R.string.error_field_required),
                CommonActivity.setErrorDrawable(context)
            )
            focuseView = et_login_password
            cancel = true
        } /*else if (!CommonActivity.isValidPassword(password)) {
            et_login_password.setError(
                context.resources.getString(R.string.error_password_required),
                CommonActivity.setErrorDrawable(context)
            )
            focuseView = et_login_password
            cancel = true
        }*/

        if (email.isEmpty()) {
            et_login_email.setError(
                context.resources.getString(R.string.error_field_required),
                CommonActivity.setErrorDrawable(context)
            )
            focuseView = et_login_email
            cancel = true
        } else if (!CommonActivity.isEmailValid(email)) {
            et_login_email.setError(
                context.resources.getString(R.string.error_invalid_email_address),
                CommonActivity.setErrorDrawable(context)
            )
            focuseView = et_login_email
            cancel = true
        }

        if (cancel) {
            focuseView?.requestFocus()
            attemptLoginLiveData.value = false
        } else {
            attemptLoginLiveData.value = true
        }

    }

    fun storeData(data: String) {
        val jsonObject = JSONObject(data)

        val user_id = jsonObject.getString("user_id")
        val user_type_id = jsonObject.getString("user_type_id")
        val user_email = jsonObject.getString("user_email")
        val user_firstname = jsonObject.getString("user_firstname")
        val user_lastname = jsonObject.getString("user_lastname")
        val user_phone = jsonObject.getString("user_phone")
        val user_company_name = jsonObject.getString("user_company_name")
        val user_company_id = jsonObject.getString("user_company_id")
        val android_token = jsonObject.getString("android_token")
        val ios_token = jsonObject.getString("ios_token")
        val is_email_verified = jsonObject.getString("is_email_verified")
        val is_mobile_verified = jsonObject.getString("is_mobile_verified")
        val verify_token = jsonObject.getString("verify_token")
        val status = jsonObject.getString("status")
        val settings = jsonObject.getString("settings")
        val family = jsonObject.getString("family")
        val addresses = jsonObject.getString("addresses")
        val user_image = jsonObject.getString("user_image")

        val sessionManagement = SessionManagement(context)
        sessionManagement.createLoginSession(
            user_id,
            user_type_id,
            user_email,
            user_firstname,
            user_phone,
            user_image
        )

        SessionManagement.UserData.setSession(
            context,
            "user_firstname",
            user_firstname
        )

        SessionManagement.UserData.setSession(
            context,
            "user_lastname",
            user_lastname
        )

        SessionManagement.UserData.setSession(
            context,
            "user_company_name",
            user_company_name
        )

        SessionManagement.UserData.setSession(
            context,
            "user_company_id",
            user_company_id
        )

        SessionManagement.UserData.setSession(
            context,
            "verify_token",
            verify_token
        )

        SessionManagement.UserData.setSession(
            context,
            "status",
            status
        )

        SessionManagement.UserData.setSession(
            context,
            "is_email_verified",
            is_email_verified
        )

        SessionManagement.UserData.setSession(
            context,
            "is_mobile_verified",
            is_mobile_verified
        )

        SessionManagement.UserData.setSession(
            context,
            "settings",
            settings
        )

        SessionManagement.UserData.setSession(
            context,
            "family",
            family
        )

        SessionManagement.UserData.setSession(
            context,
            "addresses",
            addresses
        )

        dataStoreLiveData.value = true

    }

}