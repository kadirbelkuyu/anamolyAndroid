package com.anamoly.view.user_account

import Config.BaseURL
import Dialogs.LoaderDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.no_internet.NoInternetActivity
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_contact.*
import utils.ConnectivityReceiver
import utils.SessionManagement

class ContactActivity : CommonActivity(true), View.OnClickListener {

    lateinit var contactViewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)
        setContentView(R.layout.activity_contact)
        setHeaderTitle(resources.getString(R.string.contact))

        PushDownAnim.setPushDownAnimTo(
            btn_contact_us_send,
            iv_contact_call,
            iv_contact_email,
            iv_contact_whatsapp
        )

        contactViewModel.isContactValid.observe(this, Observer { isValid ->
            if (isValid) {
                if (ConnectivityReceiver.isConnected) {
                    val params = HashMap<String, String>()
                    params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
                    params["fullname"] = et_contact_us_full_name.text.toString()
                    params["phone"] = et_contact_us_phone.text.toString()
                    params["message"] = et_contact_us_message.text.toString()

                    val loaderDialog = LoaderDialog(this)
                    loaderDialog.show()

                    contactViewModel.makeSendContact(params)
                        .observe(this, Observer { response: CommonResponse? ->
                            loaderDialog.dismiss()
                            if (response != null) {
                                if (response.responce!!) {
                                    CommonActivity.showToast(this, response.data!!)
                                    finish()
                                } else {
                                    CommonActivity.showToast(this, response.message!!)
                                }
                            }
                        })
                } else {
                    Intent(this, NoInternetActivity::class.java).apply {
                        startActivityForResult(this, 6524)
                    }
                }
            }
        })

        btn_contact_us_send.setOnClickListener(this)
        iv_contact_call.setOnClickListener(this)
        iv_contact_email.setOnClickListener(this)
        iv_contact_whatsapp.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_contact_us_send -> {
                contactViewModel.attemptContactUs(
                    this,
                    et_contact_us_full_name,
                    et_contact_us_phone,
                    et_contact_us_message
                )
            }
            R.id.iv_contact_call -> {
                contactViewModel.intentCall(this)
            }
            R.id.iv_contact_email -> {
                contactViewModel.intentEmail(this)
            }
            R.id.iv_contact_whatsapp -> {
                contactViewModel.intentWhatsapp(this)
            }
        }
    }

}
