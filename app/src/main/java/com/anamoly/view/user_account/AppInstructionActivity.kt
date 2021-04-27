package com.anamoly.view.user_account

import Config.BaseURL
import Dialogs.LoaderDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.choose_language.ChooseLanguageActivity
import com.anamoly.view.home.MainActivity
import com.anamoly.view.register.WaitingActivity
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_app_instruction.*
import utils.SessionManagement

class AppInstructionActivity : CommonActivity() {

    lateinit var loaderDialog: LoaderDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_instruction)

        val title = intent.getStringExtra("title")
        val url = intent.getStringExtra("url")

        setHeaderTitle(title!!)

        PushDownAnim.setPushDownAnimTo(btn_app_instruction_setting, btn_app_instruction_ok)

        if (intent.getBooleanExtra("showNext", false)) {
            SessionManagement.PermanentData.setSession(this, "introDone", true)
            //iv_app_instruction.visibility = View.VISIBLE
            btn_app_instruction_setting.visibility = View.VISIBLE
            btn_app_instruction_ok.visibility = View.VISIBLE
            supportActionBar?.hide()
        }

        loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        val webSettings: WebSettings = wv_app_instruction.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setSupportMultipleWindows(true)
        wv_app_instruction.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loaderDialog.dismiss()
            }
        }
        wv_app_instruction.loadUrl("$url/${BaseURL.HEADER_LANG}")

        btn_app_instruction_setting.setOnClickListener {
            Intent(this@AppInstructionActivity, SettingsActivity::class.java).apply {
                putExtra("user_email", intent.getStringExtra("user_email"))
                if (intent.hasExtra("req_queue")) {
                    putExtra("req_queue", intent.getStringExtra("req_queue"))
                }
                startActivity(this)
                finish()
            }
        }

        btn_app_instruction_ok.setOnClickListener {
            if (intent.hasExtra("req_queue")) {
                Intent(this@AppInstructionActivity, WaitingActivity::class.java).apply {
                    putExtra("req_queue", intent.getStringExtra("req_queue"))
                    startActivity(this)
                    finish()
                }
            } else {
                Intent(this@AppInstructionActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(this)
                    finishAffinity()
                }
            }
        }

    }

}
