package com.anamoly.view.order

import Config.BaseURL
import Config.GlobleVariable
import Database.CartData
import Dialogs.CommonAlertDialog
import Dialogs.LoaderDialog
import Models.OrderModel
import Models.ProductModel
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.anamoly.AppController
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.response.OrderDetailResponse
import com.anamoly.response.ProductResponse
import com.anamoly.view.order.adapter.OrderItemAdapter
import com.anamoly.view.order_tracking.TrackOrderActivity
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.include_loader.*
import utils.ConnectivityReceiver
import utils.SessionManagement
import java.io.Serializable


class OrderDetailActivity : CommonActivity(), View.OnClickListener {

    var orderModel: OrderModel? = null
    lateinit var orderViewModel: OrderViewModel
    lateinit var cartData: CartData

    var isUp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        setContentView(R.layout.activity_order_detail)
        setHeaderTitle(resources.getString(R.string.order_details))

        cartData = CartData(this)
        if (intent.hasExtra("orderData")) {
            orderModel = intent.getSerializableExtra("orderData") as OrderModel
            updateView()
        }

        PushDownAnim.setPushDownAnimTo(btn_order_detail_re_Order)

        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius =
            CommonActivity.dpToPx(AppController.instance?.applicationContext!!, 5F).toFloat()
        gradientDrawable.setColor(AppController.infoBoxBg)

        ll_order_detail_top.background = gradientDrawable
        cv_order_detail_bottom.setCardBackgroundColor(AppController.infoBoxBg)

        rv_order_item.layoutManager = LinearLayoutManager(this)

        if (orderModel?.status == "2") {
            btn_order_detail_track.visibility = View.VISIBLE
        } else {
            btn_order_detail_track.visibility = View.GONE
        }

        btn_order_detail_track.setOnClickListener(this)
        btn_order_detail_re_Order.setOnClickListener(this)

        if (ConnectivityReceiver.isConnected) {
            if (orderModel != null) {
                makeOrderDetail(orderModel!!.order_id!!)
            } else {
                makeOrderDetail(intent.getStringExtra("order_id")!!)
            }
        } else {
            ConnectivityReceiver.showSnackbar(this)
        }

