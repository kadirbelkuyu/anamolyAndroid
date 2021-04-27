package com.anamoly.view.order.adapter

import Config.BaseURL
import Config.GlobleVariable
import CustomViews.PriceView
import Models.OrderItemModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.row_order_item.view.*


class OrderItemAdapter(val context: Context, var modelList: ArrayList<OrderItemModel>) :
    RecyclerView.Adapter<OrderItemAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv_img: ImageView = view.iv_order_item_img
        val tv_title: TextView = view.tv_order_item_title
        val tv_unit: TextView = view.tv_order_item_unit
        val tv_price_main: PriceView = view.tv_order_item_price_main
        val tv_price_discount: PriceView = view.tv_order_item_price_discount
        val tv_discount: TextView = view.tv_order_item_discount
        val tv_qty: TextView = view.tv_order_item_qty
        val cl_price_main: ConstraintLayout = view.cl_order_item_price_main

        init {
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_order_item, parent, false)
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
        holder.tv_unit.text =
            "${mList.unit_value} ${
                CommonActivity.getStringByLanguage(
                    context,
                    mList.unit,
                    mList.unit_ar,
                    mList.unit_en,
                    mList.unit_tr,
                    mList.unit_de
                )
            }"

        val qty = mList.order_qty!!.toInt()
        holder.tv_qty.text = qty.toString()

        val total = mList.price.toString().toDouble() * qty
        holder.tv_price_main.setText(
            CommonActivity.getPriceWithCurrency(
                context,
                String.format(GlobleVariable.LOCALE, "%.2f", total).replace(".00", "")
            )
        )

        holder.tv_price_discount.setText(
            CommonActivity.getPriceWithCurrency(
                context,
                String.format(GlobleVariable.LOCALE, "%.2f", total).replace(".00", "")
            )
        )

        if (!mList.discount.isNullOrEmpty()
            && mList.discount.toDouble() > 0
        ) {
            if (mList.discount_type.equals("flat")) {
                holder.cl_price_main.visibility = View.VISIBLE
                holder.tv_discount.visibility = View.VISIBLE

                holder.tv_discount.text =
                    "${mList.discount} ${context.resources.getString(R.string.flat)}"
                /*holder.tv_price_discount.setText(
                    CommonActivity.getPriceWithCurrency(
                        context, String.format(
                            GlobleVariable.LOCALE,
                            "%.2f",
                            (total - mList.discount!!.toDouble())
                        )
                    ).replace(".00", "")
                )*/

                val totalPrice = mList.product_price.toString().toDouble() * qty
                holder.tv_price_main.setText(
                    CommonActivity.getPriceWithCurrency(
                        context,
                        String.format(GlobleVariable.LOCALE, "%.2f", totalPrice).replace(".00", "")
                    )
                )
            } else if (mList.discount_type.equals("percentage")) {
                holder.cl_price_main.visibility = View.VISIBLE
                holder.tv_discount.visibility = View.VISIBLE

                holder.tv_discount.text =
                    "${mList.discount}% ${context.resources.getString(R.string.Discount)}"
                /*holder.tv_price_discount.setText(
                    CommonActivity.getPriceWithCurrency(
                        context, String.format(
                            GlobleVariable.LOCALE,
                            "%.2f",
                            CommonActivity.getDiscountPrice(
                                mList.discount!!,
                                total.toString(),
                                true,
                                true
                            )
                        ).replace(".00", "")
                    )
                )*/
                val totalPrice = mList.product_price.toString().toDouble() * qty
                holder.tv_price_main.setText(
                    CommonActivity.getPriceWithCurrency(
                        context,
                        String.format(GlobleVariable.LOCALE, "%.2f", totalPrice).replace(".00", "")
                    )
                )
            } else {
                holder.cl_price_main.visibility = View.GONE
                holder.tv_discount.visibility = View.GONE
            }
        } else {
            holder.cl_price_main.visibility = View.GONE
            holder.tv_discount.visibility = View.GONE
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

    }

    override fun getItemCount(): Int {
        return modelList.size
    }

}