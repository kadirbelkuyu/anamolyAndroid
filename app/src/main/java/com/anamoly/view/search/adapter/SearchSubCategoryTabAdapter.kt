package com.anamoly.view.search.adapter

import Config.BaseURL
import Models.SubCategoryModel
import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.row_sub_category_tab.view.*


class SearchSubCategoryTabAdapter(
    val context: Context,
    var modelList: ArrayList<SubCategoryModel>,
    val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<SearchSubCategoryTabAdapter.MyViewHolder>() {

    val selectedItems = SparseBooleanArray()
    var selectedId = ""

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_title: TextView = view.tv_sub_category_title
        val view_divider: View = view.view_sub_category_divider

        init {
            view_divider.setBackgroundColor(CommonActivity.getHeaderTextColor())

            itemView.setOnClickListener {
                val position = adapterPosition
                val subCategoryModel = modelList[position]

                selectedItems.clear()
                selectedItems.put(position, true)
                notifyDataSetChanged()

                onItemClickListener.onClick(position, subCategoryModel)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_sub_category_tab, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        if (selectedId.isNotEmpty()
            && selectedId == mList.category_id
        ) {
            selectedId = ""
            selectedItems.put(position, true)
        }

        holder.tv_title.text = CommonActivity.getStringByLanguage(
            context,
            mList.sub_cat_name_en,
            mList.sub_cat_name_ar,
            mList.sub_cat_name_nl,
            mList.sub_cat_name_tr,
            mList.sub_cat_name_de
        )

        if (selectedItems[position]) {
            holder.tv_title.setTextColor(CommonActivity.getHeaderTextColor())
            holder.view_divider.visibility = View.VISIBLE
        } else {
            holder.tv_title.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            holder.view_divider.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    public interface OnItemClickListener {
        public fun onClick(position: Int, subCategoryModel: SubCategoryModel)
    }

}