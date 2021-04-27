package com.anamoly.view.otp

import Config.BaseURL
import Dialogs.LoaderDialog
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anamoly.AppController
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.register.NoDeliverActivity
import com.anamoly.view.user_account.AppInstructionActivity
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_otp.*
import org.json.JSONObject
import utils.CommonAsyTask
import utils.ConnectivityReceiver
import utils.NameValuePair
import utils.SessionManagement

class OtpActivity : CommonActivity() {

    var user_id: String = ""

    lateinit var otpViewModel: OtpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        setHeaderTitle("Otp")

        otpViewModel = ViewModelProviders.of(this).get(OtpViewModel::class.java)

        PushDownAnim.setPushDownAnimTo(btn_otp_verify)

        if (intent.hasExtra("user_id")) {
            user_id = intent.getStringExtra("user_id")!!
        }
        val email = intent.getStringExtra("user_email")!!

        tv_otp_number_or_email.text = email

        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = CommonActivity.dpToPx(this, 5F).toFloat()
        gradientDrawable.setColor(AppController.infoBoxBg)

        val gradientDrawableNormal = GradientDrawable()
        gradientDrawableNormal.cornerRadius = CommonActivity.dpToPx(this, 5F).toFloat()
        gradientDrawable.setColor(AppController.buttonColor)

        val res = StateListDrawable()
        res.addState(intArrayOf(android.R.attr.state_selected), gradientDrawable)
        res.addState(intArrayOf(android.R.attr.state_enabled), gradientDrawable)
        res.addState(intArrayOf(), gradientDrawableNormal)

        btn_otp_verify.isEnabled = false

        otp_view.setOtpCompletionListener { otp: String? ->
            btn_otp_verify.isEnabled = otp!!.length >= 6
        }

