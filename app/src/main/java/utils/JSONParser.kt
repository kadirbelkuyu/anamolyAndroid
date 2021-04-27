package utils

import Config.BaseURL
import android.content.Context
import android.util.Log
import com.anamoly.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class JSONParser(context: Context) {

    // public static final String apiLocLogin ="http://fmv.cc/micron/index.php/marketplace/api/index/";
    //http://fmv.cc/micron/index.php/marketplace/api/index/username/test@gmail.com/password/test@123

    @Throws(IOException::class)
    fun execPostScriptJSON(url: String, valuePairs: ArrayList<NameValuePair>): String? {
        var responce: String? = null
        val client = OkHttpClient()
        //increased timeout for slow response
        val eagerClient = client.newBuilder()
            .readTimeout(1, TimeUnit.MINUTES)
            .build()

        val builder = FormBody.Builder()

        val stringBuilder = StringBuilder()

        for (valuePair in valuePairs) {
            builder.add(valuePair.name, valuePair.value)
            stringBuilder.append(valuePair.name + "=" + valuePair.value)
            stringBuilder.append(", ")
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("X-API-KEY", BaseURL.HEADER_KEY)
            .addHeader("X-APP-LANGUAGE", BaseURL.HEADER_LANG)
            .addHeader("X-APP-DEVICE", BaseURL.HEADER_DEVICE)
            .addHeader("X-APP-VERSION", BaseURL.HEADER_VERSION)
            .post(builder.build()).build()

        try {
            val response = eagerClient.newCall(request).execute()
            if (response.isSuccessful) {
                responce = response.body!!.string()
            } else {
                val jsonObjectError = JSONObject()
                jsonObjectError.put("isSuccess", false)
                jsonObjectError.put("message", response.body.toString())
                responce = jsonObjectError.toString()
            }
        } catch (e: SocketTimeoutException) {
            val jsonObjectError = JSONObject()
            jsonObjectError.put("isSuccess", false)
            jsonObjectError.put("message", "Connection timeout")
            responce = jsonObjectError.toString()
        }

        /*val response = eagerClient.newCall(request).execute()
        responce = response.body()!!.string()*/

        if (BuildConfig.DEBUG) {
            Log.i("PARAMETERS", "execMultiPartPostScriptJSON: " + eagerClient.readTimeoutMillis)
            Log.i("POST:", stringBuilder.toString())
            Log.i("Registration Request::", request.toString())
            Log.i("REGISTRATION RESPONSE::", responce.toString())
        }

        return responce
    }

    @Throws(IOException::class)
    fun execMultiPartPostScriptJSON(
        url: String, valuePairs: ArrayList<NameValuePair>,
        MEDIA_TYPE_PNG: String, filepath: String?, imagename: String
    ): String? {
        var responce: String? = null

        val client = OkHttpClient()
        //increased timeout for slow response
        val eagerClient = client.newBuilder()
            .readTimeout(2, TimeUnit.MINUTES)
            .build()
        Log.i("PARAMETERS", "PARAMETERS ::" + valuePairs)

        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        if (filepath != null && !filepath.equals("", ignoreCase = true)) {
            val fileupload = File(filepath)
            if (fileupload != null) {
                builder.addFormDataPart(
                    imagename, fileupload.name,
                    RequestBody.create(MEDIA_TYPE_PNG.toMediaType(), fileupload)
                )
            }
        }

        for (valuePair in valuePairs) {
            builder.addFormDataPart(valuePair.name, valuePair.value)
        }

        val requestBody = builder.build()

        val request = Request.Builder().url(url)
            .addHeader("X-API-KEY", BaseURL.HEADER_KEY)
            .addHeader("X-APP-LANGUAGE", BaseURL.HEADER_LANG)
            .addHeader("X-APP-DEVICE", BaseURL.HEADER_DEVICE)
            .addHeader("X-APP-VERSION", BaseURL.HEADER_VERSION)
            .post(requestBody).build()
        Log.i("Registration Request::", request.toString())


        val response = eagerClient.newCall(request).execute()
        Log.i("REGISTRATION RESPONSE::", response.toString())
        responce = response.body!!.string()

        return responce
    }

    @Throws(IOException::class)
    fun execMultiPartPostScriptJSON(
        url: String, valuePairs: ArrayList<NameValuePair>,
        MEDIA_TYPE_PNG: String, valuePairsImg: ArrayList<NameValuePair>
    ): String? {
        var responce: String? = null

        val client = OkHttpClient()
        //increased timeout for slow response
        val eagerClient = client.newBuilder()
            .connectTimeout(4, TimeUnit.MINUTES)
            .readTimeout(4, TimeUnit.MINUTES)
            .writeTimeout(4, TimeUnit.MINUTES)
            .build()

        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        val stringBuilderImg = StringBuilder()
        for (valuePair in valuePairsImg) {
            val fileupload = File(valuePair.value.toString())
            if (fileupload.exists()) {
                builder.addFormDataPart(
                    valuePair.name, fileupload.name,
                    RequestBody.create(MEDIA_TYPE_PNG.toMediaType(), fileupload)
                )
            }
            stringBuilderImg.append(valuePair.name + "=" + valuePair.value)
            stringBuilderImg.append(", ")
        }

        val stringBuilder = StringBuilder()
        for (valuePair in valuePairs) {
            builder.addFormDataPart(valuePair.name, valuePair.value.toString())
            stringBuilder.append(valuePair.name + "=" + valuePair.value)
            stringBuilder.append(", ")
        }

        val requestBody = builder.build()

        val request = Request.Builder().url(url)
            .addHeader("X-API-KEY", BaseURL.HEADER_KEY)
            .addHeader("X-APP-LANGUAGE", BaseURL.HEADER_LANG)
            .addHeader("X-APP-DEVICE", BaseURL.HEADER_DEVICE)
            .addHeader("X-APP-VERSION", BaseURL.HEADER_VERSION)
            .post(requestBody).build()

        val response = eagerClient.newCall(request).execute()
        responce = response.body!!.string()

        if (BuildConfig.DEBUG) {
            Log.i("PARAMETERS", "execMultiPartPostScriptJSON: " + eagerClient.readTimeoutMillis)
            Log.i("POST:", stringBuilder.toString())
            Log.i("POSTIMG:", stringBuilderImg.toString())
            Log.i("Registration Request::", request.toString())
            Log.i("REGISTRATION RESPONSE::", responce)
        }
        return responce
    }

    @Throws(IOException::class)
    fun exeGetRequest(url: String, params: String): String {
        var result = ""

        val client = OkHttpClient()
        //increased timeout for slow response
        val eagerClient = client.newBuilder()
            .readTimeout(1, TimeUnit.MINUTES)
            .build()

        val my_url: String
        if (params.isEmpty()) {
            my_url = url
        } else {
            my_url = url + "/" + params
        }

        Log.e("query", my_url)
        val request = Request.Builder()
            .url(my_url)
            .addHeader("X-API-KEY", BaseURL.HEADER_KEY)
            .addHeader("X-APP-LANGUAGE", BaseURL.HEADER_LANG)
            .addHeader("X-APP-DEVICE", BaseURL.HEADER_DEVICE)
            .addHeader("X-APP-VERSION", BaseURL.HEADER_VERSION)
            .build()

        val call = client.newCall(request)
        val response = eagerClient.newCall(request).execute()

        result = response.body!!.string()
        Log.i("Response", result)
        return result
    }

}