package com.anamoly.view.home.adapter

import Config.BaseURL
import Config.GlobleVariable
import CustomViews.PriceView
import Database.CartData
import Models.ProductComboModel
import Models.ProductModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.Indicators.PagerIndicator
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.daimajia.slider.library.Tricks.ViewPagerEx
import com.google.gson.Gson
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.home.MainActivity
import com.anamoly.view.product_detail.ProductDetailActivity
import com.anamoly.view.search.fragment.SearchProductFragment
import kotlinx.android.synthetic.main.row_product.view.*
import kotlinx.android.synthetic.main.row_product_header.view.*
import kotlinx.android.synthetic.main.row_slider.view.*
import utils.StickHeaderItemDecoration
import java.io.Serializable


class ProductAdapter(
    val context: Context,
    val itemSelected: ItemSelected,
    var modelList: ArrayList<ProductModel>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickHeaderItemDecoration.StickyHeaderInterface {

    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_SLIDER = 3
        const val VIEW_TYPE_LIST = 2
    }

    //val cartData = CartData(context)

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var itemPositions = itemPosition
        var headerPosition = 0
        do {
            if (isHeader(itemPositions)) {
                headerPosition = itemPositions
                break
            }
            itemPositions -= 1
        } while (itemPositions >= 0)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        if (modelList.size > 0) {
            if (!modelList[headerPosition].bannerModelList.isNullOrEmpty()) {
                return if (headerPosition == 0) R.layout.row_product_header2 else R.layout.row_product_header
            } else {
                return R.layout.row_product_header
            }
        } else {
            return R.layout.row_product_header2
        }
    }

    override fun bindHeaderData(header: View?, headerPosition: Int) {
        val mList = modelList[headerPosition]

        val tv_title: TextView = header!!.tv_product_header_title
        val ll_more: LinearLayout = header.ll_product_header_more
        val linearLayout: LinearLayout = header.ll_product_header

        tv_title.text = CommonActivity.getStringByLanguage(
            context,
            mList.sub_cat_name_en,
            mList.sub_cat_name_ar,
            mList.sub_cat_name_nl,
            mList.sub_cat_name_tr,
            mList.sub_cat_name_de
        )

        if (mList.sub_category_id.isNullOrEmpty()) {
            linearLayout.visibility = View.GONE
        } else {
            linearLayout.visibility = View.VISIBLE
        }

        ll_more.setOnClickListener {
            val fragment = SearchProductFragment()
            val args = Bundle()
            args.putSerializable(
                "title", CommonActivity.getStringByLanguage(
                    context,
                    mList.sub_cat_name_en,
                    mList.sub_cat_name_ar,
                    mList.sub_cat_name_nl,
                    mList.sub_cat_name_tr,
                    mList.sub_cat_name_de
                )
            )
            args.putSerializable("category_id", mList.category_id)
            args.putSerializable("sub_category_id", mList.sub_category_id)
            args.putBoolean("allowHeader", false)
            fragment.arguments = args
            (context as MainActivity).setFragment(fragment, true)
        }

    }

    override fun isHeader(itemPosition: Int): Boolean {
        if (modelList.size > 0) {
            return (modelList[itemPosition].product_id.isNullOrEmpty())
        } else {
            return false
        }
    }

    inner class MySliderHolder(view: View) : RecyclerView.ViewHolder(view),
        ViewPagerEx.OnPageChangeListener {
        val sliderLayout: SliderLayout = view.slider_home
        val pagerIndicator: PagerIndicator = view.pi_home

        init {
            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default)
            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
            sliderLayout.setCustomAnimation(DescriptionAnimation())
            sliderLayout.setCustomIndicator(pagerIndicator)
            sliderLayout.setDuration(10000)

            sliderLayout.addOnPageChangeListener(this)

        }

        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            modelList[0].slidePosition = position
        }

        override fun onPageSelected(position: Int) {

        }
    }

    inner class MyHeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_title: TextView = view.tv_product_header_title
        val ll_more: LinearLayout = view.ll_product_header_more
        val linearLayout: LinearLayout = view.ll_product_header

        init {
            ll_more.setOnClickListener {
                val position = adapterPosition
                val productModel = modelList[position]

                val fragment = SearchProductFragment()
                val args = Bundle()
                args.putSerializable(
                    "title", CommonActivity.getStringByLanguage(
                        context,
                        productModel.sub_cat_name_en,
                        productModel.sub_cat_name_ar,
                        productModel.sub_cat_name_nl,
                        productModel.sub_cat_name_tr,
                        productModel.sub_cat_name_de
                    )
                )
                args.putSerializable("category_id", productModel.category_id)
                args.putSerializable("sub_category_id", productModel.sub_category_id)
                args.putBoolean("allowHeader", false)
                fragment.arguments = args
                (context as MainActivity).setFragment(fragment, true)
            }
        }

    }

    val gson = Gson()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv_img: ImageView = view.iv_product_img
        val iv_add: CardView = view.iv_product_add
        val iv_minus: CardView = view.iv_product_minus
        val iv_express: ImageView = view.iv_product_express
        val tv_title: TextView = view.tv_product_title
        val tv_unit: TextView = view.tv_product_unit
        val tv_discount: TextView = view.tv_product_discount
        val tv_price_main: PriceView = view.tv_product_price_main
        val tv_price_discount: PriceView = view.tv_product_price_discount
        val tv_qty: TextView = view.tv_product_qty

        //val tv_qty: TextView = view.tv_product_counter
        val pb_qty: ProgressBar = view.pb_product_qty_load

        //val pb_qty: ProgressBar = view.pb_product_counter_load
        val cl_qtry: ConstraintLayout = view.cl_product_counter
        val ll_price_main: LinearLayout = view.ll_product_price_main
        val cardView: CardView = view.cv_product

        init {
            iv_img.setOnClickListener {
                val position = adapterPosition
                val productModel = modelList[position]

                /*val productComboModel = ProductComboModel()
                productModel.productComboModelList.add(productComboModel)

                val json = gson.toJson(productModel)*/

                /*val cartQty = cartData.getProductQty(productModel.product_id!!)
                cartData.setProduct2(productModel, cartQty + 1)
                notifyItemChanged(position)*/

                itemSelected.onItemSelected(position, iv_img, productModel)
            }
            iv_add.setOnClickListener {
                val position = adapterPosition
                val productModel = modelList[position]

                itemSelected.onImageSelected(position, iv_img, productModel)
            }
            iv_minus.setOnClickListener {
                val position = adapterPosition
                val productModel = modelList[position]

                itemSelected.onRemove(position, iv_img, productModel)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (modelList[position].bannerModelList != null) {
            VIEW_TYPE_SLIDER
        } else if (modelList[position].product_id.isNullOrEmpty()) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_LIST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_product_header, parent, false)
            MyHeaderHolder(itemView)
        } else if (viewType == VIEW_TYPE_SLIDER) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_slider, parent, false)
            MySliderHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_product, parent, false)
            MyViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mList = modelList[position]

        if (holder is MyViewHolder) {
            val viewHolder = holder

            Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + mList.product_image)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_place_holder)
                .dontAnimate()
                .dontTransform()
                .override(Target.SIZE_ORIGINAL, 150)
                .into(viewHolder.iv_img)

            viewHolder.tv_title.text = CommonActivity.getStringByLanguage(
                context,
                mList.product_name_en,
                mList.product_name_ar,
                mList.product_name_nl,
                mList.product_name_tr,
                mList.product_name_de
            )
            viewHolder.tv_unit.text = "${mList.unit_value} ${
                CommonActivity.getStringByLanguage(
                    context,
                    mList.unit,
                    mList.unit_ar,
                    mList.unit_en,
                    mList.unit_tr,
                    mList.unit_de
                )
            }"

            viewHolder.tv_price_main.setText(
                CommonActivity.getPriceWithCurrency(
                    context, String.format(
                        GlobleVariable.LOCALE,
                        "%.2f",
                        mList.price!!.toDouble()
                    ).replace(".00", "")
                )
            )

            viewHolder.tv_price_discount.setText(
                CommonActivity.getPriceWithCurrency(
                    context, String.format(
                        GlobleVariable.LOCALE,
                        "%.2f",
                        mList.price!!.toDouble()
                    ).replace(".00", "")
                )
            )

            if (!mList.discount.isNullOrEmpty()
                && mList.discount!!.toDouble() > 0
            ) {
                if (mList.discount_type.equals("flat")) {
                    viewHolder.ll_price_main.visibility = View.VISIBLE
                    viewHolder.tv_discount.visibility = View.VISIBLE

                    holder.tv_discount.text =
                        "${mList.discount} ${context.resources.getString(R.string.flat)}"

                    viewHolder.tv_price_discount.setText(
                        CommonActivity.getPriceWithCurrency(
                            context, String.format(
                                GlobleVariable.LOCALE,
                                "%.2f",
                                (mList.price!!.toDouble() - mList.discount!!.toDouble())
                            ).replace(".00", "")
                        )
                    )
                } else if (mList.discount_type.equals("percentage")) {
                    viewHolder.ll_price_main.visibility = View.VISIBLE
                    viewHolder.tv_discount.visibility = View.VISIBLE

                    holder.tv_discount.text =
                        "${mList.discount}% ${context.resources.getString(R.string.Discount)}"

                    viewHolder.tv_price_discount.setText(
                        CommonActivity.getPriceWithCurrency(
                            context, String.format(
                                GlobleVariable.LOCALE,
                                "%.2f",
                                CommonActivity.getDiscountPrice(
                                    mList.discount!!,
                                    mList.price!!,
                                    true,
                                    true
                                )
                            ).replace(".00", "")
                        )
                    )
                } else {
                    viewHolder.ll_price_main.visibility = View.GONE
                    viewHolder.tv_discount.visibility = View.GONE
                }
            } else {
                viewHolder.ll_price_main.visibility = View.GONE
                viewHolder.tv_discount.visibility = View.GONE
            }

            if (!mList.offer_type.isNullOrEmpty()) {
                holder.tv_discount.apply {
                    visibility = View.VISIBLE
                    setBackgroundResource(R.drawable.xml_discount_bg_purple)
                    text = if (mList.offer_type == "flatcombo") {
                        "${mList.number_of_products} ${context.resources.getString(R.string.fors)} ${
                            CommonActivity.getPriceWithCurrency(
                                context, String.format(
                                    GlobleVariable.LOCALE,
                                    "%.2f",
                                    mList.offer_discount!!.toDouble()
                                ).replace(".00", "")
                            )
                        }"
                    } else {
                        "${mList.number_of_products} + 1 ${context.resources.getString(R.string.free)}"
                    }
                }
            }

            val qty = mList.cart_qty
            if (qty > 0) {
                //holder.cl_qtry.visibility = View.VISIBLE
                holder.tv_qty.text = qty.toString()
                holder.iv_minus.visibility = View.VISIBLE
            } else {
                //holder.cl_qtry.visibility = View.GONE
                holder.iv_minus.visibility = View.GONE
            }

            if (mList.showQtyLoader) {
                holder.pb_qty.visibility = View.VISIBLE
                holder.tv_qty.visibility = View.GONE
            } else {
                holder.pb_qty.visibility = View.GONE
                holder.tv_qty.visibility = if (qty > 0) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            if (mList.is_express == "1") {
                holder.iv_express.visibility = View.VISIBLE
            } else {
                holder.iv_express.visibility = View.GONE
            }

            /*val layoutParams = viewHolder.cardView.layoutParams as ViewGroup.MarginLayoutParams
            val isLastColumn = ((position % 2) == 1)
            if (isLastColumn) {
                layoutParams.marginEnd = CommonActivity.dpToPx(context, 10F)
            } else {
                layoutParams.marginEnd = 0
            }*/

            val layoutParams = viewHolder.cardView.layoutParams as ViewGroup.MarginLayoutParams
            if (mList.addMargin) {
                layoutParams.marginEnd = CommonActivity.dpToPx(context, 10F)
            } else {
                layoutParams.marginEnd = 0
            }

        } else if (holder is MySliderHolder) {
            val viewHolder = holder as MySliderHolder

            viewHolder.sliderLayout.removeAllSliders()

            for (sliderModel in mList.bannerModelList!!) {
                val textSliderView = DefaultSliderView(context)
                // initialize a SliderLayout
                textSliderView
                    .image(BaseURL.IMG_BANNER_URL + sliderModel.banner_image)
                    .empty(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_place_holder)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                viewHolder.sliderLayout.addSlider(textSliderView)
            }

            //viewHolder.sliderLayout.setCurrentPosition(mList.slidePosition)

        } else {
            val viewHolder = holder as MyHeaderHolder

            if (mList.sub_category_id.isNullOrEmpty()) {
                viewHolder.linearLayout.visibility = View.GONE
            } else {
                viewHolder.linearLayout.visibility = View.VISIBLE
                viewHolder.tv_title.text = CommonActivity.getStringByLanguage(
                    context,
                    mList.sub_cat_name_en,
                    mList.sub_cat_name_ar,
                    mList.sub_cat_name_nl,
                    mList.sub_cat_name_tr,
                    mList.sub_cat_name_de
                )
            }
        }

    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    interface ItemSelected {
        fun onItemSelected(position: Int, view: View, productModel: ProductModel)
        fun onImageSelected(position: Int, view: View, productModel: ProductModel)
        fun onRemove(position: Int, view: View, productModel: ProductModel)
    }

}