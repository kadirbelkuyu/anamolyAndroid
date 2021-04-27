package com.anamoly.view.checkout

import Config.BaseURL
import Config.GlobleVariable
import Database.CartData
import Dialogs.CommonAlertDialog
import Dialogs.LoaderDialog
import Interfaces.OnCouponSelected
import Interfaces.OnTimeSelected
import Models.AddressModel
import Models.CouponModel
import Models.TimeSlotModel
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anamoly.AppController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.response.OrderDetailResponse
import com.anamoly.view.checkout.dialog.BottomSheetCheckoutAddressDialog
import com.anamoly.view.checkout.dialog.BottomSheetCheckoutCouponDialog
import com.anamoly.view.payment.PaymentActivity
import com.anamoly.view.thanks.ThanksActivity
import kotlinx.android.synthetic.main.activity_checkout.*
import org.json.JSONArray
import org.json.JSONObject
import utils.CommonAsyTask
import utils.ConnectivityReceiver
import utils.NameValuePair
import utils.SessionManagement

class CheckoutActivity : CommonActivity(), View.OnClickListener {

    companion object {
        val TAG = CheckoutActivity::class.java.simpleName
    }

    var timeSlotModel: TimeSlotModel? = null
    //lateinit var cartData: CartData

    var expressCharge = 0.0

    var couponModel: CouponModel? = null

    lateinit var checkoutViewModel: CheckoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkoutViewModel = ViewModelProviders.of(this).get(CheckoutViewModel::class.java)
        setContentView(R.layout.activity_checkout)
        setHeaderTitle(resources.getString(R.string.checkout))

        //cartData = CartData(this)

        if (intent.hasExtra("timeSlotData")) {
            timeSlotModel = intent.getSerializableExtra("timeSlotData") as TimeSlotModel
        }

        checkoutViewModel.checkPaymentIntent(intent)

        updateAddress()

        val totalMainPrice =
            CommonActivity.getCartTotalAmount(this)//cartData.getProductTotalPrice(false)
        val totalDiscountPrice =
            CommonActivity.getCartNetAmount(this)//cartData.getProductTotalPrice(true)
        if (intent.getBooleanExtra("isExpress", false)) {
            val expressDeliveryCharge =
                SessionManagement.PermanentData.getSession(this, "express_delivery_charge")
            expressCharge = expressDeliveryCharge.toDouble()
        } else {
            var hasExpress = false
            var hasNonExpress = false
            for (cartModel in CommonActivity.getCartProductsList(this)) {
                for (cartItemModel in cartModel.cartItemModelList!!) {
                    if (cartItemModel.is_express == "1") {
                        hasExpress = true
                    } else {
                        hasNonExpress = true
                    }
                }
            }
            if (hasExpress) {
                val expressDeliveryCharge =
                    SessionManagement.PermanentData.getSession(this, "express_delivery_charge")
                expressCharge = expressDeliveryCharge.toDouble()
            }
        }

        tv_checkout_total_price_main.text =
            CommonActivity.getPriceWithCurrency(
                this,
                String.format(GlobleVariable.LOCALE, "%.2f", (totalMainPrice - totalDiscountPrice))
                    .replace(".00", "")
            )
        tv_checkout_total_price_discount.setText(
            CommonActivity.getPriceWithCurrency(
                this,
                String.format(GlobleVariable.LOCALE, "%.2f", (totalDiscountPrice + expressCharge))
                    .replace(".00", "")
            )
        )

        /*if ((totalMainPrice - totalDiscountPrice) > 0) {
            tv_checkout_total_price_main.visibility = View.VISIBLE
        }*/

        ll_checkout_coupon_code.visibility = View.GONE

        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = CommonActivity.dpToPx(this, 5F).toFloat()
        gradientDrawable.setColor(AppController.secondButtonColor)

        val gradientDrawableNormal = GradientDrawable()
        gradientDrawableNormal.cornerRadius = CommonActivity.dpToPx(this, 5F).toFloat()
        gradientDrawableNormal.setColor(ContextCompat.getColor(this, R.color.colorWhite))
        gradientDrawableNormal.setStroke(
            CommonActivity.dpToPx(this, 1F),
            ContextCompat.getColor(this, R.color.colorTextHint)
        )

        val res = StateListDrawable()
        res.addState(intArrayOf(android.R.attr.state_selected), gradientDrawable)
        res.addState(intArrayOf(), gradientDrawableNormal)

