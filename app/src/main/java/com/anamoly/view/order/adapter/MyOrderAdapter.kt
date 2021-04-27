package com.anamoly.view.order.adapter

import Config.GlobleVariable
import CustomViews.PriceView
import Models.OrderModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.order.OrderDetailActivity
import kotlinx.android.synthetic.main.row_order.view.*
import java.io.Serializable


class MyOrderAdapter(val context: Context, var modelList: ArrayList<OrderModel>) :
    RecyclerView.Adapter<MyOrderAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_date: TextView = view.tv_my_order_date
        val tv_price: PriceView = view.tv_my_order_price
        val tv_qty: TextView = view.tv_my_order_qty
        val tv_status: TextView = view.tv_my_order_status

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val orderModel = modelList[position]

                Intent(context, OrderDetailActivity::class.java).apply {
                    putExtra("orderData", orderModel as Serializable)
                    (context as Activity).startActivityForResult(this, 9843)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_order, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        val dateTime = "${CommonActivity.getConvertDate(
            mList.order_date!!,
            9
        )} ${CommonActivity.getConvertDate(
            mList.order_date,
            1
        )}, ${CommonActivity.getConvertDate(
            mList.order_date,
            3
        )} ${CommonActivity.getConvertDate(
            mList.order_date,
            4
        )}"

        holder.tv_date.text = dateTime
        holder.tv_price.setText(
            CommonActivity.getPriceWithCurrency(
                context, String.format(GlobleVariable.LOCALE, "%.2f", mList.net_amount!!.toDouble())
                    .replace(".00", "")
            )
        )
        holder.tv_qty.text = mList.total_qty
        holder.tv_status.text = getOrderStatus(mList.status!!)
        holder.tv_status.setTextColor(getOrderStatusColor(mList.status))

    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    private fun getOrderStatus(status: String): String {
        return when (status) {
            "0" -> context.resources.getString(R.string.pending)
            "1" -> context.resources.getString(R.string.confirmed)
            "2" -> context.resources.getString(R.string.out_for_delivery)
            "3" -> context.resources.getString(R.string.delivered)
            "4" -> context.resources.getString(R.string.declined)
            "5" -> context.resources.getString(R.string.canceled)
            "6" -> context.resources.getString(R.string.un_paid)
            "7" -> context.resources.getString(R.string.paid)
            else -> context.resources.getString(R.string.pending)
        }
    }

    private fun getOrderStatusColor(status: String): Int {
        return when (status) {
            "0" -> ContextCompat.getColor(context, R.color.colorText)
            "1" -> ContextCompat.getColor(context, R.color.colorGreen)
            "2" -> ContextCompat.getColor(context, R.color.colorGreen)
            "3" -> ContextCompat.getColor(context, R.color.colorGreen)
            "4" -> ContextCompat.getColor(context, R.color.colorRed)
            "5" -> ContextCompat.getColor(context, R.color.colorRed)
            "6" -> ContextCompat.getColor(context, R.color.colorRed)
            "7" -> ContextCompat.getColor(context, R.color.colorGreen)
            else -> ContextCompat.getColor(context, R.color.colorText)
        }
    }

}