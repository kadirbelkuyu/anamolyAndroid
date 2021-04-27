package com.anamoly.view.choose_language

import Config.BaseURL
import Interfaces.OnEditProfileSave
import Interfaces.OnTimeSelected
import Models.TimeSlotModel
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.anamoly.AppController
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.cart.dialog.BottomSheetDeliveryTimeDialog
import com.anamoly.view.choose_language.dialog.BottomSheetChooseLanguageDialog
import com.anamoly.view.login.LoginActivity
import com.anamoly.view.register.RegisterActivity
import com.bumptech.glide.Glide
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_choose_language.*
import kotlinx.coroutines.*
import utils.ContextWrapper
import utils.LanguagePrefs
import utils.SessionManagement
import java.io.Serializable
import java.util.*

class ChooseLanguageActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var languagePrefs: LanguagePrefs

    var oldLanguage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languagePrefs = LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_choose_language)
        oldLanguage = LanguagePrefs.getLang(this)!!

        PushDownAnim.setPushDownAnimTo(btn_choose_language_existing, btn_choose_language_new)

        Glide.with(this)
            .load(
                BaseURL.IMG_HEADER_URL + AppController.loginTopImage
            )
            .placeholder(R.drawable.ic_logo_login)
            .error(R.drawable.ic_logo_login)
            .into(iv_choose_language_logo)

        sp_choose_language.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.list_language)
        )

        val selection = when (oldLanguage) {
            "en" -> resources.getString(R.string.english)
            "sv" -> resources.getString(R.string.swedish)
            "ar" -> resources.getString(R.string.arabic)
            "tr" -> resources.getString(R.string.turkish)
            "de" -> resources.getString(R.string.english)
            else -> resources.getString(R.string.english)
        }

        tv_choose_language.text = selection

        //sp_choose_language.setSelection(selection)

        btn_choose_language_existing.setOnClickListener(this)
        btn_choose_language_new.setOnClickListener(this)
        ll_choose_language.setOnClickListener {
            //sp_choose_language.performClick()
            val bottomSheetChooseLanguageDialog =
                BottomSheetChooseLanguageDialog(object : OnEditProfileSave {
                    override fun onSave(title: String, newValue: String, newValue2: String) {
                        GlobalScope.launch(Dispatchers.Main) {
                            delay(500)
                            if (title != oldLanguage) {
                                languagePrefs.saveLanguage(title)
                                languagePrefs.initLanguage(title)
                                this@ChooseLanguageActivity.recreate()
                            }
                        }
                    }
                })
            bottomSheetChooseLanguageDialog.contexts = this@ChooseLanguageActivity
            if (bottomSheetChooseLanguageDialog.isVisible) {
                bottomSheetChooseLanguageDialog.dismiss()
            } else {
                val args = Bundle()
                args.putString("oldLanguage",oldLanguage)
                bottomSheetChooseLanguageDialog.arguments = args
                bottomSheetChooseLanguageDialog.show(
                    supportFragmentManager,
                    bottomSheetChooseLanguageDialog.tag
                )
            }
        }

        /*sp_choose_language.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                tv_choose_language.text = sp_choose_language.selectedItem.toString()
                val newLanguage = when (position) {
                    0 -> "en"
                    1 -> "sv"
                    2 -> "ar"
                    3 -> "tr"
                    4 -> "de"
                    else -> ""
                }
                if (newLanguage != oldLanguage) {
                    languagePrefs.saveLanguage(newLanguage)
                    languagePrefs.initLanguage(newLanguage)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //onNothingSelected
            }
        }*/

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_choose_language_existing -> {
                Intent(this, LoginActivity::class.java).apply {
                    startActivity(this)
                }
            }
            R.id.btn_choose_language_new -> {
                Intent(this, RegisterActivity::class.java).apply {
                    startActivity(this)
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
