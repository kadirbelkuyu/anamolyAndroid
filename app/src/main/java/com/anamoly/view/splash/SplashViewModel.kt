package com.anamoly.view.splash

import android.animation.ValueAnimator
import android.app.Application
import android.content.Context
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.anamoly.AppController
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse
import org.json.JSONObject
import utils.LanguagePrefs
import utils.SessionManagement

/**
 * Created on 16-03-2020.
 */
class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    val projectRepository = ProjectRepository()

    fun makeGetSettings(): LiveData<CommonResponse?> {
        return projectRepository.getSettings()
    }

    fun makeCartList(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.getCartList(params)
    }

    fun animationSlider(view1: View, view2: View, duration: Long) {
        /* change animation direction by ValueAnimator.ofFloat(0.0f, 1.0f) right to left
        * and change translationX - width
        * */
        if (LanguagePrefs.getLang(context).equals("ar")) {
            val animator = ValueAnimator.ofFloat(0.0f, 1.0f)//right to left
            animator.repeatCount = ValueAnimator.INFINITE
            animator.interpolator = LinearInterpolator()
            animator.duration = duration
            animator.addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                val width: Float = view1.width.toFloat()
                val translationX = width * progress
                view1.translationX = translationX
                view2.translationX = translationX - width
            }
            animator.start()
        } else {
            val animator = ValueAnimator.ofFloat(0.0f, -1.0f)//left to right
            animator.repeatCount = ValueAnimator.INFINITE
            animator.interpolator = LinearInterpolator()
            animator.duration = duration
            animator.addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                val width: Float = view1.width.toFloat()
                val translationX = width * progress
                view1.translationX = translationX
                view2.translationX = translationX + width
            }
            animator.start()
        }
    }

    fun storeData(data: String) {
        val jsonObject = JSONObject(data)
        val currency = jsonObject.getString("currency")
        val currency_symbol = jsonObject.getString("currency_symbol")
        val app_contact = jsonObject.getString("app_contact")
        val app_whatsapp = jsonObject.getString("app_whatsapp")
        val app_email = jsonObject.getString("app_email")
        val express_delivery_charge =
            jsonObject.getString("express_delivery_charge")
        val gateway_charges = jsonObject.getString("gateway_charges")
        val express_delivery_time = jsonObject.getString("express_delivery_time")
        val enable_code_payment = jsonObject.getString("enable_code_payment")
        val enable_ideal_payment = jsonObject.getString("enable_ideal_payment")
        val header_color = jsonObject.getString("header_color")
        val header_text_color = jsonObject.getString("header_text_color")
        val button_color = jsonObject.getString("button_color")
        val button_text_color = jsonObject.getString("button_text_color")
        val second_button_color = jsonObject.getString("second_button_color")
        val second_button_text_color = jsonObject.getString("second_button_text_color")
        val decorative_text_one = jsonObject.getString("decorative_text_one")
        val decorative_text_two = jsonObject.getString("decorative_text_two")
        val default_text_color = jsonObject.getString("default_text_color")
        val info_box_bg = jsonObject.getString("info_box_bg")
        val header_logo = jsonObject.getString("header_logo")
        val login_top_image = jsonObject.getString("login_top_image")


        SessionManagement.PermanentData.setSession(context, "currency", currency)
        SessionManagement.PermanentData.setSession(
            context,
            "currency_symbol",
            currency_symbol
        )
        SessionManagement.PermanentData.setSession(context, "app_contact", app_contact)
        SessionManagement.PermanentData.setSession(
            context,
            "app_whatsapp",
            app_whatsapp
        )
        SessionManagement.PermanentData.setSession(context, "app_email", app_email)
        SessionManagement.PermanentData.setSession(
            context,
            "express_delivery_charge",
            express_delivery_charge
        )
        SessionManagement.PermanentData.setSession(
            context,
            "gateway_charges",
            gateway_charges
        )
        SessionManagement.PermanentData.setSession(
            context,
            "express_delivery_time",
            express_delivery_time
        )
        SessionManagement.PermanentData.setSession(
            context,
            "enable_code_payment",
            enable_code_payment
        )
        SessionManagement.PermanentData.setSession(
            context,
            "enable_ideal_payment",
            enable_ideal_payment
        )
        SessionManagement.PermanentData.setSession(context, "header_color", "#$header_color")
        SessionManagement.PermanentData.setSession(
            context,
            "header_text_color",
            "#$header_text_color"
        )
        SessionManagement.PermanentData.setSession(context, "button_color", "#$button_color")
        SessionManagement.PermanentData.setSession(
            context,
            "button_text_color",
            "#$button_text_color"
        )
        SessionManagement.PermanentData.setSession(
            context,
            "second_button_color",
            "#$second_button_color"
        )
        SessionManagement.PermanentData.setSession(
            context,
            "second_button_text_color",
            "#$second_button_text_color"
        )
        SessionManagement.PermanentData.setSession(
            context,
            "decorative_text_one",
            "#$decorative_text_one"
        )
        SessionManagement.PermanentData.setSession(
            context,
            "decorative_text_two",
            "#$decorative_text_two"
        )
        SessionManagement.PermanentData.setSession(
            context,
            "default_text_color",
            "#$default_text_color"
        )
        SessionManagement.PermanentData.setSession(context, "info_box_bg", "#$info_box_bg")
        SessionManagement.PermanentData.setSession(context, "header_logo", header_logo)
        SessionManagement.PermanentData.setSession(context, "login_top_image", login_top_image)

        AppController.initTheme()
    }

}