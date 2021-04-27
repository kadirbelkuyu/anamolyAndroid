package com.anamoly.view.user_account.dialog

import Interfaces.OnEditProfileSave
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.text.InputFilter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.FrameLayout
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.dialog_bottom_edit_profile.view.*
import utils.TextInputFilter


class BottomSheetEditProfileDialog : BottomSheetDialogFragment {

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
    ): View? {
        val rootView = inflater.inflate(R.layout.dialog_bottom_edit_profile, container, false)

        rootView.ll_edit_profile.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = rootView.ll_edit_profile.height
        }

        rootView.view_dash.background = CommonActivity.getCircleBG2()

        val title = arguments!!.getString("title")

        rootView.tv_edit_profile_title.text = title
        rootView.et_edit_profile_value.setText(arguments!!.getString("value"))

        var checkValue2 = false

        if (arguments!!.containsKey("title2") && arguments!!.containsKey("value2")) {
            checkValue2 = true
            rootView.tv_edit_profile_title2.text = arguments!!.getString("title2")
            rootView.et_edit_profile_value2.setText(arguments!!.getString("value2"))
        } else {
            rootView.tv_edit_profile_title2.visibility = View.GONE
            rootView.et_edit_profile_value2.visibility = View.GONE
        }

        if (title.equals(contexts.resources.getString(R.string.first_name))) {
            rootView.et_edit_profile_value.filters = arrayOf<InputFilter>(
                TextInputFilter(
                    TextInputFilter.Filter.NOT_ALLOW_SPACE
                ), InputFilter.LengthFilter(50)
            )
            rootView.et_edit_profile_value2.filters = arrayOf<InputFilter>(
                TextInputFilter(
                    TextInputFilter.Filter.NOT_ALLOW_SPACE
                ), InputFilter.LengthFilter(50)
            )
        } else if (title.equals(contexts.resources.getString(R.string.email_id))) {
            rootView.et_edit_profile_value.filters = arrayOf<InputFilter>(
                TextInputFilter(
                    TextInputFilter.Filter.ALLOW_EMAIL_ONLY
                ), InputFilter.LengthFilter(50)
            )
        } else if (title.equals(contexts.resources.getString(R.string.phone_no))) {
            rootView.et_edit_profile_value.filters = arrayOf<InputFilter>(
                TextInputFilter(
                    TextInputFilter.Filter.ONLY_NUMERIC
                ), InputFilter.LengthFilter(50)
            )
        }

        rootView.btn_edit_profile_save.setOnClickListener {

            rootView.et_edit_profile_value.error = null

            val value1 = rootView.et_edit_profile_value.text.toString()
            val value2 = rootView.et_edit_profile_value2.text.toString()

            if (value1.isEmpty()) {
                rootView.et_edit_profile_value.setError(
                    contexts.resources.getString(R.string.error_field_required),
                    CommonActivity.setErrorDrawable(contexts)
                )
            } else if (checkValue2 && value2.isEmpty()) {
                rootView.et_edit_profile_value2.setError(
                    contexts.resources.getString(R.string.error_field_required),
                    CommonActivity.setErrorDrawable(contexts)
                )
            } else {
                if (onEditProfileSave != null) {
                    onEditProfileSave!!.onSave(title!!, value1, value2)
                }
                dismiss()
            }

        }

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contexts = context
    }

}