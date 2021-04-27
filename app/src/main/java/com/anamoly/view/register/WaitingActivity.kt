package com.anamoly.view.register

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.activity_waiting.*
import utils.ContextWrapper
import utils.LanguagePrefs
import utils.SessionManagement
import java.util.*

class WaitingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_waiting)

        tv_waiting_number.text = intent.getStringExtra("req_queue")

        ll_register_success_close.setOnClickListener {
            Intent(this, OrderInfo1Activity::class.java).apply {
                startActivity(this)
                finish()
            }
        }

        tv_waiting_contact_us.setOnClickListener {
            val email = SessionManagement.PermanentData.getSession(this, "app_email")
            if (email.isNotEmpty()) {
                Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", email, null
                    )
                ).apply {
                    putExtra(Intent.EXTRA_SUBJECT, "Subject")
                    putExtra(Intent.EXTRA_TEXT, "Body")
                    startActivity(Intent.createChooser(this, "Send email..."))
                }
            }
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
