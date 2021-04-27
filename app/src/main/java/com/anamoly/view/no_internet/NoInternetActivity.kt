package com.anamoly.view.no_internet

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anamoly.CommonActivity
import com.droidnet.DroidNet
import com.anamoly.R
import com.thekhaeng.pushdownanim.PushDown
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_no_internet.*
import utils.ContextWrapper
import utils.LanguagePrefs
import java.util.*

class NoInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_no_internet)

        PushDownAnim.setPushDownAnimTo(btn_internet_retry)

        val droidNet = DroidNet.getInstance()
        droidNet.addInternetConnectivityListener { isConnected: Boolean ->
            if (isConnected) {
                Intent().apply {
                    setResult(Activity.RESULT_OK, this)
                    finish()
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
