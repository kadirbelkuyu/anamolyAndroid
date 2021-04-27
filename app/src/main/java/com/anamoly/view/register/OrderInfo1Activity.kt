package com.anamoly.view.register

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.activity_order_info1.*
import utils.ContextWrapper
import utils.LanguagePrefs
import java.util.*

class OrderInfo1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_order_info1)

        ll_order_info1_next.setOnClickListener {
            Intent(this, OrderInfo2Activity::class.java).apply {
                startActivity(this)
                finish()
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
