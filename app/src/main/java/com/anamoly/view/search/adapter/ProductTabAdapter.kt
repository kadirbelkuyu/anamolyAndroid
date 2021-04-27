package com.anamoly.view.search.adapter

import Models.ProductModel
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.row_tab.view.*


class ProductTabAdapter(
    val context: Context,
    var modelList: ArrayList<ProductModel>,
    val onItemSelected: OnItemSelected
) :
    RecyclerView.Adapter<ProductTabAdapter.MyViewHolder>() {

    val selectedItems = SparseBooleanArray()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_title: TextView = view.tv_tab

        init {
            itemView.setOnClickListener {
                val position = adapterPosition

                selectedItems.clear()
                notifyDataSetChanged()

                onItemSelected.onClick(position, modelList[position])

                selectedItems.put(position, true)
                notifyItemChanged(position)

            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_tab, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        holder.tv_title.apply {
            text = CommonActivity.getStringByLanguage(
                context,
                mList.group_name_en,
                mList.group_name_ar,
                mList.group_name_nl,
                mList.group_name_tr,
                mList.group_name_de
            )
            if (selectedItems[position]) {
                val gradientDrawableNormal = GradientDrawable()
                gradientDrawableNormal.cornerRadius =
                    CommonActivity.dpToPx(context!!, 180F).toFloat()
                gradientDrawableNormal.setColor(CommonActivity.getHeaderTextColor())
                background = gradientDrawableNormal
                setTextColor(CommonActivity.getHeaderColor())
                //setBackgroundResource(R.drawable.xml_rounded_orange)
            } else {
                setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    public interface OnItemSelected {
        fun onClick(position: Int, productModel: ProductModel)
    }

}