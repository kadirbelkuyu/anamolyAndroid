package com.anamoly.view.search.fragment

import Models.CategoryModel
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.home.MainActivity
import com.anamoly.view.search.adapter.SearchSubCategoryAdapter
import kotlinx.android.synthetic.main.activity_search_sub_category.view.*
import kotlinx.android.synthetic.main.include_toolbar.view.*

/**
 * Created on 18-01-2020.
 */
class SearchSubCategoryFragment : Fragment() {

    companion object {
        private val TAG = SearchSubCategoryFragment::class.java.simpleName
    }

    lateinit var contexts: Context
    lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.activity_search_sub_category, container, false)

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

        val categoryModel = arguments!!.getSerializable("categoryData") as CategoryModel

        rootView.tv_toolbar_title.text = CommonActivity.getStringByLanguage(
            contexts,
            categoryModel.cat_name_en,
            categoryModel.cat_name_ar,
            categoryModel.cat_name_nl,
            categoryModel.cat_name_tr,
            categoryModel.cat_name_de
        )!!

        rootView.rv_search_sub_category.apply {
            layoutManager = LinearLayoutManager(contexts)
            adapter = SearchSubCategoryAdapter(
                contexts,
                categoryModel.subCategoryModelList!!
            )
            CommonActivity.runLayoutAnimation(this)
        }

        return rootView
    }

    override fun onAttach(context: Context) {
        this.contexts = context
        super.onAttach(context)
    }

}