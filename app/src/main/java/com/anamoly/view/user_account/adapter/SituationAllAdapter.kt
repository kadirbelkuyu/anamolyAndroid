package com.anamoly.view.user_account.adapter

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.ImageView
import com.anamoly.R
import kotlinx.android.synthetic.main.row_situation.view.*


class SituationAllAdapter(
    val context: Context,
    val modelList: ArrayList<Int>
) :
    RecyclerView.Adapter<SituationAllAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv_img: ImageView = view.iv_situation_img

        init {
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_situation, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]
        if (mList == 1) {
            holder.iv_img.setImageResource(R.drawable.ic_adult)
        } else if (mList == 2) {
            holder.iv_img.setImageResource(R.drawable.ic_child)
        } else if (mList == 3) {
            holder.iv_img.setImageResource(R.drawable.ic_dog)
        } else if (mList == 4) {
            holder.iv_img.setImageResource(R.drawable.ic_cat)
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

}