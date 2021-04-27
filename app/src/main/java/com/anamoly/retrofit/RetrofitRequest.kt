package com.anamoly.retrofit

import Config.BaseURL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitRequest {
    private var retrofit: Retrofit? = null
    val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                val httpClient = OkHttpClient.Builder()
                httpClient.connectTimeout(2, TimeUnit.MINUTES)
                httpClient.writeTimeout(2, TimeUnit.MINUTES)
                httpClient.readTimeout(2, TimeUnit.MINUTES)
                httpClient.addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("X-API-KEY", BaseURL.HEADER_KEY)
                        .addHeader("X-APP-LANGUAGE", BaseURL.HEADER_LANG)
                        .addHeader("X-APP-DEVICE", BaseURL.HEADER_DEVICE)
                        .addHeader("X-APP-VERSION", BaseURL.HEADER_VERSION)
                        .build()
                    chain.proceed(request)
                }
                retrofit = Retrofit.Builder()
                    .baseUrl(BaseURL.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()
            }
            return retrofit
        }
}