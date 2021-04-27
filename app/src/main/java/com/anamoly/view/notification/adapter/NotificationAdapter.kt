package com.vivera.notification.adapter

import Config.BaseURL
import Database.NotificationData
import Models.Notification2Model
import Models.NotificationModel
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.order.OrderDetailActivity
import kotlinx.android.synthetic.main.row_notification.view.*
import java.io.Serializable
import java.util.*


class NotificationAdapter(val context: Context, val modelList: ArrayList<Notification2Model>) :
    RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {

    val notificationData = NotificationData(context)

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv_img: ImageView = view.iv_notification
        val tv_title: TextView = view.tv_notification_title
        val tv_msg: TextView = view.tv_notification_message
        val tv_date_time: TextView = view.tv_notification_date_time

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val notificationModel = modelList[position]

                if (!notificationModel.type.isNullOrEmpty() && notificationModel.type.equals(
                        "ORDER"
                    )
                ) {
                    /*notificationData.deleteNotification(notificationModel.noti_play_id!!)
                    modelList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, modelList.size)*/
                    Intent(context, OrderDetailActivity::class.java).apply {
                        putExtra("order_id", notificationModel.type_id)
                        context.startActivity(this)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_notification, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        /*Picasso.get()
            .load(BaseURL.IMG_NOTIFICATION_URL + mList.image)
            .error(R.drawable.ic_place_holder)
            .placeholder(R.drawable.ic_place_holder)
            .into(holder.iv_img)*/

        holder.tv_title.text = CommonActivity.getStringByLanguage(
            context,
            mList.title_en,
            mList.title_nl,
            mList.title_nl,
            mList.title_tr,
            mList.title_de
        )
        holder.tv_msg.text = CommonActivity.getStringByLanguage(
            context,
            mList.message_en,
            mList.message_nl,
            mList.message_nl,
            mList.message_tr,
            mList.message_de
        )
        holder.tv_date_time.text = "${
            CommonActivity.getConvertDateTime(
                mList.created_at!!,
                3
            )
        } ${
            CommonActivity.getConvertDateTime(
                mList.created_at,
                4
            )
        } ${
            CommonActivity.getConvertDateTime(
                mList.created_at,
                5
            )
        } ${CommonActivity.getConvertDateTime(mList.created_at, 6)}"

    }

    override fun getItemCount(): Int {
        return modelList.size
    }

}