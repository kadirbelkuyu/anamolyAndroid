package com.anamoly.view.thanks

import Config.GlobleVariable
import Models.OrderModel
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.home.MainActivity
import kotlinx.android.synthetic.main.activity_thanks.*
import org.json.JSONObject
import utils.ContextWrapper
import utils.LanguagePrefs
import java.util.*


class ThanksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_thanks)

        val jsonObject = JSONObject(intent.getStringExtra("orderData")!!)

        val gson = Gson()
        val type = object : TypeToken<OrderModel>() {}.type
        val orderModel = gson.fromJson<OrderModel>(jsonObject.toString(), type)

        tv_thanks_total_title.text = if (orderModel.paid_by == "ideal") {
            resources.getString(R.string.total_paid)
        } else {
            resources.getString(R.string.total_amount)
        }

        tv_thanks_order_id.text = orderModel.order_no
        tv_thanks_order_date.text = CommonActivity.getConvertDate(orderModel.delivery_date!!, 6)
        tv_thanks_total_item.text = orderModel.orderItemModelList?.size.toString()
        tv_thanks_total_price_main.setText(
            CommonActivity.getPriceWithCurrency(
                this,
                String.format(GlobleVariable.LOCALE, "%.2f", orderModel.order_amount!!.toDouble())
                    .replace(".00", "")
            )
        )
        tv_thanks_order_discount.setText(
            "-${CommonActivity.getPriceWithCurrency(
                this,
                String.format(GlobleVariable.LOCALE, "%.2f", orderModel.discount_amount!!.toDouble())
                    .replace(".00", "")
            )}"
        )
        tv_thanks_gateway_charge.setText(
            CommonActivity.getPriceWithCurrency(
                this,
                String.format(GlobleVariable.LOCALE, "%.2f", orderModel.gateway_charges!!.toDouble())
                    .replace(".00", "")
            )
        )
        tv_thanks_total_price.setText(
            CommonActivity.getPriceWithCurrency(
                this,
                String.format(GlobleVariable.LOCALE, "%.2f", orderModel.net_amount!!.toDouble())
                    .replace(".00", "")
            )
        )

        if (orderModel.gateway_charges.toDouble() > 0) {
            ll_thanks_gateway_charge.visibility = View.VISIBLE
        } else {
            ll_thanks_gateway_charge.visibility = View.GONE
        }

        val qrCodeDetail = StringBuilder()
        qrCodeDetail.append(resources.getString(R.string.order_id))
        qrCodeDetail.append(":")
        qrCodeDetail.append(orderModel.order_no)
        qrCodeDetail.append("\n")
        qrCodeDetail.append(resources.getString(R.string.date))
        qrCodeDetail.append(":")
        qrCodeDetail.append(orderModel.delivery_date)
        qrCodeDetail.append("\n")
        qrCodeDetail.append(resources.getString(R.string.total_items))
        qrCodeDetail.append(":")
        qrCodeDetail.append(orderModel.orderItemModelList?.size.toString())
        qrCodeDetail.append("\n")
        qrCodeDetail.append(resources.getString(R.string.subtotal))
        qrCodeDetail.append(":")
        qrCodeDetail.append(orderModel.order_amount)
        qrCodeDetail.append("\n")
        qrCodeDetail.append(resources.getString(R.string.discount))
        qrCodeDetail.append(":")
        qrCodeDetail.append(orderModel.discount_amount)
        qrCodeDetail.append("\n")
        qrCodeDetail.append(resources.getString(R.string.gateway_charges))
        qrCodeDetail.append(":")
        qrCodeDetail.append(orderModel.discount_amount)
        qrCodeDetail.append("\n")
        qrCodeDetail.append(resources.getString(R.string.total_paid))
        qrCodeDetail.append(":")
        qrCodeDetail.append(orderModel.net_amount)

        iv_thanks_qr.setImageBitmap(generateQrCode(qrCodeDetail.toString()))

        btn_thanks_continue.setOnClickListener {
            goNext()
        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        goNext()
    }

    private fun goNext() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finishAffinity()
        }
    }

    fun generateQrCode(data: String?): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 150, 150)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
