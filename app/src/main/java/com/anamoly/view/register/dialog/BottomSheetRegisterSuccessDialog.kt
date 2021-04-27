package com.anamoly.view.register.dialog

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Color
import android.os.Build
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.FrameLayout
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.register.OrderInfo1Activity
import kotlinx.android.synthetic.main.dialog_bottom_register_success.view.*


class BottomSheetRegisterSuccessDialog : BottomSheetDialogFragment() {

    lateinit var contexts: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val rootView = inflater!!.inflate(R.layout.dialog_bottom_register_success, container, false)

        rootView.ll_register_success.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = rootView.ll_register_success.height
        }

        rootView.view_dash.background = CommonActivity.getCircleBG2()

        rootView.tv_waiting_number.text = arguments!!.getString("req_queue")

        rootView.ll_register_success_close.setOnClickListener {
            Intent(contexts, OrderInfo1Activity::class.java).apply {
                startActivity(this)
            }
            dismiss()
        }

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contexts = context
    }
}