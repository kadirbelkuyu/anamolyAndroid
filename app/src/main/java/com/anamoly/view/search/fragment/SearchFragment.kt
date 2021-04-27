package com.anamoly.view.search.fragment

import Models.CategoryModel
import Models.SearchTagModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CategoryResponse
import com.anamoly.response.SearchTagResponse
import com.anamoly.view.home.MainActivity
import com.anamoly.view.no_internet.NoInternetActivity
import com.anamoly.view.search.adapter.SearchCategoryAdapter
import com.anamoly.view.search.adapter.SearchTagAdapter
import com.anamoly.view.search.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.include_loader.view.*
import kotlinx.android.synthetic.main.include_toolbar.view.*
import kotlinx.coroutines.*
import utils.ConnectivityReceiver
import java.lang.Runnable


/**
 * Created on 18-01-2020.
 */
class SearchFragment : Fragment() {

    companion object {
        private val TAG = SearchFragment::class.java.simpleName
        var et_search: EditText? = null
    }

    val categoryModelList = ArrayList<CategoryModel>()
    val searchTagModelList = ArrayList<SearchTagModel>()

    lateinit var searchTagAdapter: SearchTagAdapter

    private var contexts: Context? = null
    lateinit var rootView: View
    lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_search, container, false)

        rootView.toolbar_home.setBackgroundColor(CommonActivity.getHeaderColor())
        rootView.tv_toolbar_title.setTextColor(CommonActivity.getHeaderTextColor())
        CommonActivity.setImageTint(rootView.iv_toolbar_back)

        /*rootView.isFocusableInTouchMode = true
        rootView.requestFocus()
        rootView.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (rootView.et_search_product.hasFocus()) {
                    rootView.et_search_product.clearFocus()
                } else {
                    //return@setOnKeyListener true
                }
            }
            return@setOnKeyListener true
        }*/

        rootView.tv_toolbar_title.text = resources.getString(R.string.search)
        rootView.iv_toolbar_barcode_scan.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                (contexts as MainActivity).openBarcodeSanner()
            }
        }

        searchTagAdapter = SearchTagAdapter(contexts!!, searchTagModelList)

        rootView.rv_search_category.layoutManager = LinearLayoutManager(contexts!!)
        rootView.rv_search_category_tag.apply {
            layoutManager = LinearLayoutManager(contexts!!)
            adapter = searchTagAdapter
        }

        et_search = rootView.et_search_product
        et_search?.imeOptions = EditorInfo.IME_ACTION_DONE

        // View Model
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        if (ConnectivityReceiver.isConnected) {
            makeGetCategoryList()
        } else {
            Intent(contexts!!, NoInternetActivity::class.java).apply {
                startActivityForResult(this, 9328)
            }
        }

        et_search!!.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                rootView.rv_search_category_tag.visibility = View.VISIBLE
            } else {
                rootView.rv_search_category_tag.visibility = View.GONE
            }
        }

        var job: Job? = null
        et_search!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length > 1) {
                    GlobalScope.launch(Dispatchers.IO) {
                        job?.cancel()
                        job = launch(Dispatchers.IO) {
                            delay(5_00)
                            launch(Dispatchers.Main) {
                                searchTagModelList.clear()
                                searchTagAdapter.notifyDataSetChanged()

                                searchViewModel.searchTagResponseLiveData?.removeObservers(this@SearchFragment)
                                makeGetSearchTag(s.toString())
                            }
                        }
                    }
                } else {
                    job?.cancel()
                    searchTagModelList.clear()
                    searchTagAdapter.notifyDataSetChanged()
                }
            }
        })

        return rootView
    }

    private fun makeGetCategoryList() {
        rootView.include_ll.visibility = View.VISIBLE
        rootView.rv_search_category.visibility = View.GONE

        searchViewModel.getCategoryList().observe(
            this,
            Observer { categoryResponse: CategoryResponse? ->
                if (categoryResponse != null) {
                    if (categoryResponse.responce!!) {
                        rootView.include_ll.visibility = View.GONE
                        rootView.rv_search_category.visibility = View.VISIBLE

                        categoryModelList.addAll(categoryResponse.categoryModelList!!)
                        rootView.rv_search_category.apply {
                            adapter = SearchCategoryAdapter(contexts!!, categoryModelList)
                            CommonActivity.runLayoutAnimation(this)
                        }
                    } else {
                        CommonActivity.showToast(contexts!!, categoryResponse.message!!)
                    }
                }
            }
        )

    }

    private fun makeGetSearchTag(searchText: String) {
        rootView.include_ll.visibility = View.VISIBLE

        searchTagAdapter.searchText = searchText

        val params = HashMap<String, String>()
        params["search"] = searchText
        searchViewModel.getSearchTag(params)

        searchViewModel.searchTagResponseLiveData!!.observe(
            this,
            Observer { response: SearchTagResponse? ->
                if (response != null) {
                    if (response.responce!!) {
                        rootView.include_ll.visibility = View.GONE

                        searchTagModelList.clear()
                        searchTagModelList.addAll(response.searchTagModelList!!)

                        searchTagAdapter.notifyDataSetChanged()
                        CommonActivity.runLayoutAnimation(rootView.rv_search_category_tag)
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
            makeGetCategoryList()
        }
    }

    override fun onAttach(context: Context) {
        this.contexts = context
        super.onAttach(context)
    }

}