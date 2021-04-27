package com.anamoly.view.search.adapter

import Config.BaseURL
import Models.SubCategoryModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.home.MainActivity
import com.anamoly.view.search.fragment.SearchProductFragment
import kotlinx.android.synthetic.main.row_search_category.view.*


class SearchSubCategoryAdapter(val context: Context, var modelList: ArrayList<SubCategoryModel>) :
    RecyclerView.Adapter<SearchSubCategoryAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv_img: ImageView = view.iv_search_category_img
        val tv_title: TextView = view.tv_search_category_title

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val subCategoryModel = modelList[position]

                /*Intent(context, SearchProductActivity::class.java).apply {
                    putExtra("subCategoryData", subCategoryModel as Serializable)
                    context.startActivity(this)
                }*/
                val fragment = SearchProductFragment()
                val args = Bundle()
                args.putSerializable("subCategoryData", subCategoryModel)
                fragment.arguments = args
                (context as MainActivity).setFragment(fragment, true)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_search_category, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        Glide.with(context)
            .load(BaseURL.IMG_CATEGORIES_URL + mList.sub_cat_image)
            .placeholder(R.drawable.ic_place_holder)
            .error(R.drawable.ic_place_holder)
            .dontAnimate()
            .dontTransform()
            .into(holder.iv_img)

        holder.tv_title.text = CommonActivity.getStringByLanguage(
            context,
            mList.sub_cat_name_en,
            mList.sub_cat_name_ar,
            mList.sub_cat_name_nl,
            mList.sub_cat_name_tr,
            mList.sub_cat_name_de
        )
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

}