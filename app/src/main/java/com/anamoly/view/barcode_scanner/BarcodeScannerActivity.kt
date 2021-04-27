package com.anamoly.view.barcode_scanner

import Dialogs.LoaderDialog
import Models.ProductModel
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.no_internet.NoInternetActivity
import com.anamoly.view.product_detail.ProductDetailActivity
import com.anamoly.view.search.SearchProductWithTextViewModel
import kotlinx.android.synthetic.main.activity_barcode_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import utils.ConnectivityReceiver
import utils.ContextWrapper
import utils.LanguagePrefs
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BarcodeScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    companion object {
        val TAG = BarcodeScannerActivity::class.java.simpleName
    }

    lateinit var zXingScannerView: ZXingScannerView

    lateinit var searchProductWithTextViewModel: SearchProductWithTextViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        searchProductWithTextViewModel =
            ViewModelProviders.of(this).get(SearchProductWithTextViewModel::class.java)
        setContentView(R.layout.activity_barcode_scanner)

        zXingScannerView = ZXingScannerView(this)
        val formats = ArrayList<BarcodeFormat>()
        formats.add(BarcodeFormat.CODABAR)
        //zXingScannerView.setFormats(formats)

        fm_scanner_view.addView(zXingScannerView)

    }


    override fun onResume() {
        super.onResume()
        zXingScannerView.setResultHandler(this)
        zXingScannerView.startCamera()
        zXingScannerView.setAutoFocus(true)
    }

    override fun onPause() {
        super.onPause()
        zXingScannerView.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        Log.d(TAG, "scanText::${rawResult.text}")
        Log.d(TAG, "scanFormate::${rawResult.barcodeFormat}")

        if (rawResult.barcodeFormat != BarcodeFormat.QR_CODE) {
            /*Intent().apply {
                setResult(Activity.RESULT_OK, this)
                putExtra("scanResult", rawResult.text)
                finish()
            }*/
            if (ConnectivityReceiver.isConnected) {
                makeSearchProduct(rawResult.text)
            } else {
                Intent(this@BarcodeScannerActivity, NoInternetActivity::class.java).apply {
                    startActivityForResult(this, 9328)
                }
            }
        }
    }

    private fun makeSearchProduct(scannData: String) {

        val loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        val params = HashMap<String, String>()
        params["barcode"] = scannData
        searchProductWithTextViewModel.getBarcodeProductList(params)
            .observe(this, Observer { response: CommonResponse? ->
                loaderDialog.dismiss()
                if (response != null) {
                    if (response.responce!!) {
                        val gson = Gson()
                        val type = object : TypeToken<ProductModel>() {}.type
                        val productModel = gson.fromJson<ProductModel>(
                            response.data, type
                        )

                        Intent(this, ProductDetailActivity::class.java).apply {
                            putExtra("productData", productModel as Serializable)
                            startActivity(this)
                        }
                    } else {
                        CommonActivity.showToast(this, response.message!!)
                    }
                }
                zXingScannerView.resumeCameraPreview(this)
            })
    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
