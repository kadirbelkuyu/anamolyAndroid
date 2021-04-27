package com.anamoly.view.cart.adapter

import Config.BaseURL
import Config.GlobleVariable
import CustomViews.PriceView
import Models.CartItemModel
import Models.CartModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.anamoly.AppController
import com.bumptech.glide.Glide
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.product_detail.ProductDetailActivity
import kotlinx.android.synthetic.main.row_cart_item.view.*
import java.io.Serializable


class CartItemAdapter2(
    val context: Context,
    val cartModel: CartModel,
    val mainPosition: Int,
    var modelList: ArrayList<CartItemModel>,
    var onItemClick: OnItemClick
) :
    RecyclerView.Adapter<CartItemAdapter2.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val iv_img: ImageView = view.iv_cart_img
        val iv_express: ImageView = view.iv_cart_express
        val tv_title: TextView = view.tv_cart_title
        val tv_unit: TextView = view.tv_cart_unit
        val tv_price_main: PriceView = view.tv_cart_price_main
        val tv_price_discount: PriceView = view.tv_cart_price_discount
        val tv_discount: TextView = view.tv_cart_discount
        val tv_qty: TextView = view.tv_cart_qty
        val cl_price_main: ConstraintLayout = view.cl_cart_price_main
        val ll_action: LinearLayout = view.ll_cart_action
        val iv_add: ImageView = view.iv_cart_add
        val iv_remove: ImageView = view.iv_cart_remove
        val line_top: View = view.view_cart_item_line_top
        val line_bottom: View = view.view_cart_item_line_bottom

        init {

            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = CommonActivity.dpToPx(context, 5F).toFloat()
            gradientDrawable.setColor(AppController.secondButtonColor)

            iv_add.background = gradientDrawable
            iv_remove.background = gradientDrawable

            iv_img.setOnClickListener(this)
            tv_qty.setOnClickListener(this)
            iv_add.setOnClickListener(this)
            iv_remove.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            val cartItemModel = modelList[position]

            when (v!!.id) {
                R.id.iv_cart_img -> {
                    /*Intent(context, ProductDetailActivity::class.java).apply {
                        putExtra("cartItemData", cartItemModel as Serializable)
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as Activity,
                            iv_img,
                            ViewCompat.getTransitionName(iv_img)!!
                        )
                        context.startActivity(this, options.toBundle())
                    }*/
                    onItemClick.imageClick(mainPosition, position, iv_img, cartModel, cartItemModel)
                }
                R.id.tv_cart_qty -> {
                    if (ll_action.visibility == View.VISIBLE) {
                        ll_action.visibility = View.GONE
                    } else {
                        ll_action.visibility = View.VISIBLE
                        ll_action.postDelayed(Runnable {
                            ll_action.visibility = View.GONE
                        }, 5000)
                    }
                }
                R.id.iv_cart_add -> {
                    onItemClick.addClick(mainPosition, position, cartModel, cartItemModel)
                }
                R.id.iv_cart_remove -> {
                    onItemClick.removeClick(mainPosition, position, cartModel, cartItemModel)
                }
                R.id.tv_cart_delete -> {
                    onItemClick.deleteClick(mainPosition, position, cartModel, cartItemModel)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_cart_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        Glide.with(context)
            .load(BaseURL.IMG_PRODUCT_URL + mList.product_image)
            .placeholder(R.drawable.ic_place_holder)
            .error(R.drawable.ic_place_holder)
            .into(holder.iv_img)

        holder.tv_title.text = CommonActivity.getStringByLanguage(
            context,
            mList.product_name_en,
            mList.product_name_ar,
            mList.product_name_nl,
            mList.product_name_tr,
            mList.product_name_de
        )
        holder.tv_unit.text = "${mList.unit_value} ${
            CommonActivity.getStringByLanguage(
                context,
                mList.unit,
                mList.unit_ar,
                mList.unit_en,
                mList.unit_tr,
                mList.unit_de
            )
        }"

        val qty = mList.qty
        val itemTotal = cartModel.items_total_price.toString().toDouble()// * qty
        val total = cartModel.effected_price.toString().toDouble()// * qty

        holder.tv_qty.text = qty.toString()

        if (itemTotal > total) {
            holder.cl_price_main.visibility = View.VISIBLE

            holder.tv_price_main.setText(
                CommonActivity.getPriceWithCurrency(
                    context,
                    String.format(GlobleVariable.LOCALE, "%.2f", itemTotal).replace(".00", "")
                )
            )

            holder.tv_price_discount.setText(
                CommonActivity.getPriceWithCurrency(
                    context,
                    String.format(GlobleVariable.LOCALE, "%.2f", total).replace(".00", "")
                )
            )
        } else {
            holder.cl_price_main.visibility = View.GONE

            holder.tv_price_discount.setText(
                CommonActivity.getPriceWithCurrency(
                    context,
                    String.format(GlobleVariable.LOCALE, "%.2f", total).replace(".00", "")
                )
            )
        }

        if (position == modelList.size - 1) {
            if (mList.offer_type != null) {
                holder.tv_discount.visibility = View.GONE
            } else if (!mList.discount.isNullOrEmpty()
                && mList.discount!!.toDouble() > 0
            ) {
                if (mList.discount_type.equals("flat")) {
                    holder.tv_discount.visibility = View.VISIBLE

                    holder.tv_discount.text =
                        "${mList.discount} ${context.resources.getString(R.string.flat)}"
                } else if (mList.discount_type.equals("percentage")) {
                    holder.tv_discount.visibility = View.VISIBLE

                    holder.tv_discount.text =
                        "${mList.discount}% ${context.resources.getString(R.string.Discount)}"
                } else {
                    holder.tv_discount.visibility = View.GONE
                }
            } else {
                holder.tv_discount.visibility = View.GONE
            }
        } else {
            holder.cl_price_main.visibility = View.GONE
            holder.tv_discount.visibility = View.GONE
            holder.tv_price_discount.visibility = View.GONE
        }

        if (!mList.offer_type.isNullOrEmpty()) {
            holder.tv_discount.apply {
                visibility = View.VISIBLE
                setBackgroundResource(R.drawable.xml_discount_bg_purple2)
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

        if (modelList.size <= 1) {
            holder.tv_qty.isEnabled = modelList.size <= 1
            holder.line_top.visibility = View.INVISIBLE
            holder.line_bottom.visibility = View.INVISIBLE
        } else {
            holder.tv_qty.isEnabled = modelList.size <= 1
            if (position == 0) {
                holder.line_top.visibility = View.INVISIBLE
            } else if (position == modelList.size - 1) {
                holder.line_bottom.visibility = View.INVISIBLE
            } else {
                holder.line_top.visibility = View.VISIBLE
                holder.line_bottom.visibility = View.VISIBLE
            }
        }

        if (mList.is_express == "1") {
            holder.iv_express.visibility = View.VISIBLE
        } else {
            holder.iv_express.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    interface OnItemClick {
        fun imageClick(
            positionMain: Int,
            position: Int,
            view: View,
            cartModel: CartModel,
            cartItemModel: CartItemModel
        )

        fun addClick(
            positionMain: Int,
            position: Int,
            cartModel: CartModel,
            cartItemModel: CartItemModel
        )

        fun removeClick(
            positionMain: Int,
            position: Int,
            cartModel: CartModel,
            cartItemModel: CartItemModel
        )

        fun deleteClick(
            positionMain: Int,
            position: Int,
            cartModel: CartModel,
            cartItemModel: CartItemModel
        )
    }

}