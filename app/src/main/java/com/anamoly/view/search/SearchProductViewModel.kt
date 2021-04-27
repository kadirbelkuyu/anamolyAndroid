package com.anamoly.view.search

import Models.ProductModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.droidnet.DroidListener
import com.droidnet.DroidNet
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse
import com.anamoly.response.GroupProductResponse
import okhttp3.MultipartBody
import utils.ConnectivityReceiver


/**
 * Created on 11-02-2020.
 */
class SearchProductViewModel(application: Application) :
    AndroidViewModel(application) {
    private val projectRepository: ProjectRepository = ProjectRepository()
    lateinit var productResponseLiveData: LiveData<GroupProductResponse?>

    var params: Map<String, String>? = null

    init {

    }

    fun getProductList(params: Map<String, String>) {
        this.params = params
        productResponseLiveData = projectRepository.getProductList(params)
    }

    fun getHeaderIndex(
        productModelListHeader: ArrayList<ProductModel>,
        groupName: String?
    ): Int {
        var position = 0
        for (productModel in productModelListHeader) {
            if (productModel.group_name_en == groupName) {
                return position
            }
            position++
        }
        return position
    }

    fun isHeader(
        productModelListHeader: ArrayList<ProductModel>,
        productModelList: ArrayList<ProductModel>,
        index: Int
    ): Int {
        if (index >= 0 && index < productModelList.size) {
            val productModel = productModelList[index]
            return if (productModel.product_id.isNullOrEmpty()) {
                productModel.headerPosition
            } else {
                for ((position, productModel1) in productModelListHeader.withIndex()) {
                    if (productModel1.product_group_id == productModel.product_group_id) {
                        return position
                    }
                }
                return -1
            }
        }
        return -1
    }

    fun getProductIndex(productModelList: ArrayList<ProductModel>, groupName: String): Int {
        var position = 0
        for (productModel in productModelList) {
            if (productModel.group_name_en == groupName) {
                return position
            }
            position++
        }
        return position
    }

    fun sendProductSuggestion(
        user_id: String,
        suggestion: String,
        image: MultipartBody.Part?
    ): LiveData<CommonResponse?> {
        return projectRepository.sendProductSuggestion(user_id, suggestion, image)
    }

    fun addCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.addCart(params)
    }

    fun makeMinusCart(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.minusCart(params)
    }

}