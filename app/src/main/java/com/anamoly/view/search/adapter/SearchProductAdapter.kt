package com.anamoly.view.search.adapter

import Config.BaseURL
import Config.GlobleVariable
import CustomViews.PriceView
import Database.CartData
import Dialogs.BottomSheetSuggestProductDialog
import Models.ProductModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.anamoly.CommonActivity
import com.anamoly.view.product_detail.ProductDetailActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.row_product.view.*
import kotlinx.android.synthetic.main.row_product_header.view.*
import kotlinx.android.synthetic.main.row_product_missing.view.*
import java.io.Serializable


class SearchProductAdapter(
    val context: Context,
    var modelList: ArrayList<ProductModel>,
    val itemSelected: ItemSelected
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val cartData = CartData(context)

    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_LIST = 2
        const val VIEW_TYPE_MISSING = 3
    }

    inner class MyHeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_title: TextView = view.tv_product_header_title
        val ll_more: LinearLayout = view.ll_product_header_more
        val linearLayout: LinearLayout = view.ll_product_header

        init {
            linearLayout.visibility = View.VISIBLE
            ll_more.visibility = View.GONE
        }

    }

    inner class MyMissingHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_send_request: TextView = view.tv_product_missing_request

        init {
            tv_send_request.setOnClickListener {
                val bottomSheetSuggestProductDialog = BottomSheetSuggestProductDialog()
                bottomSheetSuggestProductDialog.contexts = context
                if (bottomSheetSuggestProductDialog.isVisible) {
                    bottomSheetSuggestProductDialog.dismiss()
                } else {
                    val args = Bundle()
                    bottomSheetSuggestProductDialog.arguments = args
                    bottomSheetSuggestProductDialog.show(
                        (context as FragmentActivity).supportFragmentManager,
                        bottomSheetSuggestProductDialog.tag
                    )
                }
            }
        }

    }

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

                /*val cartQty = cartData.getProductQty(productModel.product_id!!)
                cartData.setProduct2(productModel, cartQty + 1)
                notifyItemChanged(position)*/
                itemSelected.onItemSelected(adapterPosition, iv_img, productModel)
            }
            iv_add.setOnClickListener {
                val position = adapterPosition
                val productModel = modelList[position]

                itemSelected.onImageSelected(position, iv_img, productModel)

                /*Intent(context, ProductDetailActivity::class.java).apply {
                    putExtra("productData", productModel as Serializable)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context as Activity,
                        iv_img,
                        ViewCompat.getTransitionName(iv_img)!!
                    )
                    context.startActivityForResult(this, 3564, options.toBundle())
                }*/
            }
            iv_minus.setOnClickListener {
                val position = adapterPosition
                val productModel = modelList[position]

                itemSelected.onRemove(position, iv_img, productModel)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (modelList[position].group_id.isNullOrEmpty()) {
            VIEW_TYPE_HEADER
        } else if (modelList[position].product_id.isNullOrEmpty()) {
            VIEW_TYPE_MISSING
        } else {
            VIEW_TYPE_LIST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_product_header, parent, false)
            MyHeaderHolder(itemView)
        } else if (viewType == VIEW_TYPE_MISSING) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_product_missing, parent, false)
            MyMissingHolder(itemView)
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
                .override(Target.SIZE_ORIGINAL, 150)
                .dontAnimate()
                .dontTransform()
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

            val qty = mList.cart_qty/*CommonActivity.getCartProductsQty(
                context,
                mList.product_id!!
            )*///cartData.getProductQty(mList.product_id!!)
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

        } else if (holder is MyMissingHolder) {
            val viewHolder = holder
        } else {
            val viewHolder = holder as MyHeaderHolder

            viewHolder.tv_title.text = CommonActivity.getStringByLanguage(
                context,
                mList.group_name_en,
                mList.group_name_ar,
                mList.group_name_nl,
                mList.group_name_tr,
                mList.group_name_de
            )

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