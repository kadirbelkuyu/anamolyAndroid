package com.anamoly.view.login

import Config.BaseURL
import Dialogs.LoaderDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.anamoly.AppController
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.forgot_password.ForgotPasswordActivity
import com.anamoly.view.home.MainActivity
import com.anamoly.view.otp.OtpActivity
import com.anamoly.response.CommonResponse
import com.anamoly.view.register.NoDeliverActivity
import com.anamoly.view.register.WaitingActivity
import com.bumptech.glide.Glide
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import utils.*
import java.util.*
import kotlin.collections.HashMap

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var loaderDialog: LoaderDialog

    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        setContentView(R.layout.activity_login)

        loaderDialog = LoaderDialog(this)

        Glide.with(this)
            .load(BaseURL.IMG_HEADER_URL+ AppController.loginTopImage)
            .placeholder(R.drawable.ic_logo_login)
            .error(R.drawable.ic_logo_login)
            .into(iv_login_logo)

        et_login_email.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.ALLOW_EMAIL_ONLY
            ), InputFilter.LengthFilter(50)
        )

        PushDownAnim.setPushDownAnimTo(btn_login)

        tv_login_title.makeLinks(
            Pair(
                resources.getString(R.string.delivered_to_your_home),
                object : View.OnClickListener {
                    override fun onClick(p0: View?) {

                    }
                })
        )

        btn_login.setOnClickListener(this)
        tv_login_forgot.setOnClickListener(this)

        loginViewModel.attemptLoginLiveData.observe(this, androidx.lifecycle.Observer { isSuccess ->
            if (isSuccess) {
                if (ConnectivityReceiver.isConnected) {
                    makeLogin(et_login_email.text.toString(), et_login_password.text.toString())
                } else {
                    ConnectivityReceiver.showSnackbar(this)
                }
            }
        })

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_login -> {
                /*Intent(this, MainActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }*/
                loginViewModel.attemptLogin(et_login_email, et_login_password)
            }
            R.id.tv_login_forgot -> {
                Intent(this, ForgotPasswordActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val s = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.linkColor = resources.getColor(R.color.colorOrange)
                    ds.color = resources.getColor(R.color.colorOrange)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            s.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(s, TextView.BufferType.SPANNABLE)
    }

    private fun makeLogin(user_email: String, user_password: String) {

        loaderDialog.show()

        val params = HashMap<String, String>()
        params.put("user_email", user_email)
        params["user_password"] = user_password

        loginViewModel.checkLogin(params)
        loginViewModel.loginResponseLiveData.observe(
            this,
            androidx.lifecycle.Observer { response: CommonResponse? ->
                if (response != null) {
                    if (response.responce!!) {

                        loginViewModel.dataStoreLiveData.observe(
                            this@LoginActivity,
                            androidx.lifecycle.Observer { isStore ->
                                if (isStore) {
                                    makeGetCartList()
                                }
                                loginViewModel.dataStoreLiveData.removeObservers(this@LoginActivity)
                            })
                        loginViewModel.storeData(response.data!!)

                    } else {
                        loaderDialog.dismiss()

                        if (response.code == "105") {
                            Intent(this@LoginActivity, NoDeliverActivity::class.java).apply {
                                startActivity(this)
                            }
                        } else if (response.code == "106") {
                            //waiting user

                            val jsonObject = JSONObject(response.data!!)
                            val req_queue = jsonObject.getString("req_queue")

                            /*val bottomSheetRegisterSuccessDialog =
                                BottomSheetRegisterSuccessDialog()
                            bottomSheetRegisterSuccessDialog.contexts = this@LoginActivity
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
                            Intent(
                                this@LoginActivity,
                                WaitingActivity::class.java
                            ).apply {
                                putExtra("req_queue", req_queue)
                                startActivity(this)
                            }
                        } else if (response.code == "108") {
                            //otp varification
                            Intent(this@LoginActivity, OtpActivity::class.java).apply {
                                putExtra("user_email", user_email)
                                startActivity(this)
                            }
                        } else {
                            CommonActivity.showToast(
                                this@LoginActivity,
                                response.message!!
                            )
                        }
                    }
                } else {
                    loaderDialog.dismiss()
                }
            })

    }

    private fun makeGetCartList() {
        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)

        loginViewModel.makeCartList(params)
            .observe(this, androidx.lifecycle.Observer { response: CommonResponse? ->
                loaderDialog.dismiss()
                if (response != null) {
                    if (response.responce!!) {
                        SessionManagement.UserData.setSession(
                            this@LoginActivity,
                            "cartData",
                            response.data!!
                        )
                        goNext()
                    } else {
                        goNext()
                    }
                } else {
                    goNext()
                }
            })
    }

    private fun goNext() {
        Intent(this@LoginActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
            finishAffinity()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
