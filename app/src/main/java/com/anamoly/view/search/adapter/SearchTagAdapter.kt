package com.anamoly.view.search.adapter

import Models.SearchTagModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.home.MainActivity
import com.anamoly.view.search.fragment.SearchProductFragment
import com.anamoly.view.search.fragment.SearchProductWithTextFragment
import kotlinx.android.synthetic.main.row_search_tag.view.*
import utils.StringsHelper


class SearchTagAdapter(
    val context: Context,
    var modelList: ArrayList<SearchTagModel>
) :
    RecyclerView.Adapter<SearchTagAdapter.MyViewHolder>() {

    var searchText: String = ""

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_title: TextView = view.tv_search_tag

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val searchTagModel = modelList[position]

                CommonActivity.hideKeyboard(context as MainActivity)

                val fragment = SearchProductWithTextFragment()
                val args = Bundle()
                args.putSerializable(
                    "title", CommonActivity.getStringByLanguage(
                        context,
                        searchTagModel.search_en,
                        searchTagModel.search_ar,
                        searchTagModel.search_nl,
                        searchTagModel.search_tr,
                        searchTagModel.search_de
                    )
                )
                args.putSerializable("s_type_id", searchTagModel.s_type_id)
                args.putSerializable("s_type", searchTagModel.s_type)
                fragment.arguments = args
                (context as MainActivity).setFragment(fragment, true)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_search_tag, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        holder.tv_title.text = StringsHelper.highlightLCS(
            CommonActivity.getStringByLanguage(
                context,
                mList.search_en,
                mList.search_ar,
                mList.search_nl,
                mList.search_tr,
                mList.search_de
            )!!, searchText, ContextCompat.getColor(context, R.color.colorBlue)
        )

    }

    override fun getItemCount(): Int {
        return modelList.size
    }

}