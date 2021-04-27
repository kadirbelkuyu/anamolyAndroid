package com.anamoly.response

import Models.CategoryModel
import Models.ProductModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/**
 * Created on 10-02-2020.
 */
class CommonResponse {
    @SerializedName("responce")
    @Expose
    var responce: Boolean? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("code")
    @Expose
    var code: String? = null
    var data: String? = null
}