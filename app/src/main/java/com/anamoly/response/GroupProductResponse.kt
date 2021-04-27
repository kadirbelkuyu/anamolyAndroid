package com.anamoly.response

import Models.CategoryModel
import Models.GroupProductModel
import Models.ProductModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created on 10-02-2020.
 */
class GroupProductResponse {
    @SerializedName("responce")
    @Expose
    val responce: Boolean? = null
    @SerializedName("message")
    @Expose
    val message: String? = null
    @SerializedName("code")
    @Expose
    val code: String? = null
    @SerializedName("data")
    @Expose
    val groupProductModelList: ArrayList<GroupProductModel>? = null
}