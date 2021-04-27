package Database

import Models.ProductComboModel
import Models.ProductModel
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.anamoly.CommonActivity
import java.io.Serializable

/**
 * Created on 04-02-2020.
 */
class CartData(val context: Context) {

    private var db: SQLiteDatabase? = null
    private val dbHandler: DatabaseHandler

    companion object {

        const val CART_TABLE = "CartData"

        const val COLUMN_CART_ID = "id"
        const val COLUMN_PRODUCT_ID = "product_id"
        const val COLUMN_PRODUCT_NAME = "product_name_en"
        const val COLUMN_IN_STOCK = "in_stock"
        const val COLUMN_PRICE = "price"
        const val COLUMN_QTY = "qty"
        const val COLUMN_UNIT = "unit"
        const val COLUMN_UNIT_VALUE = "unit_value"
        const val COLUMN_DISCOUNT = "discount"
        const val COLUMN_DISCOUNT_TYPE = "discount_type"
        const val COLUMN_OFFER_ID = "product_offer_id"
        const val COLUMN_DISCOUNT_ID = "product_discount_id"
        const val COLUMN_COMBO_ID = "product_combo_id"
        const val COLUMN_OFFER_TYPE = "offer_type"
        const val COLUMN_OFFER_DISCOUNT = "offer_discount"
        const val COLUMN_IMAGE = "product_image"
        const val COLUMN_EXTRA_DATA = "extra_data"

    }

    init {
        dbHandler = DatabaseHandler(context)
    }

    val gson = Gson()
    val gsonBuilder = GsonBuilder().create()
    val typeProduct = object : TypeToken<ArrayList<ProductComboModel>>() {}.type
    val typeProductModel = object : TypeToken<ProductComboModel>() {}.type

    fun setProduct(productModel: ProductModel, qty: Int): String {
        db = dbHandler.writableDatabase

        val gsonBuilder = GsonBuilder().create()
        val extra_data = gsonBuilder.toJsonTree(productModel).asJsonObject

        val values = ContentValues()
        values.put(COLUMN_PRODUCT_ID, productModel.product_id)
        values.put(COLUMN_PRODUCT_NAME, productModel.product_name_en)
        values.put(COLUMN_IN_STOCK, productModel.in_stock)
        values.put(COLUMN_PRICE, productModel.price)
        values.put(COLUMN_QTY, qty)
        values.put(COLUMN_UNIT, productModel.unit)
        values.put(COLUMN_UNIT_VALUE, productModel.unit_value)
        values.put(COLUMN_DISCOUNT, productModel.discount)
        values.put(COLUMN_DISCOUNT_TYPE, productModel.discount_type)
        values.put(COLUMN_OFFER_ID, productModel.product_offer_id)
        values.put(COLUMN_DISCOUNT_ID, productModel.product_discount_id)
        values.put(COLUMN_COMBO_ID, productModel.product_combo_id)
        values.put(COLUMN_OFFER_TYPE, productModel.offer_type)
        values.put(COLUMN_OFFER_DISCOUNT, productModel.offer_discount)
        values.put(COLUMN_IMAGE, productModel.product_image)
        values.put(COLUMN_EXTRA_DATA, extra_data.toString())

        if (isInCart(productModel.cart_id)) {
            val id = db!!.update(
                CART_TABLE,
                values,
                COLUMN_PRODUCT_ID + "=" + productModel.product_id,
                null
            )
            db!!.close()
            return id.toString()
        } else {
            val id = db!!.insert(CART_TABLE, null, values)
            db!!.close()
            return id.toString()
        }
    }

