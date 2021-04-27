package com.anamoly.view.search.fragment

import Config.BaseURL
import Models.ProductModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.home.MainActivity
import com.anamoly.response.ProductResponse
import com.anamoly.view.no_internet.NoInternetActivity
import com.anamoly.view.product_detail.ProductDetailActivity
import com.anamoly.view.search.adapter.SearchProductAdapter
import com.anamoly.view.search.SearchProductWithTextViewModel
import kotlinx.android.synthetic.main.fragment_search_product_with_text.view.*
import kotlinx.android.synthetic.main.include_loader.view.*
import kotlinx.android.synthetic.main.include_toolbar.view.*
import utils.ConnectivityReceiver
import utils.SessionManagement
import java.io.Serializable

/**
 * Created on 18-01-2020.
 */
class SearchProductWithTextFragment : Fragment() {

    companion object {
        private val TAG = SearchProductWithTextFragment::class.java.simpleName
    }

    val productModelList = ArrayList<ProductModel>()

    lateinit var searchProductAdapter: SearchProductAdapter

    lateinit var searchProductWithTextViewModel: SearchProductWithTextViewModel
    lateinit var contexts: Context
    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_search_product_with_text, container, false)

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
        searchProductWithTextViewModel =
            ViewModelProviders.of(this).get(SearchProductWithTextViewModel::class.java)

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
                    searchProductWithTextViewModel.addCart(params)
                        .observe(
                            this@SearchProductWithTextFragment,
                            Observer { response: CommonResponse? ->
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
                    searchProductWithTextViewModel.makeMinusCart(params)
                        .observe(this@SearchProductWithTextFragment, Observer { response: CommonResponse? ->
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
        rootView.rv_search_product_with_text.apply {
            layoutManager = gridLayoutManager
            adapter = searchProductAdapter
        }

        if (arguments!!.containsKey("title")) {
            rootView.tv_toolbar_title.text = arguments!!.getString("title")
        }

        rootView.rv_search_product_with_text.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                (contexts as MainActivity).hideComboPopup()
            }
        })

        if (arguments!!.containsKey("s_type")) {
            if (ConnectivityReceiver.isConnected) {
                makeGetProductList()
            } else {
                Intent(contexts, NoInternetActivity::class.java).apply {
                    startActivityForResult(this, 9328)
                }
            }
        }

        return rootView
    }

    private fun makeGetProductList() {
        SessionManagement.CommonData.setSession(contexts, "isRefresh", false)

        val s_type = arguments!!.getString("s_type")
        val s_type_id = arguments!!.getString("s_type_id")

        val params = HashMap<String, String>()
        params["user_id"] =
            SessionManagement.UserData.getSession(contexts, BaseURL.KEY_ID)

        if (s_type == "category") {
            params["category_id"] = s_type_id!!
        }
        if (s_type == "sub_category") {
            params["sub_category_id"] = s_type_id!!
        }
        if (s_type == "group") {
            params["product_group_id"] = s_type_id!!
        }
        if (s_type == "product") {
            params["product_id"] = s_type_id!!
        }

        rootView.include_ll.visibility = View.VISIBLE
        rootView.rv_search_product_with_text.visibility = View.GONE

        searchProductWithTextViewModel.getProductList(params)
        searchProductWithTextViewModel.productResponseLiveData.observe(
            this,
            Observer { response: ProductResponse? ->
                if (response != null) {
                    if (response.responce!!) {

                        rootView.include_ll.visibility = View.GONE
                        rootView.rv_search_product_with_text.visibility = View.VISIBLE

                        productModelList.clear()
                        productModelList.addAll(response.productModelList!!)
                        val productModelSuggest = ProductModel()
                        productModelSuggest.group_id = "00"
                        productModelList.add(productModelSuggest)

                        searchProductAdapter.notifyDataSetChanged()
                    } else {
                        CommonActivity.showToast(contexts!!, response.message!!)
                    }
                }
            }
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9328 && resultCode == Activity.RESULT_OK) {
            if (arguments!!.containsKey("s_type")) {
                makeGetProductList()
            }
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
            if (arguments!!.containsKey("s_type")) {
                if (ConnectivityReceiver.isConnected) {
                    makeGetProductList()
                } else {
                    Intent(contexts, NoInternetActivity::class.java).apply {
                        startActivityForResult(this, 9328)
                    }
                }
            }
        }
    }*/

    override fun onAttach(context: Context) {
        this.contexts = context
        super.onAttach(context)
    }

}