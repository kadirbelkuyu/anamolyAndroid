package com.anamoly.view.choose_language.dialog

import Interfaces.OnEditProfileSave
import Interfaces.OnTimeSelected
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.FrameLayout
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.dialog_bottom_choose_language.view.*
import kotlinx.android.synthetic.main.dialog_bottom_choose_language.view.view_dash
import utils.LanguagePrefs


class BottomSheetChooseLanguageDialog : BottomSheetDialogFragment {

    lateinit var rootView: View
    lateinit var contexts: Context

    var onEditProfileSave: OnEditProfileSave? = null

    constructor() : super()

    @SuppressLint("ValidFragment")
    constructor(onEditProfileSave: OnEditProfileSave) : super() {
        this.onEditProfileSave = onEditProfileSave
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation_slidebottom
        dialog!!.window!!.statusBarColor = Color.TRANSPARENT
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.dialog_bottom_choose_language, container, false)

        rootView.ll_choose_language.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = rootView.ll_choose_language.height
        }

        rootView.view_dash.background = CommonActivity.getCircleBG2()

        rootView.btn_choose_language_english.setOnClickListener {
            onEditProfileSave?.onSave("en", "en", "en")
            dismiss()
        }
        rootView.btn_choose_language_swedish.setOnClickListener {
            onEditProfileSave?.onSave("sv", "sv", "sv")
            dismiss()
        }
        rootView.btn_choose_language_arabic.setOnClickListener {
            onEditProfileSave?.onSave("ar", "ar", "ar")
            dismiss()
        }
        rootView.btn_choose_language_turkish.setOnClickListener {
            onEditProfileSave?.onSave("tr", "tr", "tr")
            dismiss()
        }

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contexts = context
    }
}