    fun setProduct2(productModel: ProductModel, qty: Int): String {
        db = dbHandler.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_PRODUCT_ID, productModel.product_id)
        values.put(COLUMN_PRODUCT_NAME, productModel.product_name_en)
        values.put(COLUMN_IN_STOCK, productModel.in_stock)
        values.put(COLUMN_PRICE, productModel.price)
        values.put(COLUMN_QTY, qty)
        values.put(COLUMN_UNIT, productModel.unit)
        values.put(COLUMN_UNIT_VALUE, productModel.unit_value)
        values.put(COLUMN_DISCOUNT, productModel.discount)
        values.put(COLUMN_DISCOUNT_TYPE, productModel.discount_type)
        values.put(COLUMN_OFFER_ID, productModel.product_offer_id)
        values.put(COLUMN_DISCOUNT_ID, productModel.product_discount_id)
        values.put(COLUMN_COMBO_ID, productModel.product_combo_id)
        values.put(COLUMN_OFFER_TYPE, productModel.offer_type)
        values.put(COLUMN_OFFER_DISCOUNT, productModel.offer_discount)
        values.put(COLUMN_IMAGE, productModel.product_image)

        if (productModel.offer_type == null) {
            val extra_data = gsonBuilder.toJsonTree(productModel).asJsonObject
            values.put(COLUMN_EXTRA_DATA, extra_data.toString())

            return if (isInCartProduct(productModel.product_id)) {
                val id = db!!.update(
                    CART_TABLE,
                    values,
                    COLUMN_PRODUCT_ID + "=" + productModel.product_id,
                    null
                )
                db!!.close()
                id.toString()
            } else {
                val id = db!!.insert(CART_TABLE, null, values)
                db!!.close()
                id.toString()
            }
        } else {
            var cartNewOld = "0"
            var hasNewItem = false
            /*if (isInCartProduct(productModel.product_id)) {
                Log.d(context.toString(), "cartUpdateHas")
                val productComboModelList = ArrayList<ProductComboModel>()
                if (productModel.productComboModelList != null) {
                    productComboModelList.addAll(productModel.productComboModelList!!)
                }

                Log.d(context.toString(), "cartUpdateListSize::${productComboModelList.size}")

                if (productComboModelList.size < productModel.number_of_products!!.toInt()) {
                    productComboModelList.add(productComboModel)

                    productModel.productComboModelList = productComboModelList

                    Log.d(
                        context.toString(),
                        "cartUpdateListSizeOldNew::${productModel.productComboModelList?.size}"
                    )

                    val extra_data2 = gsonBuilder.toJsonTree(productModel).asJsonObject
                    values.put(COLUMN_EXTRA_DATA, extra_data2.toString())

                    val id = db!!.update(
                        CART_TABLE,
                        values,
                        COLUMN_CART_ID + "=" + productModel.cart_id,
                        null
                    )
                    db!!.close()
                    return id.toString()
                } else {
                    val productComboModelListNew = ArrayList<ProductComboModel>()
                    productComboModelListNew.add(productComboModel)

                    productModel.productComboModelList = productComboModelListNew

                    Log.d(
                        context.toString(),
                        "cartUpdateListSizeNew::${productModel.productComboModelList?.size}"
                    )

                    val extra_data2 = gsonBuilder.toJsonTree(productModel).asJsonObject
                    values.put(COLUMN_EXTRA_DATA, extra_data2.toString())

                    val id = db!!.insert(CART_TABLE, null, values)
                    db!!.close()
                    return id.toString()
                }
            } else {*/

            productModel.qty_cart = 1
            val productComboModel = gson.fromJson<ProductComboModel>(
                gsonBuilder.toJsonTree(productModel).asJsonObject, typeProductModel
            )

            val comboProductModelList = ArrayList<ProductModel>()
            comboProductModelList.addAll(
                getProductListByOfferDetail(
                    productModel.product_offer_id!!,
                    productModel.offer_type!!
                )
            )

            Log.d(context.toString(), "comboListSize::${comboProductModelList.size}")

            val productModelList = ArrayList<ProductComboModel>()

            db = dbHandler.writableDatabase

            if (comboProductModelList.size > 0) {
                var hasMinGroup = false
                var minGroupProductModel: ProductModel? = null
                for ((position, productModelCombo) in comboProductModelList.withIndex()) {
                    if (productModelCombo.productComboModelList!!.size < productModel.number_of_products!!.toInt()) {
                        hasMinGroup = true
                        minGroupProductModel = productModelCombo
                        break
                    }
                }

                Log.d(context.toString(), "hasSize::$hasMinGroup")

                if (hasMinGroup) {
                    //productModelList.add(productComboModel)

                    minGroupProductModel?.productComboModelList?.add(productComboModel)

                    val extra_data2 = gsonBuilder.toJsonTree(minGroupProductModel).asJsonObject
                    values.put(COLUMN_EXTRA_DATA, extra_data2.toString())

                    val id = db!!.update(
                        CART_TABLE,
                        values,
                        COLUMN_CART_ID + "=" + minGroupProductModel?.cart_id,
                        null
                    )
                    db!!.close()
                    cartNewOld = minGroupProductModel?.cart_id.toString()
                    hasNewItem = false
                } else {
                    productModelList.add(productComboModel)

                    productModel.productComboModelList = productModelList

                    val extra_data2 = gsonBuilder.toJsonTree(productModel).asJsonObject
                    values.put(COLUMN_EXTRA_DATA, extra_data2.toString())

                    val id = db!!.insert(CART_TABLE, null, values)
                    db!!.close()
                    cartNewOld = id.toString()
                    hasNewItem = true
                }
            } else {
                productModelList.add(productComboModel)

                productModel.productComboModelList = productModelList

                val extra_data2 = gsonBuilder.toJsonTree(productModel).asJsonObject
                values.put(COLUMN_EXTRA_DATA, extra_data2.toString())

                val id = db!!.insert(CART_TABLE, null, values)
                db!!.close()
                cartNewOld = id.toString()
                hasNewItem = true
            }

            Intent("comboUpdate").apply {
                putExtra("type", "update")
                putExtra("hasNew", hasNewItem)
                putExtra("cartId", cartNewOld)
                putExtra("productModelData", productModel as Serializable)
                context.sendBroadcast(this)
            }

            return cartNewOld
            //}
        }
    }

    fun setProductList(productModelList: ArrayList<ProductModel>) {
        db = dbHandler.writableDatabase
        db!!.beginTransaction()

        for (productModel in productModelList) {
            val gsonBuilder = GsonBuilder().create()
            val extra_data = gsonBuilder.toJsonTree(productModel).asJsonObject

            val values = ContentValues()
            values.put(COLUMN_PRODUCT_ID, productModel.product_id)
            values.put(COLUMN_PRODUCT_NAME, productModel.product_name_en)
            values.put(COLUMN_IN_STOCK, productModel.in_stock)
            values.put(COLUMN_PRICE, productModel.price)
            values.put(COLUMN_QTY, productModel.qty_cart)
            values.put(COLUMN_UNIT, productModel.unit)
            values.put(COLUMN_UNIT_VALUE, productModel.unit_value)
            values.put(COLUMN_DISCOUNT, productModel.discount)
            values.put(COLUMN_DISCOUNT_TYPE, productModel.discount_type)
            values.put(COLUMN_OFFER_ID, productModel.product_offer_id)
            values.put(COLUMN_DISCOUNT_ID, productModel.product_discount_id)
            values.put(COLUMN_COMBO_ID, productModel.product_combo_id)
            values.put(COLUMN_OFFER_TYPE, productModel.offer_type)
            values.put(COLUMN_OFFER_DISCOUNT, productModel.offer_discount)
            values.put(COLUMN_IMAGE, productModel.product_image)
            values.put(COLUMN_EXTRA_DATA, extra_data.toString())

            if (isInCartProduct(productModel.product_id)) {
                val id = db!!.update(
                    CART_TABLE,
                    values,
                    COLUMN_PRODUCT_ID + "=" + productModel.product_id,
                    null
                )
            } else {
                val id = db!!.insert(CART_TABLE, null, values)
            }
        }

        db!!.setTransactionSuccessful()
        db!!.endTransaction()
        db!!.close()
    }

    fun isInCart(id: Int): Boolean {
        db = dbHandler.readableDatabase
        val qry = "Select *  from $CART_TABLE where $COLUMN_CART_ID = $id"
        val cursor = db!!.rawQuery(qry, null)
        cursor.moveToFirst()
        cursor.use { cursor ->
            return cursor.count > 0
        }
    }

    fun isInCartProduct(product_id: String?): Boolean {
        db = dbHandler.readableDatabase
        val qry = "Select *  from $CART_TABLE where $COLUMN_PRODUCT_ID = $product_id"
        val cursor = db!!.rawQuery(qry, null)
        cursor.moveToFirst()
        cursor.use { cursor ->
            return cursor.count > 0
        }
    }

    fun getProductDetail(product_id: String): ProductModel? {
        db = dbHandler.readableDatabase
        val qry = "Select *  from $CART_TABLE where $COLUMN_PRODUCT_ID = $product_id"
        val cursor = db!!.rawQuery(qry, null)
        try {
            cursor.moveToFirst()
            val gson = Gson()
            val type = object : TypeToken<ProductModel>() {}.type

            return if (cursor.count > 0) {
                val productModel = gson.fromJson<ProductModel>(
                    cursor.getString(
                        cursor.getColumnIndex(COLUMN_EXTRA_DATA)
                    ), type
                )
                productModel.qty_cart = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY))
                productModel.cart_id = cursor.getInt(cursor.getColumnIndex(COLUMN_CART_ID))
                return productModel
            } else {
                null
            }
        } finally {
            cursor.close()
            db!!.close()
        }
    }

    fun getProductDetailByCartId(cart_id: Int): ProductModel? {
        db = dbHandler.readableDatabase
        val qry = "Select *  from $CART_TABLE where $COLUMN_CART_ID = $cart_id"
        val cursor = db!!.rawQuery(qry, null)
        try {
            cursor.moveToFirst()
            val gson = Gson()
            val type = object : TypeToken<ProductModel>() {}.type

            return if (cursor.count > 0) {
                val productModel = gson.fromJson<ProductModel>(
                    cursor.getString(
                        cursor.getColumnIndex(COLUMN_EXTRA_DATA)
                    ), type
                )
                productModel.qty_cart = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY))
                productModel.cart_id = cursor.getInt(cursor.getColumnIndex(COLUMN_CART_ID))
                return productModel
            } else {
                null
            }
        } finally {
            cursor.close()
            db!!.close()
        }
    }

    fun getProductList(): ArrayList<ProductModel> {
        val list = ArrayList<ProductModel>()
        db = dbHandler.readableDatabase
        val qry = "Select *  from $CART_TABLE"
        val cursor = db!!.rawQuery(qry, null)
        try {
            cursor.moveToFirst()

            val gson = Gson()
            val type = object : TypeToken<ProductModel>() {}.type

            for (i in 0 until cursor.count) {

                val productModel = gson.fromJson<ProductModel>(
                    cursor.getString(
                        cursor.getColumnIndex(COLUMN_EXTRA_DATA)
                    ), type
                )
                productModel.qty_cart = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY))
                productModel.cart_id = cursor.getInt(cursor.getColumnIndex(COLUMN_CART_ID))

                list.add(productModel)
                cursor.moveToNext()
            }
            return list
        } finally {
            cursor.close()
            db!!.close()
        }
    }

    fun getHasProduct(productModel: ProductModel): Boolean {
        db = dbHandler.readableDatabase
        val qry = "Select *  from $CART_TABLE"
        val cursor = db!!.rawQuery(qry, null)
        try {
            cursor.moveToFirst()

            val gson = Gson()
            val type = object : TypeToken<ProductModel>() {}.type

            for (i in 0 until cursor.count) {

                val productModeldata = gson.fromJson<ProductModel>(
                    cursor.getString(
                        cursor.getColumnIndex(COLUMN_EXTRA_DATA)
                    ), type
                )
                productModel.qty_cart = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY))
                productModel.cart_id = cursor.getInt(cursor.getColumnIndex(COLUMN_CART_ID))

                if (productModeldata.offer_type != null
                    && productModeldata.productComboModelList != null
                    && productModeldata.productComboModelList!!.size > 0
                ) {
                    for (productComboModel in productModeldata.productComboModelList!!) {
                        if (productComboModel.product_id == productModel.product_id) {
                            return true
                        }
                    }
                } else {
                    if (productModeldata.product_id == productModel.product_id) {
                        return true
                    }
                }

                cursor.moveToNext()
            }
            return false
        } finally {
            cursor.close()
            db!!.close()
        }
    }

    fun getProductListByProductId(product_id: String): ArrayList<ProductModel> {
        val list = ArrayList<ProductModel>()
        db = dbHandler.readableDatabase
        val qry =
            "Select *  from $CART_TABLE where $COLUMN_PRODUCT_ID = $product_id"
        val cursor = db!!.rawQuery(qry, null)
        try {
            cursor.moveToFirst()

            val gson = Gson()
            val type = object : TypeToken<ProductModel>() {}.type

            for (i in 0 until cursor.count) {

                val productModel = gson.fromJson<ProductModel>(
                    cursor.getString(
                        cursor.getColumnIndex(COLUMN_EXTRA_DATA)
                    ), type
                )
                productModel.qty_cart = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY))
                productModel.cart_id = cursor.getInt(cursor.getColumnIndex(COLUMN_CART_ID))

                list.add(productModel)
                cursor.moveToNext()
            }
            return list
        } finally {
            cursor.close()
            db!!.close()
        }
    }

    fun getProductListByOfferDetail(offer_id: String, offerType: String): ArrayList<ProductModel> {
        val list = ArrayList<ProductModel>()
        db = dbHandler.readableDatabase
        val qry =
            "Select * from $CART_TABLE where $COLUMN_OFFER_ID = $offer_id AND $COLUMN_OFFER_TYPE = '$offerType'"
        val cursor = db!!.rawQuery(qry, null)
        try {
            cursor.moveToFirst()

            val gson = Gson()
            val type = object : TypeToken<ProductModel>() {}.type

            for (i in 0 until cursor.count) {

                val productModel = gson.fromJson<ProductModel>(
                    cursor.getString(
                        cursor.getColumnIndex(COLUMN_EXTRA_DATA)
                    ), type
                )
                productModel.qty_cart = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY))
                productModel.cart_id = cursor.getInt(cursor.getColumnIndex(COLUMN_CART_ID))

                list.add(productModel)
                cursor.moveToNext()
            }
            return list
        } finally {
            cursor.close()
            db!!.close()
        }
    }

    fun getProductTotalPrice(isDiscount: Boolean): Double {
        var totalPrice = 0.0
        db = dbHandler.readableDatabase
        val qry = "Select *  from $CART_TABLE"
        val cursor = db!!.rawQuery(qry, null)
        try {
            cursor.moveToFirst()

            val gson = Gson()
            val type = object : TypeToken<ProductModel>() {}.type

            for (i in 0 until cursor.count) {

                val productModel = gson.fromJson<ProductModel>(
                    cursor.getString(
                        cursor.getColumnIndex(COLUMN_EXTRA_DATA)
                    ), type
                )
                val qty = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY))
                val total = productModel.price.toString().toDouble() * qty

                if (isDiscount) {
                    if (productModel.offer_type == null) {
                        if (!productModel.discount.isNullOrEmpty()
                            && productModel.discount!!.toDouble() > 0
                        ) {
                            if (productModel.discount_type.equals("flat")) {
                                totalPrice += (total - productModel.discount!!.toDouble())
                            } else if (productModel.discount_type.equals("percentage")) {
                                totalPrice += CommonActivity.getDiscountPrice(
                                    productModel.discount!!,
                                    total.toString(),
                                    true,
                                    true
                                )
                            } else {
                                totalPrice += total
                            }
                        } else {
                            totalPrice += total
                        }
                    } else {
                        totalPrice += productModel.offer_discount!!.toDouble()
                    }
                } else {
                    totalPrice += total
                }

                cursor.moveToNext()
            }
            return totalPrice
        } finally {
            cursor.close()
            db!!.close()
        }
    }

    fun getProductQty(product_id: String): Int {
        db = dbHandler.readableDatabase
        //val qry = "Select *  from $CART_TABLE where $COLUMN_PRODUCT_ID = $product_id"
        val qry = "Select *  from $CART_TABLE"
        val cursor = db!!.rawQuery(qry, null)
        try {
            cursor.moveToFirst()

            val gson = Gson()
            val type = object : TypeToken<ProductModel>() {}.type

            var totalQty = 0

            for (i in 0 until cursor.count) {

                val productModel = gson.fromJson<ProductModel>(
                    cursor.getString(
                        cursor.getColumnIndex(COLUMN_EXTRA_DATA)
                    ), type
                )
                val qty = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY))

                if (productModel.offer_type != null
                    && productModel.productComboModelList != null
                    && productModel.productComboModelList!!.size > 0
                ) {
                    for (productComboModel in productModel.productComboModelList!!) {
                        if (productComboModel.product_id == product_id) {
                            totalQty += 1
                        }
                    }
                } else {
                    if (productModel.product_id == product_id) {
                        totalQty += qty
                    }
                }
                cursor.moveToNext()
            }
            //Log.e(context.toString(), "TotalItem::${cursor.count}")
            //Log.e(context.toString(), "TotalQTY::$totalQty")
            return totalQty
        } finally {
            cursor.close()
            db!!.close()
        }
    }

    fun deleteProduct(cart_id: Int) {
        db = dbHandler.readableDatabase
        db!!.execSQL("delete from $CART_TABLE where $COLUMN_CART_ID = $cart_id")
        db!!.close()
    }

    fun deleteProductByProductId(product_id: String) {
        db = dbHandler.readableDatabase
        db!!.execSQL("delete from $CART_TABLE where $COLUMN_PRODUCT_ID = $product_id")
        db!!.close()
    }

    fun deleteComboProduct(productModel: ProductModel): Boolean {
        val productModelList =
            getProductListByOfferDetail(productModel.product_offer_id!!, productModel.offer_type!!)
        productModel.cart_id = productModelList[productModelList.size - 1].cart_id

        for (i in productModelList.size - 1 downTo 0) {
            val productmodel = productModelList[i]
            if (productmodel.productComboModelList!!.size > 1) {
                for (i2 in productmodel.productComboModelList!!.size - 1 downTo 0) {
                    val productComboModel = productmodel.productComboModelList!![i2]
                    if (productComboModel.product_id == productModel.product_id) {
                        productmodel.productComboModelList!!.remove(productComboModel)
                        updateComboProduct(productmodel)
                        return true
                    }
                }
            } else {
                deleteProduct(productModel.cart_id)
                return true
            }
        }
        return false
    }

    fun updateComboProduct(productModel: ProductModel) {
        db = dbHandler.readableDatabase

        val values = ContentValues()
        values.put(COLUMN_PRODUCT_ID, productModel.product_id)
        values.put(COLUMN_PRODUCT_NAME, productModel.product_name_en)
        values.put(COLUMN_IN_STOCK, productModel.in_stock)
        values.put(COLUMN_PRICE, productModel.price)
        values.put(COLUMN_QTY, 0)
        values.put(COLUMN_UNIT, productModel.unit)
        values.put(COLUMN_UNIT_VALUE, productModel.unit_value)
        values.put(COLUMN_DISCOUNT, productModel.discount)
        values.put(COLUMN_DISCOUNT_TYPE, productModel.discount_type)
        values.put(COLUMN_OFFER_ID, productModel.product_offer_id)
        values.put(COLUMN_DISCOUNT_ID, productModel.product_discount_id)
        values.put(COLUMN_COMBO_ID, productModel.product_combo_id)
        values.put(COLUMN_OFFER_TYPE, productModel.offer_type)
        values.put(COLUMN_OFFER_DISCOUNT, productModel.offer_discount)
        values.put(COLUMN_IMAGE, productModel.product_image)
        val extra_data2 = gsonBuilder.toJsonTree(productModel).asJsonObject
        values.put(COLUMN_EXTRA_DATA, extra_data2.toString())

        db!!.update(
            CART_TABLE,
            values,
            COLUMN_CART_ID + "=" + productModel.cart_id,
            null
        )
        db!!.close()
    }

    fun deleteTable() {
        db = dbHandler.readableDatabase
        db!!.execSQL("delete from $CART_TABLE")
        db!!.close()
    }


}