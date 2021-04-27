package com.anamoly.view.cart.adapter

import Models.CartItemModel
import Models.CartModel
import Models.ProductComboModel
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.anamoly.R
import kotlinx.android.synthetic.main.row_cart.view.*
import utils.LanguagePrefs


class CartAdapter2(
    val context: Context,
    var modelList: ArrayList<CartModel>,
    val onItemClick: OnItemClick
) :
    RecyclerSwipeAdapter<CartAdapter2.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val tv_delete: TextView = view.tv_cart_delete
        val swipeLayout: SwipeLayout = view.swipe_cart
        val ll_delete: LinearLayout = view.ll_cart_delete
        val recyclerView: RecyclerView = view.rv_cart_item

        init {

            if (LanguagePrefs.getLang(context).equals("ar")) {
                swipeLayout.isLeftSwipeEnabled = true
                swipeLayout.isRightSwipeEnabled = false
            } else {
                swipeLayout.isLeftSwipeEnabled = false
                swipeLayout.isRightSwipeEnabled = true
            }

            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, ll_delete)
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, ll_delete)

            recyclerView.layoutManager = LinearLayoutManager(context)

            swipeLayout.addSwipeListener(object : SwipeLayout.SwipeListener {
                override fun onOpen(layout: SwipeLayout?) {
                    mItemManger.removeShownLayouts(swipeLayout)
                    mItemManger.closeAllItems()

                    val position = adapterPosition
                    val cartModel = modelList[position]

                    onItemClick.deleteClick(position, 0, cartModel, null)

                    modelList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(0, modelList.size)
                    notifyDataSetChanged()
                    Intent("cartUpdate").apply {
                        putExtra("type", "update")
                        swipeLayout.close()
                        context.sendBroadcast(this)
                    }
                }

                override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {

                }

                override fun onStartOpen(layout: SwipeLayout?) {

                }

                override fun onStartClose(layout: SwipeLayout?) {

                }

                override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {

                }

                override fun onClose(layout: SwipeLayout?) {

                }
            })

            tv_delete.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            val cartModel = modelList[position]

            when (v!!.id) {
                R.id.tv_cart_delete -> {
                    mItemManger.removeShownLayouts(swipeLayout)
                    mItemManger.closeAllItems()

                    onItemClick.deleteClick(position, 0, cartModel, null)
                    modelList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(0, modelList.size)
                    notifyDataSetChanged()
                    Intent("cartUpdate").apply {
                        putExtra("type", "update")
                        context.sendBroadcast(this)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_cart, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mList = modelList[position]

        if (mList.cartItemModelList != null) {
            holder.recyclerView.visibility = View.VISIBLE
            holder.recyclerView.adapter = CartItemAdapter2(
                context,
                mList,
                position,
                mList.cartItemModelList,
                object : CartItemAdapter2.OnItemClick {
                    override fun imageClick(
                        positionMain: Int,
                        position: Int,
                        view: View,
                        cartModel: CartModel,
                        cartItemModel: CartItemModel
                    ) {
                        onItemClick.imageClick(
                            positionMain,
                            position,
                            view,
                            cartModel,
                            cartItemModel
                        )
                    }

                    override fun addClick(
                        positionMain: Int,
                        position: Int,
                        cartModel: CartModel,
                        cartItemModel: CartItemModel
                    ) {
                        onItemClick.addClick(positionMain, position, cartModel, cartItemModel)
                    }

                    override fun removeClick(
                        positionMain: Int,
                        position: Int,
                        cartModel: CartModel,
                        cartItemModel: CartItemModel
                    ) {
                        onItemClick.removeClick(positionMain, position, cartModel, cartItemModel)
                    }

                    override fun deleteClick(
                        positionMain: Int,
                        position: Int,
                        cartModel: CartModel,
                        cartItemModel: CartItemModel
                    ) {
                        onItemClick.deleteClick(positionMain, position, cartModel, cartItemModel)
                    }
                })
        } else {
            holder.recyclerView.visibility = View.GONE
        }

        holder.swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        mItemManger.bindView(holder.itemView, position)

    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    fun removeItem(position: Int) {
        modelList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe_cart
    }

    interface OnItemClick {
        fun imageClick(
            positionMain: Int,
            position: Int,
            view: View,
            cartModel: CartModel,
            cartItemModel: CartItemModel?
        )

        fun addClick(
            positionMain: Int,
            position: Int,
            cartModel: CartModel,
            cartItemModel: CartItemModel?
        )

        fun removeClick(
            positionMain: Int,
            position: Int,
            cartModel: CartModel,
            cartItemModel: CartItemModel?
        )

        fun deleteClick(
            positionMain: Int,
            position: Int,
            cartModel: CartModel,
            cartItemModel: CartItemModel?
        )
    }

}