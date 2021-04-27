package com.anamoly.view.order

import Dialogs.LoaderDialog
import Models.OrderItemModel
import Models.ProductModel
import android.app.Application
import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse
import com.anamoly.response.MyOrderResponse
import com.anamoly.response.OrderDetailResponse
import com.anamoly.response.ProductResponse
import utils.MyBounceInterpolator

/**
 * Created on 11-02-2020.
 */
class OrderViewModel(application: Application) :
    AndroidViewModel(application) {
    private val projectRepository: ProjectRepository = ProjectRepository()
    lateinit var myOrderResponseLiveData: LiveData<MyOrderResponse?>
    lateinit var orderDetailResponseLiveData: LiveData<OrderDetailResponse?>
    lateinit var productResponseLiveData: LiveData<ProductResponse?>

    var context: Context? = null

    init {
    }

    fun registerLoader(context: Context) {
        this.context = context
    }

    fun getMyOrderList(params: Map<String, String>) {
        myOrderResponseLiveData = projectRepository.getMyOrderList(params)
    }

    fun getMyOrderDetail(params: Map<String, String>) {
        orderDetailResponseLiveData = projectRepository.getOrderDetail(params)
    }

    fun getProductListByIds(params: Map<String, String>) {
        productResponseLiveData = projectRepository.getProductListByIds(params)
    }

    fun makeGetReorderCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.reorderCart(params)
    }

    fun getOrderProductById(
        orderItemModelList: ArrayList<OrderItemModel>,
        product_id: String
    ): OrderItemModel? {
        for (orderItemModel in orderItemModelList) {
            if (orderItemModel.product_id == product_id) {
                return orderItemModel
            }
        }
        return null
    }

    // slide the view from below itself to the current position
    fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val interpolator = MyBounceInterpolator(0.1, 15.0)
        val animate = TranslateAnimation(
            0F,  // fromXDelta
            0F,  // toXDelta
            view.height.toFloat(),  // fromYDelta
            0F
        ) // toYDelta
        animate.interpolator = interpolator
        animate.duration = 300
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    // slide the view from its current position to below itself
    fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0F,  // fromXDelta
            0F,  // toXDelta
            0F,  // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 200
        animate.fillAfter = true
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                view.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })
        view.startAnimation(animate)

    }

}