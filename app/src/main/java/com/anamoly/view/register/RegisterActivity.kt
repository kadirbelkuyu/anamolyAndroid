package com.anamoly.view.register

import Config.BaseURL
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.anamoly.AppController
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.home.MainActivity
import com.anamoly.view.otp.OtpActivity
import com.anamoly.view.register.dialog.BottomSheetRegisterSuccessDialog
import com.anamoly.view.user_account.AppInstructionActivity
import com.bumptech.glide.Glide
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject
import utils.*
import java.util.*
import kotlin.collections.ArrayList


class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = RegisterActivity::class.java.simpleName
    }

    var fcmToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_register)

        Glide.with(this)
            .load(
                BaseURL.IMG_HEADER_URL + AppController.loginTopImage
            )
            .placeholder(R.drawable.ic_logo_login)
            .error(R.drawable.ic_logo_login)
            .into(iv_register_logo)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this,
            OnSuccessListener<InstanceIdResult> { instanceIdResult ->
                val token = instanceIdResult.token
                Log.e(TAG, token)
                fcmToken = token
            })

        PushDownAnim.setPushDownAnimTo(btn_register)

        et_register_email.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.ALLOW_EMAIL_ONLY
            ), InputFilter.LengthFilter(50)
        )

        et_register_mobile.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.ONLY_NUMERIC
            ), InputFilter.LengthFilter(15)
        )

        et_register_post_code.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.ONLY_NUMERIC
            ), InputFilter.LengthFilter(5)
        ) + InputFilter.AllCaps()

        et_register_house_no.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.ONLY_NUMERIC
            ), InputFilter.LengthFilter(15)
        )

        et_register_adon_no.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.NOT_ALLOW_SPACE
            ), InputFilter.LengthFilter(15)
        )

        et_register_first_name.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.NOT_ALLOW_SPACE
            ), InputFilter.LengthFilter(50)
        )

        et_register_last_name.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.NOT_ALLOW_SPACE
            ), InputFilter.LengthFilter(50)
        )

        et_register_post_code.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                et_register_post_code.hint = resources.getString(R.string.postal_hint)
            } else {
                et_register_post_code.hint = resources.getString(R.string.postal_code)
            }
        }

        /*et_register_first_name.visibility = View.GONE
        et_register_last_name.visibility = View.GONE*/
        et_register_company_name.visibility = View.GONE
        et_register_company_id.visibility = View.GONE

        et_register_password.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= et_register_password.right - et_register_password.compoundDrawables.get(
                        DRAWABLE_RIGHT
                    ).bounds.width()
                ) { // your action here
                    if (et_register_password.transformationMethod.javaClass.simpleName == "PasswordTransformationMethod") {
                        et_register_password.transformationMethod = SingleLineTransformationMethod()
                        et_register_password.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_password,
                            0,
                            R.drawable.ic_eye,
                            0
                        )
                    } else {
                        et_register_password.transformationMethod = PasswordTransformationMethod()
                        et_register_password.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_password,
                            0,
                            R.drawable.ic_eye_hide,
                            0
                        )
                    }
                    return@OnTouchListener true
                }
            }
            false
        })

        chk_register_company.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                et_register_adon_no.imeOptions = EditorInfo.IME_ACTION_NEXT
                et_register_adon_no.nextFocusDownId = R.id.et_register_company_name
                et_register_company_name.visibility = View.VISIBLE
                et_register_company_id.visibility = View.VISIBLE
            } else {
                et_register_adon_no.imeOptions = EditorInfo.IME_ACTION_DONE
                et_register_company_name.visibility = View.GONE
                et_register_company_id.visibility = View.GONE
            }
        }

        tv_register_privacy_policy.setOnClickListener {
            Intent(this, AppInstructionActivity::class.java).apply {
                putExtra("title", resources.getString(R.string.privacy_policy))
                putExtra("url", BaseURL.POLICY_URL)
                startActivity(this)
            }
        }

        btn_register.setOnClickListener {
            attemptRegister()
        }

    }

    private fun attemptRegister() {

        et_register_email.error = null
        et_register_password.error = null
        et_register_mobile.error = null
        et_register_post_code.error = null
        et_register_house_no.error = null
        et_register_adon_no.error = null
        et_register_first_name.error = null
        et_register_last_name.error = null
        et_register_company_name.error = null
        et_register_company_id.error = null

        var focuseView: View? = null
        var cancel = false

        val email = et_register_email.text.toString()
        val password = et_register_password.text.toString()
        val mobile = et_register_mobile.text.toString()
        val first_name = et_register_first_name.text.toString()
        val last_name = et_register_last_name.text.toString()
        val post_code = et_register_post_code.text.toString()
        val house_no = et_register_house_no.text.toString()
        val adon_no = et_register_adon_no.text.toString()

        val company_name: String
        val company_id: String

        if (chk_register_company.isChecked) {
            company_name = et_register_company_name.text.toString()
            company_id = et_register_company_id.text.toString()
        } else {
            company_name = ""
            company_id = ""
        }

        if (chk_register_company.isChecked) {
            if (company_id.isEmpty()) {
                et_register_company_id.setError(
                    resources.getString(R.string.error_field_required),
                    CommonActivity.setErrorDrawable(this)
                )
                focuseView = et_register_company_id
                cancel = true
            }

            if (company_name.isEmpty()) {
                et_register_company_name.setError(
                    resources.getString(R.string.error_field_required),
                    CommonActivity.setErrorDrawable(this)
                )
                focuseView = et_register_company_name
                cancel = true
            }

        }

        /*if (adon_no.isEmpty()) {
            et_register_adon_no.setError(
                resources.getString(R.string.error_field_required),
                CommonActivity.setErrorDrawable(this)
            )
            focuseView = et_register_adon_no
            cancel = true
        }*/

        if (house_no.isEmpty()) {
            et_register_house_no.setError(
                resources.getString(R.string.error_field_required),
                CommonActivity.setErrorDrawable(this)
            )
            focuseView = et_register_house_no
            cancel = true
        }

        if (post_code.isEmpty()) {
            et_register_post_code.setError(
                resources.getString(R.string.error_field_required),
                CommonActivity.setErrorDrawable(this)
            )
            focuseView = et_register_post_code
            cancel = true
        } else if (post_code.length < 5) {
            et_register_post_code.setError(
                resources.getString(R.string.error_invalid_postal_code),
                CommonActivity.setErrorDrawable(this)
            )
            focuseView = et_register_post_code
            cancel = true
        }

        if (last_name.isEmpty()) {
            et_register_last_name.setError(
                resources.getString(R.string.error_field_required),
                CommonActivity.setErrorDrawable(this)
            )
            focuseView = et_register_last_name
            cancel = true
        }

        if (first_name.isEmpty()) {
            et_register_first_name.setError(
                resources.getString(R.string.error_field_required),
                CommonActivity.setErrorDrawable(this)
            )
            focuseView = et_register_first_name
            cancel = true
        }

        if (mobile.isEmpty()) {
            et_register_mobile.setError(
                resources.getString(R.string.error_field_required),
                CommonActivity.setErrorDrawable(this)
            )
            focuseView = et_register_mobile
            cancel = true
        }

        if (password.isEmpty()) {
            et_register_password.setError(
                resources.getString(R.string.error_field_required),
                CommonActivity.setErrorDrawable(this)
            )
            focuseView = et_register_password
            cancel = true
        } /*else if (!CommonActivity.isValidPassword(password)) {
            et_register_password.setError(
                resources.getString(R.string.error_password_required),
                CommonActivity.setErrorDrawable(this)
            )
            focuseView = et_register_password
            cancel = true
        }*/

        if (email.isEmpty()) {
            et_register_email.setError(
                resources.getString(R.string.error_field_required),
                CommonActivity.setErrorDrawable(this)
            )
            focuseView = et_register_email
            cancel = true
        } else if (!CommonActivity.isEmailValid(email)) {
            et_register_email.setError(
                resources.getString(R.string.error_invalid_email_address),
                CommonActivity.setErrorDrawable(this)
            )
            focuseView = et_register_email
            cancel = true
        }

        if (cancel) {
            focuseView?.requestFocus()
        } else {
            if (ConnectivityReceiver.isConnected) {
                makeRegister(
                    email,
                    password,
                    mobile,
                    post_code,
                    house_no,
                    adon_no,
                    chk_register_company.isChecked.toString(),
                    first_name,
                    last_name,
                    company_name,
                    company_id
                )
            } else {
                ConnectivityReceiver.showSnackbar(this)
            }
        }

    }

    private fun makeRegister(
        user_email: String, user_password: String, user_phone: String, postal_code: String,
        house_no: String, add_on_house_no: String, is_company: String, user_firstname: String,
        user_lastname: String, user_company_name: String, user_company_id: String
    ) {
        val params = ArrayList<NameValuePair>()
        params.add(NameValuePair("user_email", user_email))
        params.add(NameValuePair("user_password", user_password))
        params.add(NameValuePair("user_phone", user_phone))
        params.add(NameValuePair("postal_code", postal_code))
        params.add(NameValuePair("house_no", house_no))
        params.add(NameValuePair("add_on_house_no", add_on_house_no))
        params.add(NameValuePair("android_token", fcmToken))
        params.add(NameValuePair("ios_token", ""))
        params.add(NameValuePair("is_company", is_company))
        params.add(NameValuePair("user_firstname", user_firstname))
        params.add(NameValuePair("user_lastname", user_lastname))
        if (chk_register_company.isChecked) {
            params.add(NameValuePair("user_company_name", user_company_name))
            params.add(NameValuePair("user_company_id", user_company_id))
        }
        val referralCode =
            SessionManagement.PermanentData.getSession(this@RegisterActivity, "friendReferrerCode")
        if (referralCode.isNotEmpty()) {
            params.add(NameValuePair("referred_by", referralCode))
        }

        val task = CommonAsyTask(
            BaseURL.POST, params,
            BaseURL.REGISTER_URL, object : CommonAsyTask.VJsonResponce {
                override fun VResponce(responce: String, message: String) {

                    SessionManagement.PermanentData.setSession(
                        this@RegisterActivity,
                        "friendReferrerCode",
                        ""
                    )

                    val jsonObject = JSONObject(responce)
                    val user_email = jsonObject.getString("user_email")

                    Intent(this@RegisterActivity, OtpActivity::class.java).apply {
                        putExtra("user_email", user_email)
                        startActivity(this)
                        finish()
                    }

                }

                override fun VError(responce: String, code: String) {
                    CommonActivity.showToast(this@RegisterActivity, responce)
                    if (code == "105") {
                        Intent(this@RegisterActivity, NoDeliverActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    } else if (code == "106") {
                        //waiting user
                        val jsonObjectMain = JSONObject(responce)
                        val jsonObject = jsonObjectMain.getJSONObject("data")
                        val req_queue = jsonObject.getString("req_queue")

                        /*val bottomSheetRegisterSuccessDialog = BottomSheetRegisterSuccessDialog()
                        bottomSheetRegisterSuccessDialog.contexts = this@RegisterActivity
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
                        Intent(this@RegisterActivity, AppInstructionActivity::class.java).apply {
                            putExtra("title", resources.getString(R.string.app_instruction))
                            putExtra("url", BaseURL.INTRO_URL)
                            putExtra("user_email", user_email)
                            putExtra("req_queue", req_queue)
                            putExtra("showNext", true)
                            startActivity(this)
                            finish()
                        }
                    } else if (code == "108") {
                        //otp varification
                        Intent(this@RegisterActivity, OtpActivity::class.java).apply {
                            putExtra("user_email", user_email)
                            startActivity(this)
                            finish()
                        }
                    }
                }
            }, BaseURL.PROGRESSDIALOG, this
        )
        task.execute()
    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
