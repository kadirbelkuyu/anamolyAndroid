package com.anamoly.view.search.fragment

import Config.BaseURL
import Models.CategoryModel
import Models.GroupProductModel
import Models.ProductModel
import Models.SubCategoryModel
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.*
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.response.GroupProductResponse
import com.anamoly.view.home.MainActivity
import com.anamoly.view.no_internet.NoInternetActivity
import com.anamoly.view.product_detail.ProductDetailActivity
import com.anamoly.view.search.SearchProductViewModel
import com.anamoly.view.search.adapter.ProductTabAdapter
import com.anamoly.view.search.adapter.SearchProductAdapter
import com.anamoly.view.search.adapter.SearchSubCategoryTabAdapter
import kotlinx.android.synthetic.main.activity_search_product.view.*
import kotlinx.android.synthetic.main.include_loader.view.*
import kotlinx.android.synthetic.main.include_toolbar.view.*
import utils.ConnectivityReceiver
import utils.SessionManagement
import java.io.Serializable


/**
 * Created on 18-01-2020.
 */
class SearchProductFragment : Fragment() {

    companion object {
        private val TAG = SearchProductFragment::class.java.simpleName
    }

    val productModelList = ArrayList<ProductModel>()
    val productModelListHeader = ArrayList<ProductModel>()
    val groupProductModelList = ArrayList<GroupProductModel>()

    lateinit var productTabAdapter: ProductTabAdapter
    lateinit var searchProductAdapter: SearchProductAdapter

    var category_id = ""
    var sub_category_id = ""
    var allowHeader: Boolean = true

    var lastScrollHeaderPosition = 0

