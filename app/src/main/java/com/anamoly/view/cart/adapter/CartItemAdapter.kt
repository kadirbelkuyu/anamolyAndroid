package com.anamoly.view.cart.adapter

import Config.BaseURL
import Config.GlobleVariable
import CustomViews.PriceView
import Database.CartData
import Models.ProductComboModel
import Models.ProductModel
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.daimajia.swipe.SwipeLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.product_detail.ProductDetailActivity
import kotlinx.android.synthetic.main.row_cart_item.view.*
import utils.LanguagePrefs
import java.io.Serializable


class CartItemAdapter(
    val context: Context,
    var modelList: ArrayList<ProductComboModel>,
    val cart_id: Int
) :
    RecyclerView.Adapter<CartItemAdapter.MyViewHolder>() {

    val cartData = CartData(context)

    val gson = Gson()
    val gsonBuilder = GsonBuilder().create()
    val typeProductModel = object : TypeToken<ProductModel>() {}.type
    val typeProductComboModel = object : TypeToken<ProductComboModel>() {}.type

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val iv_img: ImageView = view.iv_cart_img
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

            iv_img.setOnClickListener(this)
            tv_qty.setOnClickListener(this)
            iv_add.setOnClickListener(this)
            iv_remove.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            val productComboModel = modelList[position]

            val productModel = gson.fromJson<ProductModel>(
                gsonBuilder.toJsonTree(productComboModel).asJsonObject, typeProductModel
            )

            when (v!!.id) {
                R.id.iv_cart_img -> {
                    Intent(context, ProductDetailActivity::class.java).apply {
                        putExtra("productComboModel", productComboModel as Serializable)
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as Activity,
                            iv_img,
                            ViewCompat.getTransitionName(iv_img)!!
                        )
                        context.startActivity(this, options.toBundle())
                    }
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
                    val cartQty = cartData.getProductQty(productComboModel.product_id!!)
                    cartData.setProduct2(productModel, cartQty + 1)
                    val productModelCart = cartData.getProductDetail(productComboModel.product_id!!)
                    val productComboModel2 = gson.fromJson<ProductComboModel>(
                        gsonBuilder.toJsonTree(productModelCart).asJsonObject, typeProductComboModel
                    )
                    if (productComboModel2 != null) {
                        modelList[position] = productComboModel2
                        notifyItemChanged(position)
                        Intent("cartUpdate").apply {
                            putExtra("type", "update")
                            context.sendBroadcast(this)
                        }
                    }
                }
                R.id.iv_cart_remove -> {
                    val cartQty = cartData.getProductQty(productComboModel.product_id!!) - 1
                    if (cartQty <= 0) {
                        cartData.deleteProduct(cart_id)
                        modelList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(0, modelList.size)
                        Intent("cartUpdate").apply {
                            putExtra("type", "update")
                            context.sendBroadcast(this)
                        }
                    } else {
                        cartData.setProduct2(productModel, cartQty)
                        val productModelCart = cartData.getProductDetail(productModel.product_id!!)
                        val productComboModel2 = gson.fromJson<ProductComboModel>(
                            gsonBuilder.toJsonTree(productModelCart).asJsonObject,
                            typeProductComboModel
                        )
                        if (productComboModel2 != null) {
                            modelList[position] = productComboModel2
                            notifyItemChanged(position)
                            Intent("cartUpdate").apply {
                                putExtra("type", "update")
                                context.sendBroadcast(this)
                            }
                        }
                    }
                }
                R.id.tv_cart_delete -> {
                    cartData.deleteProduct(cart_id)
                    modelList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(0, modelList.size)
                    notifyDataSetChanged()
                    Intent("cartUpdate").apply {
                        putExtra("type", "update")
                        context.sendBroadcast(this)
                    }
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
        holder.tv_unit.text = "${mList.unit_value} ${mList.unit}"

        val qty = mList.qty_cart
        val total = mList.price.toString().toDouble() * qty

        holder.tv_qty.text = qty.toString()

        holder.tv_price_main.setText(
            CommonActivity.getPriceWithCurrency(
                context,
                String.format(GlobleVariable.LOCALE, "%.1f", total).replace(".0", "")
            )
        )
        holder.tv_price_discount.setText(
            CommonActivity.getPriceWithCurrency(
                context,
                String.format(GlobleVariable.LOCALE, "%.1f", total).replace(".0", "")
            )
        )

        if (!mList.discount.isNullOrEmpty()
            && mList.discount!!.toDouble() > 0
        ) {
            if (mList.discount_type.equals("flat")) {
                holder.cl_price_main.visibility = View.VISIBLE
                holder.tv_discount.visibility = View.VISIBLE

                holder.tv_discount.text =
                    "${mList.discount} ${context.resources.getString(R.string.flat)}"
                holder.tv_price_discount.setText(
                    CommonActivity.getPriceWithCurrency(
                        context, String.format(
                            GlobleVariable.LOCALE,
                            "%.1f",
                            (total - mList.discount!!.toDouble())
                        ).replace(".0", "")
                    )
                )

            } else if (mList.discount_type.equals("percentage")) {
                holder.cl_price_main.visibility = View.VISIBLE
                holder.tv_discount.visibility = View.VISIBLE

                holder.tv_discount.text =
                    "${mList.discount}% ${context.resources.getString(R.string.Discount)}"
                holder.tv_price_discount.setText(
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
                )
            } else {
                holder.cl_price_main.visibility = View.GONE
                holder.tv_discount.visibility = View.GONE
            }
        } else {
            holder.cl_price_main.visibility = View.GONE
            holder.tv_discount.visibility = View.GONE
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

    }

    override fun getItemCount(): Int {
        return modelList.size
    }

}