package com.anamoly.view.product_detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse

/**
 * Created on 11-02-2020.
 */
class ProductDetailViewModel(application: Application) :
    AndroidViewModel(application) {
    private val projectRepository: ProjectRepository = ProjectRepository()

    fun addCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.addCart(params)
    }

    fun makeMinusCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.minusCart(params)
    }

}