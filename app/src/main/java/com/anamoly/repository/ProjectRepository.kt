package com.anamoly.repository

import Models.ProductModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.anamoly.response.*
import com.anamoly.retrofit.ApiRequest
import com.anamoly.retrofit.RetrofitRequest.retrofitInstance
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created on 10-02-2020.
 */
class ProjectRepository {
    private val apiRequest: ApiRequest = retrofitInstance!!.create(ApiRequest::class.java)
    val gsonBuilder = GsonBuilder().create()
    val gson = Gson()

    companion object {
        private val TAG = ProjectRepository::class.java.simpleName
    }

    fun checkLogin(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Sata::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.checkLogin(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun getHomeProductTagList(params: Map<String, String>): LiveData<HomeProductTagResponse?> {
        val data = MutableLiveData<HomeProductTagResponse?>()
        apiRequest.getHomeProductTagList(params)
            .enqueue(object : Callback<HomeProductTagResponse?> {
                override fun onResponse(
                    call: Call<HomeProductTagResponse?>,
                    response: Response<HomeProductTagResponse?>
                ) {
                    Log.d(TAG, "onResponse response:: $response")
                    if (response.body() != null) {
                        Log.d(
                            TAG,
                            "onResponse data:: ${gsonBuilder.toJsonTree(response.body()!!).asJsonObject}"
                        )
                        data.value = response.body()
                    }
                }

                override fun onFailure(
                    call: Call<HomeProductTagResponse?>,
                    t: Throwable
                ) {
                    data.value = null
                }
            })
        return data
    }

    fun getCategoryList(): LiveData<CategoryResponse?> {
        val data = MutableLiveData<CategoryResponse?>()
        apiRequest.getCategoryList().enqueue(object : Callback<CategoryResponse?> {
            override fun onResponse(
                call: Call<CategoryResponse?>,
                response: Response<CategoryResponse?>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(
                        TAG,
                        "onResponse data:: ${gsonBuilder.toJsonTree(response.body()!!).asJsonObject}"
                    )
                    data.value = response.body()
                }
            }

            override fun onFailure(
                call: Call<CategoryResponse?>,
                t: Throwable
            ) {
                data.value = null
            }
        })
        return data
    }

    fun getProductList(params: Map<String, String>): LiveData<GroupProductResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<GroupProductResponse?>()
        apiRequest.getProductList(params).enqueue(object :
            Callback<GroupProductResponse?> {
            override fun onResponse(
                call: Call<GroupProductResponse?>,
                response: Response<GroupProductResponse?>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(
                        TAG,
                        "onResponse data:: ${gsonBuilder.toJsonTree(response.body()!!).asJsonObject}"
                    )
                    data.value = response.body()
                }
            }

            override fun onFailure(
                call: Call<GroupProductResponse?>,
                t: Throwable
            ) {
                data.value = null
            }
        })
        return data
    }

    fun getMyOrderList(params: Map<String, String>): LiveData<MyOrderResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<MyOrderResponse?>()
        apiRequest.getOrderList(params).enqueue(object :
            Callback<MyOrderResponse?> {
            override fun onResponse(
                call: Call<MyOrderResponse?>,
                response: Response<MyOrderResponse?>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(
                        TAG,
                        "onResponse data:: ${gsonBuilder.toJsonTree(response.body()!!).asJsonObject}"
                    )
                    data.value = response.body()
                }
            }

            override fun onFailure(
                call: Call<MyOrderResponse?>,
                t: Throwable
            ) {
                data.value = null
            }
        })
        return data
    }

    fun getOrderDetail(params: Map<String, String>): LiveData<OrderDetailResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<OrderDetailResponse?>()
        apiRequest.getOrderDetail(params).enqueue(object :
            Callback<OrderDetailResponse?> {
            override fun onResponse(
                call: Call<OrderDetailResponse?>,
                response: Response<OrderDetailResponse?>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(
                        TAG,
                        "onResponse data:: ${gsonBuilder.toJsonTree(response.body()!!).asJsonObject}"
                    )
                    data.value = response.body()
                }
            }

            override fun onFailure(
                call: Call<OrderDetailResponse?>,
                t: Throwable
            ) {
                data.value = null
            }
        })
        return data
    }

    fun getTimeSlotList(params: Map<String, String>): LiveData<TimeSlotResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<TimeSlotResponse?>()
        apiRequest.getTimeSlotList(params).enqueue(object :
            Callback<TimeSlotResponse?> {
            override fun onResponse(
                call: Call<TimeSlotResponse?>,
                response: Response<TimeSlotResponse?>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(
                        TAG,
                        "onResponse data:: ${gsonBuilder.toJsonTree(response.body()!!).asJsonObject}"
                    )
                    data.value = response.body()
                }
            }

            override fun onFailure(
                call: Call<TimeSlotResponse?>,
                t: Throwable
            ) {
                data.value = null
            }
        })
        return data
    }

    fun getProductListByIds(params: Map<String, String>): LiveData<ProductResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<ProductResponse?>()
        apiRequest.getProductListByIds(params).enqueue(object :
            Callback<ProductResponse?> {
            override fun onResponse(
                call: Call<ProductResponse?>,
                response: Response<ProductResponse?>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(
                        TAG,
                        "onResponse data:: ${gsonBuilder.toJsonTree(response.body()!!).asJsonObject}"
                    )
                    data.value = response.body()
                }
            }

            override fun onFailure(
                call: Call<ProductResponse?>,
                t: Throwable
            ) {
                data.value = null
            }
        })
        return data
    }

    fun getSearchTagList(params: Map<String, String>): LiveData<SearchTagResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<SearchTagResponse?>()
        apiRequest.getSearchList(params).enqueue(object :
            Callback<SearchTagResponse?> {
            override fun onResponse(
                call: Call<SearchTagResponse?>,
                response: Response<SearchTagResponse?>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(
                        TAG,
                        "onResponse data:: ${gsonBuilder.toJsonTree(response.body()!!).asJsonObject}"
                    )
                    data.value = response.body()
                }
            }

            override fun onFailure(
                call: Call<SearchTagResponse?>,
                t: Throwable
            ) {
                data.value = null
            }
        })
        return data
    }

    fun getSearchProductList(params: Map<String, String>): LiveData<ProductResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<ProductResponse?>()
        apiRequest.getSearchProductList(params).enqueue(object :
            Callback<ProductResponse?> {
            override fun onResponse(
                call: Call<ProductResponse?>,
                response: Response<ProductResponse?>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(
                        TAG,
                        "onResponse data:: ${gsonBuilder.toJsonTree(response.body()!!).asJsonObject}"
                    )
                    data.value = response.body()
                }
            }

            override fun onFailure(
                call: Call<ProductResponse?>,
                t: Throwable
            ) {
                data.value = null
            }
        })
        return data
    }

    fun updateEmail(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.updateEmail(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun updatePhone(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.updatePhone(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun verifyEmail(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.verifyEmail(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun resendOtp(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.resendOtp(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun forgotPassword(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.forgotPassword(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun changePassword(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.changePassword(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun registerPlayerId(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.registerPlayerId(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun uploadProfilePicture(
        user_id: String,
        image: MultipartBody.Part
    ): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$user_id")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.updateProfileImage(user_id, image).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun sendProductSuggestion(
        user_id: String,
        suggestion: String,
        image: MultipartBody.Part?
    ): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$user_id")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.sendProductSuggestion(user_id, suggestion, image)
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    Log.d(TAG, "onResponse response:: $response")
                    if (response.body() != null) {
                        Log.d(TAG, "onResponse data:: ${response.body()!!}")

                        val jsonObject = JSONObject(response.body().toString())

                        val commonResponse = CommonResponse()
                        commonResponse.responce = jsonObject.getBoolean("responce")
                        if (jsonObject.has("code"))
                            commonResponse.code = jsonObject.getString("code")
                        if (jsonObject.has("message"))
                            commonResponse.message = jsonObject.getString("message")
                        if (jsonObject.has("data"))
                            commonResponse.data = jsonObject.getString("data")

                        data.value = commonResponse
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                    data.value = null
                }
            })
        return data
    }

    fun getBarcodeProductList(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.getBarcodeProductList(params).enqueue(object :
            Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(
                        TAG,
                        "onResponse data:: ${response.body()!!}"
                    )

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                data.value = null
            }
        })
        return data
    }

    fun validateAddress(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.validateAddress(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun getSettings(): LiveData<CommonResponse?> {
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.settingsList().enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun getCartList(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.cartList(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    try {
                        val jsonObject = JSONObject(response.body().toString())

                        val commonResponse = CommonResponse()
                        commonResponse.responce = jsonObject.getBoolean("responce")
                        if (jsonObject.has("code"))
                            commonResponse.code = jsonObject.getString("code")
                        if (jsonObject.has("message"))
                            commonResponse.message = jsonObject.getString("message")
                        if (jsonObject.has("data"))
                            commonResponse.data = jsonObject.getString("data")

                        data.value = commonResponse
                    } catch (e: Exception) {
                        e.printStackTrace()
                        data.value = null
                    }
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun addCart(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.cartAdd(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun deleteCart(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.cartDelete(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun cleanCart(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.cartClean(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun minusCart(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.cartMinus(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun reorderCart(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.cartReorder(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun getTabList(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.getTabList(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun getTabProductList(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.getTabProductList(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun sendContact(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.sendContact(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun getReferralCode(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.getReferralCode(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun getNotificationList(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.getNotificationList(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

    fun getHomeList(params: Map<String, String>): LiveData<CommonResponse?> {
        Log.d(TAG, "Post Data::$params")
        val data = MutableLiveData<CommonResponse?>()
        apiRequest.getHomeList(params).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, "onResponse response:: $response")
                if (response.body() != null) {
                    Log.d(TAG, "onResponse data:: ${response.body()!!}")

                    val jsonObject = JSONObject(response.body().toString())

                    val commonResponse = CommonResponse()
                    commonResponse.responce = jsonObject.getBoolean("responce")
                    if (jsonObject.has("code"))
                        commonResponse.code = jsonObject.getString("code")
                    if (jsonObject.has("message"))
                        commonResponse.message = jsonObject.getString("message")
                    if (jsonObject.has("data"))
                        commonResponse.data = jsonObject.getString("data")

                    data.value = commonResponse
                }
            }

            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.d(TAG, "onFailure error:: ${t.printStackTrace()}")
                data.value = null
            }
        })
        return data
    }

}