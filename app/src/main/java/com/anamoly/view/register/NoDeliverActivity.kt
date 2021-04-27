package com.anamoly.view.register

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.activity_no_deliver.*
import utils.ContextWrapper
import utils.LanguagePrefs
import java.util.*

class NoDeliverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_no_deliver)

        ll_no_delivery_close.setOnClickListener {
            finish()
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
