package com.anamoly.view.home

import Config.BaseURL
import Config.GlobleVariable
import CustomViews.GridSpacingItemDecoration
import Database.CartData
import Models.CartModel
import Models.CategoryModel
import Models.ProductModel
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anamoly.BuildConfig
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.barcode_scanner.BarcodeScannerActivity
import com.anamoly.view.cart.CartFragment
import com.anamoly.view.home.adapter.ComboOfferAdapter
import com.anamoly.view.home.adapter.FeaturedCategoryAdapter
import com.anamoly.view.home.fragment.CategoryFragment
import com.anamoly.view.order.OrderDetailActivity
import com.anamoly.view.order_tracking.TrackOrderActivity
import com.anamoly.view.search.fragment.SearchFragment
import com.anamoly.view.user_account.fragment.UserFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.onesignal.OneSignal
import com.skydoves.balloon.*
import com.skydoves.balloon.overlay.BalloonOverlayAnimation
import com.skydoves.balloon.overlay.BalloonOverlayCircle
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_bottom_featured_category.view.*
import kotlinx.android.synthetic.main.dialog_combooffer.*
import utils.ContextWrapper
import utils.LanguagePrefs
import utils.RevealAnimationView
import utils.SessionManagement
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        val TAG = MainActivity::class.java.simpleName

        val categoryModelList = ArrayList<CategoryModel>()
        var categoryModelSelected: CategoryModel? = null
    }

    var actionbarheight = 0
    var lastSelection = 0

    lateinit var cartData: CartData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_main)

        cartData = CartData(this)

        val tv = TypedValue()
        if (theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionbarheight =
                TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val fr = supportFragmentManager.findFragmentById(R.id.fl_home)
            val fm_name = fr?.javaClass!!.simpleName
            if (fm_name == "CategoryFragment") {
                setBottomSelection(1)
            } else if (fm_name == "SearchFragment") {
                setBottomSelection(2)
            } else if (fm_name == "CartFragment") {
                setBottomSelection(3)
            } else if (fm_name == "UserFragment") {
                setBottomSelection(4)
            }
        }

        if (savedInstanceState == null) {
            loadHome()
        }

        PushDownAnim.setPushDownAnimTo(
            iv_home_product,
            iv_home_search,
            iv_home_cart,
            iv_home_profile
        )

        val homeFragmentViewModel =
            ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)

        var text = "OneSignal UserID:\n${OneSignal.getDeviceState()?.userId}\n\n"

        text += if (OneSignal.getDeviceState()?.pushToken != null)
            "Google Registration Id:\n${OneSignal.getDeviceState()?.pushToken}"
        else
            "Google Registration Id:\nCould not subscribe for push"

        Log.i("TEST", text)

        OneSignal.sendTag("anamoly", "1")

        if (!OneSignal.getDeviceState()?.userId.isNullOrEmpty()) {
            val params = HashMap<String, String>()
            params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
            params["player_id"] = OneSignal.getDeviceState()?.userId.toString()
            params["device"] = BuildConfig.HEADER_DEVICE

            homeFragmentViewModel.registerPlayerId(params)
        }

        if (intent.hasExtra("type")) {
            if (intent.getStringExtra("type") == "ORDER") {
                Intent(this, OrderDetailActivity::class.java).apply {
                    putExtra("order_id", intent.getStringExtra("ref_id"))
                    startActivity(this)
                }
            } else if (intent.getStringExtra("type") == "ORDER OUT OF DELIVERY") {
                Intent(this, TrackOrderActivity::class.java).apply {
                    putExtra("order_id", intent.getStringExtra("ref_id"))
                    startActivity(this)
                }
            }
        }

        tv_home_cart_main_counter.background = CommonActivity.getCircleBG2()

        iv_home_product.setOnClickListener(this)
        iv_home_search.setOnClickListener(this)
        iv_home_menu.setOnClickListener(this)
        iv_home_cart.setOnClickListener(this)
        tv_home_cart_main_counter.setOnClickListener(this)
        iv_home_profile.setOnClickListener(this)

        include_home_combo.visibility = View.INVISIBLE
        cv_combo_offer.visibility = View.INVISIBLE
        iv_combo_offer_close.setOnClickListener {
            RevealAnimationView().hideWithCircularRevealAnimation(
                cv_combo_offer,
                include_home_combo, false
            )
        }

        comboOfferAdapter = ComboOfferAdapter(this, comboOfferImageList)

        rv_combo_offer.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = comboOfferAdapter
        }

    }

    override fun onClick(v: View?) {
        val fr = supportFragmentManager.findFragmentById(R.id.fl_home)
        val fm_name = fr?.javaClass!!.simpleName

        if (cv_combo_offer.visibility == View.VISIBLE) {
            RevealAnimationView().hideWithCircularRevealAnimation(
                cv_combo_offer,
                include_home_combo, false
            )
        }

        var fm: Fragment? = null
        when (v!!.id) {
            R.id.iv_home_product -> {
                setBottomSelection(1)
                if (!fm_name.equals("CategoryFragment")) {
                    fm = CategoryFragment()
                }
            }
            R.id.iv_home_search -> {
                /*Intent(this, ProductDetailActivity::class.java).apply {
                    startActivity(this)
                }*/
                setBottomSelection(2)
                if (!fm_name.equals("SearchFragment")) {
                    fm = SearchFragment()
                }
            }
            R.id.iv_home_cart, R.id.tv_home_cart_main_counter -> {
                setBottomSelection(3)
                if (!fm_name.equals("CartFragment")) {
                    fm = CartFragment()
                }
            }
            R.id.iv_home_profile -> {
                setBottomSelection(4)
                if (!fm_name.equals("UserFragment")) {
                    fm = UserFragment()
                }
            }
            R.id.iv_home_menu -> {
                if (categoryModelList.isNotEmpty()) {
                    showFeaturedCategory()
                }
            }
        }
        if (fm != null) {
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                //.replace(R.id.fl_home, fm, "Home_fragment")
                .add(R.id.fl_home, fm)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onBackPressed() {
        val fr = supportFragmentManager.findFragmentById(R.id.fl_home)
        val fm_name = fr?.javaClass!!.simpleName

        if (fm_name.contentEquals("CartFragment")
            || fm_name.contentEquals("UserFragment")
            || fm_name.contentEquals("SearchFragment")
        ) {
            if (SearchFragment.et_search == null
                || !SearchFragment.et_search!!.hasFocus()
            ) {
                loadHome()
                fm_home.updateCart()
                fm_home.updateData()
            } else {
                SearchFragment.et_search!!.setText("")
                SearchFragment.et_search!!.clearFocus()
            }
        } else if (fm_name.contentEquals("SearchProductFragment")) {
            fm_home.updateCart()
            fm_home.updateData()
            super.onBackPressed()
        } else if (fm_name.contentEquals("CategoryFragment")) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    var allowHeader = true
    val fm_home = CategoryFragment()

    private fun loadHome() {
        setBottomSelection(1)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fl_home, fm_home, "Home_fragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun loadHomeWithCategory(categoryModel: CategoryModel) {
        setBottomSelection(1)
        val args = Bundle()
        args.putSerializable("categoryModelData", categoryModel as Serializable)
        fm_home.arguments = args
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fl_home, fm_home, "Home_fragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
        fm_home.updateViewpager(categoryModel)
    }

    fun setBottomSelection(position: Int) {
        if (position != 5) {
            lastSelection = position
        }

        iv_home_product.isSelected = (position == 1)
        iv_home_search.isSelected = (position == 2)
        iv_home_cart.isSelected = (position == 3)
        iv_home_profile.isSelected = (position == 4)
        iv_home_menu.isSelected = (position == 5)
    }

    fun setFragment(fm: Fragment?, allowAnimation: Boolean) {
        val fragmentManager = supportFragmentManager
        if (fm != null) {
            if (allowAnimation) {
                if (LanguagePrefs.getLang(this@MainActivity).equals("ar")) {
                    fragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.transition_slide_in_left,
                            R.anim.transition_slide_out_right,
                            R.anim.transition_slide_in_right,
                            R.anim.transition_slide_out_left
                        )
                        .add(R.id.fl_home, fm)
                        .addToBackStack(null)
                        .commit()
                } else {
                    fragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.transition_slide_in_right,
                            R.anim.transition_slide_out_left,
                            R.anim.transition_slide_in_left,
                            R.anim.transition_slide_out_right
                        )
                        .add(R.id.fl_home, fm)
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                fragmentManager.beginTransaction()
                    //.replace(R.id.fl_home, fm, "Home_fragment")
                    .add(R.id.fl_home, fm)
                    .addToBackStack(null)
                    .commit()
            }
        }

    }

    fun flyToCartView(
        itemView: View,
        b: Bitmap,
        productModel: ProductModel?
    ) {

        //updateCart()

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        val imageView = ImageView(this)
        //imageView.setAlpha(0.5F);// set alpha
        imageView.layoutParams = layoutParams
        imageView.visibility = View.GONE
        fl_home_main.addView(imageView)
        imageView.setImageBitmap(b)
        imageView.visibility = View.VISIBLE
        val u = IntArray(2)
        iv_home_cart.getLocationInWindow(u)

        val u2 = IntArray(2)
        itemView.getLocationInWindow(u2)
        /*imageView.left = itemView.left
        imageView.top = itemView.top
        imageView.right = itemView.right*/
        Log.e("itemPosition", "y:" + u2[1])
        Log.e("itemPosition", "x:" + u2[0])
        Log.e("itemPosition", "-------")

        imageView.left = u2[0]
        imageView.top = u2[1]

        val animSetXY = AnimatorSet()
        val y = ObjectAnimator.ofFloat(
            imageView,
            "translationY",
            imageView.top.toFloat(),
            u[1] - (2 * iv_home_cart.height).toFloat()
        )
        val x = ObjectAnimator.ofFloat(
            imageView,
            "translationX",
            imageView.left.toFloat(),
            u[0] - (iv_home_cart.width / 2).toFloat()
        )
        val sy = ObjectAnimator.ofFloat(imageView, "scaleY", 0.8f, 0.1f)
        val sx = ObjectAnimator.ofFloat(imageView, "scaleX", 0.8f, 0.1f)
        animSetXY.playTogether(x, y, sx, sy)
        animSetXY.duration = 700 // animation duration
        animSetXY.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) { // remove view after animation done
                fl_home_main.removeView(imageView)
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animSetXY.start()
    }

    fun updateCart() {
        fm_home.updateCart()
        val total = CommonActivity.getCartTotalAmount(this)//cartData.getProductTotalPrice(true)
        if (total > 0) {
            tv_home_cart_main_counter.visibility = View.VISIBLE
            tv_home_cart_main_counter.setText(
                String.format(
                    GlobleVariable.LOCALE,
                    "%.2f",
                    total
                ).replace(".00", "")
            )
            CommonActivity.runBounceAnimation(this, tv_home_cart_main_counter)
        } else {
            tv_home_cart_main_counter.visibility = View.GONE
        }
    }

    val comboOfferImageList = ArrayList<String?>()
    lateinit var comboOfferAdapter: ComboOfferAdapter
    var hasNew = false

    fun displayComboPopup(productModel: ProductModel?) {
        if (productModel?.offer_type != null) {
            val cartModelList = CommonActivity.getCartProductsList(this)

            tv_combo_offer_title.apply {
                text = if (productModel.offer_type == "flatcombo") {
                    "${productModel.number_of_products} ${resources.getString(R.string.fors)} ${
                        CommonActivity.getPriceWithCurrency(
                            context, String.format(
                                GlobleVariable.LOCALE,
                                "%.2f",
                                productModel.offer_discount!!.toDouble()
                            ).replace(".00", "")
                        )
                    }"
                } else {
                    "${productModel.number_of_products} + 1 ${resources.getString(R.string.free)}"
                }
            }

            comboOfferImageList.clear()

            var comboSize = productModel.number_of_products!!.toInt()

            if (productModel.offer_type == "plusone") {
                comboSize++
            }

            for (i in 0..comboSize) {
                comboOfferImageList.add(null)
            }

            var hasCartModel: CartModel? = null

            for (i in cartModelList.size - 1 downTo 0) {
                val cartModel = cartModelList[i]
                for (i1 in cartModel.cartItemModelList!!.size - 1 downTo 0) {
                    val cartItemModel = cartModel.cartItemModelList[i1]
                    if (cartItemModel.offer_type == productModel.offer_type &&
                        cartItemModel.product_offer_id == productModel.product_offer_id &&
                        cartItemModel.product_id == productModel.product_id
                    ) {
                        if (hasCartModel == null) {
                            hasCartModel = cartModel
                        }
                        break
                    }
                }
            }

            if (hasCartModel != null) {
                for ((position, cartItemModel) in hasCartModel.cartItemModelList!!.withIndex()) {
                    comboOfferImageList[position] = cartItemModel.product_image
                }
                showComboOffer()
                hasNew = comboSize == hasCartModel.cartItemModelList!!.size
            }

        }
    }

    fun showComboOffer() {
        if (hasNew) {
            RevealAnimationView().hideWithCircularRevealAnimation(
                cv_combo_offer,
                include_home_combo,
                true
            )
        }

        include_home_combo.visibility = View.VISIBLE

        if (cv_combo_offer.visibility != View.VISIBLE) {
            RevealAnimationView().showWithCircularRevealAnimation(cv_combo_offer)
        }

        comboOfferAdapter.notifyDataSetChanged()

        /*var areAllFill = false
        for ((position, bitmap) in comboOfferImageList.withIndex()) {
            areAllFill = (bitmap == null)
        }
        if (isStartNewCombo) {
            RevealAnimationView().hideWithCircularRevealAnimation(
                cv_combo_offer,
                include_home_combo,
                true
            )
            val lastBitmap = comboOfferImageList[comboOfferImageList.size - 1]
            for ((position, bitmap) in comboOfferImageList.withIndex()) {
                comboOfferImageList[position] = null
            }
            comboOfferImageList[0] = lastBitmap
        }*/
    }

    fun hideComboPopup() {
        RevealAnimationView().hideWithCircularRevealAnimation(
            cv_combo_offer,
            include_home_combo, false
        )
    }

    fun openBarcodeSanner() {
        Dexter.withActivity(this)
            .withPermission(android.Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    Intent(this@MainActivity, BarcodeScannerActivity::class.java).apply {
                        startActivityForResult(this, 9873)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                }
            }).check()
    }

    private fun showFeaturedCategory() {
        val balloon = createBalloon(this) {
            setWidthRatio(0.95f)
            setLayout(R.layout.dialog_bottom_featured_category)
            setBalloonAnimation(BalloonAnimation.FADE)
            setLifecycleOwner(this@MainActivity)
            setIsVisibleOverlay(true)
            setOverlayColorResource(R.color.colorOverlayBG)
            setOverlayPadding(6F)
            setBackgroundColorResource(R.color.colorBG)
            setBalloonOverlayAnimation(BalloonOverlayAnimation.FADE)
            setDismissWhenOverlayClicked(true)
            setOverlayShape(BalloonOverlayCircle(radius = 90f))
            setArrowSize(10)
            setArrowOrientation(ArrowOrientation.BOTTOM)
            setArrowPosition(0.50f)
            setMarginBottom(35)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            build()
        }
        balloon.getContentView().findViewById<RecyclerView>(R.id.rv_featured_category).apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = FeaturedCategoryAdapter(
                this@MainActivity,
                categoryModelList,
                object : FeaturedCategoryAdapter.OnItemClick {
                    override fun onClick(position: Int, categoryModel: CategoryModel) {
                        balloon.dismiss()

                        categoryModelSelected = categoryModel
                        loadHomeWithCategory(categoryModel)
                    }
                }).apply {
                if (categoryModelSelected != null) {
                    selectedId = categoryModelSelected?.category_id.toString()
                }
            }
        }

        balloon.show(iv_home_menu)

        setBottomSelection(5)

    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mUpdate)
    }

    override fun onResume() {
        super.onResume()
        updateCart()
        registerReceiver(mUpdate, IntentFilter("comboUpdate"))
    }

    // broadcast reciver for receive data
    private val mUpdate = object : BroadcastReceiver() {
        override fun onReceive(context: Context, data: Intent) {
            val type = data.getStringExtra("type")!!

            if (type.contentEquals("update")) {
                val productModel =
                    cartData.getProductDetailByCartId(data.getStringExtra("cartId")!!.toInt())

                Log.d(TAG, "cartID::${productModel?.cart_id}")

                if (data.getBooleanExtra("hasNew", false)) {
                    RevealAnimationView().hideWithCircularRevealAnimation(
                        cv_combo_offer,
                        include_home_combo,
                        true
                    )
                }

                if (productModel?.offer_type != null) {

                    comboOfferImageList.clear()

                    for (i in 0..productModel.number_of_products!!.toInt()) {
                        comboOfferImageList.add(null)
                    }

                    if (productModel.productComboModelList != null && productModel.productComboModelList!!.size > 0) {
                        for ((position, productModelCombo) in productModel.productComboModelList!!.withIndex()) {
                            comboOfferImageList[position] = productModelCombo.product_image
                        }
                    }
                    showComboOffer()
                }

            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