        otp_view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn_otp_verify.isEnabled = otp_view.text.toString().length >= 6
            }
        })

        btn_otp_verify.setOnClickListener {
            val otp = otp_view.text.toString()
            if (ConnectivityReceiver.isConnected) {
                if (user_id.isEmpty()) {
                    makeVerifyOtp(email, otp)
                } else {
                    makeVeifyEmail(email, otp)
                }
            } else {
                ConnectivityReceiver.showSnackbar(this)
            }
        }

        btn_otp_resend.setOnClickListener {
            if (ConnectivityReceiver.isConnected) {
                makeResendOtp(email)
            } else {
                ConnectivityReceiver.showSnackbar(this)
            }
        }

    }

    private fun makeVerifyOtp(user_email: String, otp: String) {
        val params = ArrayList<NameValuePair>()
        params.add(NameValuePair("user_email", user_email))
        params.add(NameValuePair("otp", otp))

        val task = CommonAsyTask(
            BaseURL.POST, params,
            BaseURL.VERIFY_EMAIL_URL, object : CommonAsyTask.VJsonResponce {
                override fun VResponce(responce: String, message: String) {

                    val jsonObjectMain = JSONObject(responce)

                    val jsonObject = jsonObjectMain.getJSONObject("data")

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

                    val sessionManagement = SessionManagement(this@OtpActivity)
                    sessionManagement.createLoginSession(
                        user_id,
                        user_type_id,
                        user_email,
                        user_firstname,
                        user_phone,
                        ""
                    )

                    SessionManagement.UserData.setSession(
                        this@OtpActivity,
                        "user_firstname",
                        user_firstname
                    )

                    SessionManagement.UserData.setSession(
                        this@OtpActivity,
                        "user_lastname",
                        user_lastname
                    )

                    SessionManagement.UserData.setSession(
                        this@OtpActivity,
                        "user_company_name",
                        user_company_name
                    )

                    SessionManagement.UserData.setSession(
                        this@OtpActivity,
                        "user_company_id",
                        user_company_id
                    )

                    SessionManagement.UserData.setSession(
                        this@OtpActivity,
                        "verify_token",
                        verify_token
                    )

                    SessionManagement.UserData.setSession(
                        this@OtpActivity,
                        "status",
                        status
                    )

                    SessionManagement.UserData.setSession(
                        this@OtpActivity,
                        "is_email_verified",
                        is_email_verified
                    )

                    SessionManagement.UserData.setSession(
                        this@OtpActivity,
                        "is_mobile_verified",
                        is_mobile_verified
                    )

                    SessionManagement.UserData.setSession(
                        this@OtpActivity,
                        "settings",
                        settings
                    )

                    SessionManagement.UserData.setSession(
                        this@OtpActivity,
                        "family",
                        family
                    )

                    SessionManagement.UserData.setSession(
                        this@OtpActivity,
                        "addresses",
                        addresses
                    )

                    /*Intent(this@OtpActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(this)
                        finishAffinity()
                    }*/
                    Intent(this@OtpActivity, AppInstructionActivity::class.java).apply {
                        putExtra("title", resources.getString(R.string.app_instruction))
                        putExtra("url", BaseURL.INTRO_URL)
                        putExtra("user_email", user_email)
                        putExtra("showNext", true)
                        startActivity(this)
                        finish()
                    }

                }

                override fun VError(responce: String, code: String) {
                    val jsonObjectMain = JSONObject(responce)

                    if (code == "105") {
                        Intent(this@OtpActivity, NoDeliverActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    } else if (code == "106") {
                        //waiting user
                        val jsonObject = jsonObjectMain.getJSONObject("data")
                        val req_queue = jsonObject.getString("req_queue")

                        /*val bottomSheetRegisterSuccessDialog = BottomSheetRegisterSuccessDialog()
                        bottomSheetRegisterSuccessDialog.contexts = this@OtpActivity
                        if (bottomSheetRegisterSuccessDialog.isVisible) {
                            bottomSheetRegisterSuccessDialog.dismiss()
                        } else {
                            val args = Bundle()
                            args.putString("req_queue", req_queue)
                            bottomSheetRegisterSuccessDialog.arguments = args
                            bottomSheetRegisterSuccessDialog.show(
                                supportFragmentManager,
                                bottomSheetRegisterSuccessDialog.tag
                            )
                        }*/
                        Intent(this@OtpActivity, AppInstructionActivity::class.java).apply {
                            putExtra("title", resources.getString(R.string.app_instruction))
                            putExtra("url", BaseURL.INTRO_URL)
                            putExtra("user_email", user_email)
                            putExtra("req_queue", req_queue)
                            putExtra("showNext", true)
                            startActivity(this)
                            finish()
                        }
                    } else {
                        showToast(this@OtpActivity, jsonObjectMain.getString("message"))
                    }
                }
            }, BaseURL.PROGRESSDIALOG, this, "", null
        )
        task.execute()
    }

    private fun makeVeifyEmail(user_email: String, otp: String) {
        val params = HashMap<String, String>()
        params["user_id"] = user_id
        params["user_email"] = user_email
        params["otp"] = otp

        val loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        otpViewModel.verifyEmail(params)
        otpViewModel.verifyEmailResponseLiveData!!.observe(
            this,
            Observer { response: CommonResponse? ->
                if (loaderDialog.isShowing) {
                    loaderDialog.dismiss()
                }
                if (response != null) {
                    if (response.responce!!) {
                        SessionManagement.UserData.setSession(
                            this@OtpActivity,
                            BaseURL.KEY_EMAIL,
                            user_email
                        )
                        finish()
                    } else {
                        showToast(this@OtpActivity, response.message!!)
                    }
                }
            })

    }

    private fun makeResendOtp(userEmail: String) {
        val params = HashMap<String, String>()
        params["user_email"] = userEmail

        val loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        otpViewModel.resendOtp(params).observe(this, Observer { response ->
            if (loaderDialog.isShowing) {
                loaderDialog.dismiss()
            }
            if (response != null) {
                showToast(this@OtpActivity, response.message!!)
            }
        })
    }

}
