package com.anamoly.view.home.fragment

import Config.BaseURL
import Config.GlobleVariable
import Database.NotificationData
import Models.HomeProductTagModel
import Models.TagModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.anamoly.AppController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.cart.CartFragment
import com.anamoly.view.home.MainActivity
import com.anamoly.response.HomeProductTagResponse
import com.anamoly.view.no_internet.NoInternetActivity
import com.anamoly.view.notification.NotificationActivity
import com.anamoly.view.home.HomeFragmentViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.include_loader.view.*
import utils.ConnectivityReceiver
import utils.SessionManagement


/**
 * Created on 18-01-2020.
 */
class HomeFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = HomeFragment::class.java.simpleName

        var viewPager: ViewPager? = null
        val homeProductTagModelList = ArrayList<HomeProductTagModel>()
        val homeProductTitleList = ArrayList<String>()
    }

    var fragmentAdapter: FragmentStatePagerItemAdapter? = null

    lateinit var homeFragmentViewModel: HomeFragmentViewModel
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
        rootView = inflater.inflate(R.layout.fragment_home, container, false)

        rootView.toolbar_home.setBackgroundColor(CommonActivity.getHeaderColor())

        Glide.with(contexts!!)
            .load(
                BaseURL.IMG_HEADER_URL + AppController.headerLogo
            )
            .placeholder(R.drawable.ic_logo_text)
            .error(R.drawable.ic_logo_text)
            .into(rootView.iv_toolbar_logo)

        viewPager = rootView.vp_home

        rootView.iv_home_barcode_scan.setOnClickListener(this)
        rootView.cl_home_cart.setOnClickListener(this)
        rootView.iv_home_notification.setOnClickListener(this)

        homeFragmentViewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)

        if (ConnectivityReceiver.isConnected) {
            //makeGetHomeProductList()
            makeGetTabList()
        } else {
            Intent(contexts!!, NoInternetActivity::class.java).apply {
                startActivityForResult(this, 9328)
            }
        }

        return rootView
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_home_barcode_scan -> {
                (contexts as MainActivity).openBarcodeSanner()
            }
            R.id.cl_home_cart -> {
                (contexts as MainActivity).setFragment(CartFragment(), false)
            }
            R.id.iv_home_notification -> {
                Intent(contexts!!, NotificationActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    private fun makeGetHomeProductList() {
        rootView.include_ll.visibility = View.VISIBLE
        viewPager!!.visibility = View.GONE

        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(contexts!!, BaseURL.KEY_ID)

        homeFragmentViewModel.getHomeProduct(params).observe(
            this,
            Observer { response: HomeProductTagResponse? ->
                if (response != null) {
                    if (response.responce!!) {

                        rootView.include_ll.visibility = View.GONE
                        viewPager!!.visibility = View.VISIBLE

                        homeProductTagModelList.clear()
                        homeProductTitleList.clear()

                        homeProductTagModelList.addAll(response.homeProductTagModelList!!)

                        val fragmentPagerItems = FragmentPagerItems.with(contexts)
                        for (productTagModel: HomeProductTagModel in homeProductTagModelList) {
                            homeProductTitleList.add(
                                CommonActivity.getStringByLanguage(
                                    contexts!!,
                                    productTagModel.tag_name_en,
                                    productTagModel.tag_name_ar,
                                    productTagModel.tag_name_nl,
                                    productTagModel.tag_name_tr,
                                    productTagModel.tag_name_de
                                )!!
                            )
                            val args = Bundle()
                            args.putString(
                                "title", CommonActivity.getStringByLanguage(
                                    contexts!!,
                                    productTagModel.tag_name_en,
                                    productTagModel.tag_name_ar,
                                    productTagModel.tag_name_nl,
                                    productTagModel.tag_name_tr,
                                    productTagModel.tag_name_de
                                )
                            )
                            args.putSerializable(
                                "homeTagData",
                                productTagModel
                            )

                            fragmentPagerItems.add(
                                CommonActivity.getStringByLanguage(
                                    contexts!!,
                                    productTagModel.tag_name_en,
                                    productTagModel.tag_name_ar,
                                    productTagModel.tag_name_nl,
                                    productTagModel.tag_name_tr,
                                    productTagModel.tag_name_de
                                ),
                                HomeProductFragment::class.java,
                                args
                            )
                        }
                        //val adapters = FragmentPagerItemAdapter(childFragmentManager, fragmentPagerItems.create())
                        fragmentAdapter = FragmentStatePagerItemAdapter(
                            childFragmentManager,
                            fragmentPagerItems.create()
                        )

                        viewPager?.adapter = fragmentAdapter
                        rootView.stl_home.setViewPager(viewPager)
                        /*viewPager!!.adapter = HomeFragmentAdapter(
                            childFragmentManager,
                            false
                        )
                        rootView.stl_home_2.setViewPager(viewPager)*/
                        viewPager?.offscreenPageLimit = homeProductTitleList.size

                    } else {
                        CommonActivity.showToast(contexts!!, response.message!!)
                    }
                }
            })

    }

    private fun makeGetTabList() {
        rootView.include_ll.visibility = View.VISIBLE
        viewPager!!.visibility = View.GONE

        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(contexts!!, BaseURL.KEY_ID)

        homeFragmentViewModel.getTabList(params).observe(
            this,
            Observer { response: CommonResponse? ->
                if (response != null) {
                    if (response.responce!!) {
                        rootView.include_ll.visibility = View.GONE
                        viewPager!!.visibility = View.VISIBLE

                        val gson = Gson()
                        val typeProduct = object : TypeToken<ArrayList<TagModel>>() {}.type
                        val tagModelList =
                            gson.fromJson<ArrayList<TagModel>>(response.data, typeProduct)

                        homeProductTagModelList.clear()
                        homeProductTitleList.clear()

                        //homeProductTagModelList.addAll(response.homeProductTagModelList!!)

                        val fragmentPagerItems = FragmentPagerItems.with(contexts)
                        for (tagModel: TagModel in tagModelList) {
                            homeProductTitleList.add(
                                CommonActivity.getStringByLanguage(
                                    contexts!!,
                                    tagModel.tag_name_en,
                                    tagModel.tag_name_ar,
                                    tagModel.tag_name_nl,
                                    tagModel.tag_name_tr,
                                    tagModel.tag_name_de
                                )!!
                            )
                            val args = Bundle()
                            args.putString(
                                "title", CommonActivity.getStringByLanguage(
                                    contexts!!,
                                    tagModel.tag_name_en,
                                    tagModel.tag_name_ar,
                                    tagModel.tag_name_nl,
                                    tagModel.tag_name_tr,
                                    tagModel.tag_name_de
                                )
                            )
                            args.putSerializable(
                                "tagData",
                                tagModel
                            )

                            fragmentPagerItems.add(
                                CommonActivity.getStringByLanguage(
                                    contexts!!,
                                    tagModel.tag_name_en,
                                    tagModel.tag_name_ar,
                                    tagModel.tag_name_nl,
                                    tagModel.tag_name_tr,
                                    tagModel.tag_name_de
                                ),
                                HomeProductFragment::class.java,
                                args
                            )
                        }
                        //val adapters = FragmentPagerItemAdapter(childFragmentManager, fragmentPagerItems.create())
                        fragmentAdapter = FragmentStatePagerItemAdapter(
                            childFragmentManager,
                            fragmentPagerItems.create()
                        )

                        viewPager?.adapter = fragmentAdapter
                        rootView.stl_home.setViewPager(viewPager)
                        /*viewPager!!.adapter = HomeFragmentAdapter(
                            childFragmentManager,
                            false
                        )
                        rootView.stl_home_2.setViewPager(viewPager)*/
                        //viewPager?.offscreenPageLimit = homeProductTitleList.size

                    } else {
                        CommonActivity.showToast(contexts!!, response.message!!)
                    }
                }
            })
    }

    fun updateCart() {
        if (::rootView.isInitialized) {
            val total =
                CommonActivity.getCartTotalAmount(contexts!!)//CartData(contexts!!).getProductTotalPrice(true)
            if (total > 0) {
                rootView.tv_home_cart_counter.visibility = View.VISIBLE
                rootView.tv_home_cart_counter.text = String.format(
                    GlobleVariable.LOCALE,
                    "%.2f",
                    total
                ).replace(".00", "")
            } else {
                rootView.tv_home_cart_counter.visibility = View.GONE
            }
            val totalNotification = NotificationData(contexts!!).getNotificationList().size
            if (totalNotification > 0) {
                rootView.view_home_notification_dot.background = CommonActivity.getCircleBG2()
                rootView.view_home_notification_dot.visibility = View.VISIBLE
            } else {
                rootView.view_home_notification_dot.visibility = View.GONE
            }
        }
    }

    fun updateData() {
        if (contexts != null && fragmentAdapter != null && viewPager != null) {
            //(fragmentAdapter!!.getPage(viewPager!!.currentItem) as HomeProductFragment).productAdapter!!.notifyDataSetChanged()
            if (SessionManagement.CommonData.getSessionBoolean(contexts!!, "isRefreshHome")) {
                SessionManagement.CommonData.setSession(contexts!!, "isRefreshHome", false)
                if (fragmentAdapter!!.count > 0) {
                    try {
                        (fragmentAdapter?.getPage(viewPager!!.currentItem) as HomeProductFragment?)?.updateView()
                        (fragmentAdapter?.getPage(viewPager!!.currentItem + 1) as HomeProductFragment?)?.updateView()
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    } catch (e: TypeCastException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        /*if (contexts != null) {
            if (SessionManagement.CommonData.getSessionBoolean(contexts!!, "isRefreshHome")) {
                SessionManagement.CommonData.setSession(contexts!!, "isRefreshHome", false)
                makeGetTabList()
            }
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9328 && resultCode == Activity.RESULT_OK) {
            //makeGetHomeProductList()
            makeGetTabList()
        }
    }

    override fun onResume() {
        super.onResume()
        //updateCart()
        //updateData()
    }

    override fun onAttach(context: Context) {
        this.contexts = context
        super.onAttach(context)
    }

}