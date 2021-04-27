package com.anamoly.view.cart

import android.app.Application
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse
import com.anamoly.response.MyOrderResponse
import com.anamoly.response.OrderDetailResponse
import com.anamoly.response.TimeSlotResponse
import utils.MyBounceInterpolator

/**
 * Created on 11-02-2020.
 */
class CartViewModel(application: Application) :
    AndroidViewModel(application) {
    private val projectRepository: ProjectRepository = ProjectRepository()
    lateinit var timeSlotResponseLiveData: LiveData<TimeSlotResponse?>
    val isAnimationStopLiveData = MutableLiveData<Boolean>()

    init {
    }

    fun getTimeSlotList(params: Map<String, String>) {
        timeSlotResponseLiveData = projectRepository.getTimeSlotList(params)
    }

    fun makeClearCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.cleanCart(params)
    }

    fun makeAddCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.addCart(params)
    }

    fun makeDeleteCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.deleteCart(params)
    }

    fun makeMinusCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.minusCart(params)
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
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                isAnimationStopLiveData.value = true
            }

            override fun onAnimationStart(animation: Animation?) {
                isAnimationStopLiveData.value = false
            }
        })
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
        animate.duration = 300
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

    fun slideDownImidiat(view: View) {
        val animate = TranslateAnimation(
            0F,  // fromXDelta
            0F,  // toXDelta
            0F,  // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 0
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