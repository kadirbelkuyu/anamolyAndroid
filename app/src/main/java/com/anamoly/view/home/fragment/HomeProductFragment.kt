package com.anamoly.view.home.fragment

import Config.BaseURL
import Database.CartData
import Models.HomeProductTagModel
import Models.ProductModel
import Models.TagModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.home.MainActivity
import com.anamoly.view.home.adapter.ProductAdapter
import com.anamoly.view.product_detail.ProductDetailActivity
import com.anamoly.view.home.HomeFragmentViewModel
import kotlinx.android.synthetic.main.fragment_home_product.view.*
import utils.SessionManagement
import utils.StickHeaderItemDecoration
import java.io.Serializable


class HomeProductFragment : Fragment() {

    companion object {
        val TAG = HomeProductFragment::class.java.simpleName
    }

    val productModelList = ArrayList<ProductModel>()
    var productAdapter: ProductAdapter? = null

    lateinit var cartData: CartData

    val gson = Gson()
    val typeProduct = object : TypeToken<HomeProductTagModel>() {}.type

    private var contexts: Context? = null
    lateinit var rootView: View
    lateinit var homeFragmentViewModel: HomeFragmentViewModel

    val handler = Handler()
    var runnableMain: Runnable? = null
    var runnableBind: Runnable? = null

    fun newInstance(position: Int): HomeProductFragment {
        val f = HomeProductFragment()
        val b = Bundle()
        b.putInt("position", position)
        f.arguments = b
        return f
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            // animate here
            Log.e(TAG, "animate here")
            //updateView()
            updateViewQty()
        } else {
            Log.e(TAG, "fragment is no longer visible")
            // fragment is no longer visible
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_home_product, container, false)
        //updateView()

        homeFragmentViewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)

        //cartData = CartData(contexts!!)

        val gridLayoutManager = GridLayoutManager(contexts!!, 2)
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (productModelList[position].product_id.isNullOrEmpty()) {
                    2
                } else {
                    1
                }
            }
        }

        productAdapter = ProductAdapter(contexts!!, object : ProductAdapter.ItemSelected {
            override fun onItemSelected(position: Int, view: View, productModel: ProductModel) {
                Intent(context, ProductDetailActivity::class.java).apply {
                    putExtra("updateHome", false)
                    putExtra("position", position)
                    putExtra("productData", productModel as Serializable)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context as Activity,
                        view,
                        ViewCompat.getTransitionName(view)!!
                    )
                    startActivityForResult(this, 6879, options.toBundle())
                }
            }

            override fun onImageSelected(
                position: Int,
                view: View,
                productModel: ProductModel
            ) {
                (contexts as MainActivity).flyToCartView(
                    view,
                    CommonActivity.loadBitmapFromView(view, view.width, view.height)!!,
                    productModel
                )

                productModel.showQtyLoader = true
                productAdapter?.modelList?.set(position, productModel)
                productAdapter?.notifyItemChanged(position)

                val params = HashMap<String, String>()
                params["user_id"] =
                    SessionManagement.UserData.getSession(contexts!!, BaseURL.KEY_ID)
                params["product_id"] = productModel.product_id!!
                params["qty"] = "1"
                homeFragmentViewModel.addCart(params)
                    .observe(
                        this@HomeProductFragment,
                        Observer { response: CommonResponse? ->
                            if (response != null) {
                                if (response.responce!!) {
                                    SessionManagement.UserData.setSession(
                                        contexts!!,
                                        "cartData",
                                        response.data!!
                                    )
                                    productModel.cart_qty = productModel.cart_qty + 1
                                    productModel.showQtyLoader = false
                                    productAdapter?.modelList?.set(position, productModel)
                                    productAdapter?.notifyItemChanged(position)

                                    (contexts as MainActivity).updateCart()
                                    (contexts as MainActivity).displayComboPopup(
                                        productModel
                                    )
                                } else {
                                    CommonActivity.showToast(contexts!!, response.message!!)
                                    productModel.showQtyLoader = false
                                    productAdapter?.modelList?.set(position, productModel)
                                    productAdapter?.notifyItemChanged(position)
                                }
                            }
                        })

            }

            override fun onRemove(position: Int, view: View, productModel: ProductModel) {
                productModel.showQtyLoader = true
                productAdapter?.modelList?.set(position, productModel)
                productAdapter?.notifyItemChanged(position)

                val params = HashMap<String, String>()
                params["user_id"] =
                    SessionManagement.UserData.getSession(contexts!!, BaseURL.KEY_ID)
                params["product_id"] = productModel.product_id!!
                params["qty"] = "1"
                homeFragmentViewModel.makeMinusCart(params)
                    .observe(
                        this@HomeProductFragment,
                        Observer { response: CommonResponse? ->
                            if (response != null) {
                                if (response.responce!!) {
                                    SessionManagement.UserData.setSession(
                                        contexts!!,
                                        "cartData",
                                        response.data!!
                                    )
                                    if (productModel.cart_qty > 0) {
                                        productModel.cart_qty = productModel.cart_qty - 1
                                    }
                                    productModel.showQtyLoader = false
                                    productAdapter?.modelList?.set(position, productModel)
                                    productAdapter?.notifyItemChanged(position)

                                    (contexts as MainActivity).updateCart()
                                    (contexts as MainActivity).displayComboPopup(
                                        productModel
                                    )
                                } else {
                                    CommonActivity.showToast(contexts!!, response.message!!)
                                    productModel.showQtyLoader = false
                                    productAdapter?.modelList?.set(position, productModel)
                                    productAdapter?.notifyItemChanged(position)
                                }
                            }
                        })

            }
        }, productModelList)

        rootView.rv_home_product.apply {
            itemAnimator = null
            layoutManager = gridLayoutManager
            adapter = productAdapter
            //setHasFixedSize(true)
            addItemDecoration(StickHeaderItemDecoration(productAdapter!!))
            CommonActivity.runLayoutAnimation(rootView.rv_home_product)
        }

        rootView.rv_home_product.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                (contexts as MainActivity).hideComboPopup()
            }
        })

        runnableMain = Runnable {
            (contexts as MainActivity).runOnUiThread {
                updateView()
            }
        }
        handler.postDelayed(runnableMain!!, 200)

        return rootView
    }

    private fun bindView(homeProductTagModel: HomeProductTagModel) {

        val homeFragmentViewModel =
            ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)

        productModelList.clear()

        /*rootView.slider_home.setPresetTransformer(SliderLayout.Transformer.Default)
        rootView.slider_home.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        rootView.slider_home.setCustomAnimation(DescriptionAnimation())
        rootView.slider_home.setCustomIndicator(rootView.pi_home)
        rootView.slider_home.setDuration(10000)*/

        /*val homeProductTagModel =
            HomeFragment.homeProductTagModelList[arguments!!.getInt("position")]*/

        /*if (arguments!!.containsKey("homeTagData")) {
            val homeProductTagModel =
                arguments!!.getSerializable("homeTagData") as HomeProductTagModel*/

        if (homeProductTagModel.bannerModelList != null
            && homeProductTagModel.bannerModelList.size > 0
        ) {
            val productModel = ProductModel()
            productModel.bannerModelList = homeProductTagModel.bannerModelList
            productModelList.add(productModel)
        }

        if (homeProductTagModel.subCategoryModelList != null) {
            for (subCategoryModel in homeProductTagModel.subCategoryModelList) {
                if (subCategoryModel.productModelList != null
                    && subCategoryModel.productModelList.size > 0
                ) {
                    val productModel = ProductModel()
                    productModel.sub_category_id = subCategoryModel.sub_category_id
                    productModel.category_id = subCategoryModel.category_id
                    productModel.sub_cat_name_en = subCategoryModel.sub_cat_name_en
                    productModel.sub_cat_name_ar = subCategoryModel.sub_cat_name_ar
                    productModel.sub_cat_name_nl = subCategoryModel.sub_cat_name_nl
                    productModelList.add(productModel)

                    for ((position, productModel2: ProductModel) in subCategoryModel.productModelList.withIndex()) {
                        productModel2.addMargin = ((position % 2) == 1)
                        productModelList.add(productModel2)
                    }
                    //productModelList.addAll(subCategoryModel.productModelList)
                }
            }
        }

        productAdapter?.notifyDataSetChanged()

        runnableBind = Runnable {
            (contexts as MainActivity).runOnUiThread {

            }
        }
        handler.postDelayed(runnableBind!!, 50)

        /*val bannerModelList = ArrayList<BannerModel>()

        if (homeProductTagModel.bannerModelList != null
            && homeProductTagModel.bannerModelList.size > 0
        ) {
            bannerModelList.addAll(homeProductTagModel.bannerModelList)
            rootView.cl_home_slider.visibility = View.VISIBLE
        } else {
            rootView.cl_home_slider.visibility = View.GONE
        }

        for (sliderModel in bannerModelList) {
            val textSliderView = DefaultSliderView(contexts!!)
            // initialize a SliderLayout
            textSliderView
                .image(BaseURL.IMG_BANNER_URL + sliderModel.banner_image)
                .empty(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .setScaleType(BaseSliderView.ScaleType.Fit)

            rootView.slider_home.addSlider(textSliderView)
        }*/

        /*} else {
            rootView.cl_home_slider.visibility = View.GONE
            rootView.rv_home_product.visibility = View.GONE
        }*/

        //productAdapter?.notifyDataSetChanged()

        /*rootView.nsv_home.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            (contexts as MainActivity).hideComboPopup()
        }*/

    }

    fun updateView() {
        /*if (::rootView.isInitialized && productAdapter != null) {
            productAdapter!!.notifyDataSetChanged()
        }*/
        /*val handler = Handler()
        handler.postDelayed(Runnable {
            if (::rootView.isInitialized && productAdapter != null) {
                productAdapter!!.notifyDataSetChanged()
            }
        }, 100)*/
        /*if (::rootView.isInitialized && productAdapter != null) {
            for ((position, productModel) in productAdapter?.modelList!!.withIndex()) {
                if (!productModel.product_id.isNullOrEmpty()) {
                    if (cartData.getHasProduct(productModel)) {
                        productAdapter?.notifyItemChanged(position)
                    }
                }
            }
        }*/

        /*if (::rootView.isInitialized && productAdapter != null) {
            for ((position, productModel) in productAdapter?.modelList!!.withIndex()) {
                if (!productModel.product_id.isNullOrEmpty()) {
                    if (CommonActivity.getCartProductsQty(
                            contexts!!,
                            productModel.product_id!!
                        ) > 0
                    ) {
                        productAdapter?.notifyItemChanged(position)
                    }
                }
            }
        }*/

        if (::rootView.isInitialized && ::homeFragmentViewModel.isInitialized) {

            val params = HashMap<String, String>()
            params["user_id"] =
                SessionManagement.UserData.getSession(contexts!!, BaseURL.KEY_ID)
            if (arguments?.containsKey("tagData")!!) {
                val tagModel = arguments?.getSerializable("tagData") as TagModel
                params["tab_ref"] = tagModel.tag_ref!!
            }

            //rootView.cl_home_slider.visibility = View.GONE
            rootView.rv_home_product.visibility = View.GONE
            rootView.pb_home_product.visibility = View.VISIBLE

            homeFragmentViewModel.getTabProductList(params)
                .observe(this, Observer { response: CommonResponse? ->
                    //rootView.cl_home_slider.visibility = View.VISIBLE
                    rootView.rv_home_product.visibility = View.VISIBLE
                    rootView.pb_home_product.visibility = View.GONE
                    if (response != null) {
                        if (response.responce!!) {
                            val homeProductTagModel =
                                gson.fromJson<HomeProductTagModel>(response.data, typeProduct)
                            bindView(homeProductTagModel)
                        } else {
                            CommonActivity.showToast(contexts!!, response.message!!)
                        }
                    }
                })
        }

    }

    private fun updateViewQty() {
        if (::rootView.isInitialized && productAdapter != null) {
            for ((position, productModel) in productAdapter?.modelList!!.withIndex()) {
                if (!productModel.product_id.isNullOrEmpty()) {
                    val qty = CommonActivity.getCartProductsQty(
                        contexts!!,
                        productModel.product_id!!
                    )
                    productModel.cart_qty = qty
                    productAdapter?.notifyItemChanged(position)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 6879 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.hasExtra("position")) {
                    val newQty = data.getStringExtra("newQty")
                    val position = data.getIntExtra("position", 0)
                    if (productModelList.size > 0) {
                        productModelList[position].cart_qty = newQty!!.toInt()
                        productAdapter?.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        /*runnableMain = Runnable {
            (contexts as MainActivity).runOnUiThread {
                updateView()
            }
        }
        handler.postDelayed(runnableMain!!, 200)*/
    }

    override fun onPause() {
        super.onPause()
        if (runnableMain != null)
            handler.removeCallbacks(runnableMain!!)
        if (runnableBind != null)
            handler.removeCallbacks(runnableBind!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contexts = activity
    }

}
