package com.anamoly.view.checkout.dialog

import Config.BaseURL
import Interfaces.OnTimeSelected
import Models.AddressModel
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
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.FrameLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.dialog_bottom_delivery_address.view.*
import utils.*


class BottomSheetCheckoutAddressDialog : BottomSheetDialogFragment {

    lateinit var rootView: View
    lateinit var contexts: Context

    var onTimeSelected: OnTimeSelected? = null

    constructor() : super()

    @SuppressLint("ValidFragment")
    constructor(onTimeSelected: OnTimeSelected) : super() {
        this.onTimeSelected = onTimeSelected
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
        rootView = inflater.inflate(R.layout.dialog_bottom_delivery_address, container, false)

        rootView.ll_delivery_address.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = rootView.ll_delivery_address.height
        }

        rootView.view_dash.background = CommonActivity.getCircleBG2()

        rootView.et_delivery_address_pine.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.ONLY_NUMERIC
            ), InputFilter.LengthFilter(5)
        ) + InputFilter.AllCaps()

        rootView.et_delivery_address_house_no.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.ONLY_NUMERIC
            ), InputFilter.LengthFilter(15)
        )

        rootView.et_delivery_address_house_ad_no.filters = arrayOf<InputFilter>(
            TextInputFilter(
                TextInputFilter.Filter.NOT_ALLOW_SPACE
            ), InputFilter.LengthFilter(15)
        )

        rootView.et_delivery_address_pine.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                rootView.et_delivery_address_pine.hint = resources.getString(R.string.postal_hint)
            } else {
                rootView.et_delivery_address_pine.hint = ""
            }
        }

        val address = SessionManagement.UserData.getSession(contexts, "addresses")
        if (address != "null") {
            val addressModelList = ArrayList<AddressModel>()

            val gson = Gson()
            val type = object : TypeToken<ArrayList<AddressModel>>() {}.type
            addressModelList.addAll(gson.fromJson<ArrayList<AddressModel>>(address, type))

            if (addressModelList.size > 0) {
                val addressModel = addressModelList[0]

                rootView.et_delivery_address_pine.setText(addressModel.postal_code)
                rootView.et_delivery_address_house_no.setText(addressModel.house_no)
                rootView.et_delivery_address_house_ad_no.setText(addressModel.add_on_house_no)
                rootView.et_delivery_address_area.setText(addressModel.city)
                rootView.et_delivery_address_street.setText(addressModel.street_name)
            }

        }

        rootView.btn_delivery_address_save.setOnClickListener {
            attemptAddress()
        }

        return rootView
    }

    private fun attemptAddress() {

        val ic_error = CommonActivity.setErrorDrawable(contexts)

        var cancel = false
        var focusView: View? = null

        val pine_code = rootView.et_delivery_address_pine.text.toString()
        val house_no = rootView.et_delivery_address_house_no.text.toString()
        val adon_no = rootView.et_delivery_address_house_ad_no.text.toString()
        val city = rootView.et_delivery_address_area.text.toString()
        val street = rootView.et_delivery_address_street.text.toString()

        if (street.isEmpty()) {
            rootView.et_delivery_address_street.setError(
                contexts.resources.getString(R.string.error_field_required),
                ic_error
            )
            focusView = rootView.et_delivery_address_street
            cancel = true
        }

        if (city.isEmpty()) {
            rootView.et_delivery_address_area.setError(
                contexts.resources.getString(R.string.error_field_required),
                ic_error
            )
            focusView = rootView.et_delivery_address_area
            cancel = true
        }

        /*if (adon_no.isEmpty()) {
            rootView.et_delivery_address_house_ad_no.setError(
                contexts.resources.getString(R.string.error_field_required),
                ic_error
            )
            focusView = rootView.et_delivery_address_house_ad_no
            cancel = true
        }*/

        if (house_no.isEmpty()) {
            rootView.et_delivery_address_house_no.setError(
                contexts.resources.getString(R.string.error_field_required),
                ic_error
            )
            focusView = rootView.et_delivery_address_house_no
            cancel = true
        }

        if (pine_code.isEmpty()) {
            rootView.et_delivery_address_pine.setError(
                contexts.resources.getString(R.string.error_field_required),
                ic_error
            )
            focusView = rootView.et_delivery_address_pine
            cancel = true
        } else if (pine_code.length < 5) {
            rootView.et_delivery_address_pine.setError(
                contexts.resources.getString(R.string.error_invalid_postal_code),
                ic_error
            )
            focusView = rootView.et_delivery_address_pine
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            if (ConnectivityReceiver.isConnected) {
                makeUpdateAddress(pine_code, house_no, adon_no, city, street)
            } else {
                ConnectivityReceiver.showSnackbar(contexts)
            }
        }

    }

    private fun makeUpdateAddress(
        postal_code: String,
        house_no: String,
        add_on_house_no: String,
        city: String,
        street_name: String
    ) {
        val params = ArrayList<NameValuePair>()
        params.add(
            NameValuePair(
                "user_id",
                SessionManagement.UserData.getSession(contexts, BaseURL.KEY_ID)
            )
        )
        params.add(NameValuePair("postal_code", postal_code))
        params.add(NameValuePair("house_no", house_no))
        params.add(NameValuePair("add_on_house_no", add_on_house_no))
        params.add(NameValuePair("area", ""))
        params.add(NameValuePair("street_name", street_name))
        params.add(NameValuePair("city", city))
        params.add(NameValuePair("latitude", "0"))
        params.add(NameValuePair("longitude", "0"))

        val task = CommonAsyTask(
            BaseURL.POST, params,
            BaseURL.EDIT_ADDRESS_URL, object : CommonAsyTask.VJsonResponce {
                override fun VResponce(responce: String, message: String) {
                    CommonActivity.showToast(contexts, message)

                    SessionManagement.UserData.setSession(contexts, "addresses", "[$responce]")
                    if (onTimeSelected != null) {
                        onTimeSelected!!.onSelected(null)
                    }
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