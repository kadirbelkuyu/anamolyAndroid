package com.anamoly.retrofit

import com.anamoly.response.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiRequest {

    @FormUrlEncoded
    @POST("index.php/rest/products/home")
    fun getHomeProductTagList(@FieldMap params: Map<String, String>): Call<HomeProductTagResponse?>

    @POST("index.php/rest/categories/list")
    fun getCategoryList(): Call<CategoryResponse?>

    @FormUrlEncoded
    @POST("index.php/rest/products/list")
    fun getProductList(@FieldMap params: Map<String, String>): Call<GroupProductResponse?>

    @FormUrlEncoded
    @POST("index.php/rest/user/login")
    fun checkLogin(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/order/list")
    fun getOrderList(@FieldMap params: Map<String, String>): Call<MyOrderResponse?>

    @FormUrlEncoded
    @POST("index.php/rest/order/list")
    fun getOrderDetail(@FieldMap params: Map<String, String>): Call<OrderDetailResponse?>

    @FormUrlEncoded
    @POST("index.php/rest/timeslots/list")
    fun getTimeSlotList(@FieldMap params: Map<String, String>): Call<TimeSlotResponse?>

    @FormUrlEncoded
    @POST("index.php/rest/search/list")
    fun getSearchList(@FieldMap params: Map<String, String>): Call<SearchTagResponse?>

    @FormUrlEncoded
    @POST("index.php/rest/products/ids")
    fun getProductListByIds(@FieldMap params: Map<String, String>): Call<ProductResponse?>

    @FormUrlEncoded
    @POST("index.php/rest/products/search")
    fun getSearchProductList(@FieldMap params: Map<String, String>): Call<ProductResponse?>

    @FormUrlEncoded
    @POST("index.php/rest/user/update_email")
    fun updateEmail(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/user/verify_update_email")
    fun verifyEmail(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/user/update_phone")
    fun updatePhone(@FieldMap params: Map<String, String>): Call<String>

    @Multipart
    @POST("index.php/rest/user/photo")
    fun updateProfileImage(
        @Part("user_id") user_id: String,
        @Part image: MultipartBody.Part
    ): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/user/forgotpassword")
    fun forgotPassword(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/user/changepassword")
    fun changePassword(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/user/playerid")
    fun registerPlayerId(@FieldMap params: Map<String, String>): Call<String>

    @Multipart
    @POST("index.php/rest/products/suggest")
    fun sendProductSuggestion(
        @Part("user_id") user_id: String,
        @Part("suggestion") suggestion: String,
        @Part image: MultipartBody.Part?
    ): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/products/barcode")
    fun getBarcodeProductList(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/address/validate")
    fun validateAddress(@FieldMap params: Map<String, String>): Call<String>

    @POST("index.php/rest/settings/list")
    fun settingsList(): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/cart/list")
    fun cartList(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/cart/add")
    fun cartAdd(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/cart/delete")
    fun cartDelete(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/cart/clean")
    fun cartClean(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/cart/minus")
    fun cartMinus(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/cart/reorder")
    fun cartReorder(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/products/tabs")
    fun getTabList(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/products/tabdata")
    fun getTabProductList(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/contact/send")
    fun sendContact(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/referral/list")
    fun getReferralCode(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/notifications/list")
    fun getNotificationList(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/user/resendotp")
    fun resendOtp(@FieldMap params: Map<String, String>): Call<String>

    @FormUrlEncoded
    @POST("index.php/rest/home/list")
    fun getHomeList(@FieldMap params: Map<String, String>): Call<String>

}