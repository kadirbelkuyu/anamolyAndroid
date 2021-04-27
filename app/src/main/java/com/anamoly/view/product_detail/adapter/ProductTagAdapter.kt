package com.anamoly.view.product_detail.adapter

import Models.ProductIngredientModel
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.row_product_tag.view.*


class ProductTagAdapter(val context: Context, var modelList: ArrayList<ProductIngredientModel>) :
    RecyclerView.Adapter<ProductTagAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.cv_product_tag
        val tv_title: TextView = view.tv_product_tag

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val productIngredientModel = modelList[position]

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_product_tag, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        holder.tv_title.text = CommonActivity.getStringByLanguage(
            context,
            mList.ingredient_name_en,
            mList.ingredient_name_ar,
            mList.ingredient_name_nl,
            mList.ingredient_name_tr,
            mList.ingredient_name_de
        )

        val color = Color.parseColor("#${mList.ingredient_colour}")

        holder.tv_title.setTextColor(color)
        holder.cardView.setCardBackgroundColor(color)

    }

    override fun getItemCount(): Int {
        return modelList.size
    }

}