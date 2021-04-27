package com.anamoly.view.user_account.fragment

import Config.BaseURL
import Database.CartData
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anamoly.CommonActivity
import com.bumptech.glide.Glide
import com.anamoly.R
import com.anamoly.view.cart.CartFragment
import com.anamoly.view.home.MainActivity
import com.anamoly.view.order.MyOrderActivity
import com.anamoly.view.user_account.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.android.synthetic.main.include_toolbar.view.*
import utils.SessionManagement


/**
 * Created on 18-01-2020.
 */
class UserFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = UserFragment::class.java.simpleName
    }

    private var contexts: Context? = null
    lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_user, container, false)

        rootView.toolbar_home.setBackgroundColor(CommonActivity.getHeaderColor())
        rootView.tv_toolbar_title.setTextColor(CommonActivity.getHeaderTextColor())
        CommonActivity.setImageTint(rootView.iv_toolbar_back)

        rootView.tv_toolbar_title.text = resources.getString(R.string.user_account)
        rootView.iv_toolbar_delete.visibility = View.VISIBLE
        rootView.iv_toolbar_delete.setImageResource(R.drawable.ic_logout)
        rootView.iv_toolbar_delete.setOnClickListener {
            CartData(contexts!!).deleteTable()
            SessionManagement(contexts!!).logoutSessionLogin()
        }

        rootView.iv_user_edit.setOnClickListener(this)
        rootView.ll_user_order.setOnClickListener(this)
        rootView.ll_user_settings.setOnClickListener(this)
        rootView.ll_user_share_app.setOnClickListener(this)
        rootView.ll_user_app_instruction.setOnClickListener(this)
        rootView.ll_user_contact_us.setOnClickListener(this)

        return rootView
    }

    override fun onClick(v: View?) {
        var commonIntent: Intent? = null
        when (v!!.id) {
            R.id.iv_user_edit -> {
                commonIntent = Intent(contexts!!, MyProfileActivity::class.java)
            }
            R.id.ll_user_order -> {
                Intent(contexts!!, MyOrderActivity::class.java).apply {
                    startActivityForResult(this, 6598)
                }
            }
            R.id.ll_user_settings -> {
                commonIntent = Intent(contexts!!, SettingsActivity::class.java)
            }
            R.id.ll_user_share_app -> {
                commonIntent = Intent(contexts!!, ShareAppActivity::class.java)
            }
            R.id.ll_user_app_instruction -> {
                commonIntent = Intent(contexts!!, AppInstructionActivity::class.java)
                commonIntent.putExtra("title", resources.getString(R.string.app_instruction))
                commonIntent.putExtra("url", BaseURL.ABOUT_URL)
            }
            R.id.ll_user_contact_us -> {
                commonIntent = Intent(contexts!!, ContactActivity::class.java)
            }
        }
        if (commonIntent != null) {
            startActivity(commonIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        Glide.with(contexts!!)
            .load(
                BaseURL.IMG_PROFILE_URL + SessionManagement.UserData.getSession(
                    contexts!!,
                    BaseURL.KEY_IMAGE
                )
            )
            .placeholder(R.color.colorTextHint)
            .error(R.color.colorTextHint)
            .into(rootView.iv_user_img)

        rootView.tv_user_mobile.text =
            SessionManagement.UserData.getSession(contexts!!, BaseURL.KEY_MOBILE)
        rootView.tv_user_email.text =
            SessionManagement.UserData.getSession(contexts!!, BaseURL.KEY_EMAIL)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 6598 && resultCode == Activity.RESULT_OK) {
            (contexts as MainActivity).setFragment(CartFragment(), false)
        }
    }

    override fun onAttach(context: Context) {
        this.contexts = context
        super.onAttach(context)
    }

}