    lateinit var searchProductViewModel: SearchProductViewModel
    lateinit var contexts: Context
    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_search_product, container, false)

        rootView.toolbar_home.setBackgroundColor(CommonActivity.getHeaderColor())
        rootView.tv_toolbar_title.setTextColor(CommonActivity.getHeaderTextColor())
        CommonActivity.setImageTint(rootView.iv_toolbar_back)

        rootView.iv_toolbar_back.visibility = View.VISIBLE
        rootView.iv_toolbar_back.setOnClickListener {
            //fragmentManager!!.popBackStack()
            (contexts as Activity).onBackPressed()
        }
        rootView.iv_toolbar_barcode_scan.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                (contexts as MainActivity).openBarcodeSanner()
            }
        }

        // View Model
        searchProductViewModel = ViewModelProviders.of(this).get(SearchProductViewModel::class.java)

        val smoothScroller = object : LinearSmoothScroller(contexts) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }

        val gridLayoutManager = GridLayoutManager(contexts, 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (productModelList[position].group_id.isNullOrEmpty()) {
                    2
                } else {
                    1
                }
            }
        }

        productTabAdapter = ProductTabAdapter(
            contexts,
            productModelListHeader,
            object : ProductTabAdapter.OnItemSelected {
                override fun onClick(position: Int, productModel: ProductModel) {
                    /*gridLayoutManager.scrollToPositionWithOffset(
                        getProductIndex(productModel.group_name_en!!),
                        10
                    )*/
                    smoothScroller.targetPosition = searchProductViewModel.getProductIndex(
                        productModelList,
                        productModel.group_name_en!!
                    )
                    gridLayoutManager.startSmoothScroll(smoothScroller)
                }
            })
        productTabAdapter.selectedItems.put(0, true)

        val linearLayoutManagerTab =
            LinearLayoutManager(contexts, LinearLayoutManager.HORIZONTAL, false)

        rootView.rv_search_product_tab.apply {
            layoutManager = linearLayoutManagerTab
            adapter = productTabAdapter
        }

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(rootView.rv_search_product_tab)

        searchProductAdapter = SearchProductAdapter(
            contexts,
            productModelList,
            object : SearchProductAdapter.ItemSelected {
                override fun onItemSelected(position: Int, view: View, productModel: ProductModel) {
                    Intent(context, ProductDetailActivity::class.java).apply {
                        putExtra("updateHome", true)
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
                        productModelList[position]
                    )

                    SessionManagement.CommonData.setSession(contexts, "isRefreshHome", true)

                    productModel.showQtyLoader = true
                    searchProductAdapter.modelList.set(position, productModel)
                    searchProductAdapter.notifyItemChanged(position)

                    val params = HashMap<String, String>()
                    params["user_id"] =
                        SessionManagement.UserData.getSession(contexts, BaseURL.KEY_ID)
                    params["product_id"] = productModel.product_id!!
                    params["qty"] = "1"
                    searchProductViewModel.addCart(params)
                        .observe(this@SearchProductFragment, Observer { response: CommonResponse? ->
                            if (response != null) {
                                if (response.responce!!) {
                                    SessionManagement.UserData.setSession(
                                        contexts,
                                        "cartData",
                                        response.data!!
                                    )
                                    productModel.cart_qty = productModel.cart_qty + 1
                                    productModel.showQtyLoader = false
                                    searchProductAdapter.modelList.set(position, productModel)
                                    searchProductAdapter.notifyItemChanged(position)

                                    (contexts as MainActivity).updateCart()
                                    (contexts as MainActivity).displayComboPopup(productModel)
                                } else {
                                    CommonActivity.showToast(contexts, response.message!!)
                                    productModel.showQtyLoader = false
                                    searchProductAdapter.modelList.set(position, productModel)
                                    searchProductAdapter.notifyItemChanged(position)
                                }
                            }
                        })

                }

                override fun onRemove(position: Int, view: View, productModel: ProductModel) {
                    SessionManagement.CommonData.setSession(contexts, "isRefreshHome", true)

                    productModel.showQtyLoader = true
                    searchProductAdapter.modelList[position] = productModel
                    searchProductAdapter.notifyItemChanged(position)

                    val params = HashMap<String, String>()
                    params["user_id"] =
                        SessionManagement.UserData.getSession(contexts, BaseURL.KEY_ID)
                    params["product_id"] = productModel.product_id!!
                    params["qty"] = "1"
                    searchProductViewModel.makeMinusCart(params)
                        .observe(this@SearchProductFragment, Observer { response: CommonResponse? ->
                            if (response != null) {
                                if (response.responce!!) {
                                    SessionManagement.UserData.setSession(
                                        contexts,
                                        "cartData",
                                        response.data!!
                                    )
                                    if (productModel.cart_qty > 0) {
                                        productModel.cart_qty = productModel.cart_qty - 1
                                    }
                                    productModel.showQtyLoader = false
                                    searchProductAdapter.modelList[position] = productModel
                                    searchProductAdapter.notifyItemChanged(position)

                                    (contexts as MainActivity).updateCart()
                                    (contexts as MainActivity).displayComboPopup(productModel)
                                } else {
                                    CommonActivity.showToast(contexts, response.message!!)
                                    productModel.showQtyLoader = false
                                    searchProductAdapter.modelList[position] = productModel
                                    searchProductAdapter.notifyItemChanged(position)
                                }
                            }
                        })
                }
            })
        rootView.rv_search_product.apply {
            layoutManager = gridLayoutManager
            adapter = searchProductAdapter
        }

        rootView.rv_search_product.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val itemPosition: Int = gridLayoutManager.findFirstCompletelyVisibleItemPosition()
                val itemLastPosition: Int =
                    gridLayoutManager.findLastVisibleItemPosition()
                Log.e(TAG, "itemPosition:" + itemPosition)

                val headerPosition =
                    searchProductViewModel.isHeader(
                        productModelListHeader,
                        productModelList,
                        itemPosition
                    )
                if (headerPosition >= 0) {
                    if (headerPosition != lastScrollHeaderPosition) {
                        Log.e(TAG, "headerPosition:" + headerPosition)
                        productTabAdapter.selectedItems.clear()
                        productTabAdapter.selectedItems.put(headerPosition, true)
                        productTabAdapter.notifyDataSetChanged()
                        linearLayoutManagerTab.scrollToPositionWithOffset(headerPosition, 0)
                        lastScrollHeaderPosition = headerPosition
                    }
                }

            }
        })

        rootView.rv_search_product_sub_category.setBackgroundColor(CommonActivity.getHeaderColor())
        rootView.rv_search_product_tab.setBackgroundColor(CommonActivity.getHeaderColor())

        if (arguments!!.containsKey("category_id")) {
            category_id = arguments!!.getString("category_id")!!
        }
        if (arguments!!.containsKey("sub_category_id")) {
            sub_category_id = arguments!!.getString("sub_category_id")!!
        }
        allowHeader = arguments!!.getBoolean("allowHeader", true)
        (contexts as MainActivity).allowHeader = allowHeader
        if (allowHeader) {
            rootView.rv_search_product_tab.visibility = View.VISIBLE
        } else {
            rootView.rv_search_product_tab.visibility = View.GONE
            rootView.rv_search_product.setPaddingRelative(
                0,
                CommonActivity.dpToPx(contexts, 10F),
                CommonActivity.dpToPx(contexts, 10F),
                0
            )
        }
        if (arguments!!.containsKey("title")) {
            rootView.tv_toolbar_title.text = arguments!!.getString("title")
        }

        if (arguments!!.containsKey("categoryData")) {
            val categoryModel = arguments!!.getSerializable("categoryData") as CategoryModel
            category_id = categoryModel.category_id!!
            rootView.tv_toolbar_title.text = CommonActivity.getStringByLanguage(
                contexts,
                categoryModel.cat_name_en,
                categoryModel.cat_name_ar,
                categoryModel.cat_name_nl,
                categoryModel.cat_name_tr,
                categoryModel.cat_name_de
            )!!
        }

        if (arguments!!.containsKey("subCategoryData")) {
            val subCategoryModel =
                arguments!!.getSerializable("subCategoryData") as SubCategoryModel

            category_id = subCategoryModel.category_id!!
            sub_category_id = subCategoryModel.sub_category_id!!
            rootView.tv_toolbar_title.text = CommonActivity.getStringByLanguage(
                contexts,
                subCategoryModel.sub_cat_name_en,
                subCategoryModel.sub_cat_name_ar,
                subCategoryModel.sub_cat_name_nl,
                subCategoryModel.sub_cat_name_tr,
                subCategoryModel.sub_cat_name_de
            )!!
        }

        if (arguments!!.containsKey("subCategoryDataList")) {
            val subCategoryModelList =
                arguments!!.getSerializable("subCategoryDataList") as ArrayList<SubCategoryModel>

            val snapHelper: SnapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(rootView.rv_search_product_sub_category)

            val linearLayoutManagerCat =
                LinearLayoutManager(contexts, LinearLayoutManager.HORIZONTAL, false)

            rootView.rv_search_product_sub_category.apply {
                val searchSubCategoryTabAdapter =
                    SearchSubCategoryTabAdapter(
                        contexts,
                        subCategoryModelList,
                        object : SearchSubCategoryTabAdapter.OnItemClickListener {
                            override fun onClick(
                                position: Int,
                                subCategoryModel: SubCategoryModel
                            ) {
                                category_id = subCategoryModel.category_id!!
                                sub_category_id = subCategoryModel.sub_category_id!!
                                rootView.tv_toolbar_title.text = CommonActivity.getStringByLanguage(
                                    contexts,
                                    subCategoryModel.sub_cat_name_en,
                                    subCategoryModel.sub_cat_name_ar,
                                    subCategoryModel.sub_cat_name_nl,
                                    subCategoryModel.sub_cat_name_tr,
                                    subCategoryModel.sub_cat_name_de
                                )!!

                                if (ConnectivityReceiver.isConnected) {
                                    makeGetProductList(category_id, sub_category_id)
                                } else {
                                    Intent(contexts, NoInternetActivity::class.java).apply {
                                        startActivityForResult(this, 9328)
                                    }
                                }
                            }
                        })
                searchSubCategoryTabAdapter.selectedId = category_id

                visibility = View.VISIBLE
                layoutManager = linearLayoutManagerCat
                adapter = searchSubCategoryTabAdapter
                linearLayoutManagerCat.scrollToPositionWithOffset(
                    arguments!!.getInt("position", 0),
                    0
                )
            }
        }

        if (ConnectivityReceiver.isConnected) {
            makeGetProductList(category_id, sub_category_id)
        } else {
            Intent(contexts, NoInternetActivity::class.java).apply {
                startActivityForResult(this, 9328)
            }
        }

        return rootView
    }

    private fun makeGetProductList(category_id: String, sub_category_id: String) {

        SessionManagement.CommonData.setSession(contexts, "isRefresh", false)

        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(contexts, BaseURL.KEY_ID)
        if (category_id.isNotEmpty()) {
            params["category_id"] = category_id
        }
        if (sub_category_id.isNotEmpty()) {
            params["sub_category_id"] = sub_category_id
        }

        rootView.include_ll.visibility = View.VISIBLE
        rootView.rv_search_product.visibility = View.GONE

        searchProductViewModel.getProductList(params)
        searchProductViewModel.productResponseLiveData.observe(
            this,
            Observer { response: GroupProductResponse? ->
                if (response != null) {
                    if (response.responce!!) {

                        rootView.include_ll.visibility = View.GONE
                        rootView.rv_search_product.visibility = View.VISIBLE

                        productModelList.clear()
                        productModelListHeader.clear()
                        groupProductModelList.clear()

                        groupProductModelList.addAll(response.groupProductModelList!!)

                        var headerPosition = 0
                        for (groupProductModel in groupProductModelList) {
                            if (groupProductModel.productModelList != null
                                && groupProductModel.productModelList.size > 0
                            ) {
                                if (allowHeader) {
                                    val productModel = ProductModel()
                                    productModel.product_group_id =
                                        groupProductModel.product_group_id
                                    productModel.group_name_en = groupProductModel.group_name_en
                                    productModel.group_name_ar = groupProductModel.group_name_ar
                                    productModel.group_name_nl = groupProductModel.group_name_nl
                                    productModel.headerPosition = headerPosition
                                    productModelList.add(productModel)
                                    productModelListHeader.add(productModel)
                                    headerPosition++
                                }
                                productModelList.addAll(groupProductModel.productModelList)
                                /*if (allowHeader) {
                                    val productModelSuggest = ProductModel()
                                    productModelSuggest.group_id =
                                        groupProductModel.product_group_id
                                    productModelSuggest.group_name_en =
                                        groupProductModel.group_name_en
                                    productModelSuggest.group_name_ar =
                                        groupProductModel.group_name_ar
                                    productModelSuggest.group_name_nl =
                                        groupProductModel.group_name_nl
                                    productModelList.add(productModelSuggest)
                                }*/
                            }
                        }
                        /*if (!allowHeader && productModelList.size > 0) {
                            val productModelSuggest = ProductModel()
                            productModelSuggest.group_id = "00"
                            productModelList.add(productModelSuggest)
                        }*/
                        val productModelSuggest = ProductModel()
                        productModelSuggest.group_id = "00"
                        productModelList.add(productModelSuggest)

                        searchProductAdapter.notifyDataSetChanged()
                        productTabAdapter.notifyDataSetChanged()
                    } else {
                        CommonActivity.showToast(contexts, response.message!!)
                    }
                }
            }
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9328 && resultCode == Activity.RESULT_OK) {
            makeGetProductList(category_id, sub_category_id)
        } else if (requestCode == 6879 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.hasExtra("position")) {
                    val newQty = data.getStringExtra("newQty")
                    val position = data.getIntExtra("position", 0)
                    if (productModelList.size > 0) {
                        productModelList[position].cart_qty = newQty!!.toInt()
                        searchProductAdapter.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    /*override fun onResume() {
        super.onResume()
        if (SessionManagement.CommonData.getSessionBoolean(contexts, "isRefresh")) {
            SessionManagement.CommonData.setSession(contexts, "isRefresh", false)
            if (ConnectivityReceiver.isConnected) {
                makeGetProductList(category_id, sub_category_id)
            } else {
                Intent(contexts, NoInternetActivity::class.java).apply {
                    startActivityForResult(this, 9328)
                }
            }
        }
    }*/

    override fun onAttach(context: Context) {
        this.contexts = context
        super.onAttach(context)
    }

}