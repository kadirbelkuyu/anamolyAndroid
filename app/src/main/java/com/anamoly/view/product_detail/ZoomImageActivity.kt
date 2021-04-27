package com.anamoly.view.product_detail

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.anamoly.CommonActivity
import com.bumptech.glide.Glide
import com.anamoly.R
import kotlinx.android.synthetic.main.activity_zoom_image.*
import kotlinx.android.synthetic.main.custom_actionbar.view.*
import utils.ContextWrapper
import utils.LanguagePrefs
import java.util.*

class ZoomImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_zoom_image)
        setSupportActionBar(tl_zoom)
        setHeaderTitle("")

        val imagePath = intent.getStringExtra("imagePath")

        Glide.with(this)
            .load(imagePath)
            .into(pv_zoom_img)

    }

    fun setHeaderTitle(title: String) {
        val mActionBar = supportActionBar
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true)
            mActionBar.setDisplayHomeAsUpEnabled(true)
            mActionBar.setDisplayShowTitleEnabled(false)
            val mInflater = LayoutInflater.from(this)

            val mCustomView = mInflater.inflate(R.layout.custom_actionbar, null)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = Gravity.CENTER_VERTICAL
            mCustomView.layoutParams = layoutParams
            val mTitleTextView = mCustomView.tv_actionbar_title
            mTitleTextView!!.text = ""
            mActionBar.customView = mCustomView
            mActionBar.setDisplayShowCustomEnabled(true)

            val upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material)!!
            upArrow.setColorFilter(
                ContextCompat.getColor(this, R.color.colorBlack),
                PorterDuff.Mode.SRC_ATOP
            )
            mActionBar.setHomeAsUpIndicator(upArrow)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item!!.itemId == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition()
            } else {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