        nsv_order_detail.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                if (isUp) {
                    orderViewModel.slideDown(ll_order_detail_bottom)
                    isUp = false
                }
            } else {
                if (!isUp) {
                    orderViewModel.slideUp(ll_order_detail_bottom)
                    isUp = true
                }
            }
        })

        /*rv_order_item.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0
                    && ll_order_detail_bottom.visibility == View.VISIBLE
                ) {
                    ll_order_detail_bottom.visibility = View.GONE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ll_order_detail_bottom.visibility = View.VISIBLE
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })*/

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_order_detail_track -> {
                Intent(this, TrackOrderActivity::class.java).apply {
                    putExtra("orderData", orderModel as Serializable)
                    startActivity(this)
                }
            }
            R.id.btn_order_detail_re_Order -> {
/*if (orderModel!!.orderItemModelList != null) {
            var ids = ""
            for (productItem in orderModel!!.orderItemModelList!!) {
                ids += ",${productItem.product_id}"
            }
            if (ids.isNotEmpty()) {*/
                val commonAlertDialog = CommonAlertDialog(this,
                    resources.getString(R.string.are_you_sure_you_want_to_clear_cart),
                    resources.getString(R.string.if_you_re_order_your_product_than_old_cart_data_is_cleared_and_re_order_data_will_added),
                    null,
                    null,
                    object : CommonAlertDialog.OnClickListener {
                        override fun cancelClick() {

                        }

                        override fun okClick() {
                            //makeGetProductListByIds(ids)
                            makeReOrderCart()
                        }
                    })
                //if (cartData.getProductList().size > 0) {
                if (CommonActivity.getCartTotalAmount(this) > 0) {
                    commonAlertDialog.show()
                } else {
                    makeReOrderCart()
                    //makeGetProductListByIds(ids)
                }
                /*}
            }*/
            }
        }
    }

    private fun updateView() {
        val dateTime = "${
            CommonActivity.getConvertDate(
                orderModel!!.order_date!!,
                9
            )
        } ${
            CommonActivity.getConvertDate(
                orderModel!!.order_date!!,
                1
            )
        }, ${
            CommonActivity.getConvertDate(
                orderModel!!.order_date!!,
                3
            )
        } ${
            CommonActivity.getConvertDate(
                orderModel!!.order_date!!,
                4
            )
        }"

        tv_order_detail_total_title.text = if (orderModel?.paid_by == "ideal") {
            resources.getString(R.string.total_paid)
        } else {
            resources.getString(R.string.total_amount)
        }

        tv_order_detail_date.text = dateTime
        tv_order_detail_price.setText(
            CommonActivity.getPriceWithCurrency(
                this,
                String.format(GlobleVariable.LOCALE, "%.2f", orderModel!!.net_amount!!.toDouble())
                    .replace(".00", "")
            )
        )
        tv_order_detail_total_item.text = orderModel!!.total_qty
        tv_order_detail_total_price_main.setText(
            CommonActivity.getPriceWithCurrency(
                this,
                String.format(GlobleVariable.LOCALE, "%.2f", orderModel!!.order_amount!!.toDouble())
                    .replace(".00", "")
            )
        )
        tv_order_detail_order_discount.setText(
            "-${
                CommonActivity.getPriceWithCurrency(
                    this,
                    String.format(
                        GlobleVariable.LOCALE,
                        "%.2f",
                        orderModel!!.discount_amount!!.toDouble()
                    )
                        .replace(".00", "")
                )
            }"
        )
        tv_order_detail_delivery_charge.setText(
            CommonActivity.getPriceWithCurrency(
                this,
                String.format(
                    GlobleVariable.LOCALE,
                    "%.2f",
                    orderModel!!.delivery_amount!!.toDouble()
                )
                    .replace(".00", "")
            )
        )
        tv_order_detail_gateway_charge.setText(
            CommonActivity.getPriceWithCurrency(
                this,
                String.format(
                    GlobleVariable.LOCALE,
                    "%.2f",
                    orderModel!!.gateway_charges!!.toDouble()
                )
                    .replace(".00", "")
            )
        )
        tv_order_detail_total_price.setText(
            CommonActivity.getPriceWithCurrency(
                this,
                String.format(GlobleVariable.LOCALE, "%.2f", orderModel!!.net_amount!!.toDouble())
                    .replace(".00", "")
            )
        )

        if (orderModel!!.gateway_charges!!.toDouble() > 0) {
            ll_order_detail_gateway.visibility = View.VISIBLE
        } else {
            ll_order_detail_gateway.visibility = View.GONE
        }

        if (orderModel!!.delivery_amount!!.toDouble() > 0) {
            ll_order_detail_delivery.visibility = View.VISIBLE
        } else {
            ll_order_detail_delivery.visibility = View.GONE
        }

    }

    private fun makeOrderDetail(order_id: String) {

        include_ll.visibility = View.VISIBLE
        rv_order_item.visibility = View.GONE

        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
        params["order_id"] = order_id

        orderViewModel.getMyOrderDetail(params)
        orderViewModel.orderDetailResponseLiveData.observe(
            this,
            Observer { response: OrderDetailResponse? ->
                if (response != null) {
                    include_ll.visibility = View.GONE
                    rv_order_item.visibility = View.VISIBLE
                    if (response.responce!!) {
                        orderModel = response.orderModel!!

                        updateView()

                        if (orderModel!!.orderItemModelList != null
                            && orderModel!!.orderItemModelList!!.size > 0
                        ) {
                            rv_order_item.apply {
                                adapter = OrderItemAdapter(
                                    this@OrderDetailActivity,
                                    orderModel!!.orderItemModelList!!
                                )
                                CommonActivity.runLayoutAnimation(this)
                            }
                        }
                    } else {
                        showToast(this@OrderDetailActivity, response.message!!)
                    }
                }
            })

    }

    private fun makeGetProductListByIds(ids: String) {
        val params = HashMap<String, String>()
        params["ids"] = ids

        val loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        orderViewModel.getProductListByIds(params)
        orderViewModel.productResponseLiveData.observe(
            this,
            Observer { response: ProductResponse? ->
                loaderDialog.dismiss()
                if (response != null) {
                    if (response.responce!!) {

                        val productModelList = ArrayList<ProductModel>()

                        for (productModel in response.productModelList!!) {
                            val oldProduct = orderViewModel.getOrderProductById(
                                orderModel!!.orderItemModelList!!,
                                productModel.product_id!!
                            )
                            if (oldProduct != null) {
                                productModel.qty_cart = oldProduct.order_qty!!.toInt()
                                productModelList.add(productModel)
                            }
                        }

                        if (productModelList.size > 0) {
                            cartData.deleteTable()
                            cartData.setProductList(productModelList)
                            showToast(
                                this@OrderDetailActivity,
                                resources.getString(R.string.re_order_done)
                            )
                            Intent().apply {
                                setResult(Activity.RESULT_OK, this)
                                finish()
                            }
                        }
                    } else {
                        showToast(this@OrderDetailActivity, response.message!!)
                    }
                }
            })

    }

    private fun makeReOrderCart() {
        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
        params["order_id"] = orderModel?.order_id!!

        SessionManagement.UserData.setSession(
            this@OrderDetailActivity,
            "cartData",
            ""
        )

        val loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        orderViewModel.makeGetReorderCart(params)
            .observe(this, Observer { response: CommonResponse? ->
                loaderDialog.dismiss()
                if (response != null) {
                    if (response.responce!!) {
                        SessionManagement.UserData.setSession(
                            this@OrderDetailActivity,
                            "cartData",
                            response.data!!
                        )
                        SessionManagement.CommonData.setSession(
                            this@OrderDetailActivity,
                            "isRefreshHome",
                            true
                        )
                        showToast(
                            this@OrderDetailActivity,
                            resources.getString(R.string.re_order_done)
                        )
                        Intent().apply {
                            setResult(Activity.RESULT_OK, this)
                            finish()
                        }
                    } else {
                        showToast(this@OrderDetailActivity, response.message!!)
                    }
                }
            })

    }

}
