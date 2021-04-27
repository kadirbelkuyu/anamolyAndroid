package com.anamoly.view.payment

import Config.BaseURL
import Dialogs.LoaderDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.anamoly.BuildConfig
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.thanks.ThanksActivity
import kotlinx.android.synthetic.main.activity_payment.*
import utils.*
import java.net.URISyntaxException
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class PaymentActivity : AppCompatActivity() {

    companion object {
        val TAG = PaymentActivity::class.java.simpleName
    }

    lateinit var loaderDialog: LoaderDialog

    var isSuccess = false

    var webMessage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_payment)

        webMessage =
            "<html><body><h3>${resources.getString(R.string.please_wait_until_finish_transaction)}</h3></body></html>"

        val url = intent.getStringExtra("paymentUrl")!!

        Log.d(TAG, "PaymentUrl:$url")

        loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        val webSettings: WebSettings = wv_payment.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setSupportMultipleWindows(true)
        wv_payment.webChromeClient = UriWebChromeClient()
        wv_payment.webViewClient = UriWebViewClient()

        wv_payment.loadUrl(url)

    }

    inner class UriWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (newProgress == 100) {
                loaderDialog.dismiss()
            } else {
                if (!this@PaymentActivity.isFinishing && !loaderDialog.isShowing)
                    loaderDialog.show()
            }
        }
    }

    inner class UriWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            checkUrl(view, url)
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
            checkUrl(view, url!!)
            return super.shouldInterceptRequest(view, url)
        }

        override fun onReceivedSslError(
            view: WebView, handler: SslErrorHandler,
            error: SslError
        ) {
            Log.d("onReceivedSslError", "onReceivedSslError")
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            if (!loaderDialog.isShowing)
                loaderDialog.show()
        }

        override fun onPageFinished(view: WebView, url: String) {
            loaderDialog.dismiss()
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onReceivedError: ${error.toString()}")
            }
            view?.loadUrl("about:blank");
            view?.invalidate()
            super.onReceivedError(view, request, error)
        }

    }

    fun checkUrl(view: WebView?, url: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "shouldOverrideUrlLoading: $url")
        }

        if (URLUtil.isNetworkUrl(url)) {
            val p: Pattern = Pattern.compile(".*idealpayment/response.*")
            val m: Matcher = p.matcher(url)
            if (m.matches()) {
                Log.d(TAG, "shouldOverrideUrlLoading: success")
                isSuccess = true
                Handler(Looper.getMainLooper()).post(Runnable {
                    makeGetPaymentResponse(url)
                })
                wv_payment.loadDataWithBaseURL("", webMessage, "text/html", "UTF-8", "")
            }
        } else {
            if (url.startsWith("intent://")) {
                try {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    if (intent != null) {
                        view?.stopLoading()
                        val packageManager = this@PaymentActivity.packageManager
                        val info = packageManager.resolveActivity(
                            intent,
                            PackageManager.MATCH_DEFAULT_ONLY
                        )
                        if (info != null) {
                            this@PaymentActivity.startActivity(intent)
                        } else {
                            //try to find fallback url
                            val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                            if (fallbackUrl != null) {
                                view?.loadUrl(fallbackUrl)
                            } else {
                                //invite to install
                                val marketIntent = Intent(Intent.ACTION_VIEW).setData(
                                    Uri.parse("market://details?id=" + intent.getPackage())
                                )
                                if (marketIntent.resolveActivity(packageManager) != null) {
                                    this@PaymentActivity.startActivity(marketIntent)
                                }
                            }
                        }
                    }
                } catch (e: URISyntaxException) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Can't resolve intent://", e)
                    }
                }
            }

            /*Handler(Looper.getMainLooper()).post(Runnable {

                var webpage = Uri.parse(url)

                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    webpage = Uri.parse("http://$url")
                }
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                startActivity(intent)
                finish()

                *//*if (appInstalledOrNot(url)) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    finish()
                } else {
                    CommonActivity.showToast(
                        this@PaymentActivity,
                        "This app is not install in your phone."
                    )
                    finish()
                }*//*
            })*/
        }
    }

    private fun makeGetPaymentResponse(url: String) {
        val params = ArrayList<NameValuePair>()

        if (!loaderDialog.isShowing)
            loaderDialog.show()

        val task = CommonAsyTask(
            BaseURL.POST, params,
            url, object : CommonAsyTask.VJsonResponce {
                override fun VResponce(responce: String, message: String) {
                    loaderDialog.dismiss()

                    SessionManagement.UserData.setSession(
                        this@PaymentActivity,
                        "cartData",
                        ""
                    )

                    Intent(this@PaymentActivity, ThanksActivity::class.java).apply {
                        putExtra("orderData", responce)
                        startActivity(this)
                        finish()
                    }
                }

                override fun VError(responce: String, code: String) {
                    loaderDialog.dismiss()
                    CommonActivity.showToast(this@PaymentActivity, responce)
                    finish()
                }
            }, BaseURL.PROGRESSDIALOG, this
        )
        task.execute()
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
