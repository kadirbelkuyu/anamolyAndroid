package com.anamoly.response

import Models.CategoryModel
import Models.OrderModel
import Models.ProductModel
import Models.TimeSlotModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created on 10-02-2020.
 */
class TimeSlotResponse {
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
    val timeSlotModelList: ArrayList<TimeSlotModel>? = null
}