        ll_checkout_ideal.background = res
        ll_checkout_cash.background = res

        ll_checkout_ideal.visibility = View.GONE
        ll_checkout_cash.visibility = View.GONE

        var isIdealEnable = false
        var isCashEnable = false

        if (SessionManagement.PermanentData.getSession(this, "enable_ideal_payment") == "yes") {
            isIdealEnable = true
            ll_checkout_ideal.visibility = View.VISIBLE
        }
        if (SessionManagement.PermanentData.getSession(this, "enable_code_payment") == "yes") {
            isCashEnable = true
            ll_checkout_cash.visibility = View.VISIBLE
        }

        if (isIdealEnable && isCashEnable) {
            ll_checkout_ideal.isSelected = true
            tv_checkout_ideal.isSelected = true
        } else if (isIdealEnable) {
            ll_checkout_ideal.isSelected = true
            tv_checkout_ideal.isSelected = true
        } else if (isCashEnable) {
            ll_checkout_cash.isSelected = true
            tv_checkout_cash.isSelected = true
        }

        iv_checkout_edit_address.setOnClickListener(this)
        ll_checkout_coupon.setOnClickListener(this)
        ll_checkout_ideal.setOnClickListener(this)
        ll_checkout_cash.setOnClickListener(this)
        iv_checkout_coupon_delete.setOnClickListener(this)
        btn_checkout_continue.setOnClickListener(this)

