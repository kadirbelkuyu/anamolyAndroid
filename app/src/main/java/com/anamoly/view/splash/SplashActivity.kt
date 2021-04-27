package com.anamoly.view.splash

import Config.BaseURL
import Database.DatabaseHandler
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.choose_language.ChooseLanguageActivity
import com.anamoly.view.home.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.*
import org.json.JSONObject
import utils.ContextWrapper
import utils.LanguagePrefs
import utils.SessionManagement
import java.util.*
import kotlin.collections.HashMap


class SplashActivity : AppCompatActivity() {

    companion object {
        val TAG = SplashActivity::class.java.simpleName
    }

    var isResponseSuccess = false
    var isCartDone = false
    var isTimerFinish = false

    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_splash)

        DatabaseHandler(this)

        //OneSignal.clearOneSignalNotifications()

        view_splash_1.background = CommonActivity.getCircleBG2()
        view_splash_2.background = CommonActivity.getCircleBG2()
        view_splash_3.background = CommonActivity.getCircleBG2()

        splashViewModel.animationSlider(tv_splash_building1, tv_splash_building2, 8000L)
        splashViewModel.animationSlider(tv_splash_cloud1, tv_splash_cloud2, 5000L)

        GlobalScope.launch(context = Dispatchers.Main) {
            delay(3000)
            isTimerFinish = true
            if (isCartDone && isResponseSuccess) {
                launch(Dispatchers.Main) {
                    goNext()
                }
            }
        }

        if (SessionManagement.UserData.isLogin(this)) {
            makeGetCartList()
        } else {
            isCartDone = true
            makeGetSettings()
        }

    }

    private fun makeGetCartList() {
        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)

        splashViewModel.makeCartList(params)
            .observe(this, androidx.lifecycle.Observer { response: CommonResponse? ->
                isCartDone = true
                if (response != null) {
                    if (response.responce!!) {
                        SessionManagement.UserData.setSession(
                            this@SplashActivity,
                            "cartData",
                            response.data!!
                        )
                        makeGetSettings()
                    } else {
                        makeGetSettings()
                    }
                } else {
                    makeGetSettings()
                }
            })
    }

    private fun makeGetSettings() {
        splashViewModel.makeGetSettings()
            .observe(this, androidx.lifecycle.Observer { response: CommonResponse? ->
                if (response != null) {
                    if (response.responce!!) {
                        isResponseSuccess = true

                        splashViewModel.storeData(response.data!!)

                        if (isTimerFinish) {
                            goNext()
                        }
                    }
                }
            })
    }

    private fun goNext() {
        if (SessionManagement.UserData.isLogin(this@SplashActivity)) {
            Intent(this@SplashActivity, MainActivity::class.java).apply {
                if (intent.hasExtra("data")) {
                    val jsonObject = JSONObject(intent.getStringExtra("data")!!)
                    putExtra("type", jsonObject.getString("type"))
                    putExtra("ref_id", jsonObject.getString("ref_id"))
                }
                startActivity(this)
                finish()
            }
        } else {
            /*if (SessionManagement.PermanentData.getSessionBoolean(
                    this@SplashActivity,
                    "introDone"
                )
            ) {
                Intent(this@SplashActivity, ChooseLanguageActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            } else {
                Intent(this@SplashActivity, AppInstructionActivity::class.java).apply {
                    putExtra("title", resources.getString(R.string.app_instruction))
                    putExtra("url", BaseURL.INTRO_URL)
                    putExtra("showNext", true)
                    startActivity(this)
                    finish()
                }
            }*/
            Intent(this@SplashActivity, ChooseLanguageActivity::class.java).apply {
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
