package com.anamoly.response

import Models.SearchTagModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created on 14-02-2020.
 */
class SearchTagResponse {

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
    val searchTagModelList: ArrayList<SearchTagModel>? = null

}