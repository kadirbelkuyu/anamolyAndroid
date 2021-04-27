package utils

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.anamoly.view.cart.adapter.CartAdapter

/**
 * Created on 05-02-2020.
 */
class SwipeToDeleteCallback(
    dragDirs: Int,
    swipeDirs: Int,
    private val listener: SwipeToDeleteCallbackListener
) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        if (viewHolder != null) {
            val foregroundView: View =
                (viewHolder as CartAdapter.MyViewHolder).ll_delete
            ItemTouchHelper.Callback.getDefaultUIUtil()
                .onSelected(foregroundView)
        }
    }

    override fun onChildDrawOver(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView: View =
            (viewHolder as CartAdapter.MyViewHolder).ll_delete
        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(
            c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        val foregroundView: View =
            (viewHolder as CartAdapter.MyViewHolder).ll_delete
        ItemTouchHelper.Callback.getDefaultUIUtil()
            .clearView(foregroundView)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView: View =
            (viewHolder as CartAdapter.MyViewHolder).ll_delete
        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(
            c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder, direction, viewHolder.adapterPosition)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    interface SwipeToDeleteCallbackListener {
        fun onSwiped(
            viewHolder: RecyclerView.ViewHolder?,
            direction: Int,
            position: Int
        )
    }

}