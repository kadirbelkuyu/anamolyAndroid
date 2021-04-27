package com.anamoly.view.forgot_password

import Dialogs.LoaderDialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import androidx.lifecycle.ViewModelProviders
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_forgot_password.*
import utils.ConnectivityReceiver
import utils.ContextWrapper
import utils.LanguagePrefs
import utils.TextInputFilter
import java.util.*
import kotlin.collections.HashMap

class ForgotPasswordActivity : CommonActivity() {

    private lateinit var forgotViewModel: ForgotViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        setContentView(R.layout.activity_forgot_password)
        setHeaderTitle(resources.getString(R.string.need_help))

        PushDownAnim.setPushDownAnimTo(btn_forgot_send)

        forgotViewModel = ViewModelProviders.of(this).get(ForgotViewModel::class.java)

        et_forgot_password_email.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.ALLOW_EMAIL_ONLY
            ), InputFilter.LengthFilter(50)
        )

        btn_forgot_send.setOnClickListener {

            et_forgot_password_email.error = null

            val email = et_forgot_password_email.text.toString()

            if (email.isEmpty()) {
                et_forgot_password_email.setError(
                    resources.getString(R.string.error_field_required),
                    CommonActivity.setErrorDrawable(this)
                )
            } else if (!CommonActivity.isEmailValid(email)) {
                et_forgot_password_email.setError(
                    resources.getString(R.string.error_invalid_email_address),
                    CommonActivity.setErrorDrawable(this)
                )
            } else {
                if (ConnectivityReceiver.isConnected) {
                    makeForgotPassword(email)
                } else {
                    ConnectivityReceiver.showSnackbar(this)
                }
            }

        }

    }

    private fun makeForgotPassword(user_email: String) {
        val params = HashMap<String, String>()
        params["user_email"] = user_email

        val loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        forgotViewModel.forgotPassword(params)
        forgotViewModel.forgotResponseLiveData.observe(
            this,
            androidx.lifecycle.Observer { response: CommonResponse? ->
                loaderDialog.dismiss()
                if (response != null) {
                    if (response.responce!!) {
                        CommonActivity.showToast(this@ForgotPasswordActivity, response.message!!)
                        finish()
                    } else {
                        CommonActivity.showToast(this@ForgotPasswordActivity, response.message!!)
                    }
                }
            })

    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
