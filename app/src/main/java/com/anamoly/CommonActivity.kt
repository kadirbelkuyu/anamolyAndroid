package com.anamoly

import Config.GlobleVariable
import Database.CartData
import Interfaces.ActionBarLoadListener
import Models.CartModel
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.custom_actionbar.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import utils.ContextWrapper
import utils.LanguagePrefs
import utils.MyBounceInterpolator
import utils.SessionManagement
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


open class CommonActivity() : AppCompatActivity() {

    var allowAnimation = false

    constructor(allowAnimation: Boolean) : this() {
        this.allowAnimation = allowAnimation
    }


    var mTitleTextView: TextView? = null

    var actionBarLoadListener: ActionBarLoadListener? = null
    var menuSetting: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (LanguagePrefs.getLang(this) != null) {
            LanguagePrefs(this)
        }
        if (allowAnimation) {
            if (LanguagePrefs.getLang(this).equals("ar")) {
                setTheme(R.style.ActionBarRTL)
            } else {
                setTheme(R.style.ActionBarLTR)
            }
        }
        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setBackgroundDrawable(
                ColorDrawable(
                    getHeaderColor()
                )
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getHeaderColor()
        }

    }

    fun setHeaderTitle(title: String) {
        val mActionBar = supportActionBar
        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false)
            val mInflater = LayoutInflater.from(this)

            val mCustomView = mInflater.inflate(R.layout.custom_actionbar, null)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = Gravity.CENTER_VERTICAL
            mCustomView.layoutParams = layoutParams
            mTitleTextView = mCustomView.tv_actionbar_title
            mTitleTextView!!.text = title
            mTitleTextView!!.setTextColor(getHeaderTextColor())
            mActionBar.customView = mCustomView
            mActionBar.setDisplayShowCustomEnabled(true)

            val upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                upArrow?.colorFilter = BlendModeColorFilter(
                    getHeaderTextColor(), BlendMode.SRC_ATOP
                )
            } else {
                upArrow?.setColorFilter(
                    getHeaderTextColor(), PorterDuff.Mode.SRC_ATOP
                )
            }
            mActionBar.setHomeAsUpIndicator(upArrow)
        }
    }

    fun setHeaderTitle(title: String, removePadding: Boolean) {
        val mActionBar = supportActionBar
        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false)
            val mInflater = LayoutInflater.from(this)

            val mCustomView = mInflater.inflate(R.layout.custom_actionbar, null)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = Gravity.CENTER_VERTICAL
            mCustomView.layoutParams = layoutParams
            if (removePadding) {
                mCustomView.ll_actionbar.setPadding(0, 0, 0, 0)
            }
            mTitleTextView = mCustomView.tv_actionbar_title
            mTitleTextView!!.text = title
            mActionBar.customView = mCustomView
            mActionBar.setDisplayShowCustomEnabled(true)
        }
    }

    fun registerActionbarListener(actionBarLoadListener: ActionBarLoadListener) {
        this.actionBarLoadListener = actionBarLoadListener
    }

    fun showSetting() {
        if (menuSetting != null)
            menuSetting!!.isVisible = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        menuSetting = menu!!.findItem(R.id.action_settings)

        if (actionBarLoadListener != null)
            actionBarLoadListener!!.onLoaded()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item!!.itemId == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition()
            } else {
                finish()
            }
        } else if (item.itemId == R.id.action_settings) {
            CartData(this).deleteTable()
            SessionManagement(this).logoutSessionLogin()
        }

        if (actionBarLoadListener != null)
            actionBarLoadListener!!.onLoaded()

        return super.onOptionsItemSelected(item)
    }

    companion object {

        @JvmStatic
        fun getHeaderColor(): Int {
            return AppController.headerColor
        }

        fun getHeaderTextColor(): Int {
            return AppController.headerTextColor
        }

        fun setStatusAndHeaderColor(activity: AppCompatActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = getHeaderColor()
            }
            activity.supportActionBar?.setBackgroundDrawable(
                ColorDrawable(
                    getHeaderColor()
                )
            )
        }

        fun setImageTint(imageView: ImageView) {
            imageView.setColorFilter(getHeaderTextColor(), PorterDuff.Mode.SRC_IN)
        }

        fun getCircleBG2(): GradientDrawable {
            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius =
                dpToPx(AppController.instance?.applicationContext!!, 180F).toFloat()
            gradientDrawable.setColor(AppController.secondButtonColor)
            return gradientDrawable
        }

        var toast: Toast? = null
        fun showToast(context: Context, message: CharSequence) {
            if (toast != null) {
                toast!!.cancel()
            }
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            toast!!.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            //layoutParams.setMargins(30, 10, 30, 0)
            val linearLayoutMain = LinearLayout(context)
            linearLayoutMain.layoutParams = layoutParams
            linearLayoutMain.setPadding(30, 10, 30, 10)
            val linearLayout = LinearLayout(context)
            linearLayout.layoutParams = layoutParams
            linearLayout.orientation = LinearLayout.HORIZONTAL
            //linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen2))
            linearLayout.setBackgroundResource(R.drawable.xml_view_green2)
            linearLayout.setPadding(50, 50, 50, 50)
            val textView = TextView(context)
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            textView.apply {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
                text = message
            }
            linearLayout.addView(textView)
            linearLayoutMain.addView(linearLayout)

            toast!!.view = linearLayoutMain
            toast!!.duration = Toast.LENGTH_LONG
            toast!!.show()
        }

        fun isEmailValid(email: String): Boolean {
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            return EMAIL_ADDRESS.matcher(email).matches()//email.matches(emailPattern.toRegex())
        }

        fun isPhoneValid(phone: String): Boolean {
            return phone.length >= 8
        }

        fun isPasswordValid(password: String): Boolean {
            return password.length >= 6
        }

        fun isValidPassword(password: String?): Boolean {
            /*(?=.*[@#$%^&+=])*/
            password?.let {
                val passwordPattern =
                    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$"
                val passwordMatcher = Regex(passwordPattern)
                return passwordMatcher.find(password) != null
            } ?: return false
        }

        fun isValidPostalCode(postalcode: String?): Boolean {
            /*(?=.*[@#$%^&+=])*/
            postalcode?.let {
                val passwordPattern =
                    "^[0-9]{4}[a-zA-Z]{2}$"
                val passwordMatcher = Regex(passwordPattern)
                return passwordMatcher.find(postalcode) != null
            } ?: return false
        }

        fun setEditTextMaxLength(editText: EditText, length: Int) {
            val filterArray = arrayOfNulls<InputFilter>(1)
            filterArray[0] = InputFilter.LengthFilter(length)
            editText.filters = filterArray
        }

        fun runLayoutAnimation(recyclerView: RecyclerView) {
            val context = recyclerView.context
            val controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

            recyclerView.layoutAnimation = controller
            recyclerView.adapter?.notifyDataSetChanged()
            recyclerView.scheduleLayoutAnimation()
        }

        fun runBounceAnimation(context: Context, view: View) {
            val animation =
                AnimationUtils.loadAnimation(context, R.anim.bounce)
            animation.interpolator = MyBounceInterpolator(0.2, 20.0)
            view.startAnimation(animation)
        }

        fun setErrorTextColor(context: Context, message: String): Spannable {
            val foregroundColorSpan =
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorBlack))
            val spannableStringBuilder = SpannableStringBuilder(message)
            spannableStringBuilder.setSpan(
                foregroundColorSpan,
                0,
                message.length,
                0
            )
            return spannableStringBuilder
        }

        fun setErrorDrawable(context: Context): Drawable {
            val drawableError = ContextCompat.getDrawable(context, R.drawable.ic_error)!!
            drawableError.setBounds(
                0,
                0,
                drawableError.intrinsicWidth,
                drawableError.intrinsicHeight
            )
            return drawableError
        }

        fun isJSONValid(test: String): Boolean {
            try {
                JSONObject(test)
            } catch (ex: JSONException) {
                // edited, to include @Arthur's comment
                // e.g. in case JSONArray is valid as well...
                try {
                    JSONArray(test)
                } catch (ex1: JSONException) {
                    return false
                }
            }
            return true
        }

        @JvmStatic
        fun dpToPx(context: Context, dp: Float): Int {
            return Math.round(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    context.resources.displayMetrics
                )
            )
        }

        fun getPriceWithCurrency(context: Context, price: String?): String {
            val currency = SessionManagement.PermanentData.getSession(context, "currency_symbol")
            return if (LanguagePrefs.getLang(context).equals("ar")) {
                "$price $currency"
            } else {
                "$currency $price"
            }
        }

        fun loadBitmapFromView(view: View, width: Int, height: Int): Bitmap? {
            val returnedBitmap =
                Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(returnedBitmap)
            //val bgDrawable = view.background
            //if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
            view.draw(canvas)
            Log.e("width", "=$width")
            Log.e("height", "=$height")
            return returnedBitmap
        }

        fun getStringByLanguage(
            context: Context,
            textEN: String?,
            textAR: String?,
            textNL: String?,
            textTR: String?,
            textDE: String?
        ): String? {
            return if (LanguagePrefs.getLang(context).equals("sv")
                && !textNL.isNullOrEmpty()
            ) {
                textNL
            } else if (LanguagePrefs.getLang(context).equals("ar")
                && !textAR.isNullOrEmpty()
            ) {
                textAR
            } else if (LanguagePrefs.getLang(context).equals("tr")
                && !textAR.isNullOrEmpty()
            ) {
                textTR
            } else if (LanguagePrefs.getLang(context).equals("de")
                && !textAR.isNullOrEmpty()
            ) {
                textDE
            } else {
                return if (textEN.isNullOrEmpty()) {
                    textNL
                } else {
                    textEN
                }
            }
        }

        // get discount price by discount amount and price
        fun getDiscountPrice(
            discount: String,
            price: String,
            getEffectedprice: Boolean,
            getPointValue: Boolean
        ): Double {
            val discount1 = java.lang.Double.parseDouble(discount)
            val price1 = java.lang.Double.parseDouble(price)
            val discount_amount = discount1 * price1 / 100

            val symbols = DecimalFormatSymbols(GlobleVariable.LOCALE)
            val format = DecimalFormat("0.#", symbols)

            if (getEffectedprice) {
                val effected_price = price1 - discount_amount
                return if (!getPointValue) {
                    String.format(GlobleVariable.LOCALE, "%.0f", effected_price).toDouble()
                } else {
                    effected_price
                }
            } else {
                return if (!getPointValue) {
                    String.format(GlobleVariable.LOCALE, "%.0f", discount_amount).toDouble()
                } else {
                    discount_amount
                }
            }
        }

        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun getCartProductsList(context: Context): ArrayList<CartModel> {
            val cartModelList = ArrayList<CartModel>()
            val cartData = SessionManagement.UserData.getSession(context, "cartData")
            if (cartData.isNotEmpty()) {
                val jsonObject = JSONObject(cartData)
                val gson = Gson()
                val typeToken = object : TypeToken<ArrayList<CartModel>>() {}.type
                cartModelList.addAll(
                    gson.fromJson<ArrayList<CartModel>>(
                        jsonObject.getString("products"),
                        typeToken
                    )
                )
            }
            return cartModelList
        }

        fun getCartDetailByCartId(context: Context, cart_id: String): CartModel? {
            val cartModelList = ArrayList<CartModel>()
            val cartData = SessionManagement.UserData.getSession(context, "cartData")
            if (cartData.isNotEmpty()) {
                val jsonObject = JSONObject(cartData)
                val gson = Gson()
                val typeToken = object : TypeToken<ArrayList<CartModel>>() {}.type
                cartModelList.addAll(
                    gson.fromJson<ArrayList<CartModel>>(
                        jsonObject.getString("products"),
                        typeToken
                    )
                )

                for (cartModel in cartModelList) {
                    for (cartItemModel in cartModel.cartItemModelList!!) {
                        if (cartItemModel.cart_id == cart_id) {
                            return cartModel
                        }
                    }
                }

            }
            return null
        }

        fun getCartDetailByProductId(context: Context, product_id: String): CartModel? {
            val cartModelList = ArrayList<CartModel>()
            val cartData = SessionManagement.UserData.getSession(context, "cartData")
            if (cartData.isNotEmpty()) {
                val jsonObject = JSONObject(cartData)
                val gson = Gson()
                val typeToken = object : TypeToken<ArrayList<CartModel>>() {}.type
                cartModelList.addAll(
                    gson.fromJson<ArrayList<CartModel>>(
                        jsonObject.getString("products"),
                        typeToken
                    )
                )

                for (i in cartModelList.size - 1 downTo 0) {
                    val cartModel = cartModelList[i]
                    for (i2 in cartModel.cartItemModelList!!.size - 1 downTo 0) {
                        val cartItemModel = cartModel.cartItemModelList[i2]
                        if (cartItemModel.product_id == product_id) {
                            return cartModel
                        }
                    }
                }
            }
            return null
        }

        fun getCartProductsQty(context: Context, product_id: String): Int {
            val cartData = SessionManagement.UserData.getSession(context, "cartData")
            if (cartData.isNotEmpty()) {
                val jsonObject = JSONObject(cartData)
                val gson = Gson()
                val typeToken = object : TypeToken<ArrayList<CartModel>>() {}.type
                val cartModelList = gson.fromJson<ArrayList<CartModel>>(
                    jsonObject.getString("products"),
                    typeToken
                )

                var totalQty = 0

                for (cartModel in cartModelList) {
                    if (cartModel.cartItemModelList?.size!! > 0) {
                        for (cartItemModel in cartModel.cartItemModelList) {
                            if (cartItemModel.product_id == product_id) {
                                totalQty += cartItemModel.qty
                            }
                        }
                    }
                }
                return totalQty
            }
            return 0
        }

        fun getCartTotalAmount(context: Context): Double {
            val cartData = SessionManagement.UserData.getSession(context, "cartData")
            if (cartData.isNotEmpty()) {
                val jsonObject = JSONObject(cartData)
                //return jsonObject.getDouble("total_amount")
                return if (jsonObject.has("cart_total")) jsonObject.getDouble("cart_total") else 0.0
            }
            return 0.0
        }

        fun getCartNetAmount(context: Context): Double {
            val cartData = SessionManagement.UserData.getSession(context, "cartData")
            if (cartData.isNotEmpty()) {
                val jsonObject = JSONObject(cartData)
                return jsonObject.getDouble("net_paid_amount")
            }
            return 0.0
        }

        fun getCartDiscount(context: Context): Double {
            val cartData = SessionManagement.UserData.getSession(context, "cartData")
            if (cartData.isNotEmpty()) {
                val jsonObject = JSONObject(cartData)
                return if (jsonObject.has("discount")) jsonObject.getDouble("discount") else 0.0
            }
            return 0.0
        }

        fun getConvertDate(converDate: String, convertType: Int): String {
            try {
                val inputPattern = "yyyy-MM-dd"

                val outputPattern1 = "dd"
                val outputPattern2 = "MM"
                val outputPattern3 = "MMM"
                val outputPattern4 = "yyyy"
                val outputPattern5 = "dd MMM yyyy"
                val outputPattern6 = "dd-MM-yyyy"
                val outputPattern7 = "hh:mm aa"
                val outputPattern8 = "dd/MM/yyyy"
                val outputPattern9 = "EEEE"

                val inputFormat = SimpleDateFormat(inputPattern, GlobleVariable.LOCALE)

                val outputFormat1 = SimpleDateFormat(outputPattern1, GlobleVariable.LOCALE)
                val outputFormat2 = SimpleDateFormat(outputPattern2, GlobleVariable.LOCALE)
                val outputFormat3 = SimpleDateFormat(outputPattern3, Locale.getDefault())
                val outputFormat4 = SimpleDateFormat(outputPattern4, GlobleVariable.LOCALE)
                val outputFormat5 = SimpleDateFormat(outputPattern5, GlobleVariable.LOCALE)
                val outputFormat6 = SimpleDateFormat(outputPattern6, GlobleVariable.LOCALE)
                val outputFormat7 = SimpleDateFormat(outputPattern7, GlobleVariable.LOCALE)
                val outputFormat8 = SimpleDateFormat(outputPattern8, GlobleVariable.LOCALE)
                val outputFormat9 = SimpleDateFormat(outputPattern9, Locale.getDefault())

                return if (convertType == 1) {
                    outputFormat1.format(inputFormat.parse(converDate)!!)
                } else if (convertType == 2) {
                    outputFormat2.format(inputFormat.parse(converDate)!!)
                } else if (convertType == 3) {
                    outputFormat3.format(inputFormat.parse(converDate)!!)
                } else if (convertType == 4) {
                    outputFormat4.format(inputFormat.parse(converDate)!!)
                } else if (convertType == 5) {
                    outputFormat5.format(inputFormat.parse(converDate)!!)
                } else if (convertType == 6) {
                    outputFormat6.format(inputFormat.parse(converDate)!!)
                } else if (convertType == 7) {
                    outputFormat7.format(inputFormat.parse(converDate)!!)
                } else if (convertType == 8) {
                    outputFormat8.format(inputFormat.parse(converDate)!!)
                } else if (convertType == 9) {
                    outputFormat9.format(inputFormat.parse(converDate)!!)
                } else {
                    converDate
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                return converDate
            }
        }

        fun getConvertTime(converDate: String, convertType: Int): String {
            try {
                val inputPattern = "HH:mm:ss"
                val outputPattern1 = "hh:mm a"
                val outputPattern2 = "HH:mm"
                val inputFormat = SimpleDateFormat(inputPattern, GlobleVariable.LOCALE)
                val outputFormat1 = SimpleDateFormat(outputPattern1, GlobleVariable.LOCALE)
                val outputFormat2 = SimpleDateFormat(outputPattern2, GlobleVariable.LOCALE)

                return if (convertType == 1) {
                    outputFormat1.format(inputFormat.parse(converDate)!!).toUpperCase()
                } else if (convertType == 2) {
                    outputFormat2.format(inputFormat.parse(converDate)!!).toUpperCase()
                } else {
                    converDate
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                return converDate
            }
        }

        fun getCurrentDateTime(isDateOnly: Boolean): String {
            val inputPattern = "yyyy-MM-dd HH:mm:ss"
            val inputPattern2 = "yyyy-MM-dd"
            val inputFormat = SimpleDateFormat(inputPattern, GlobleVariable.LOCALE)
            val inputFormat2 = SimpleDateFormat(inputPattern2, GlobleVariable.LOCALE)
            return if (isDateOnly) {
                inputFormat2.format(Date().time)
            } else {
                inputFormat.format(Date().time)
            }
        }

        fun getConvertDateTime(convertDate: String, convertType: Int): String {
            val inputPattern = "yyyy-MM-dd HH:mm:ss"

            val inputFormat = SimpleDateFormat(inputPattern, GlobleVariable.LOCALE)

            val outputPattern1 = "dd MMMM yyyy HH:mm"
            val outputPattern2 = "dd MMM yyyy HH:mm"
            val outputPattern3 = "dd"
            val outputPattern4 = "MMM"
            val outputPattern5 = "yyyy"
            val outputPattern6 = "HH:mm:ss"

            val outputFormat1 = SimpleDateFormat(outputPattern1, GlobleVariable.LOCALE)
            val outputFormat2 = SimpleDateFormat(outputPattern2, GlobleVariable.LOCALE)
            val outputFormat3 = SimpleDateFormat(outputPattern3, GlobleVariable.LOCALE)
            val outputFormat4 = SimpleDateFormat(outputPattern4, Locale.getDefault())
            val outputFormat5 = SimpleDateFormat(outputPattern5, GlobleVariable.LOCALE)
            val outputFormat6 = SimpleDateFormat(outputPattern6, GlobleVariable.LOCALE)

            return if (convertType == 1) {
                outputFormat1.format(inputFormat.parse(convertDate)!!)
            } else if (convertType == 2) {
                outputFormat2.format(inputFormat.parse(convertDate)!!)
            } else if (convertType == 3) {
                outputFormat3.format(inputFormat.parse(convertDate)!!)
            } else if (convertType == 4) {
                outputFormat4.format(inputFormat.parse(convertDate)!!)
            } else if (convertType == 5) {
                outputFormat5.format(inputFormat.parse(convertDate)!!)
            } else if (convertType == 6) {
                outputFormat6.format(inputFormat.parse(convertDate)!!)
            } else {
                convertDate
            }
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}