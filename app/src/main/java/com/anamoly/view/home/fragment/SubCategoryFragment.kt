package com.anamoly.view.home.fragment

import Config.BaseURL
import CustomViews.GridSpacingItemDecoration
import Database.CartData
import Models.CategoryModel
import Models.HomeProductTagModel
import Models.ProductModel
import Models.TagModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.anamoly.view.home.adapter.SubCategoryAdapter
import kotlinx.android.synthetic.main.fragment_home_product.view.*
import utils.SessionManagement
import utils.StickHeaderItemDecoration
import java.io.Serializable


class SubCategoryFragment : Fragment() {

    companion object {
        val TAG = SubCategoryFragment::class.java.simpleName
    }

    val productModelList = ArrayList<ProductModel>()
    var productAdapter: ProductAdapter? = null

    private var contexts: Context? = null
    lateinit var rootView: View
    lateinit var homeFragmentViewModel: HomeFragmentViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeFragmentViewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)
        rootView = inflater.inflate(R.layout.fragment_home_product, container, false)

        val mHandler = Handler(Looper.getMainLooper())
        mHandler.post(Runnable {
            (contexts as MainActivity).runOnUiThread {
                bindView()
            }
        })

        return rootView
    }

    private fun bindView() {
        if (::rootView.isInitialized && ::homeFragmentViewModel.isInitialized) {
            val categoryModel = arguments?.getSerializable("tagData") as CategoryModel

            if (!categoryModel.subCategoryModelList.isNullOrEmpty()) {
                rootView.pb_home_product.visibility = View.GONE
                rootView.rv_home_product.apply {
                    layoutManager = GridLayoutManager(contexts, 4)
                    addItemDecoration(GridSpacingItemDecoration(4, 30, true))
                    adapter = SubCategoryAdapter(contexts!!, categoryModel.subCategoryModelList)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contexts = activity
    }

}
