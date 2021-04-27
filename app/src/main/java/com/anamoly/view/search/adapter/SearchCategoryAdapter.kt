package com.anamoly.view.search.adapter

import Config.BaseURL
import Models.CategoryModel
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
import com.anamoly.view.search.fragment.SearchSubCategoryFragment
import kotlinx.android.synthetic.main.row_search_category.view.*


class SearchCategoryAdapter(val context: Context, var modelList: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<SearchCategoryAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv_img: ImageView = view.iv_search_category_img
        val tv_title: TextView = view.tv_search_category_title

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val categoryModel = modelList[position]

                if (categoryModel.subCategoryModelList != null
                    && categoryModel.subCategoryModelList.size > 0
                ) {
                    /*Intent(context, SearchSubCategoryActivity::class.java).apply {
                        putExtra("categoryData", modelList[position] as Serializable)
                        context.startActivity(this)
                    }*/
                    val fragment = SearchSubCategoryFragment()
                    val args = Bundle()
                    args.putSerializable("categoryData", categoryModel)
                    fragment.arguments = args
                    (context as MainActivity).setFragment(fragment, true)
                } else {
                    val fragment = SearchProductFragment()
                    val args = Bundle()
                    args.putSerializable("categoryData", categoryModel)
                    fragment.arguments = args
                    (context as MainActivity).setFragment(fragment, true)
                    /*Intent(context, SearchProductActivity::class.java).apply {
                        putExtra("categoryData", modelList[position] as Serializable)
                        context.startActivity(this)
                    }*/
                }

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
            .load(BaseURL.IMG_CATEGORIES_URL + mList.cat_image)
            .placeholder(R.drawable.ic_place_holder)
            .error(R.drawable.ic_place_holder)
            .dontAnimate()
            .dontTransform()
            .into(holder.iv_img)

        holder.tv_title.text = CommonActivity.getStringByLanguage(
            context,
            mList.cat_name_en,
            mList.cat_name_ar,
            mList.cat_name_nl,
            mList.cat_name_tr,
            mList.cat_name_de
        )
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

}