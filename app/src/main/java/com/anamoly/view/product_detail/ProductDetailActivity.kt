package com.anamoly.view.product_detail

import Config.BaseURL
import Config.GlobleVariable
import Database.CartData
import Models.CartItemModel
import Models.ProductModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.anamoly.AppController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayoutSpringBehavior
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.no_internet.NoInternetActivity
import com.anamoly.view.product_detail.adapter.ProductTagAdapter
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.content_product_detail.*
import kotlinx.android.synthetic.main.custom_actionbar.view.*
import utils.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ProductDetailActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        val TAG: String = ProductDetailActivity::class.java.simpleName
    }

    lateinit var productModel: ProductModel
    var cartItemModel: CartItemModel? = null

    lateinit var cartData: CartData

    lateinit var productDetailViewModel: ProductDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_product_detail)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var flags = cdl_product_detail.systemUiVisibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            cdl_product_detail.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }

        toolbar.title = ""
        setSupportActionBar(toolbar)

        val appBarLayoutSpringBehavior: AppBarLayoutSpringBehavior =
            (app_bar.layoutParams as CoordinatorLayout.LayoutParams).behavior as AppBarLayoutSpringBehavior
        appBarLayoutSpringBehavior.allowFullTouchZoom(false)
        appBarLayoutSpringBehavior.setPullSpeed(0)

        productDetailViewModel = ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)

        cartData = CartData(this)

        if (intent.hasExtra("productData")) {
            productModel = intent.getSerializableExtra("productData") as ProductModel
        } else if (intent.hasExtra("cartItemData")) {
            cartItemModel = intent.getSerializableExtra("cartItemData") as CartItemModel

            val gson = Gson()
            val gsonBuilder = GsonBuilder().create()
            val typeProductModel = object : TypeToken<ProductModel>() {}.type
            productModel = gson.fromJson<ProductModel>(
                gsonBuilder.toJsonTree(cartItemModel).asJsonObject, typeProductModel
            )

            productModel.cart_id = cartItemModel?.cart_id!!.toInt()

        }/*else if (intent.hasExtra("productComboModel")) {
            val productComboModel =
                intent.getSerializableExtra("productComboModel") as ProductComboModel

            val gson = Gson()
            val gsonBuilder = GsonBuilder().create()
            val typeProductModel = object : TypeToken<ProductModel>() {}.type
            productModel = gson.fromJson<ProductModel>(
                gsonBuilder.toJsonTree(productComboModel).asJsonObject, typeProductModel
            )
        }*/

        /*if (productModel.offer_type == null) {
            productModel.cart_id = cartData.getProductDetail(productModel.product_id!!)!!.cart_id
        } */
        /*else {
            val productModelList = cartData.getProductListByOfferDetail(productModel.product_offer_id!!,productModel.offer_type!!)
            productModel.cart_id = productModelList[productModelList.size-1].cart_id

            for(productmodel in productModelList){
                for (productComboModel in productmodel.productComboModelList!!){
                    if (productComboModel.product_id == productModel.product_id){

                    }
                }
            }

            productModel.productComboModelList = productModelList[productModelList.size-1].productComboModelList
        }*/

        setHeaderTitle(
            CommonActivity.getStringByLanguage(
                this,
                productModel.product_name_en,
                productModel.product_name_ar,
                productModel.product_name_nl,
                productModel.product_name_tr,
                productModel.product_name_de
            )!!
        )

        val webviewSetting = wv_product_detail.settings
        webviewSetting.javaScriptEnabled = true
        wv_product_detail.webViewClient = WebViewClient()

        Glide.with(this)
            .load(BaseURL.IMG_PRODUCT_URL + productModel.product_image)
            .placeholder(R.drawable.ic_place_holder)
            .error(R.drawable.ic_place_holder)
            .into(iv_product_detail_img)

        tv_product_detail_title.text =
            CommonActivity.getStringByLanguage(
                this,
                productModel.product_name_en,
                productModel.product_name_ar,
                productModel.product_name_nl,
                productModel.product_name_tr,
                productModel.product_name_de
            )
        tv_product_detail_unit.text =
            "${productModel.unit_value} ${
                CommonActivity.getStringByLanguage(
                    this,
                    productModel.unit,
                    productModel.unit_ar,
                    productModel.unit_en,
                    productModel.unit_tr,
                    productModel.unit_de
                )
            }"

        tv_product_detail_price_main.setText(
            CommonActivity.getPriceWithCurrency(
                this, String.format(
                    GlobleVariable.LOCALE,
                    "%.2f",
                    productModel.price!!.toDouble()
                ).replace(".00", "")
            )
        )
        tv_product_detail_price_discount.setText(
            CommonActivity.getPriceWithCurrency(
                this, String.format(
                    GlobleVariable.LOCALE,
                    "%.2f",
                    productModel.price!!.toDouble()
                ).replace(".00", "")
            )
        )

        if (!productModel.discount.isNullOrEmpty()
            && productModel.discount!!.toDouble() > 0
        ) {
            if (productModel.discount_type.equals("flat")) {
                cl_product_detail_price_main.visibility = View.VISIBLE
                tv_product_detail_discount.visibility = View.VISIBLE

                tv_product_detail_discount.text =
                    "${productModel.discount} ${resources.getString(R.string.flat)}"

                tv_product_detail_price_discount.setText(
                    CommonActivity.getPriceWithCurrency(
                        this, String.format(
                            GlobleVariable.LOCALE,
                            "%.2f",
                            (productModel.price!!.toDouble() - productModel.discount!!.toDouble())
                        ).replace(".00", "")
                    )
                )

            } else if (productModel.discount_type.equals("percentage")) {
                cl_product_detail_price_main.visibility = View.VISIBLE
                tv_product_detail_discount.visibility = View.VISIBLE

                tv_product_detail_discount.text =
                    "${productModel.discount}% ${resources.getString(R.string.Discount)}"

                tv_product_detail_price_discount.setText(
                    CommonActivity.getPriceWithCurrency(
                        this, String.format(
                            GlobleVariable.LOCALE,
                            "%.2f",
                            CommonActivity.getDiscountPrice(
                                productModel.discount!!,
                                productModel.price!!,
                                true,
                                true
                            )
                        ).replace(".00", "")
                    )
                )

            } else {
                cl_product_detail_price_main.visibility = View.GONE
                tv_product_detail_discount.visibility = View.GONE
            }
        } else {
            cl_product_detail_price_main.visibility = View.GONE
            tv_product_detail_discount.visibility = View.GONE
        }

        toolbar_layout.setOnClickListener {
            Intent(this@ProductDetailActivity, ZoomImageActivity::class.java).apply {
                putExtra("imagePath", BaseURL.IMG_PRODUCT_URL + productModel.product_image)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@ProductDetailActivity,
                    iv_product_detail_img,
                    ViewCompat.getTransitionName(iv_product_detail_img)!!
                )
                startActivity(this, options.toBundle())
            }
        }

        tv_product_detail_qty.text =
            CommonActivity.getCartProductsQty(this, productModel.product_id!!)
                .toString()//cartData.getProductQty(productModel.product_id!!).toString()

        if (ConnectivityReceiver.isConnected) {
            makeGetProductDetail()
        } else {
            Intent(this, NoInternetActivity::class.java).apply {
                startActivityForResult(this, 9328)
            }
        }

        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius =
            CommonActivity.dpToPx(AppController.instance?.applicationContext!!, 280F).toFloat()
        gradientDrawable.setColor(AppController.infoBoxBg)
        gradientDrawable.setStroke(
            CommonActivity.dpToPx(this, 1F),
            ContextCompat.getColor(this, R.color.colorTextHint)
        )
        ll_product_detail_add_remove.background = gradientDrawable

        iv_product_detail_add.setOnClickListener(this)
        iv_product_detail_remove.setOnClickListener(this)

        //ll_product_detail_add_remove.startAnimation(fade_in)
        animationGrow(iv_product_detail_add)
        animationGrow(iv_product_detail_remove)
    }

    override fun onClick(v: View?) {
        val cartQty = CommonActivity.getCartProductsQty(
            this,
            productModel.product_id!!
        )//cartData.getProductQty(productModel.product_id!!)


        if (cartQty > 0) {
            val cartModel =
                CommonActivity.getCartDetailByProductId(this, productModel.product_id!!)
            if (cartModel != null) {
                for (i in cartModel.cartItemModelList!!.size - 1 downTo 0) {
                    val cartItemModelData = cartModel.cartItemModelList[i]
                    if (cartItemModelData.product_id == productModel.product_id) {
                        cartItemModel = cartItemModelData
                        productModel.cart_id = cartItemModel?.cart_id!!.toInt()
                        break
                    }
                }
            }
        }

        when (v!!.id) {
            R.id.iv_product_detail_add -> {
                /*cartData.setProduct2(productModel, cartQty + 1)
                tv_product_detail_qty.text = (cartQty + 1).toString()*/
                SessionManagement.CommonData.setSession(this, "isRefresh", true)
                if (intent.getBooleanExtra("updateHome", false))
                    SessionManagement.CommonData.setSession(this, "isRefreshHome", true)

                updateQty(false)

                val params = HashMap<String, String>()
                params["user_id"] =
                    SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
                params["product_id"] = productModel.product_id!!
                params["qty"] = "1"
                productDetailViewModel.addCart(params)
                    .observe(this, Observer { response: CommonResponse? ->
                        updateQty(true)

                        if (response != null) {
                            if (response.responce!!) {
                                SessionManagement.UserData.setSession(
                                    this,
                                    "cartData",
                                    response.data!!
                                )
                                tv_product_detail_qty.text = (cartQty + 1).toString()

                                Intent().apply {
                                    setResult(Activity.RESULT_OK, this)
                                    putExtra("newQty", tv_product_detail_qty.text.toString())
                                    putExtra("productData", productModel as Serializable)
                                    if (intent.hasExtra("position"))
                                        putExtra("position", intent.getIntExtra("position", 0))
                                    if (intent.hasExtra("positionMain"))
                                        putExtra(
                                            "positionMain",
                                            intent.getIntExtra("positionMain", 0)
                                        )
                                }
                            } else {
                                CommonActivity.showToast(this, response.message!!)
                            }
                        }
                    })

            }
            R.id.iv_product_detail_remove -> {
                if (cartQty > 0) {
                    SessionManagement.CommonData.setSession(this, "isRefresh", true)
                    if (intent.getBooleanExtra("updateHome", false))
                        SessionManagement.CommonData.setSession(this, "isRefreshHome", true)

                    updateQty(false)

                    val params = HashMap<String, String>()
                    params["user_id"] =
                        SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
                    //params["cart_id"] = productModel.cart_id.toString()
                    params["product_id"] = productModel.product_id.toString()
                    params["qty"] = "1"

                    productDetailViewModel.makeMinusCart(params)
                        .observe(this@ProductDetailActivity, Observer { response: CommonResponse? ->
                            updateQty(true)

                            if (response != null) {
                                if (response.responce!!) {
                                    SessionManagement.UserData.setSession(
                                        this,
                                        "cartData",
                                        response.data!!
                                    )

                                    tv_product_detail_qty.text = (cartQty - 1).toString()

                                    Intent().apply {
                                        setResult(Activity.RESULT_OK, this)
                                        putExtra("newQty", tv_product_detail_qty.text.toString())
                                        putExtra("productData", productModel as Serializable)
                                        if (intent.hasExtra("position"))
                                            putExtra("position", intent.getIntExtra("position", 0))
                                        if (intent.hasExtra("positionMain"))
                                            putExtra(
                                                "positionMain",
                                                intent.getIntExtra("positionMain", 0)
                                            )
                                    }
                                } else {
                                    CommonActivity.showToast(this, response.message!!)
                                }
                            }
                        })
                }
                /*if (productModel.offer_type == null) {
                    if (cartQty != 0) {
                        if (cartQty > 1) {
                            cartData.setProduct2(productModel, cartQty - 1)
                            tv_product_detail_qty.text = (cartQty - 1).toString()
                        } else {
                            cartData.deleteProduct(productModel.cart_id)
                            tv_product_detail_qty.text = (cartQty - 1).toString()
                        }
                    }
                } else {
                    val isDeleted = cartData.deleteComboProduct(productModel)
                    if (isDeleted) {
                        tv_product_detail_qty.text = (cartQty - 1).toString()
                    }
                }*/
            }
        }
    }

    private fun makeGetProductDetail() {
        val params = ArrayList<NameValuePair>()
        params.add(NameValuePair("product_id", productModel.product_id!!))

        val task = CommonAsyTask(
            BaseURL.POST, params,
            BaseURL.GET_PRODUCT_DETAIL_URL, object : CommonAsyTask.VJsonResponce {
                override fun VResponce(responce: String, message: String) {

                    val gson = Gson()
                    val type = object : TypeToken<ProductModel>() {}.type

                    productModel = gson.fromJson<ProductModel>(responce, type)

                    val desc =
                        CommonActivity.getStringByLanguage(
                            this@ProductDetailActivity,
                            productModel.product_desc_en,
                            productModel.product_desc_ar,
                            productModel.product_desc_nl,
                            productModel.product_desc_tr,
                            productModel.product_desc_de
                        )
                    wv_product_detail.loadDataWithBaseURL("", desc!!, "text/html", "UTF-8", "")

                    if (productModel.productIngredientModelList != null
                        && productModel.productIngredientModelList!!.size > 0
                    ) {
                        rv_product_detail_tag.apply {
                            layoutManager = LinearLayoutManager(
                                this@ProductDetailActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            adapter = ProductTagAdapter(
                                this@ProductDetailActivity,
                                productModel.productIngredientModelList!!
                            )
                            CommonActivity.runLayoutAnimation(this)
                        }
                    }
                }

                override fun VError(responce: String, code: String) {
                    CommonActivity.showToast(
                        this@ProductDetailActivity,
                        responce
                    )
                }
            }, BaseURL.NONE, this
        )
        task.execute()
    }

    fun animationGrow(view: View) {
        val fade_in = ScaleAnimation(
            0f,
            1f,
            0f,
            1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        fade_in.duration = 1000 // animation duration in milliseconds
        fade_in.fillAfter =
            true // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        view.startAnimation(fade_in)
    }

    fun setHeaderTitle(title: String) {
        val mActionBar = supportActionBar
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true)
            mActionBar.setDisplayHomeAsUpEnabled(true)
            mActionBar.setDisplayShowTitleEnabled(false)
            val mInflater = LayoutInflater.from(this)

            val mCustomView = mInflater.inflate(R.layout.custom_actionbar, null)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = Gravity.CENTER_VERTICAL
            mCustomView.layoutParams = layoutParams
            val mTitleTextView = mCustomView.tv_actionbar_title
            mTitleTextView!!.text = ""
            mActionBar.customView = mCustomView
            mActionBar.setDisplayShowCustomEnabled(true)

            val upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material)!!
            upArrow.setColorFilter(
                ContextCompat.getColor(this, R.color.colorBlack),
                PorterDuff.Mode.SRC_ATOP
            )
            mActionBar.setHomeAsUpIndicator(upArrow)
        }
    }

    private fun updateQty(isEnable: Boolean) {
        iv_product_detail_add.isEnabled = isEnable
        iv_product_detail_remove.isEnabled = isEnable
        if (isEnable) {
            pb_product_detail_counter_load.visibility = View.GONE
            tv_product_detail_qty.visibility = View.VISIBLE
        } else {
            pb_product_detail_counter_load.visibility = View.VISIBLE
            tv_product_detail_qty.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item!!.itemId == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition()
            } else {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9328 && resultCode == Activity.RESULT_OK) {
            makeGetProductDetail()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
