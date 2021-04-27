package com.anamoly.response

import Models.HomeProductTagModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created on 10-02-2020.
 */
class HomeProductTagResponse {
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
    val homeProductTagModelList: ArrayList<HomeProductTagModel>? = null
}