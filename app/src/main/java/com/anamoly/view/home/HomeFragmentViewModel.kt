package com.anamoly.view.home

import Models.ProductModel
import android.app.Application
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.droidnet.DroidListener
import com.droidnet.DroidNet
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse
import com.anamoly.response.GroupProductResponse
import com.anamoly.response.HomeProductTagResponse
import utils.ConnectivityReceiver
import utils.MyBounceInterpolator


/**
 * Created on 11-02-2020.
 */
class HomeFragmentViewModel(application: Application) :
    AndroidViewModel(application) {
    private val projectRepository: ProjectRepository = ProjectRepository()

    fun getHomeProduct(params: Map<String, String>): LiveData<HomeProductTagResponse?> {
        return projectRepository.getHomeProductTagList(params)
    }

    fun registerPlayerId(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.registerPlayerId(params)
    }

    fun addCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.addCart(params)
    }

    fun makeMinusCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.minusCart(params)
    }

    fun getTabList(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.getTabList(params)
    }

    fun getTabProductList(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.getTabProductList(params)
    }

    fun getHomeList(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.getHomeList(params)
    }

}