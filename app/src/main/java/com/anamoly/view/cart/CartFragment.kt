package com.anamoly.view.cart

import Config.BaseURL
import Config.GlobleVariable
import Database.CartData
import Dialogs.CommonAlertDialog
import Interfaces.OnTimeSelected
import Models.*
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.util.Attributes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.response.TimeSlotResponse
import com.anamoly.view.cart.adapter.CartAdapter
import com.anamoly.view.cart.adapter.CartAdapter2
import com.anamoly.view.cart.dialog.BottomSheetDeliveryTimeDialog
import com.anamoly.view.checkout.CheckoutActivity
import com.anamoly.view.home.MainActivity
import com.anamoly.view.no_internet.NoInternetActivity
import com.anamoly.view.product_detail.ProductDetailActivity
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.include_toolbar.view.*
import utils.*
import java.io.Serializable
import java.lang.StringBuilder

/**
 * Created on 18-01-2020.
 */
class CartFragment : Fragment(), View.OnClickListener,
    SwipeToDeleteCallback.SwipeToDeleteCallbackListener {

    companion object {
        private val TAG = CartFragment::class.java.simpleName
    }

    val timeSlotModelList = ArrayList<TimeSlotModel>()
    var timeSlotModelSelected: TimeSlotModel? = null

    lateinit var cartData: CartData

    lateinit var cartAdapter: CartAdapter2

    var hasExpress = false
    var hasNonExpress = false

    val cartModelList = ArrayList<CartModel>()

    lateinit var cartViewModel: CartViewModel
    private var contexts: Context? = null
    lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_cart, container, false)

        cartData = CartData(contexts!!)

        rootView.toolbar_home.setBackgroundColor(CommonActivity.getHeaderColor())
        rootView.tv_toolbar_title.setTextColor(CommonActivity.getHeaderTextColor())
        CommonActivity.setImageTint(rootView.iv_toolbar_back)

        rootView.tv_toolbar_title.text = resources.getString(R.string.cart)
        rootView.iv_toolbar_delete.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_delete_white)
        }
        rootView.iv_toolbar_delete.setOnClickListener {
            //if (cartData.getProductList().size > 0) {
            if (CommonActivity.getCartTotalAmount(contexts!!) > 0) {
                val commonAlertDialog = CommonAlertDialog(
                    contexts!!,
                    resources.getString(R.string.cart_info),
                    resources.getString(R.string.are_you_sure_you_want_to_clear_cart),
                    null,
                    null,
                    object : CommonAlertDialog.OnClickListener {
                        override fun cancelClick() {

                        }

                        override fun okClick() {
                            SessionManagement.CommonData.setSession(
                                contexts!!,
                                "isRefreshHome",
                                true
                            )

                            val params = HashMap<String, String>()
                            params["user_id"] =
                                SessionManagement.UserData.getSession(contexts!!, BaseURL.KEY_ID)
                            cartViewModel.makeClearCart(params)
                                .observe(this@CartFragment, Observer { response: CommonResponse? ->
                                    if (response != null) {
                                        if (response.responce!!) {
                                            SessionManagement.UserData.setSession(
                                                context!!,
                                                "cartData",
                                                ""
                                            )
                                            cartData.deleteTable()
                                            rootView.rv_cart.apply {
                                                layoutManager = LinearLayoutManager(contexts!!)
                                                adapter = CartAdapter(
                                                    contexts!!,
                                                    cartData.getProductList()
                                                )
                                                CommonActivity.runLayoutAnimation(this)
                                            }
                                            updatePrice()
                                        } else {
                                            CommonActivity.showToast(contexts!!, response.message!!)
                                        }
                                    }
                                })
                        }
                    })
                commonAlertDialog.show()
            }
        }

        PushDownAnim.setPushDownAnimTo(rootView.btn_cart_continue)

        /*cartAdapter = CartAdapter(contexts!!, cartData.getProductList())
        (cartAdapter as CartAdapter).mode = Attributes.Mode.Single
        rootView.rv_cart.apply {
            layoutManager = LinearLayoutManager(contexts!!)
            adapter = cartAdapter
            CommonActivity.runLayoutAnimation(this)
        }*/

        cartModelList.clear()
        cartModelList.addAll(CommonActivity.getCartProductsList(contexts!!))

        cartAdapter = CartAdapter2(
            contexts!!,
            cartModelList,
            object : CartAdapter2.OnItemClick {
                override fun imageClick(
                    positionMain: Int,
                    position: Int,
                    view: View,
                    cartModel: CartModel,
                    cartItemModel: CartItemModel?
                ) {
                    Intent(context, ProductDetailActivity::class.java).apply {
                        putExtra("updateHome", true)
                        putExtra("positionMain", positionMain)
                        putExtra("position", position)
                        putExtra("cartItemData", cartItemModel as Serializable)
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as Activity,
                            view,
                            ViewCompat.getTransitionName(view)!!
                        )
                        startActivityForResult(this, 6879, options.toBundle())
                    }
                }

                override fun addClick(
                    positionMain: Int,
                    position: Int,
                    cartModel: CartModel,
                    cartItemModel: CartItemModel?
                ) {
                    SessionManagement.CommonData.setSession(contexts!!, "isRefreshHome", true)

                    val params = HashMap<String, String>()
                    params["user_id"] =
                        SessionManagement.UserData.getSession(contexts!!, BaseURL.KEY_ID)
                    params["product_id"] = cartItemModel?.product_id!!
                    params["qty"] = "1"
                    cartViewModel.makeAddCart(params)
                        .observe(this@CartFragment, Observer { response: CommonResponse? ->
                            if (response != null) {
                                if (response.responce!!) {
                                    SessionManagement.UserData.setSession(
                                        contexts!!,
                                        "cartData",
                                        response.data!!
                                    )
                                    /*cartItemModel.qty = cartItemModel.qty + 1
                                    cartModel.cartItemModelList?.set(position, cartItemModel)*/

                                    cartAdapter.modelList.set(
                                        positionMain,
                                        CommonActivity.getCartDetailByCartId(
                                            contexts!!,
                                            cartItemModel.cart_id!!
                                        )!!
                                    )
                                    cartAdapter.notifyItemChanged(positionMain)

                                    updatePrice()
                                } else {
                                    CommonActivity.showToast(contexts!!, response.message!!)
                                }
                            }
                        })
                }

                override fun removeClick(
                    positionMain: Int,
                    position: Int,
                    cartModel: CartModel,
                    cartItemModel: CartItemModel?
                ) {
                    SessionManagement.CommonData.setSession(contexts!!, "isRefreshHome", true)

                    val params = HashMap<String, String>()
                    params["user_id"] =
                        SessionManagement.UserData.getSession(contexts!!, BaseURL.KEY_ID)
                    //params["cart_id"] = cartItemModel?.cart_id!!
                    params["product_id"] = cartItemModel?.product_id!!
                    params["qty"] = "1"

                    cartViewModel.makeMinusCart(params)
                        .observe(this@CartFragment, Observer { response: CommonResponse? ->
                            if (response != null) {
                                if (response.responce!!) {
                                    SessionManagement.UserData.setSession(
                                        contexts!!,
                                        "cartData",
                                        response.data!!
                                    )

                                    val cartModelNew = CommonActivity.getCartDetailByCartId(
                                        contexts!!,
                                        cartItemModel.cart_id!!
                                    )

                                    if (cartModelNew != null) {
                                        /*cartItemModel.qty = cartItemModel.qty - 1
                                        cartModel.cartItemModelList?.set(position, cartItemModel)*/

                                        cartAdapter.modelList[positionMain] = cartModelNew
                                        cartAdapter.notifyItemChanged(positionMain)
                                    } else {
                                        cartAdapter.modelList.removeAt(positionMain)
                                        cartAdapter.notifyItemRemoved(positionMain)
                                        cartAdapter.notifyItemRangeChanged(
                                            0,
                                            cartAdapter.modelList.size
                                        )
                                    }
                                    updatePrice()
                                } else {
                                    CommonActivity.showToast(contexts!!, response.message!!)
                                }
                            }
                        })
                }

                override fun deleteClick(
                    positionMain: Int,
                    position: Int,
                    cartModel: CartModel,
                    cartItemModel: CartItemModel?
                ) {
                    SessionManagement.CommonData.setSession(contexts!!, "isRefreshHome", true)

                    val params = HashMap<String, String>()
                    params["user_id"] =
                        SessionManagement.UserData.getSession(contexts!!, BaseURL.KEY_ID)
                    val ids = StringBuilder()
                    for (cartItemModel2 in cartModel.cartItemModelList!!) {
                        ids.append(cartItemModel2.cart_id)
                        ids.append(",")
                    }
                    params["cart_id"] = ids.toString()

                    cartViewModel.makeDeleteCart(params)
                        .observe(this@CartFragment, Observer { response: CommonResponse? ->
                            if (response != null) {
                                if (response.responce!!) {
                                    SessionManagement.UserData.setSession(
                                        contexts!!,
                                        "cartData",
                                        response.data!!
                                    )
                                    updatePrice()
                                } else {
                                    CommonActivity.showToast(contexts!!, response.message!!)
                                }
                            }
                        })
                }
            })
        (cartAdapter as CartAdapter2).mode = Attributes.Mode.Single
        rootView.rv_cart.apply {
            layoutManager = LinearLayoutManager(contexts!!)
            adapter = cartAdapter
            CommonActivity.runLayoutAnimation(this)
        }

        val expressDeliveryCharge =
            SessionManagement.PermanentData.getSession(contexts!!, "express_delivery_charge")
        val expressDeliveryMinute =
            SessionManagement.PermanentData.getSession(contexts!!, "express_delivery_time")
        if (expressDeliveryCharge != "") {
            val expressCharge = CommonActivity.getPriceWithCurrency(
                contexts!!,
                String.format(GlobleVariable.LOCALE, "%.2f", expressDeliveryCharge.toDouble())
                    .replace(".00", "")
            )

            val hour = expressDeliveryMinute.toDouble() / 60
            val minute = expressDeliveryMinute.toInt()

            var displayTime = ""

            if (hour > 0) {
                val stringHour = String.format(GlobleVariable.LOCALE, "%.2f", hour)
                Log.d(TAG, "ExpressHour::$stringHour")
                val hourSplit = stringHour.split(".")
                if (hourSplit[0].toInt() > 0) {
                    displayTime = "${hourSplit[0]} ${resources.getString(R.string.hour)}"
                }
                if (hourSplit[1] != "00") {
                    displayTime =
                        if (hourSplit[1].toInt() > 1) {
                            "$displayTime ${hourSplit[1]} ${resources.getString(R.string.minutes)}"
                        } else {
                            "$displayTime ${hourSplit[1]} ${resources.getString(R.string.minute)}"
                        }
                }
            } else {
                displayTime = if (minute > 1) {
                    "$minute ${resources.getString(R.string.minutes)}"
                } else {
                    "$minute ${resources.getString(R.string.minute)}"
                }
            }

            rootView.tv_cart_express_detail.text =
                "${resources.getString(R.string.delivery_within)} $displayTime (${resources.getString(
                    R.string.extra
                )} $expressCharge)"
        }

        updatePrice()

        rootView.ll_cart_delivery_time.setOnClickListener(this)
        rootView.btn_cart_continue.setOnClickListener(this)

        cartViewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)

        cartViewModel.slideDownImidiat(rootView.btn_cart_continue)

        if (ConnectivityReceiver.isConnected) {
            val address = SessionManagement.UserData.getSession(contexts!!, "addresses")
            if (address.isNotEmpty() && address != "null") {
                val addressModelList = ArrayList<AddressModel>()

                val gson = Gson()
                val type = object : TypeToken<ArrayList<AddressModel>>() {}.type
                addressModelList.addAll(gson.fromJson<ArrayList<AddressModel>>(address, type))

                if (addressModelList.size > 0) {
                    val addressModel = addressModelList[0]

                    makeTimeList(addressModel.postal_code!!)
                }
            }
        } else {
            Intent(contexts!!, NoInternetActivity::class.java).apply {
                startActivityForResult(this, 9328)
            }
        }

        return rootView
    }

    private fun updatePrice() {
        val totalDiscount =
            CommonActivity.getCartDiscount(contexts!!)//cartData.getProductTotalPrice(false)
        val totalDiscountPrice =
            CommonActivity.getCartTotalAmount(contexts!!)//cartData.getProductTotalPrice(true)

        rootView.tv_cart_total_discount.setText(
            CommonActivity.getPriceWithCurrency(
                contexts!!,
                String.format(GlobleVariable.LOCALE, "%.2f", totalDiscount)
                    .replace(".00", "")
            )
        )
        rootView.tv_cart_total_price.setText(
            CommonActivity.getPriceWithCurrency(
                contexts!!, String.format(GlobleVariable.LOCALE, "%.2f", totalDiscountPrice)
                    .replace(".00", "")
            )
        )

        (contexts as MainActivity).updateCart()

        hasExpress = false
        hasNonExpress = false
        for (cartModel in cartAdapter.modelList) {
            for (cartItemModel in cartModel.cartItemModelList!!) {
                if (cartItemModel.is_express == "1") {
                    hasExpress = true
                } else {
                    hasNonExpress = true
                }
            }
        }

        if (hasExpress) {
            rootView.ll_cart_express.visibility = View.VISIBLE
            rootView.ll_cart_delivery_time.visibility = View.GONE
        } else {
            rootView.ll_cart_express.visibility = View.GONE
            rootView.ll_cart_delivery_time.visibility = View.VISIBLE
        }

        if (hasExpress && totalDiscountPrice > 0) {
            rootView.ll_cart_bottom.visibility = View.VISIBLE
            rootView.btn_cart_continue.isEnabled = true
        } else if ((timeSlotModelList.size > 0 && totalDiscountPrice > 0)) {
            rootView.ll_cart_bottom.visibility = View.VISIBLE
            rootView.btn_cart_continue.isEnabled = true
        } else {
            rootView.ll_cart_bottom.visibility = View.GONE
            rootView.btn_cart_continue.isEnabled = false
            if (::cartViewModel.isInitialized)
                cartViewModel.slideDown(rootView.btn_cart_continue)
        }

    }

    private fun updateTime() {
        if (timeSlotModelSelected != null) {
            val dateTime = "${CommonActivity.getConvertDate(
                timeSlotModelSelected!!.date!!,
                1
            )} ${CommonActivity.getConvertDate(
                timeSlotModelSelected!!.date!!,
                3
            )}, ${CommonActivity.getConvertTime(
                timeSlotModelSelected!!.from_time!!,
                2
            )} - ${CommonActivity.getConvertTime(timeSlotModelSelected!!.to_time!!, 2)}"


            rootView.tv_cart_delivery_date_time.text = dateTime
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ll_cart_delivery_time -> {
                val bottomSheetDeliveryTimeDialog =
                    BottomSheetDeliveryTimeDialog(object : OnTimeSelected {
                        override fun onSelected(timeSlotModel: TimeSlotModel?) {
                            timeSlotModelSelected = timeSlotModel
                            updateTime()

                        }
                    })
                bottomSheetDeliveryTimeDialog.contexts = contexts!!
                if (bottomSheetDeliveryTimeDialog.isVisible) {
                    bottomSheetDeliveryTimeDialog.dismiss()
                } else {
                    val args = Bundle()
                    args.putSerializable("timeSlotData", timeSlotModelList as Serializable)
                    if (timeSlotModelSelected != null) {
                        args.putSerializable(
                            "timeSlotSelected",
                            timeSlotModelSelected as Serializable
                        )
                    }
                    bottomSheetDeliveryTimeDialog.arguments = args
                    bottomSheetDeliveryTimeDialog.show(
                        fragmentManager!!,
                        bottomSheetDeliveryTimeDialog.tag
                    )
                }
            }
            R.id.btn_cart_continue -> {
                if (timeSlotModelSelected != null) {
                    if (hasExpress && hasNonExpress) {
                        CommonAlertDialog(
                            contexts!!,
                            resources.getString(R.string.attention_nthe_products_with_express_icon_are_immediately_available),
                            resources.getString(R.string.you_have_chosen_express_delivery_unfortunately_not_all_products_you_have_chosen_can_be_delivered_with_express_delivery_please_double_check_your_order_and_remove_non_express_products_from_your_cart_if_you_still_want_to_order_the_products_choose_another_time_for_delivery),
                            "",
                            "",
                            true,
                            false,
                            object : CommonAlertDialog.OnClickListener {
                                override fun cancelClick() {

                                }

                                override fun okClick() {

                                }
                            }).show()
                    } else {
                        Intent(contexts!!, CheckoutActivity::class.java).apply {
                            putExtra("timeSlotData", timeSlotModelSelected as Serializable)
                            putExtra("isExpress", hasExpress)
                            startActivity(this)
                        }
                    }
                }
            }
        }
    }

    private fun makeTimeList(postal_code: String) {
        val params = HashMap<String, String>()
        params["postal_code"] = postal_code

        cartViewModel.getTimeSlotList(params)
        cartViewModel.timeSlotResponseLiveData.observe(
            this,
            Observer { response: TimeSlotResponse? ->
                if (response != null) {
                    if (response.responce!!) {
                        timeSlotModelList.clear()
                        timeSlotModelList.addAll(response.timeSlotModelList!!)

                        if (timeSlotModelList.size > 0) {
                            timeSlotModelSelected = timeSlotModelList[0]
                            updateTime()

                            cartViewModel.slideUp(rootView.btn_cart_continue)
                        }
                        updatePrice()

                    } else {
                        CommonActivity.showToast(contexts!!, response.message!!)
                    }
                }
            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9328 && resultCode == Activity.RESULT_OK) {
            val address = SessionManagement.UserData.getSession(contexts!!, "addresses")
            if (address != "null") {
                val addressModelList = ArrayList<AddressModel>()

                val gson = Gson()
                val type = object : TypeToken<ArrayList<AddressModel>>() {}.type
                addressModelList.addAll(gson.fromJson<ArrayList<AddressModel>>(address, type))

                if (addressModelList.size > 0) {
                    val addressModel = addressModelList[0]

                    makeTimeList(addressModel.postal_code!!)
                }
            }
        } else if (requestCode == 6879 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.hasExtra("position")) {
                    val positionMain = data.getIntExtra("positionMain", 0)
                    val position = data.getIntExtra("position", 0)
                    cartModelList.clear()
                    cartModelList.addAll(CommonActivity.getCartProductsList(contexts!!))
                    cartAdapter.notifyDataSetChanged()
                    updatePrice()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // unregister reciver
        contexts!!.unregisterReceiver(mUpdate)
    }

    override fun onResume() {
        super.onResume()
        // register reciver
        contexts!!.registerReceiver(mUpdate, IntentFilter("cartUpdate"))

    }

    // broadcast reciver for receive data
    private val mUpdate = object : BroadcastReceiver() {
        override fun onReceive(context: Context, data: Intent) {
            val type = data.getStringExtra("type")!!

            if (type.contentEquals("update")) {
                updatePrice()
            }
        }
    }

    override fun onAttach(context: Context) {
        this.contexts = context
        super.onAttach(context)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        if (viewHolder is CartAdapter.MyViewHolder) {
            cartAdapter.removeItem(viewHolder.adapterPosition)
        }
    }

}