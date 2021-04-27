package com.anamoly.view.home.adapter

import Config.BaseURL
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.anamoly.R
import kotlinx.android.synthetic.main.row_combo_offer.view.*


class ComboOfferAdapter(
    val context: Context,
    var modelList: ArrayList<String?>
) :
    RecyclerView.Adapter<ComboOfferAdapter.MyViewHolder>() {


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv_img: ImageView = view.iv_combo_img
        val tv_title: TextView = view.tv_combo_offer_position
        val tv_plus: TextView = view.tv_combo_offer_plus

        init {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_combo_offer, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        Glide.with(context)
            .load(BaseURL.IMG_PRODUCT_URL + mList)
            .dontAnimate()
            .dontTransform()
            .into(holder.iv_img)

        holder.tv_title.text = (position + 1).toString()
        if (position == (modelList.size - 2)) {
            holder.tv_plus.visibility = View.GONE
        } else {
            holder.tv_plus.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return modelList.size - 1
    }

}