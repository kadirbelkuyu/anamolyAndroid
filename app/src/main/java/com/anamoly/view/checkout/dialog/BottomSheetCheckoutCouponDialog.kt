package com.anamoly.view.checkout.dialog

import Config.BaseURL
import Interfaces.OnCouponSelected
import Models.CouponModel
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.FrameLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.dialog_bottom_coupon.view.*
import utils.CommonAsyTask
import utils.ConnectivityReceiver
import utils.NameValuePair
import utils.SessionManagement


class BottomSheetCheckoutCouponDialog : BottomSheetDialogFragment {

    lateinit var contexts: Context

    var onCouponSelected: OnCouponSelected? = null

    constructor() : super()

    @SuppressLint("ValidFragment")
    constructor(onCouponSelected: OnCouponSelected) : super() {
        this.onCouponSelected = onCouponSelected
    }

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
        val rootView = inflater!!.inflate(R.layout.dialog_bottom_coupon, container, false)

        rootView.ll_coupon.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = rootView.ll_coupon.height
        }

        rootView.view_dash.background = CommonActivity.getCircleBG2()

        rootView.btn_coupon_verify.setOnClickListener {

            rootView.et_coupon_code.error = null

            val code = rootView.et_coupon_code.text.toString()

            if (code.isEmpty()) {
                rootView.et_coupon_code.setError(
                    contexts.resources.getString(
                        R.string.error_field_required
                    ), CommonActivity.setErrorDrawable(contexts)
                )
            } else {
                if (ConnectivityReceiver.isConnected) {
                    makeValidCoupon(code)
                } else {
                    ConnectivityReceiver.showSnackbar(contexts)
                }
            }

        }

        return rootView
    }

    private fun makeValidCoupon(coupon_code: String) {
        val params = ArrayList<NameValuePair>()
        params.add(
            NameValuePair(
                "user_id",
                SessionManagement.UserData.getSession(contexts, BaseURL.KEY_ID)
            )
        )
        params.add(NameValuePair("coupon_code", coupon_code))

        val task = CommonAsyTask(
            BaseURL.POST, params,
            BaseURL.VALID_COUPON_URL, object : CommonAsyTask.VJsonResponce {
                override fun VResponce(responce: String, message: String) {

                    val gson = Gson()
                    val type = object : TypeToken<CouponModel>() {}.type
                    val couponModel = gson.fromJson<CouponModel>(
                        responce,
                        type
                    )

                    if (onCouponSelected != null)
                        onCouponSelected!!.onSelected(couponModel)

                    dismiss()
                }

                override fun VError(responce: String, code: String) {
                    CommonActivity.showToast(contexts, responce)
                }
            }, BaseURL.PROGRESSDIALOG, contexts
        )
        task.execute()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contexts = context
    }
}