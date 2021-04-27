package com.anamoly.view.user_account.adapter

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.ImageView
import com.anamoly.R
import kotlinx.android.synthetic.main.row_situation.view.*


class SituationAdapter(val context: Context, var icon: Int, var total: Int) :
    RecyclerView.Adapter<SituationAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv_img: ImageView = view.iv_situation_img

        init {
            iv_img.setImageResource(icon)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_situation, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return total
    }

}