        checkoutViewModel.paymentStatusLiveData.observe(this, Observer { status ->
            if (status) {
                checkoutViewModel.paymentOrderIDLiveData.observe(this, Observer { orderID ->
                    if (orderID.isNotEmpty()) {
                        if (ConnectivityReceiver.isConnected) {
                            makeOrderDetail(orderID)
                        }
                    }
                })
            } else {
                CommonAlertDialog(
                    this,
                    resources.getString(R.string.did_not_succeed),
                    resources.getString(R.string.please_try_again),
                    null,
                    null,
                    hideCancel = true,
                    hideOk = false,
                    onClickListener = object : CommonAlertDialog.OnClickListener {
                        override fun cancelClick() {

                        }

                        override fun okClick() {

                        }
                    }).show()
            }
        })

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_checkout_edit_address -> {
                val bottomSheetCheckoutAddressDialog = BottomSheetCheckoutAddressDialog(object :
                    OnTimeSelected {
                    override fun onSelected(timeSlotModel: TimeSlotModel?) {
                        updateAddress()
                    }
                })
                bottomSheetCheckoutAddressDialog.contexts = this
                if (bottomSheetCheckoutAddressDialog.isVisible) {
                    bottomSheetCheckoutAddressDialog.dismiss()
                } else {
                    val args = Bundle()
                    bottomSheetCheckoutAddressDialog.arguments = args
                    bottomSheetCheckoutAddressDialog.show(
                        supportFragmentManager,
                        bottomSheetCheckoutAddressDialog.tag
                    )
                }
            }
            R.id.ll_checkout_coupon -> {
                val bottomSheetCheckoutCouponDialog =
                    BottomSheetCheckoutCouponDialog(object : OnCouponSelected {
                        override fun onSelected(couponModel: CouponModel) {
                            this@CheckoutActivity.couponModel = couponModel
                            updatePrice()
                        }
                    })
                bottomSheetCheckoutCouponDialog.contexts = this
                if (bottomSheetCheckoutCouponDialog.isVisible) {
                    bottomSheetCheckoutCouponDialog.dismiss()
                } else {
                    val args = Bundle()
                    bottomSheetCheckoutCouponDialog.arguments = args
                    bottomSheetCheckoutCouponDialog.show(
                        supportFragmentManager,
                        bottomSheetCheckoutCouponDialog.tag
                    )
                }
            }
            R.id.ll_checkout_ideal -> {
                ll_checkout_ideal.isSelected = true
                tv_checkout_ideal.isSelected = true
                ll_checkout_cash.isSelected = false
                tv_checkout_cash.isSelected = false
            }
            R.id.ll_checkout_cash -> {
                ll_checkout_ideal.isSelected = false
                tv_checkout_ideal.isSelected = false
                ll_checkout_cash.isSelected = true
                tv_checkout_cash.isSelected = true
            }
            R.id.iv_checkout_coupon_delete -> {
                couponModel = null
                updatePrice()
            }
            R.id.btn_checkout_continue -> {
                attemptCheckOut()
            }
        }
    }

    private fun updatePrice() {
        var totalDiscountedPrice =
            CommonActivity.getCartNetAmount(this)//cartData.getProductTotalPrice(true)
        totalDiscountedPrice += expressCharge

        if (couponModel != null) {
            ll_checkout_coupon_code.visibility = View.VISIBLE
            ll_checkout_coupon_discount.visibility = View.VISIBLE
            view_checkout_coupon.visibility = View.VISIBLE

            val discountTitle = "${resources.getString(R.string.discount)} ${
                if (couponModel!!.discount_type!! == "percentage") "${couponModel?.discount}%" else CommonActivity.getPriceWithCurrency(
                    this@CheckoutActivity,
                    couponModel?.discount
                )
            }"

            val couponDeductAmount = couponModel!!.deduct_amount!!.toDouble()

            tv_checkout_coupon_title.text = resources.getString(R.string.coupon)
            tv_checkout_coupon_code.text = couponModel?.coupon_code

            tv_checkout_coupon_discount.text = discountTitle
            tv_checkout_coupon_discount_title.text = discountTitle
            tv_checkout_coupon_discount_amount.setText(
                CommonActivity.getPriceWithCurrency(
                    this@CheckoutActivity, String.format(
                        GlobleVariable.LOCALE,
                        "%.2f",
                        couponDeductAmount
                    )
                        .replace(".00", "")
                )
            )

            tv_checkout_total_price_discount.setText(
                CommonActivity.getPriceWithCurrency(
                    this,
                    String.format(
                        GlobleVariable.LOCALE,
                        "%.2f",
                        (totalDiscountedPrice - couponDeductAmount)
                    )
                        .replace(".00", "")
                )
            )
        } else {
            ll_checkout_coupon_code.visibility = View.GONE
            ll_checkout_coupon_discount.visibility = View.GONE
            view_checkout_coupon.visibility = View.GONE

            tv_checkout_total_price_discount.setText(
                CommonActivity.getPriceWithCurrency(
                    this,
                    String.format(
                        GlobleVariable.LOCALE,
                        "%.2f",
                        totalDiscountedPrice
                    )
                        .replace(".00", "")
                )
            )
        }
    }

    private fun updateAddress() {
        val address = SessionManagement.UserData.getSession(this, "addresses")
        if (address != "null") {
            val addressModelList = ArrayList<AddressModel>()

            val gson = Gson()
            val type = object : TypeToken<ArrayList<AddressModel>>() {}.type
            addressModelList.addAll(gson.fromJson<ArrayList<AddressModel>>(address, type))

            if (addressModelList.size > 0) {
                val addressModel = addressModelList[0]

                val fullAddress =
                    "${addressModel.postal_code}, ${addressModel.house_no}, ${addressModel.add_on_house_no}, ${addressModel.city}, ${addressModel.street_name}"

                tv_checkout_address.text = fullAddress
            }
        }
    }

    private fun attemptCheckOut() {
        val jsonArray = JSONArray()
        /*for (productModel in cartData.getProductList()) {
            val jsonObject = JSONObject()
            jsonObject.put("product_id", productModel.product_id)
            jsonObject.put("order_qty", productModel.qty_cart)
            jsonArray.put(jsonObject)
        }*/

        //if (jsonArray.length() > 0) {
        val address = SessionManagement.UserData.getSession(this, "addresses")
        if (address != "null") {
            val addressModelList = ArrayList<AddressModel>()

            val gson = Gson()
            val type = object : TypeToken<ArrayList<AddressModel>>() {}.type
            addressModelList.addAll(gson.fromJson<ArrayList<AddressModel>>(address, type))

            if (addressModelList.size > 0) {
                val addressModel = addressModelList[0]

                if (ConnectivityReceiver.isConnected) {
                    //checkAddress(addressModel, jsonArray.toString())
                    makeSendOrder(
                        addressModel.postal_code!!,
                        addressModel.house_no!!,
                        addressModel.add_on_house_no!!,
                        addressModel.street_name!!,
                        addressModel.city!!,
                        addressModel.area!!,
                        jsonArray.toString()
                    )
                } else {
                    ConnectivityReceiver.showSnackbar(this)
                }
            }
        }
        // }

    }

    private fun checkAddress(addressModel: AddressModel, jsonArray: String) {

        val loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        val params = HashMap<String, String>()
        params["postal_code"] = addressModel.postal_code!!
        params["house_no"] = addressModel.house_no!!

        checkoutViewModel.checkValidateAddress(params)
            .observe(this, Observer { response: CommonResponse? ->
                loaderDialog.dismiss()
                if (response != null) {
                    if (response.responce!!) {
                        if (ConnectivityReceiver.isConnected) {
                            makeSendOrder(
                                addressModel.postal_code,
                                addressModel.house_no,
                                addressModel.add_on_house_no!!,
                                addressModel.street_name!!,
                                addressModel.city!!,
                                addressModel.area!!,
                                jsonArray
                            )
                        }
                    } else {
                        CommonActivity.showToast(this, response.message!!)
                    }
                }
            })

    }

    private fun makeSendOrder(
        postal_code: String,
        house_no: String,
        add_on_house_no: String,
        street_name: String,
        city: String,
        area: String,
        order_items: String
    ) {
        val params = ArrayList<NameValuePair>()
        params.add(
            NameValuePair(
                "user_id",
                SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
            )
        )
        params.add(NameValuePair("delivery_date", timeSlotModel?.date!!))
        params.add(
            NameValuePair(
                "delivery_time",
                "${timeSlotModel?.from_time!!}-${timeSlotModel?.to_time!!}"
            )
        )
        params.add(NameValuePair("postal_code", postal_code))
        params.add(NameValuePair("house_no", house_no))
        params.add(NameValuePair("add_on_house_no", add_on_house_no))
        params.add(NameValuePair("street_name", street_name))
        params.add(NameValuePair("city", city))
        params.add(NameValuePair("area", area))
        params.add(NameValuePair("latitude", "0"))
        params.add(NameValuePair("longitude", "0"))
        params.add(
            NameValuePair(
                "coupon_code",
                if (couponModel != null) couponModel!!.coupon_code!! else ""
            )
        )
        params.add(NameValuePair("paid_by", if (ll_checkout_ideal.isSelected) "ideal" else "cod"))
        params.add(
            NameValuePair(
                "is_express",
                intent.getBooleanExtra("isExpress", false).toString()
            )
        )
        params.add(NameValuePair("order_note", ""))
        //params.add(NameValuePair("order_items", order_items))

        val task = CommonAsyTask(
            BaseURL.POST, params,
            BaseURL.SEND_ORDER_URL, object : CommonAsyTask.VJsonResponce {
                override fun VResponce(responce: String, message: String) {

                    if (ll_checkout_ideal.isSelected) {
                        val jsonObject = JSONObject(responce)
                        if (jsonObject.has("responseURL")) {
                            /*Intent(this@CheckoutActivity, PaymentActivity::class.java).apply {
                                putExtra("paymentUrl", jsonObject.getString("responseURL"))
                                startActivity(this)
                            }*/
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(jsonObject.getString("responseURL"))
                            )
                            startActivity(intent)
                        }
                    } else {
                        //cartData.deleteTable()
                        SessionManagement.UserData.setSession(
                            this@CheckoutActivity,
                            "cartData",
                            ""
                        )

                        Intent(this@CheckoutActivity, ThanksActivity::class.java).apply {
                            putExtra("orderData", responce)
                            startActivity(this)
                        }
                    }
                }

                override fun VError(responce: String, code: String) {
                    showToast(this@CheckoutActivity, responce)
                }
            }, BaseURL.PROGRESSDIALOG, this
        )
        task.execute()
    }

    private fun makeOrderDetail(order_id: String) {

        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
        params["order_id"] = order_id

        val loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        checkoutViewModel.getMyOrderDetail(params).observe(
            this,
            Observer { response: OrderDetailResponse? ->
                loaderDialog.dismiss()
                if (response != null) {
                    if (response.responce!!) {
                        if (response.orderModel != null) {
                            //cartData.deleteTable()
                            SessionManagement.UserData.setSession(
                                this@CheckoutActivity,
                                "cartData",
                                ""
                            )
                            val gson = Gson()
                            val orderData = gson.toJsonTree(response.orderModel).asJsonObject
                            Intent(this@CheckoutActivity, ThanksActivity::class.java).apply {
                                putExtra("orderData", orderData.toString())
                                startActivity(this)
                            }
                        }
                    } else {
                        showToast(this@CheckoutActivity, response.message!!)
                    }
                }
            })

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkoutViewModel.checkPaymentIntent(intent)
    }

}
