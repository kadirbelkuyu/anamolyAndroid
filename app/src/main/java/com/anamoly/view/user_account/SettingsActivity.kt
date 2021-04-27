package com.anamoly.view.user_account

import Config.BaseURL
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.home.MainActivity
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_settings.*
import org.json.JSONObject
import utils.CommonAsyTask
import utils.ConnectivityReceiver
import utils.NameValuePair
import utils.SessionManagement


class SettingsActivity : CommonActivity(true), View.OnClickListener {

    var user_email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setHeaderTitle(resources.getString(R.string.settings))

        if (intent.hasExtra("user_email")) {
            user_email = intent.getStringExtra("user_email")!!
        } else if (SessionManagement.UserData.isLogin(this)) {
            user_email = SessionManagement.UserData.getSession(this, BaseURL.KEY_EMAIL)
        }

        val settings = SessionManagement.UserData.getSession(this, "settings")
        if (settings.isNotEmpty() && settings != "null") {
            val jsonObject = JSONObject(settings)

            sw_setting_notification_general.isChecked =
                (jsonObject.getString("general_notifications") == "1")
            sw_setting_notification_order.isChecked =
                (jsonObject.getString("order_notifications") == "1")
            sw_setting_email_general.isChecked = (jsonObject.getString("general_emails") == "1")
            sw_setting_email_order.isChecked = (jsonObject.getString("order_emails") == "1")
        }

        PushDownAnim.setPushDownAnimTo(btn_setting_save)

        ll_setting_notification_general.setOnClickListener(this)
        ll_setting_notification_order.setOnClickListener(this)
        ll_setting_email_general.setOnClickListener(this)
        ll_setting_email_order.setOnClickListener(this)
        btn_setting_save.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ll_setting_notification_general -> {
                sw_setting_notification_general.isChecked =
                    !sw_setting_notification_general.isChecked
            }
            R.id.ll_setting_notification_order -> {
                sw_setting_notification_order.isChecked =
                    !sw_setting_notification_order.isChecked
            }
            R.id.ll_setting_email_general -> {
                sw_setting_email_general.isChecked =
                    !sw_setting_email_general.isChecked
            }
            R.id.ll_setting_email_order -> {
                sw_setting_email_order.isChecked =
                    !sw_setting_email_order.isChecked
            }
            R.id.btn_setting_save -> {
                if (ConnectivityReceiver.isConnected) {
                    makeEditSeting(
                        if (sw_setting_notification_general.isChecked) "1" else "0",
                        if (sw_setting_notification_order.isChecked) "1" else "0",
                        if (sw_setting_email_general.isChecked) "1" else "0",
                        if (sw_setting_email_order.isChecked) "1" else "0"
                    )
                } else {
                    ConnectivityReceiver.showSnackbar(this@SettingsActivity)
                }
            }
        }
    }

    private fun makeEditSeting(
        general_notifications: String, order_notifications: String,
        general_emails: String, order_emails: String
    ) {
        val params = ArrayList<NameValuePair>()
        //params.add(NameValuePair("user_id", SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)))
        params.add(NameValuePair("user_email", user_email))
        params.add(NameValuePair("general_notifications", general_notifications))
        params.add(NameValuePair("order_notifications", order_notifications))
        params.add(NameValuePair("general_emails", general_emails))
        params.add(NameValuePair("order_emails", order_emails))

        val task = CommonAsyTask(
            BaseURL.POST, params,
            BaseURL.UPDATE_SETTING_URL, object : CommonAsyTask.VJsonResponce {
                override fun VResponce(responce: String, message: String) {

                    SessionManagement.UserData.setSession(
                        this@SettingsActivity,
                        "settings",
                        responce
                    )

                    if (!intent.hasExtra("req_queue")) {
                        Intent(this@SettingsActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(this)
                            finishAffinity()
                        }
                    } else {
                        finish()
                    }
                }

                override fun VError(responce: String, code: String) {
                    showToast(this@SettingsActivity, responce)
                }
            }, BaseURL.PROGRESSDIALOG, this
        )
        task.execute()
    }

}
