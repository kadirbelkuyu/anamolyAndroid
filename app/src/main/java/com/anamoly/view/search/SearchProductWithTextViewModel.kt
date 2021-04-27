package com.anamoly.view.search

import Models.ProductModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse
import com.anamoly.response.GroupProductResponse
import com.anamoly.response.ProductResponse

/**
 * Created on 11-02-2020.
 */
class SearchProductWithTextViewModel(application: Application) :
    AndroidViewModel(application) {
    private val projectRepository: ProjectRepository = ProjectRepository()
    lateinit var productResponseLiveData: LiveData<ProductResponse?>

    var params: Map<String, String>? = null

    init {

    }

    fun getProductList(params: Map<String, String>) {
        this.params = params
        productResponseLiveData = projectRepository.getSearchProductList(params)
    }

    fun getBarcodeProductList(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.getBarcodeProductList(params)
    }

    fun addCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.addCart(params)
    }

    fun makeMinusCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.minusCart(params)
    }

}