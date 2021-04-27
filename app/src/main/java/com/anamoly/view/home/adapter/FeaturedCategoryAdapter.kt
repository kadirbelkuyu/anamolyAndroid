package com.anamoly.view.home.adapter

import Config.BaseURL
import Models.CategoryModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anamoly.AppController
import com.anamoly.CommonActivity
import com.anamoly.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_featured_category.view.*


class FeaturedCategoryAdapter(
    val context: Context, var modelList: ArrayList<CategoryModel>,
    val onItemClick: OnItemClick?
) :
    RecyclerView.Adapter<FeaturedCategoryAdapter.MyViewHolder>() {

    var selectedId: String = ""

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val iv_img: ImageView = view.iv_featured_category_img
        val tvTitle: TextView = view.tv_featured_category_title
        val linearLayout: LinearLayout = view.ll_featured_category

        init {
            tvTitle.setTextColor(AppController.headerColor)
            itemView.setOnClickListener {
                val position = adapterPosition
                val categoryModel = modelList[position]

                onItemClick?.onClick(position, categoryModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_featured_category, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        /*Glide.with(context)
            .load(BaseURL.IMG_CATEGORIES_URL + mList.cat_image)
            .placeholder(R.drawable.ic_place_holder)
            .error(R.drawable.ic_place_holder)
            .dontAnimate()
            .dontTransform()
            .into(holder.iv_img)*/

        holder.tvTitle.text = CommonActivity.getStringByLanguage(
            context,
            mList.cat_name_en,
            mList.cat_name_ar,
            mList.cat_name_nl,
            mList.cat_name_tr,
            mList.cat_name_de
        )

        if (selectedId == mList.category_id) {
            holder.linearLayout.setBackgroundColor(AppController.headerColor)
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        } else {
            holder.linearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorWhite
                )
            )
            holder.tvTitle.setTextColor(AppController.headerColor)
        }

    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    interface OnItemClick {
        fun onClick(position: Int, categoryModel: CategoryModel)
    }

}