package com.anamoly.view.cart.adapter

import Models.TimeSlotModel
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anamoly.AppController
import com.anamoly.CommonActivity
import com.anamoly.R
import kotlinx.android.synthetic.main.row_time_slot.view.*


class TimeSlotAdapter(
    val context: Context,
    var modelList: ArrayList<TimeSlotModel>,
    val onItemSelected: OnItemSelected
) :
    RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder>() {

    val selectedItems = SparseBooleanArray()
    var selectedPosition = 0
    var timeSlotModelSelected: TimeSlotModel? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val linearLayout: LinearLayout = view.ll_time_slot
        val tv_day_name: TextView = view.tv_time_slot_day_name
        val tv_month: TextView = view.tv_time_slot_month
        val tv_time: TextView = view.tv_time_slot_time

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val timeSlotModel = modelList[position]

                selectedItems.clear()
                selectedItems.put(position, true)
                notifyDataSetChanged()

                onItemSelected.onClick(position, timeSlotModel)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_time_slot, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        holder.tv_day_name.text = CommonActivity.getConvertDate(mList.date!!, 9).capitalize()
        holder.tv_month.text = "${
            CommonActivity.getConvertDate(
                mList.date!!,
                1
            )
        } ${CommonActivity.getConvertDate(mList.date!!, 3)}"

        val time = "${
            CommonActivity.getConvertTime(
                mList.from_time!!,
                2
            )
        } - ${CommonActivity.getConvertTime(mList.to_time!!, 2)}"

        holder.tv_time.text = time

        if (selectedPosition == position) {
            selectedItems.put(position, true)
            selectedPosition = -1
        } else if (timeSlotModelSelected != null
            && timeSlotModelSelected == mList
        ) {
            selectedItems.put(position, true)
            timeSlotModelSelected = null
        }

        if (selectedItems[position]) {
            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = CommonActivity.dpToPx(context, 5F).toFloat()
            gradientDrawable.setColor(AppController.secondButtonColor)

            holder.linearLayout.background = gradientDrawable
            holder.tv_day_name.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            holder.tv_month.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            holder.tv_time.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        } else {
            holder.linearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorWhite
                )
            )
            holder.tv_day_name.setTextColor(ContextCompat.getColor(context, R.color.colorText))
            holder.tv_month.setTextColor(ContextCompat.getColor(context, R.color.colorText))
            holder.tv_time.setTextColor(ContextCompat.getColor(context, R.color.colorText))
        }

    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    public interface OnItemSelected {
        fun onClick(position: Int, timeSlotModel: TimeSlotModel)
    }

}