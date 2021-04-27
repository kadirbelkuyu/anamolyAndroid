package com.anamoly.view.home.fragment

import Config.BaseURL
import Config.GlobleVariable
import Database.NotificationData
import Models.BannerModel
import Models.CategoryModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.anamoly.AppController
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.cart.CartFragment
import com.anamoly.view.home.HomeFragmentViewModel
import com.anamoly.view.home.MainActivity
import com.anamoly.view.no_internet.NoInternetActivity
import com.anamoly.view.notification.NotificationActivity
import com.bumptech.glide.Glide
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter
import kotlinx.android.synthetic.main.fragment_category.view.*
import kotlinx.android.synthetic.main.include_loader.view.*
import kotlinx.android.synthetic.main.row_slider.view.*
import org.json.JSONObject
import utils.ConnectivityReceiver
import utils.SessionManagement


/**
 * Created on 18-01-2020.
 */
class CategoryFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = CategoryFragment::class.java.simpleName

        var viewPager: ViewPager? = null
    }

    var fragmentAdapter: FragmentStatePagerItemAdapter? = null

    var tagModelList = ArrayList<CategoryModel>()
    var categoryModelSelected: CategoryModel? = null

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
        rootView = inflater.inflate(R.layout.fragment_category, container, false)

        viewPager = rootView.vp_home_category

        if (arguments != null && arguments!!.containsKey("categoryModelData")) {
            categoryModelSelected =
                arguments!!.getSerializable("categoryModelData") as CategoryModel
        }

        rootView.toolbar_home.setBackgroundColor(CommonActivity.getHeaderColor())

        Glide.with(contexts!!)
            .load(BaseURL.IMG_HEADER_URL + AppController.headerLogo)
            .placeholder(R.drawable.ic_logo_text)
            .error(R.drawable.ic_logo_text)
            .into(rootView.iv_toolbar_logo)

        rootView.slider_home.setPresetTransformer(SliderLayout.Transformer.Default)
        rootView.slider_home.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        rootView.slider_home.setCustomAnimation(DescriptionAnimation())
        rootView.slider_home.setCustomIndicator(rootView.pi_home)
        rootView.slider_home.setDuration(10000)

        rootView.iv_home_barcode_scan.setOnClickListener(this)
        rootView.cl_home_cart.setOnClickListener(this)
        rootView.iv_home_notification.setOnClickListener(this)

        homeFragmentViewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)

        rootView.stl_home_category.setDefaultTabTextColor(makeSelector())

        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = CommonActivity.dpToPx(contexts!!, 5F).toFloat()
        gradientDrawable.setColor(CommonActivity.getHeaderColor())

        val gradientDrawableNormal = GradientDrawable()
        gradientDrawableNormal.cornerRadius = CommonActivity.dpToPx(contexts!!, 5F).toFloat()
        gradientDrawableNormal.setColor(ContextCompat.getColor(contexts!!, R.color.colorWhite))

        val res = StateListDrawable()
        res.addState(intArrayOf(android.R.attr.state_selected), gradientDrawable)
        res.addState(intArrayOf(android.R.attr.state_empty), gradientDrawableNormal)

        //rootView.stl_home_category.setDefaultTabBackground(res)

        if (ConnectivityReceiver.isConnected) {
            makeGetTabList()
        } else {
            Intent(contexts!!, NoInternetActivity::class.java).apply {
                startActivityForResult(this, 9328)
            }
        }

        rootView.stl_home_category.setOnTabClickListener {
            if (it >= 0) {
                MainActivity.categoryModelSelected = tagModelList[it]
            }
        }

        return rootView
    }

    fun makeSelector(): ColorStateList {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_selected),
            intArrayOf()
        )

        val colors = intArrayOf(
            CommonActivity.getHeaderTextColor(),
            CommonActivity.getHeaderColor()
        )
        return ColorStateList(states, colors)
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

    private fun makeGetTabList() {
        rootView.include_ll.visibility = View.VISIBLE
        viewPager!!.visibility = View.GONE

        val params = HashMap<String, String>()

        homeFragmentViewModel.getHomeList(params).observe(
            this,
            Observer { response: CommonResponse? ->
                if (response != null) {
                    if (response.responce!!) {
                        rootView.include_ll.visibility = View.GONE
                        viewPager!!.visibility = View.VISIBLE

                        val jsonObject = JSONObject(response.data.toString())

                        val gson = Gson()

                        if (jsonObject.has("banners")) {
                            val typeProduct = object : TypeToken<ArrayList<BannerModel>>() {}.type
                            val bannerModelList = gson.fromJson<ArrayList<BannerModel>>(
                                jsonObject.getJSONArray("banners").toString(), typeProduct
                            )

                            rootView.slider_home.removeAllSliders()

                            bannerModelList.forEach {
                                val textSliderView = DefaultSliderView(context)
                                // initialize a SliderLayout
                                textSliderView
                                    .image(BaseURL.IMG_BANNER_URL + it.banner_image)
                                    .empty(R.drawable.ic_place_holder)
                                    .error(R.drawable.ic_place_holder).scaleType =
                                    BaseSliderView.ScaleType.CenterCrop
                                rootView.slider_home.addSlider(textSliderView)
                            }
                        }

                        if (jsonObject.has("categories")) {
                            val typeProduct = object : TypeToken<ArrayList<CategoryModel>>() {}.type
                            tagModelList.clear()
                            tagModelList = gson.fromJson<ArrayList<CategoryModel>>(
                                jsonObject.getJSONArray("categories").toString(), typeProduct
                            )

                            val fragmentPagerItems = FragmentPagerItems.with(contexts)
                            tagModelList.forEach {
                                val args = Bundle()
                                args.putString(
                                    "title", CommonActivity.getStringByLanguage(
                                        contexts!!,
                                        it.cat_name_en,
                                        it.cat_name_ar,
                                        it.cat_name_nl,
                                        it.cat_name_tr,
                                        it.cat_name_de,
                                    )
                                )
                                args.putSerializable(
                                    "tagData",
                                    it
                                )

                                fragmentPagerItems.add(
                                    CommonActivity.getStringByLanguage(
                                        contexts!!,
                                        it.cat_name_en,
                                        it.cat_name_ar,
                                        it.cat_name_nl,
                                        it.cat_name_tr,
                                        it.cat_name_de,
                                    ),
                                    SubCategoryFragment::class.java,
                                    args
                                )
                            }
                            fragmentAdapter = FragmentStatePagerItemAdapter(
                                childFragmentManager,
                                fragmentPagerItems.create()
                            )

                            viewPager?.adapter = fragmentAdapter
                            rootView.stl_home_category.setViewPager(viewPager)

                            if (tagModelList.isNotEmpty()) {
                                MainActivity.categoryModelSelected = tagModelList[0]
                            }
                        }

                        MainActivity.categoryModelList.clear()
                        if (jsonObject.has("is_featured")) {
                            val typeProduct = object : TypeToken<ArrayList<CategoryModel>>() {}.type
                            val categoryModelList = gson.fromJson<ArrayList<CategoryModel>>(
                                jsonObject.getJSONArray("is_featured").toString(), typeProduct
                            )
                            MainActivity.categoryModelList.addAll(categoryModelList)
                        }

                    } else {
                        CommonActivity.showToast(contexts!!, response.message!!)
                    }
                }
            })
    }

    fun updateViewpager(categoryModel: CategoryModel) {
        var selectedPosition = 0
        for ((index, categoryModellist) in tagModelList.withIndex()) {
            if (categoryModellist.category_id == categoryModel.category_id) {
                selectedPosition = index
                break
            }
        }
        viewPager?.post {
            viewPager?.currentItem = selectedPosition
        }
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
            //(fragmentAdapter!!.getPage(viewPager!!.currentItem) as SubCategoryFragment).productAdapter!!.notifyDataSetChanged()
            if (SessionManagement.CommonData.getSessionBoolean(contexts!!, "isRefreshHome")) {
                SessionManagement.CommonData.setSession(contexts!!, "isRefreshHome", false)
                /*if (fragmentAdapter!!.count > 0) {
                    try {
                        (fragmentAdapter?.getPage(viewPager!!.currentItem) as SubCategoryFragment?)?.updateView()
                        (fragmentAdapter?.getPage(viewPager!!.currentItem + 1) as SubCategoryFragment?)?.updateView()
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    } catch (e: TypeCastException) {
                        e.printStackTrace()
                    }
                }*/
            }
        }
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