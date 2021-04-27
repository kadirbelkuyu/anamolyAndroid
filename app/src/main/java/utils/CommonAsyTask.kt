package utils

import Config.BaseURL
import Dialogs.LoaderDialog
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class CommonAsyTask /*: AsyncTask<String, Int, String>*/ {

    private var _nameValuePairs: ArrayList<NameValuePair>? = null
    private var _baseUrl: String? = null
    private var context: Context? = null
    private var message = ""
    private var error_code = ""
    private var progressDialog: ProgressDialog? = null
    private var loaderDialog: LoaderDialog? = null
    private val is_progress_show: Boolean = false
    private var progress_status: Int = 0
    private var is_success: Boolean = false
    private var responceObj: JSONObject? = null
    private val PARM_RESPONCE = "responce"
    private var PARM_ERROR = "message"
    private var PARM_DATA = "data"
    private var vresponce: VJsonResponce? = null
    private var request_type: String? = null

    private val is_internet_connected: Boolean
        get() {
            if (context != null) {
                val cm =
                    context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val activeNetwork = cm.activeNetworkInfo
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting
            } else {
                return false
            }
        }

    constructor(
        request_type: String,
        nameValuePairs: ArrayList<NameValuePair>,
        baseUrl: String,
        vresponce: VJsonResponce,
        progress_status: Int,
        activity: Context
    ) {
        responceObj = JSONObject()
        this.vresponce = vresponce
        _nameValuePairs = nameValuePairs
        _baseUrl = baseUrl
        context = activity
        //this.is_progress_show = is_progress_show;
        this.progress_status = progress_status
        this.request_type = request_type
    }

    constructor(
        request_type: String,
        nameValuePairs: ArrayList<NameValuePair>,
        baseUrl: String,
        vresponce: VJsonResponce,
        progress_status: Int,
        activity: Context,
        returnData: String,
        returnTypeError: Boolean?
    ) {
        responceObj = JSONObject()
        this.vresponce = vresponce
        _nameValuePairs = nameValuePairs
        _baseUrl = baseUrl
        context = activity
        //this.is_progress_show = is_progress_show;
        this.progress_status = progress_status
        this.request_type = request_type
        if (returnTypeError == null) {
            this.PARM_ERROR = returnData
            this.PARM_DATA = returnData
        } else {
            if (returnTypeError) {
                this.PARM_ERROR = returnData
            } else {
                this.PARM_DATA = returnData
            }
        }
    }

    /**
     * if response success then return response data and if message available then pass message otherwise return empty message body
     * if response false then return message and if code is available then pass code otherwise return empty code
     * */
    interface VJsonResponce {
        /**
         * if response success then return response data and if message available then pass message otherwise return empty message body
         * */
        fun VResponce(responce: String, message: String)

        /**
         * if response false then return message and if code is available then pass code otherwise return empty code
         * */
        fun VError(responce: String, code: String)
    }

    fun execute() {
        if (is_internet_connected) {
            GlobalScope.launch(Dispatchers.Default) {
                withContext(Dispatchers.Default) {
                    if (progress_status == BaseURL.PROGRESSDIALOG) {
                        (context as Activity).runOnUiThread {
                            loaderDialog = LoaderDialog(context!!)
                            loaderDialog!!.show()
                        }
                    } else if (progress_status == BaseURL.PROGRESSBAR) {
                        loaderDialog = LoaderDialog(context!!)
                        loaderDialog!!.show()
                    }
                }
                val resultData = withContext(Dispatchers.IO) {
                    val jsonParser = JSONParser(context!!)

                    try {

                        val json_responce: String?

                        if (request_type == BaseURL.GET) {
                            var get_value = ""
                            val sb = StringBuilder()
                            if (_nameValuePairs!!.size > 0) {
                                for (i in _nameValuePairs!!.indices) {
                                    sb.append("/" + _nameValuePairs!![i].name + "/" + _nameValuePairs!![i].value)
                                }
                                get_value = sb.toString().substring(1)
                                //get_value = get_value.substring(1);
                                //get_value = _nameValuePairs.get(0).name + "=" + _nameValuePairs.get(0).value;
                            }
                            json_responce = jsonParser.exeGetRequest(_baseUrl!!, get_value)
                            Log.e(context!!.toString(), "GET")
                        } else {
                            json_responce =
                                jsonParser.execPostScriptJSON(_baseUrl!!, _nameValuePairs!!)
                            Log.e(context!!.toString(), "POST")
                        }

                        Log.e(context!!.toString(), json_responce.toString())

                        val jObj = JSONObject(json_responce)
                        responceObj = jObj
                        if (jObj.has(PARM_RESPONCE) && !jObj.getBoolean(PARM_RESPONCE)) {
                            is_success = false
                            //error_string = jObj.getString(PARM_ERROR)

                            message = if (PARM_ERROR.isEmpty()) {
                                jObj.toString()
                            } else if (jObj.has(PARM_ERROR)) {
                                jObj.getString(PARM_ERROR)
                            } else {
                                jObj.getString(PARM_ERROR)
                            }

                            if (jObj.has("code")) {
                                error_code = jObj.getString("code")
                            }

                            return@withContext null
                        } else {
                            is_success = true

                            if (jObj.has(PARM_ERROR)) {
                                message = jObj.getString(PARM_ERROR)
                            }

                            return@withContext if (PARM_DATA.isEmpty()) {
                                jObj.toString()
                            } else if (jObj.has(PARM_DATA)) {
                                jObj.getString(PARM_DATA)
                            } else {
                                "" + jObj.getBoolean(PARM_RESPONCE)
                            }
                        }

                    } catch (e: JSONException) {
                        message = e.message.toString()
                        e.printStackTrace()
                        return@withContext null
                    } catch (e: IOException) {
                        message = e.message.toString()
                        e.printStackTrace()
                        return@withContext null
                    }
                }

                try {
                    if (progressDialog != null && progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                        progressDialog = null
                    }
                    if (loaderDialog != null && loaderDialog!!.isShowing) {
                        loaderDialog!!.dismiss()
                        loaderDialog = null
                    }
                } catch (e: IllegalArgumentException) {
                    // Handle or log or ignore
                } catch (e: Exception) {
                    // Handle or log or ignore
                } finally {
                    progressDialog = null
                }
                withContext(Dispatchers.Main) {
                    if (resultData != null) {
                        vresponce!!.VResponce(resultData, message)
                    } else {
                        vresponce!!.VError(message, error_code)
                    }
                }
            }
        }
    }

    /*override fun onPreExecute() {
        if (is_internet_connected) {
            if (progress_status == BaseURL.PROGRESSDIALOG) {
                *//*progressDialog = ProgressDialog(context, R.style.AppCompatAlertDialogStyle)
                progressDialog!!.setMessage("Process with data..")
                progressDialog!!.setCancelable(false)
                progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog!!.show()*//*
                (context as Activity).runOnUiThread {
                    loaderDialog = LoaderDialog(context!!)
                    loaderDialog!!.show()
                }
            } else if (progress_status == BaseURL.PROGRESSBAR) {
                loaderDialog = LoaderDialog(context!!)
                loaderDialog!!.show()
            }
        } else {
            this.cancel(true)
        }
        super.onPreExecute()
    }

    override fun onCancelled() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
        if (loaderDialog != null && loaderDialog!!.isShowing) {
            loaderDialog!!.dismiss()
            loaderDialog = null
        }
        super.onCancelled()
    }

    override fun doInBackground(vararg strings: String): String? {

        val jsonParser = JSONParser(context!!)

        try {

            val json_responce: String?

            if (request_type == BaseURL.GET) {
                var get_value = ""
                val sb = StringBuilder()
                if (_nameValuePairs!!.size > 0) {
                    for (i in _nameValuePairs!!.indices) {
                        sb.append("/" + _nameValuePairs!![i].name + "/" + _nameValuePairs!![i].value)
                    }
                    get_value = sb.toString().substring(1)
                    //get_value = get_value.substring(1);
                    //get_value = _nameValuePairs.get(0).name + "=" + _nameValuePairs.get(0).value;
                }
                json_responce = jsonParser.exeGetRequest(_baseUrl!!, get_value)
                Log.e(context!!.toString(), "GET")
            } else {
                json_responce = jsonParser.execPostScriptJSON(_baseUrl!!, _nameValuePairs!!)
                Log.e(context!!.toString(), "POST")
            }

            Log.e(context!!.toString(), json_responce.toString())

            val jObj = JSONObject(json_responce)
            responceObj = jObj
            if (jObj.has(PARM_RESPONCE) && !jObj.getBoolean(PARM_RESPONCE)) {
                is_success = false
                //error_string = jObj.getString(PARM_ERROR)

                message = if (PARM_ERROR.isEmpty()) {
                    jObj.toString()
                } else if (jObj.has(PARM_ERROR)) {
                    jObj.getString(PARM_ERROR)
                } else {
                    jObj.getString(PARM_ERROR)
                }

                if (jObj.has("code")) {
                    error_code = jObj.getString("code")
                }

                return null
            } else {
                is_success = true

                if (jObj.has(PARM_ERROR)) {
                    message = jObj.getString(PARM_ERROR)
                }

                return if (PARM_DATA.isEmpty()) {
                    jObj.toString()
                } else if (jObj.has(PARM_DATA)) {
                    jObj.getString(PARM_DATA)
                } else {
                    "" + jObj.getBoolean(PARM_RESPONCE)
                }
            }

        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            message = e.message.toString()
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            message = e.message.toString()
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(responce: String?) {
        try {
            if (this.progressDialog != null && this.progressDialog!!.isShowing) {
                this.progressDialog!!.dismiss()
                this.progressDialog = null
            }
            if (loaderDialog != null && loaderDialog!!.isShowing) {
                loaderDialog!!.dismiss()
                loaderDialog = null
            }
        } catch (e: IllegalArgumentException) {
            // Handle or log or ignore
        } catch (e: Exception) {
            // Handle or log or ignore
        } finally {
            this.progressDialog = null
        }

        if (responce != null) {
            vresponce!!.VResponce(responce, message)
        } else {
            vresponce!!.VError(message, error_code)
        }
        super.onPostExecute(responce)
    }*/

}