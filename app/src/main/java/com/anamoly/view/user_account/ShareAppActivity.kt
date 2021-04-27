package com.anamoly.view.user_account

import Config.BaseURL
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anamoly.AppController
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_share_app.*
import org.json.JSONObject
import utils.SessionManagement
import java.lang.StringBuilder


class ShareAppActivity : CommonActivity(true), View.OnClickListener {

    /*Facebook App: com.facebook.katana
        Facebook Lite: com.facebook.lite
        Messenger: com.facebook.orca
        Messenger Lite: com.facebook.mlite*/

    companion object {
        val TAG = ShareAppActivity::class.java.simpleName
    }

    lateinit var shareAppViewModel: ShareAppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareAppViewModel = ViewModelProviders.of(this).get(ShareAppViewModel::class.java)
        setContentView(R.layout.activity_share_app)
        setHeaderTitle(resources.getString(R.string.share_app))

        val user_email = SessionManagement.UserData.getSession(this, BaseURL.KEY_EMAIL)
        val user_full_name = SessionManagement.UserData.getSession(this, BaseURL.KEY_NAME)
        val currency = SessionManagement.PermanentData.getSession(this, "currency_symbol")
        val shareableLink =
            "https://play.google.com/store/apps/details?id=${packageName}&referrer=$user_email"

        PushDownAnim.setPushDownAnimTo(tv_share_facebook, tv_share_whatsapp, tv_share_email)

        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius =
            CommonActivity.dpToPx(AppController.instance?.applicationContext!!, 5F).toFloat()
        gradientDrawable.setColor(AppController.infoBoxBg)

        ll_share_note.background = gradientDrawable

        tv_share_facebook.isEnabled = false
        tv_share_whatsapp.isEnabled = false
        tv_share_email.isEnabled = false

        tv_share_coupon_code.visibility = View.GONE
        tv_share_coupon_discount.visibility = View.GONE
        pb_share_coupon.visibility = View.VISIBLE

        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)

        shareAppViewModel.makeGetReferralCode(params)
            .observe(this, Observer { response: CommonResponse? ->
                if (response != null) {
                    if (response.responce!!) {
                        val jsonObject = JSONObject(response.data!!)

                        val discount = jsonObject.getString("discount")

                        tv_share_coupon_code.text = jsonObject.getString("coupon_code")
                        tv_share_coupon_discount.text =
                            "$discount% ${resources.getString(R.string.discount)}"

                        tv_share_facebook.isEnabled = true
                        tv_share_whatsapp.isEnabled = true
                        tv_share_email.isEnabled = true

                        tv_share_coupon_code.visibility = View.VISIBLE
                        tv_share_coupon_discount.visibility = View.VISIBLE
                        pb_share_coupon.visibility = View.GONE

                        val stringBuilder = StringBuilder()
                        stringBuilder.append(
                            "${resources.getString(R.string.have_you_tried)} ${resources.getString(R.string.app_name)} ${resources.getString(
                                R.string.yet_use_my_code_with_your_first_order_and_we_both_get_a
                            )} $discount $currency ${resources.getString(R.string.discount)} :)"
                        )
                        stringBuilder.append("\n")
                        stringBuilder.append("${resources.getString(R.string.greetings)}, $user_full_name")
                        stringBuilder.append("\n\n")
                        stringBuilder.append(resources.getString(R.string.download_the_app_from_this_link))
                        stringBuilder.append("\n\n")
                        stringBuilder.append(shareableLink)

                        shareAppViewModel.newDeepLink = stringBuilder.toString()

                    } else {
                        CommonActivity.showToast(this, response.message!!)
                    }
                }
            })

        tv_share_facebook.setOnClickListener(this)
        tv_share_whatsapp.setOnClickListener(this)
        tv_share_email.setOnClickListener(this)
        tv_share_tnc.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        shareAppViewModel.onClick(this, v!!.id)
    }

}
