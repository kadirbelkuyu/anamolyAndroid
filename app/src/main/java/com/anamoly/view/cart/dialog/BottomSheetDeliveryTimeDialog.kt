package com.anamoly.view.cart.dialog

import Interfaces.OnTimeSelected
import Models.TimeSlotModel
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.cart.adapter.TimeSlotAdapter
import kotlinx.android.synthetic.main.dialog_bottom_delivery_time.view.*


class BottomSheetDeliveryTimeDialog : BottomSheetDialogFragment {

    lateinit var contexts: Context

    var onTimeSelected: OnTimeSelected? = null

    constructor() : super()

    @SuppressLint("ValidFragment")
    constructor(onTimeSelected: OnTimeSelected) : super() {
        this.onTimeSelected = onTimeSelected
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
        val rootView = inflater.inflate(R.layout.dialog_bottom_delivery_time, container, false)

        rootView.ll_delivery_time.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = rootView.ll_delivery_time.height
        }

        rootView.view_dash.background = CommonActivity.getCircleBG2()

        val timeSlotModelList =
            arguments!!.getSerializable("timeSlotData") as ArrayList<TimeSlotModel>

        val timeSlotAdapter = TimeSlotAdapter(
            contexts,
            timeSlotModelList,
            object : TimeSlotAdapter.OnItemSelected {
                override fun onClick(position: Int, timeSlotModel: TimeSlotModel) {
                    if (onTimeSelected != null) {
                        onTimeSelected!!.onSelected(timeSlotModel)
                    }
                    dismiss()
                }
            })

        if (arguments!!.containsKey("timeSlotSelected")) {
            timeSlotAdapter.selectedPosition = -1
            val timeSlotModelSelected =
                arguments!!.getSerializable("timeSlotSelected") as TimeSlotModel
            timeSlotAdapter.timeSlotModelSelected = timeSlotModelSelected
        }

        rootView.rv_delivery_time.apply {
            layoutManager = LinearLayoutManager(contexts)
            adapter = timeSlotAdapter
            CommonActivity.runLayoutAnimation(this)
        }

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contexts = context
    }
}